package com.example.sectionadapterdemo.section;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

public abstract class Section<T> {

    private Header mHeader;
    private ArrayList<T> mItemData;

    public Section(Header header, ArrayList<T> itemData) {
        mHeader = header;
        mItemData = itemData;
    }

    public ArrayList<T> getItemData(){
        return mItemData;
    }

    public int getItemSize(){
        return mItemData.size();
    }

    public Header getHeader() {
        return mHeader;
    }

    public abstract int getViewType();
    public abstract RecyclerView.ViewHolder getHeaderViewHolder(ViewGroup parent, int position);
    public abstract RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent, int position);

    public boolean hasHeader(){
        return mHeader != null;
    }

    public abstract class Header {
        abstract int getHeaderLayoutId();
    }

}
