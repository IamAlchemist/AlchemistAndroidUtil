package com.morgenworks.utilexample;

import android.app.FragmentManager;
import android.content.Context;

import com.morgenworks.alchemistutil.fingerprint.FingerprintHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This is Created by wizard on 7/30/16.
 */

@Singleton
@Module
public class AppModule {

    private Context context;
    private FragmentManager fragmentManager;

    public AppModule(Context context, FragmentManager fragmentManager){
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Provides
    FingerprintHelper provideFingerprintHelper() {
        return new FingerprintHelper(context, fragmentManager);
    }
}
