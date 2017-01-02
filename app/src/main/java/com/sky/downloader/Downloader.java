package com.sky.downloader;

import android.os.Environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by BaoCheng on 2016/12/30.
 */

public class Downloader {

    private static final String TAG = "Downloader";

    private static final int DOWNLOAD_FAIL_OTHER = -2;
    private static final int DOWNLOAD_FAIL_MD5 = -1;
    private static final int DOWNLOAD_SUCCESS = 0;


    private static volatile Downloader instance;
    private DownloadService mDownloadService;
    private static final String DEST_FILE_NAME = "ADImages";

    /**
     * 私有化构造函数
     */
    private Downloader() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://images.nationalgeographic.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        mDownloadService = retrofit.create(DownloadService.class);
    }

    public static Downloader getInstance() {
        if (instance == null) {
            synchronized (Downloader.class) {
                if (instance == null) {
                    instance = new Downloader();
                }
            }
        }
        return instance;
    }

    /**
     * 文件下载
     *
     * @param url 文件下载地址
     */
    public void download(final String url, final String md5, final Callback callback) {
        mDownloadService.downloadFile(url)
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        if (responseBody == null || responseBody.byteStream() == null) {
                            return;
                        }

                        //用ByteArrayOutputStream先把InputStream的内容放到byte[]数组里，读的时候用ByteArrayInputStream读取
                        ByteArrayOutputStream baos = null;
                        try {
                            baos = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = responseBody.byteStream().read(buffer)) != -1) {
                                baos.write(buffer, 0, len);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        byte[] content = baos.toByteArray();

                        //生成两个InputStream一个用来校验MD5，另一个用来保存文件
                        InputStream inputStream1 = new ByteArrayInputStream(content);

                        if (MD5Utils.checkMD5(md5, inputStream1)) {
                            //下载成功，MD5值正确
                            InputStream inputStream2 = new ByteArrayInputStream(content);
                            saveFileToDisk(inputStream2, url);
                            callback.onSuccess(DOWNLOAD_SUCCESS);
                        } else {
                            //MD5值错误
                            callback.onFail(DOWNLOAD_FAIL_MD5);
                        }
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable t) {
                        callback.onFail(DOWNLOAD_FAIL_OTHER);
                    }
                });
    }

    /**
     * 保存文件至本地
     *
     * @param inputStream 输入流
     * @param url         文件下载地址（这里之所以传入url， 是因为不知道下载文件的后缀，需要根据url来判断文件类型）
     * @return 是否保存成功
     */
    private void saveFileToDisk(InputStream inputStream, String url) {
        if (inputStream == null || url == null) {
            return;
        }

        File appDir = new File(Environment.getExternalStorageDirectory(), DEST_FILE_NAME);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        /*获取文件后缀名 */
        String suffix = url.substring(url.lastIndexOf(".") + 1).toLowerCase();
        String fileName = MD5Utils.calculateMD5(url) + "." + suffix;
        File destFile = new File(appDir, fileName);

        OutputStream out = null;

        try {
            byte[] buffer = new byte[4096];
            out = new FileOutputStream(destFile);
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
