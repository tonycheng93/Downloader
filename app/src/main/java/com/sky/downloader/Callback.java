package com.sky.downloader;

/**
 * Created by BaoCheng on 2016/12/30.
 */

public interface Callback {

    /**
     * 下载成功的回调
     * @param result
     */
    void onSuccess(int result);

    /**
     * 下载失败的回调
     * @param result
     */
    void onFail(int result);
}
