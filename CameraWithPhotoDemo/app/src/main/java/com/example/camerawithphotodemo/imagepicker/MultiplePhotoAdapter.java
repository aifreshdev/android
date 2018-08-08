package com.example.camerawithphotodemo.imagepicker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.camerawithphotodemo.R;
import com.example.camerawithphotodemo.imagepicker.model.Image;
import com.example.camerawithphotodemo.imagepicker.utils.DisplayUtils;
import com.example.camerawithphotodemo.imagepicker.utils.ScreenSize;

import java.util.ArrayList;

public class MultiplePhotoAdapter extends RecyclerView.Adapter<MultiplePhotoAdapter.GridItemView> {

    private Context mContext;
    private ScreenSize mScreenSize;
    private ArrayList<Image> mData = new ArrayList<>();
    private ArrayList<Image> mSelectedData = new ArrayList<>();

    public MultiplePhotoAdapter(Context context) {
        mContext = context;
    }

    public void setScreenSize(ScreenSize screenSize){
        mScreenSize = screenSize;
    }

    public void setDate(ArrayList<Image> data){
        mData = new ArrayList<>(data);
    }

    public boolean isSelected(long id){
        for (Image image : mData){
            if(id == image.id) return true;
        }

        return false;
    }

    public ArrayList<Image> getSelectedData(){
        return mSelectedData;
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public MultiplePhotoAdapter.GridItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GridItemView(LayoutInflater.from(mContext).inflate(R.layout.row_multiple_photo_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(MultiplePhotoAdapter.GridItemView holder, int position) {
        final Image image = mData.get(position);
        ViewGroup.LayoutParams lp = holder.ivPhoto.getLayoutParams();
        int size = (int) ((float) mScreenSize.width / 3f);
        lp.width = size;
        lp.height = size;

        holder.ivPhoto.setLayoutParams(lp);
        Glide.with(mContext).load(image.path).into(holder.ivPhoto);
    }

    public class GridItemView extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivPhoto;

        private GridItemView(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            final Image image = mData.get(position);
            if(isSelected(image.id)){
                mSelectedData.add(image);
            }else{
                mSelectedData.remove(image);
            }
        }
    }

}
