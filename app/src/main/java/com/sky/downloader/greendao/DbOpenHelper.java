package com.sky.downloader.greendao;

import android.content.Context;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

/**
 * @author tonycheng
 * @date 2017/12/1
 */

public class DbOpenHelper extends DaoMaster.OpenHelper {

    private static final String TAG = "DbOpenHelper";

    public DbOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: old version = " + oldVersion + " to new version = " + newVersion);

        MigrationHelper.getInstance().migrate(db, UserDao.class);
    }
}
