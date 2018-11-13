package com.example.yucong.imageloader.ocp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.example.yucong.imageloader.ocp.cache.DiskCache;
import com.example.yucong.imageloader.ocp.cache.DoubleCache;
import com.example.yucong.imageloader.siingleresponsibility.refactor.ImageCache;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 要实现图片加载   并将图片缓存起来
 *
 *
 *
 *
 *
 * 每次修改内容  都需要修改原类   不符合开闭原则
 */
public class ImageLoader {
     // 内存缓存
    ImageCache mImageCache=new ImageCache();
    // sd卡缓存
    DiskCache mDiskCache = new DiskCache();
    // 双缓存
    DoubleCache mDoubleCache = new DoubleCache() ;
    // 使用sd卡缓存
    boolean isUseDiskCache = false;
    // 使用双缓存
    boolean isUseDoubleCache = false;

        // 线程池,线程数量为CPU的数量
        ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime()
                     .availableProcessors());
   //UiHandler
    private Handler mUiHandler = new Handler(Looper.getMainLooper()) ;

    public ImageLoader() {
        super();

    }


    /**
     * 在主线程中更新界面
     * @param imageView
     * @param bitmap
     */
    private void updateImageView(final ImageView imageView, final Bitmap bitmap) {

        mUiHandler.post(new Runnable() {

            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });


    }


    public void displayImage(final String url, final ImageView imageView){


        Bitmap bitmap = null;
        if (isUseDoubleCache) {
            bitmap = mDoubleCache.get(url);
        } else if (isUseDiskCache) {
            bitmap = mDiskCache.get(url);
        } else {
            bitmap = mImageCache.get(url);
        }


        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

            //没有缓存  则联网下载
        imageView.setTag(url);
        mExecutorService.submit(new Runnable() {

            @Override
            public void run() {
                Bitmap bitmap = downloadImage(url);
                if (bitmap == null) {
                    return;
                }
                if (imageView.getTag().equals(url)) {
                    updateImageView(imageView, bitmap) ;
                }
                mImageCache.put(url, bitmap);
            }
        });
    }

    public Bitmap downloadImage(String imageUrl) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

}
