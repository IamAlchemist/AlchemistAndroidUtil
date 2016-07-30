package com.morgenworks.alchemistutil.fingerprint;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.morgenworks.alchemistutil.R;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.inject.Inject;

/**
 * This is Created by wizard on 7/29/16.
 */
public class FingerprintHelper {

    @Inject KeyguardManager keyguardManager;
    @Inject FingerprintManager fingerprintManager;
    @Inject KeyStore keyStore;
    @Inject KeyGenerator keyGenerator;
    @Inject Cipher cipher;
    @Inject SharedPreferences sharedPreferences;
    @Inject FingerprintAuthenticationDialogFragment fingerprintAuthenticationDialogFragment;

    private Context context;
    private FragmentManager fragmentManager;
//    private android.support.v4.app.FragmentManager v4fragmentManager;

    private static final String KEY_NAME = "FINGER_PRINT_HELPER_KEY_NAME";
    private static final String DIALOG_FRAGMENT_TAG = "FINGER_PRINT_DIALOG_FRAGMENT";

    public FingerprintHelper(Context context, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            DaggerFingerprintComponent.builder().fingerprintModule(new FingerprintModule(context)).build().inject(this);
            fingerprintAuthenticationDialogFragment.fingerprintHelper = this;
        }
    }

//    public FingerprintHelper(Context context, android.support.v4.app.FragmentManager v4fragmentManager) {
//        this.v4fragmentManager = v4fragmentManager;
//        this.context = context;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            DaggerFingerprintComponent.builder().fingerprintModule(new FingerprintModule(context)).build().inject(this);
//        }
//    }

    public enum FingerprintError {
        NO_SECURE_LOCK_SCREEN,
        NO_FINGER_PRINT,
        NO_PERMISSION,
        NO_KEYGUARD_MANAGER,
        NO_FINGER_PRINT_MANAGER,
        NO_KEY_STORE,
        NO_KEY_GENERATOR,
        NO_CIPHER,
        NO_GENERATED_KEY,
        NO_INIT_CIPHER,
        API_NOT_SUPPORT,
        KEY_PERMANENTLY_INVALIDATE,
        INTERNAL_ERROR,
        OK
    }

    public FingerprintError available() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return FingerprintError.API_NOT_SUPPORT;
        }

        if (keyguardManager == null) { return FingerprintError.NO_KEYGUARD_MANAGER; }
        if (fingerprintManager == null) { return FingerprintError.NO_FINGER_PRINT_MANAGER; }
        if (keyStore == null) { return FingerprintError.NO_KEY_STORE; }
        if (keyGenerator == null) { return FingerprintError.NO_KEY_GENERATOR; }
        if (cipher == null) { return FingerprintError.NO_CIPHER; }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return FingerprintError.NO_PERMISSION;
        }
        
        if (!keyguardManager.isKeyguardSecure()) {
            return FingerprintError.NO_SECURE_LOCK_SCREEN;
        }


        //noinspection MissingPermission
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            return FingerprintError.NO_FINGER_PRINT;
        }

        if (!createKey()) {
            return FingerprintError.NO_GENERATED_KEY;
        }

        return FingerprintError.OK;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public FingerprintError showFingerprintAuthenticationDialogFragment(FingerprintAuthenticationCallback callback) {
        fingerprintAuthenticationDialogFragment.fingerprintAuthenticationCallback = callback;

        FingerprintError fingerprintError = initCipher();

        if (fingerprintError == FingerprintError.NO_INIT_CIPHER) {
            return fingerprintError;
        }

        if (fingerprintError == FingerprintError.KEY_PERMANENTLY_INVALIDATE) {
            fingerprintAuthenticationDialogFragment.setCryptoObject(
                    new FingerprintManager.CryptoObject(cipher));

            fingerprintAuthenticationDialogFragment.setStage(
                    FingerprintAuthenticationDialogFragment.Stage.NEW_FINGERPRINT_ENROLLED);

            fingerprintAuthenticationDialogFragment.show(fragmentManager,
                    DIALOG_FRAGMENT_TAG);

            return FingerprintError.OK;
        }
        else if (fingerprintError == FingerprintError.OK) {
            fingerprintAuthenticationDialogFragment.setCryptoObject(
                    new FingerprintManager.CryptoObject(cipher));

            boolean useFingerprintPreference = sharedPreferences
                    .getBoolean(context.getString(R.string.use_fingerprint_to_authenticate_key),
                            true);

            if (useFingerprintPreference) {
                fingerprintAuthenticationDialogFragment.setStage(
                        FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
            } else {
                fingerprintAuthenticationDialogFragment.setStage(
                        FingerprintAuthenticationDialogFragment.Stage.PASSWORD);
            }

            fingerprintAuthenticationDialogFragment.show(fragmentManager, DIALOG_FRAGMENT_TAG);

            return FingerprintError.OK;
        }
        else {
            return FingerprintError.INTERNAL_ERROR;
        }
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean createKey() {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            keyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
            
            return true;
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            e.printStackTrace();
            Log.e(getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }
    
    /**
     * Initialize the {@link Cipher} instance with the created key in the {@link #createKey()}
     * method.
     *
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private FingerprintError initCipher() {
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return FingerprintError.OK;
        } catch (KeyPermanentlyInvalidatedException e) {
            return FingerprintError.KEY_PERMANENTLY_INVALIDATE;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            Log.e(getClass().getSimpleName(), e.getMessage());
            return FingerprintError.NO_INIT_CIPHER;
        }
    }
}
