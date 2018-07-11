package com.example.complexrecyclerview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by vincent on 23/3/2018.
 */

public class ComplexAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_TITLE = 0,
            TYPE_IMAGE = 1,
            TYPE_GALLERY = 2,
            TYPE_GRID = 3,
            TYPE_INFO = 4;

    Context context;
    ArrayList<HashMap<String, Integer>> list = new ArrayList<>();

    public ComplexAdapter(Context ctx, ArrayList<HashMap<String, Integer>> list) {
        this.context = ctx;
        this.list = new ArrayList<>(list);
    }

    @Override
    public int getItemViewType(int position) {
        HashMap<String, Integer> data = list.get(position);
        return data.get("type");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_TITLE: return new TitleHolder(LayoutInflater.from(context).inflate(R.layout.row_complex_list_title_item, parent, false));
            case TYPE_INFO: return new InfoHolder(LayoutInflater.from(context).inflate(R.layout.row_complex_list_info_item, parent, false));
            case TYPE_IMAGE: return new ImageHolder(LayoutInflater.from(context).inflate(R.layout.row_complex_list_image_item, parent, false));
            case TYPE_GALLERY: return new GalleryHolder(LayoutInflater.from(context).inflate(R.layout.row_complex_list_gallery_item, parent, false));
            case TYPE_GRID: return new GridHolder(LayoutInflater.from(context).inflate(R.layout.row_complex_list_grid_item, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof TitleHolder){
            TitleHolder titleHolder = (TitleHolder)holder;
            titleHolder.tvTitle.setText("Title " + position);
        }else if(holder instanceof InfoHolder) {
            InfoHolder infoHolder = (InfoHolder)holder;
            infoHolder.tvInfo.setText("Info " + position);
        }else if(holder instanceof ImageHolder){
            ImageHolder imageHolder = (ImageHolder)holder;
        }else if(holder instanceof GalleryHolder){
            GalleryHolder galleryHolder = (GalleryHolder)holder;
            galleryHolder.setItems();
        }else if(holder instanceof GridHolder){
            GridHolder gridHolder = (GridHolder)holder;
            gridHolder.setItems();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class GridHolder extends RecyclerView.ViewHolder {

        GridView gridView;
        BaseAdapter imageAdapter;

        public GridHolder(View itemView) {
            super(itemView);
            gridView = itemView.findViewById(R.id.gridview);
        }

        public void setItems(){
            imageAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return 2;
                }

                @Override
                public Object getItem(int position) {
                    return null;
                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ImageView imageView;
                    if (convertView == null) {
                        // if it's not recycled, initialize some attributes
                        imageView = new ImageView(context);
                        imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setPadding(8, 8, 8, 8);
                    } else {
                        imageView = (ImageView) convertView;
                    }

                    imageView.setImageResource(R.mipmap.ic_launcher);
                    return imageView;
                }
            };

            gridView.setAdapter(imageAdapter);
        }
    }

    class GalleryHolder extends RecyclerView.ViewHolder{

        ViewPager viewPager;
        CircleIndicator indicator;
        PagerAdapter pagerAdapter;

        public GalleryHolder(View itemView) {
            super(itemView);
            viewPager = itemView.findViewById(R.id.viewpager);
            indicator = itemView.findViewById(R.id.indicator);
        }

        public void setItems(){
            pagerAdapter = new PagerAdapter() {

                @Override
                public Object instantiateItem(ViewGroup container, int position) {
                    View view = new View(container.getContext());
                    container.addView(view);
                    return view;
                }

                @Override
                public int getCount() {
                    return 10;
                }

                @Override
                public boolean isViewFromObject(View view, Object object) {
                    return view == object;
                }
            };

            viewPager.setAdapter(pagerAdapter);
            indicator.setViewPager(viewPager);
        }

    }

    class ImageHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;

        public ImageHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
        }
    }

    class TitleHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;

        public TitleHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }

    class InfoHolder extends RecyclerView.ViewHolder {

        TextView tvInfo;

        public InfoHolder(View itemView) {
            super(itemView);
            tvInfo = itemView.findViewById(R.id.tv_info);
        }
    }
}
