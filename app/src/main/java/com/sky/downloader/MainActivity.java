package com.sky.downloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.sky.downloader.downloader.Callback;
import com.sky.downloader.downloader.Downloader;
import com.sky.downloader.downloader.FileUtils;
import com.sky.downloader.greendao.DaoSession;
import com.sky.downloader.greendao.User;

import org.greenrobot.greendao.rx.RxDao;
import org.greenrobot.greendao.rx.RxQuery;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Button mBtnAdd;
    private Button mBtnDelete;
    private Button mBtnUpdate;
    private Button mBtnQuery;
    private Button mBtnCancel;
    private ImageView mIvImage;

    private DaoSession mDaoSession;
    private User mUser = new User();
    private RxDao<User, Long> mRxDao;
    private RxQuery<User> mRxQuery;

    private String url = "http://img.taopic.com/uploads/allimg/120423/107913-12042323220753.jpg";
    private String md5 = "73421a41929c0b98142fb096d5eebbfd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mBtnAdd = (Button) findViewById(R.id.add);
        mBtnDelete = (Button) findViewById(R.id.delete);
        mBtnUpdate = (Button) findViewById(R.id.update);
        mBtnQuery = (Button) findViewById(R.id.query);
        mBtnCancel = (Button) findViewById(R.id.cancel);
        mIvImage = (ImageView) findViewById(R.id.iv_image);

        mDaoSession = App.getDaoSession();
        mRxDao = mDaoSession.getUserDao().rx();
        /*--------------------test start-----------------*/
        FileUtils.setContext(this);
        final File appDir = FileUtils.getAppDir();

        Downloader.getInstance().download(url, md5, new Callback() {
            @Override
            public void onSuccess(int result) {
                Log.d(TAG, "onSuccess: " + result);
            }

            @Override
            public void onFail(int result) {
                Log.d(TAG, "onFail: " + result);
            }
        });

        Button delete = (Button) findViewById(R.id.delete_folder);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtils.deleteDirectory(appDir);
                mDaoSession.getUserDao().deleteAll();
            }
        });
         /*--------------------test end-----------------*/

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUser.setId(1L);
                mUser.setName("tonycheng93");
                mUser.setAge(21);
                try {
                    mDaoSession.getUserDao().insert(mUser);
                } catch (Exception e) {
                    Log.e(TAG, "数据库插入失败");
                }
                Toast.makeText(MainActivity.this, "插入成功", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    mDaoSession.getUserDao().deleteByKey(1l);//通过Long型id删除一行记录
                    mDaoSession.getUserDao().deleteAll();
                } catch (Exception e) {
                    Log.e(TAG, "删除数据库失败");
                }
                Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mUser.setName("tony");
                    mDaoSession.getUserDao().update(mUser);
                } catch (Exception e) {
                    Log.e(TAG, "更新数据失败");
                }
                Toast.makeText(MainActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<User> userList = mDaoSession.getUserDao().loadAll();
                    mDaoSession.getUserDao().queryRaw("name", "21");
                    for (User user : userList) {
                        Log.d(TAG, "id = " + user.getId() +
                                ",name = " + user.getName() +
                                ",age = " + user.getAge());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "查询数据失败");
                }
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
