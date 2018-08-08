package com.example.camerawithphotodemo.imagepicker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.camerawithphotodemo.R;
import com.example.camerawithphotodemo.imagepicker.model.Image;
import com.example.camerawithphotodemo.imagepicker.utils.DisplayUtils;
import com.example.camerawithphotodemo.imagepicker.utils.PermissionUtils;

import java.util.ArrayList;

public class MultiplePhotoPicker extends AppCompatActivity {

    private final String TAG = "MultiplePhotoPicker";
    private final int PERMISSION_READ_AND_WRITE = 1;

    private String[] mProjection = {
            MediaStore.Images.Media._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT
    };

    private final int FLAG_ALL_PHOTO_LOADED = 1;

    private ContentObserver mObserver;
    private ContentResolver mResolver;
    private PhotoLoader mPhotoLoader;
    private RecyclerView mRecyclerView;
    private MultiplePhotoAdapter mMultiplePhotoAdapter;
    private Handler mFeedbackHandler;

    public static void open(Context context) {
        Intent intent = new Intent(context, MultiplePhotoPicker.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_photo_picker);
        mMultiplePhotoAdapter = new MultiplePhotoAdapter(this);
        mMultiplePhotoAdapter.setScreenSize(DisplayUtils.getScreenSize(this));
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(mMultiplePhotoAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionUtils.canReadAndWrite(this)) {
            setContentObserver();
            loadAllPhoto();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PERMISSION_READ_AND_WRITE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ_AND_WRITE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setContentObserver();
                    loadAllPhoto();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mResolver != null) {
            mResolver.unregisterContentObserver(mObserver);
            mObserver = null;
        }

        if (mFeedbackHandler != null) {
            mFeedbackHandler.removeCallbacksAndMessages(null);
            mFeedbackHandler = null;
        }

        if (mPhotoLoader != null) {
            mPhotoLoader.cancel(true);
            mPhotoLoader = null;
        }
    }

    private void loadAllPhoto() {
        mPhotoLoader = new PhotoLoader();
        mPhotoLoader.execute();
    }

    private void setContentObserver() {
        mFeedbackHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case FLAG_ALL_PHOTO_LOADED:
                        Log.i(TAG, "handleMessage: all photo loaded.");
                        break;
                }
            }
        };

        mResolver = getContentResolver();
        mObserver = new ContentObserver(mFeedbackHandler) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                Log.i(TAG, "onChange: " + uri.toString());
                mPhotoLoader.execute();
            }
        };

        mResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, mObserver);
    }

    private class PhotoLoader extends AsyncTask<Void, Void, ArrayList<Image>> {

        @Override
        protected ArrayList<Image> doInBackground(Void... voids) {
            final ArrayList<Image> listOfAllImages = new ArrayList<>();
            final Cursor cursor = getApplicationContext().getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mProjection,
                            null, null, MediaStore.Images.Media.DATE_ADDED);

            if (cursor == null || isCancelled()) {
                return null;
            }

            int indexCount = 0;
            if (cursor.moveToLast()) {
                do {
                    if (isCancelled())
                        break;

                    long id = cursor.getLong(cursor.getColumnIndex(mProjection[0]));
                    String name = cursor.getString(cursor.getColumnIndex(mProjection[1]));
                    String path = cursor.getString(cursor.getColumnIndex(mProjection[2]));
                    String mimeType = cursor.getString(cursor.getColumnIndex(mProjection[3]));
                    long size = cursor.getInt(cursor.getColumnIndex(mProjection[4]));
                    int width = cursor.getInt(cursor.getColumnIndex(mProjection[5]));
                    int height = cursor.getInt(cursor.getColumnIndex(mProjection[5]));
                    boolean isSelected = mMultiplePhotoAdapter.isSelected(id);

                    listOfAllImages.add(indexCount++, new Image(id, name, path, mimeType, size, width, height, isSelected));
                } while (cursor.moveToPrevious());
            }

            cursor.close();

            sendFeedbackMessage(FLAG_ALL_PHOTO_LOADED);
            return listOfAllImages;
        }

        @Override
        protected void onPostExecute(ArrayList<Image> listOfAllImages) {
            mMultiplePhotoAdapter.setDate(listOfAllImages);
            mMultiplePhotoAdapter.notifyDataSetChanged();
        }
    }

    private void sendFeedbackMessage(int flag) {
        if (mFeedbackHandler == null) {
            return;
        }

        Message message = mFeedbackHandler.obtainMessage();
        message.arg1 = flag;
        message.sendToTarget();
    }
}
