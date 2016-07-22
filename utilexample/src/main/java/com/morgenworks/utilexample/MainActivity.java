package com.morgenworks.utilexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.morgenworks.alchemistutil.FileUtils;
import com.morgenworks.alchemistutil.RecyclerStringItem;
import com.morgenworks.alchemistutil.SimpleRecyclerAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        List<RecyclerStringItem> data = new ArrayList<>();
        data.add(new RecyclerStringItem("hello"));
        data.add(new RecyclerStringItem("world"));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        recyclerView.setAdapter(new SimpleRecyclerAdapter<>(data, null));

        FileUtils.createGetContentIntent();
    }
}
