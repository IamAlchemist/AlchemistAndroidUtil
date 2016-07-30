package com.morgenworks.utilexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.morgenworks.alchemistutil.FileUtils;
import com.morgenworks.alchemistutil.RecyclerStringItem;
import com.morgenworks.alchemistutil.SimpleRecyclerAdapter;
import com.morgenworks.alchemistutil.SimpleRecyclerAdapterDelegate;
import com.morgenworks.alchemistutil.fingerprint.FingerprintAuthenticationCallback;
import com.morgenworks.alchemistutil.fingerprint.FingerprintHelper;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    @Inject FingerprintHelper fingerprintHelper;

    private static String FingerPrint = "finger print";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaggerAppComponent.builder().appModule(new AppModule(getBaseContext(), getFragmentManager())).build().inject(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        List<RecyclerStringItem> data = new ArrayList<>();
        data.add(new RecyclerStringItem(FingerPrint));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        recyclerView.setAdapter(new SimpleRecyclerAdapter<>(data, recyclerDelegate));

        FileUtils.createGetContentIntent();
    }

    private SimpleRecyclerAdapterDelegate<RecyclerStringItem> recyclerDelegate = new SimpleRecyclerAdapterDelegate<RecyclerStringItem>() {
        @Override
        public void itemDidSelected(RecyclerStringItem item, int position) {
            if (item.getText().equals(FingerPrint)) {

                FingerprintHelper.FingerprintError error = fingerprintHelper.available();

                if (error == FingerprintHelper.FingerprintError.OK) {

                    fingerprintHelper.showFingerprintAuthenticationDialogFragment(
                            new FingerprintAuthenticationCallback() {
                                @Override
                                public void succeeded() {
                                    Toast.makeText(getBaseContext(), "success", Toast.LENGTH_SHORT)
                                            .show();
                                }

                                @Override
                                public void cancelled() {
                                    Toast.makeText(getBaseContext(), "success", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                }
                else {
                    Toast.makeText(getBaseContext(), "not available", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    };
}
