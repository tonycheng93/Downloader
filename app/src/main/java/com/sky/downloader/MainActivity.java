package com.sky.downloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Button mBtnLoadImage;
    private ImageView mIvImage;

    String url = "http://images.nationalgeographic.com/wpf/media-live/photos/000/936/cache/bear-road-denali_93621_990x742.jpg";
    String md5 = "a9060c0270f0e0bc7461fc6b17d38cb9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnLoadImage = (Button) findViewById(R.id.load_image);
        mIvImage = (ImageView) findViewById(R.id.iv_image);

        mBtnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Downloader.getInstance().download(url, md5, new Callback() {
                    @Override
                    public void onSuccess(int result) {
                        Log.d(TAG, "onSuccess: " + result);
                    }

                    @Override
                    public void onFail(int result) {
                        Log.e(TAG, "onFail: " + result);
                    }
                });
            }
        });
    }
}
