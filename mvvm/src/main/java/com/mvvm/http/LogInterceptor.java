package com.mvvm.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.mvvm.util.PackageUtils;
import com.mvvm.util.SharedPreferencesUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * 后续所有的网络相关公共参数以及缓存配置可以在该类实现
 * Created by hq on 2018/9/12.
 */

public class LogInterceptor implements Interceptor {

    public static String TAG = "LogInterceptor";
    private final Context mContext;

    public LogInterceptor(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Request.Builder newRequestBuild = null;
        String method = oldRequest.method();
        String postBodyString = "";
        if ("POST".equals(method)) {
            RequestBody oldBody = oldRequest.body();
            if (oldBody instanceof FormBody) {
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                formBodyBuilder.add("appid", "ANDRID");
                formBodyBuilder.add("versionNum", PackageUtils.getVersionName(mContext));
                formBodyBuilder.add("appName", PackageUtils.getAppName(mContext));
                newRequestBuild = oldRequest.newBuilder();

                RequestBody formBody = formBodyBuilder.build();
                postBodyString = bodyToString(oldRequest.body());
                postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);
                newRequestBuild.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), postBodyString));
            } else if (oldBody instanceof MultipartBody) {
                MultipartBody oldBodyMultipart = (MultipartBody) oldBody;
                List<MultipartBody.Part> oldPartList = oldBodyMultipart.parts();
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), "ANDROID");
                RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), PackageUtils.getVersionName(mContext));
                RequestBody requestBody3 = RequestBody.create(MediaType.parse("text/plain"), PackageUtils.getAppName(mContext));
                for (MultipartBody.Part part : oldPartList) {
                    builder.addPart(part);
                    postBodyString += (bodyToString(part.body()) + "\n");
                }
                postBodyString += (bodyToString(requestBody1) + "\n");
                postBodyString += (bodyToString(requestBody2) + "\n");
                postBodyString += (bodyToString(requestBody3) + "\n");
//              builder.addPart(oldBody);  //不能用这个方法，因为不知道oldBody的类型，可能是PartMap过来的，也可能是多个Part过来的，所以需要重新逐个加载进去
                builder.addPart(requestBody1);
                builder.addPart(requestBody2);
                builder.addPart(requestBody3);
                newRequestBuild = oldRequest.newBuilder();
                newRequestBuild.post(builder.build());
                Log.e(TAG, "MultipartBody," + oldRequest.url());
            } else {
                newRequestBuild = oldRequest.newBuilder();
            }
        } else {
            // 添加新的参数
            HttpUrl.Builder commonParamsUrlBuilder = oldRequest.url()
                    .newBuilder()
                    .scheme(oldRequest.url().scheme())
                    .host(oldRequest.url().host())
                    .addQueryParameter("deviceOs", "ANDROID")
                    .addQueryParameter("versionNum", PackageUtils.getVersionName(mContext))
                    .addQueryParameter("appName", PackageUtils.getAppName(mContext));
            newRequestBuild = oldRequest.newBuilder()
                    .method(oldRequest.method(), oldRequest.body())
                    .url(commonParamsUrlBuilder.build());
        }

        String token = SharedPreferencesUtils.getFromSharedPreferences(mContext, "Login_File", "Login_Key_Token");
        Request newRequest = newRequestBuild
                .addHeader("Accept", "application/json")
                .addHeader("Accept-Language", "zh")
                .addHeader("token", token)
                .build();

        long startTime = System.currentTimeMillis();
        okhttp3.Response response = chain.proceed(newRequest);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        int httpStatus = response.code();
        StringBuilder logSB = new StringBuilder();
        logSB.append("-------start:" + method + "|");
        logSB.append(newRequest.toString() + "\n|");
        logSB.append(method.equalsIgnoreCase("POST") ? "post参数{" + postBodyString + "}\n|" : "");
        logSB.append("httpCode=" + httpStatus + ";Response:" + content + "\n|");
        logSB.append("----------End:" + duration + "毫秒----------");
        Log.d(TAG, logSB.toString());
        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}