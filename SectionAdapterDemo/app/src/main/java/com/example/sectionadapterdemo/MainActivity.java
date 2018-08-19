package com.example.sectionadapterdemo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.example.sectionadapterdemo.section.Section;
import com.example.sectionadapterdemo.section.SectionAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private class SampleAdapter extends SectionAdapter {

        private final int TYPE_ITEM = 1;

        public class ItemSection extends Section<String>{

            public ItemSection(Header header, ArrayList<String> itemData) {
                super(header, itemData);
            }

            @Override
            public int getViewType() {
                return TYPE_ITEM;
            }

            @Override
            public RecyclerView.ViewHolder getHeaderViewHolder(ViewGroup parent, int position) {
                return null;
            }

            @Override
            public RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent, int position) {
                return null;
            }
        }

        public void addHeader(){
            addSection();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }
    }
}
