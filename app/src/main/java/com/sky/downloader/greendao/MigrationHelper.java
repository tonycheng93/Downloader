package com.sky.downloader.greendao;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tonycheng on 2017/12/1.
 */

public final class MigrationHelper {

    private static final String TAG = "MigrationHelper";

    private MigrationHelper() {
    }

    private static class SingletonHolder {
        private static final MigrationHelper INSTANCE = new MigrationHelper();
    }

    public static MigrationHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void migrate(Database db, Class<? extends AbstractDao<?, ?>>... daoClazz) {
        //1、新建临时表
        createTempTables(db, daoClazz);
        //2、创建新表
//        createNewTables(db, daoClazz);
        DaoMaster.dropAllTables(db, true);
        DaoMaster.createAllTables(db, false);
//        //3、将临时表中的数据写入新表，同时删除临时表
        restoreDataToNewTables(db, daoClazz);
    }

    private void createTempTables(Database db, Class<? extends AbstractDao<?, ?>>[] daoClazz) {
        for (Class<? extends AbstractDao<?, ?>> aDaoClazz : daoClazz) {
            DaoConfig daoConfig = new DaoConfig(db, aDaoClazz);

            String divider = "";
            String tableName = daoConfig.tablename;
            String tempTableName = tableName.concat("_TEMP");
            Log.d(TAG, "createTempTables: table name = " + tableName + ",temp table name = " + tempTableName);
            ArrayList<String> properties = new ArrayList<>();

            StringBuilder createTableBuilder = new StringBuilder();
            createTableBuilder.append("CREATE TABLE ")
                    .append(tempTableName)
                    .append(" (");
            for (int i = 0, length = daoConfig.properties.length; i < length; i++) {
                String columnName = daoConfig.properties[i].columnName;
                if (getColumns(db, tableName).contains(columnName)) {
                    properties.add(columnName);
                }
                String type = getTypeByClass(daoConfig.properties[i].type);
                createTableBuilder.append(divider)
                        .append(columnName)
                        .append(" ")
                        .append(type);
                if (daoConfig.properties[i].primaryKey) {
                    createTableBuilder.append(" PRIMARY KEY");
                }
                divider = ",";
            }
            createTableBuilder.append(");");

            db.execSQL(createTableBuilder.toString());

            StringBuilder insertTableBuilder = new StringBuilder();
            insertTableBuilder.append("INSERT INTO ")
                    .append(tempTableName)
                    .append(" (")
                    .append(TextUtils.join(",", properties))
                    .append(") SELECT ")
                    .append(TextUtils.join(",", properties))
                    .append(" FROM ")
                    .append(tableName)
                    .append(";");
            db.execSQL(insertTableBuilder.toString());
        }
    }

    private static void createNewTables(Database db, Class<? extends AbstractDao<?, ?>>[] daoClazz) {

    }

    private void restoreDataToNewTables(Database db, Class<? extends AbstractDao<?, ?>>[] daoClazz) {
        for (Class<? extends AbstractDao<?, ?>> aDaoClazz : daoClazz) {
            DaoConfig daoConfig = new DaoConfig(db, aDaoClazz);

            String tableName = daoConfig.tablename;
            String tempTableName = tableName.concat("_TEMP");
            ArrayList<String> properties = new ArrayList<>();

            for (int i = 0, length = daoConfig.properties.length; i < length; i++) {
                String columnName = daoConfig.properties[i].columnName;
                if (getColumns(db, tempTableName).contains(columnName)) {
                    properties.add(columnName);
                }
            }

            StringBuilder insertTableBuilder = new StringBuilder();
            insertTableBuilder.append("INSERT INTO ")
                    .append(tableName)
                    .append(" (")
                    .append(TextUtils.join(",", properties))
                    .append(") SELECT ")
                    .append(TextUtils.join(",", properties))
                    .append(" FROM ")
                    .append(tempTableName)
                    .append(";");

            StringBuilder dropTableBuilder = new StringBuilder();
            dropTableBuilder.append("DROP TABLE ")
                    .append(tempTableName);

            db.execSQL(insertTableBuilder.toString());
            db.execSQL(dropTableBuilder.toString());
        }
    }

    private String getTypeByClass(Class<?> type) {
        if (type.equals(String.class)) {
            return "TEXT";
        }
        if (type.equals(Long.class) || type.equals(Integer.class) || type.equals(long.class)) {
            return "INTEGER";
        }
        if (type.equals(Boolean.class)) {
            return "BOOLEAN";
        }
        return null;
    }

    private List<String> getColumns(Database db, String tableName) {
        List<String> columns = new ArrayList<>();
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 1", null);
        try {
            if (cursor != null) {
                columns = Arrays.asList(cursor.getColumnNames());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return columns;
    }
}
