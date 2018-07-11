package com.example.complexrecyclerview;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewpager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPager.post(new Runnable() {
            @Override
            public void run() {

                viewPager.setAdapter(new PagerAdapter() {

                    @Override
                    public Object instantiateItem(ViewGroup container, int position) {
                        RecyclerView recyclerView = new RecyclerView(container.getContext());
                        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
                        recyclerView.setAdapter(new ComplexAdapter(MainActivity.this, getList()));
                        container.addView(recyclerView);
                        return recyclerView;
                    }

                    @Override
                    public int getCount() {
                        return 20;
                    }

                    @Override
                    public void destroyItem(ViewGroup container, int position, Object object) {
                        container.removeView((RecyclerView) object);
                    }

                    @Override
                    public boolean isViewFromObject(View view, Object object) {
                        return view == object;
                    }
                });
            }
        });
    }

    private ArrayList<HashMap<String, Integer>> getList(){
        ArrayList<HashMap<String, Integer>> list = new ArrayList<>();
        list.add(getTypeMap("type", ComplexAdapter.TYPE_TITLE));
        list.add(getTypeMap("type", ComplexAdapter.TYPE_GALLERY));
        list.add(getTypeMap("type", ComplexAdapter.TYPE_INFO));
        list.add(getTypeMap("type", ComplexAdapter.TYPE_TITLE));
        list.add(getTypeMap("type", ComplexAdapter.TYPE_GRID));
        list.add(getTypeMap("type", ComplexAdapter.TYPE_TITLE));
        list.add(getTypeMap("type", ComplexAdapter.TYPE_IMAGE));
        return list;
    }

    private HashMap<String, Integer> getTypeMap(String type, int value){
        HashMap<String, Integer> map = new HashMap<>();
        map.put(type, value);
        return map;
    }


}
