package com.example.sectionadapterdemo.section;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public abstract class SectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final static int TYPE_HEADER = 1;
    public final static int TYPE_ITEM = 2;

    private ArrayList<Section> mSections = new ArrayList<>();

    public void clear(){
        mSections = new ArrayList<>();
    }

    public void addSection(Section section){
        mSections.add(section);
    }

    @Override
    public int getItemViewType(int position) {
        return mSections.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        int total = 0;
        for(Section section : mSections){
            if(section.hasHeader()){
                total += section.getItemSize() + 1;
            }else{
                total += section.getItemSize();
            }
        }

        return total;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        int viewType = getItemViewType(position);
        switch (viewType){
            case TYPE_HEADER:
                return getHeaderViewHolder(parent, position);
            case TYPE_ITEM:
                return getItemViewHolder(parent, position);

        }

        return null;
    }

    abstract RecyclerView.ViewHolder getHeaderViewHolder(ViewGroup parent, int position);
    abstract RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent, int position);

}
