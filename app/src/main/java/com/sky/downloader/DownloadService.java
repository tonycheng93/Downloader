package com.sky.downloader;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by BaoCheng on 2016/12/30.
 */

public interface DownloadService {

    /**
     * 默认情况下，Retrofit在处理结果前会将整个Server Response读进内存，
     * 这在JSON或者XML等Response上表现还算良好，但如果是一个非常大的文件，
     * 就可能造成OutOfMemory异常。
     *
     * @param url 文件url
     * @return 返回 Flowable 对象
     */
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);

//    /**
//     * 下载大文件使用该方法，添加 @Streaming注解
//     * @param url
//     * @return
//     */
//    @Streaming
//    @GET
//    Flowable<ResponseBody> downloadFile(@Url String url);
}
