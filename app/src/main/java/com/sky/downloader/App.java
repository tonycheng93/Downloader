package com.sky.downloader;

import android.app.Application;
import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.sky.downloader.greendao.DaoMaster;
import com.sky.downloader.greendao.DaoSession;
import com.sky.downloader.greendao.DbOpenHelper;
import com.squareup.picasso.Picasso;

import org.greenrobot.greendao.database.Database;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by tonycheng on 2017/1/13.
 */

public class App extends Application {

    private static final String DATA_BASE_NAME = "User.db";
    private static DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        setupDataBase(this);

        setPicasso();
    }

    private void setPicasso() {
        File file = new File("your cache path");
        if (!file.exists()) {
            file.mkdirs();
        }

        long maxSize = Runtime.getRuntime().maxMemory() / 8;//设置图片缓存大小为运行时缓存的八分之一
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(new Cache(file, maxSize))
                .build();

        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(client))
                .build();
    }

    private void setupDataBase(Context context) {
        DbOpenHelper openHelper = new DbOpenHelper(context, DATA_BASE_NAME);
        Database db = openHelper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return mDaoSession;
    }
}
