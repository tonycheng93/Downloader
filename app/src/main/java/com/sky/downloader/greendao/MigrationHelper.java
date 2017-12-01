package com.sky.downloader.greendao;

import android.util.Log;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.internal.DaoConfig;

/**
 * Created by tonycheng on 2017/12/1.
 */

public final class MigrationHelper {

    private static final String TAG = "MigrationHelper";

    private MigrationHelper() {
    }

    public static void migrate(Database db, Class<? extends AbstractDao<?, ?>>... daoClazz) {
        //1、新建临时表
        createTempTables(db, daoClazz);
        //2、创建新表
//        createNewTables(db, daoClazz);
//        DaoMaster.dropAllTables(db, true);
//        DaoMaster.createAllTables(db, false);
        //3、将临时表中的数据写入新表，同时删除临时表
//        restoreDataToNewTables(db, daoClazz);
    }

    private static void createTempTables(Database db, Class<? extends AbstractDao<?, ?>>[] daoClazz) {
        for (int i = 0, length = daoClazz.length; i < length; i++) {
            DaoConfig daoConfig = new DaoConfig(db, daoClazz[i]);

            String tableName = daoConfig.tablename;
            String tempTableName = tableName.concat("_TEMP");
            Log.d(TAG, "createTempTables: table name = " + tableName + ",temp table name = " + tempTableName);
            String sql = "alter table Table_User rename to Table_User_Temp";
            db.execSQL(sql);
        }
    }

    private static void createNewTables(Database db, Class<? extends AbstractDao<?, ?>>[] daoClazz) {

    }

    private static void restoreDataToNewTables(Database db, Class<? extends AbstractDao<?, ?>>[] daoClazz) {

    }
}
