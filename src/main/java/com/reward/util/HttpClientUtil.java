package com.reward.util;

import com.reward.model.CallBackInfo;
import com.squareup.okhttp.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OkHttpClient
 * Created by hztps on 2016/9/8.
 */
public class HttpClientUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static Response response;

    private static OkHttpClient client = new OkHttpClient();


    /**
     * 发送get请求,不携带cookie
     *
     * @param url
     * @param content
     * @return
     */
    public static String sendGet(String url, String content) {
        try {
            return doGet(url, client, content, null).getContent();
        } catch (Exception e) {
            logger.error("发送 GET 请求出现异常！", e);
        }

        return null;
    }

    /**
     * 发送get请求，携带cookie
     *
     * @param url
     * @param content
     * @return
     */
    public static CallBackInfo sendGet(String url, String content, String cookie) throws Exception {
        return doGet(url, client, content, cookie);
    }

    /**
     * 发送put请求，不携带cookie
     *
     * @param url
     * @param content
     * @return
     */
    public static String sendPut(String url, String content) {
        try {
            return doPut(url, client, content, null).getContent();
        } catch (Exception e) {
            logger.error("发送 PUT 请求出现异常！", e);
        }

        return null;
    }

    /**
     * 发送put请求，携带cookie
     *
     * @param url
     * @param content
     * @return
     */
    public static CallBackInfo sendPut(String url, String content, String cookie) throws Exception {
        return doPut(url, client, content, cookie);
    }


    /**
     * post发http请求,可获取cookie
     *
     * @param url     地址
     * @param content 发送数据体
     * @return 返回消息体
     */
    public static CallBackInfo sendPostForCoookie(String url, String content) {
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        CallBackInfo callBackInfo = new CallBackInfo();
        String result = "";
        Request.Builder build = new Request.Builder();
        build.url(url);
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("accept", "*/*");
        headerMap.put("connection", "Keep-Alive");
        headerMap.put("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        buildHeader(build, headerMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), content.trim());//contentType暂时为空，有特殊需要再改
        logger.info("postforCookie请求内容：" + url + " : " + convertPassword(content));
        build.post(body);
        Request request = build.build();
        try {
            response = client.newCall(request).execute();
            result = response.body().string();
            logger.info("postforCookie返回内容：" + result);
            callBackInfo.setContent(result);
            callBackInfo.setCode(response.code());
            if (!StringUtils.isBlank(response.header("Set-Cookie"))) {
                String cookie = response.header("Set-Cookie").split(";")[0];
                callBackInfo.setToken(cookie);
                logger.info("获得cookie : " + cookie);
            }
        } catch (IOException e) {
            logger.error("发送 POST 请求出现异常！", e);
        }

        return callBackInfo;
    }

    /**
     * post发http请求，不携带cookie
     *
     * @param url     地址
     * @param content 发送数据体
     * @return 返回消息体
     */
    public static CallBackInfo sendPost(String url, String content) {

        try {
            return doPost(url, client, content, "application/json", null);
        } catch (Exception e) {
            logger.error("发送 POST 请求出现异常！", e);
        }

        return null;
    }

    /**
     * post发http请求，上传文件
     *
     * @param url
     * @param params
     * @param cookie
     * @return
     */
    public static CallBackInfo sendPostForFile(String url, Map<String, Object> params, String cookie) {
        try {
            return doPostForFile(url, client, params, cookie);
        } catch (Exception e) {
            logger.error("发送 POST 请求出现异常！", e);
        }

        return null;
    }

    /**
     * 上传文件实现方法
     *
     * @param url
     * @param client
     * @param params
     * @param cookie
     * @return
     * @throws Exception
     */
    private static CallBackInfo doPostForFile(String url, OkHttpClient client, Map<String, Object> params, String cookie) throws Exception {
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        CallBackInfo callBackInfo = new CallBackInfo();

        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        for (String key : params.keySet()) {
            if (params.get(key) instanceof byte[]) {
                builder.addFormDataPart(key, null, RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), (byte[])params.get(key)));
            } else {
                builder.addFormDataPart(key, params.get(key).toString());
            }
        }

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .addHeader("connection", "Keep-Alive")
                .addHeader("Cookie", cookie)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(url)
                .post(requestBody)
                .build();
        logger.info("post请求内容：" + url);

        try {
            response = client.newCall(request).execute();
            int code = response.code();
            logger.info("post请求返回code : " + code);
            //if (isSuccessful(response)) {
            String result = response.body().string();
            callBackInfo.setCode(code);
            callBackInfo.setContent(result);
            logger.info("post请求返回内容 : " + result);
            //}
        } catch (IOException e) {
            logger.error("发送 POST 请求出现异常！", e);
        }

        return callBackInfo;
    }

    /**
     * post发http请求，携带cookie
     *
     * @param url     地址
     * @param content 发送数据体
     * @return 返回消息体
     */
    public static CallBackInfo sendPost(String url, String content, String cookie) throws Exception {
        return doPost(url, client, content, "application/json", cookie);
    }

    /**
     * 发送form表单请求
     *
     * @param url
     * @param params
     * @return
     */
    public static CallBackInfo sendPost(String url, Map<String, String> params, String cookie) throws Exception {
        String content = "";
        for (String name : params.keySet()) {
            if ("".equals(content)) {
                content = name + "=" + params.get(name);
            } else {
                content = content + "&" + name + "=" + params.get(name);
            }
        }

        return doPost(url, client, content, "application/x-www-form-urlencoded", cookie);
    }

    /**
     * post请求操作
     *
     * @param url
     * @param client
     * @param content
     * @param contentType
     * @param cookie
     * @return
     * @throws Exception
     */
    private static CallBackInfo doPost(String url, OkHttpClient client, String content, String contentType, String cookie) throws Exception {
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        CallBackInfo callBackInfo = new CallBackInfo();
        Request.Builder build = new Request.Builder();
        build.url(url);
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("accept", "*/*");
        headerMap.put("connection", "Keep-Alive");
        headerMap.put("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        if (StringUtils.isNotBlank(contentType)) {
            headerMap.put("Content-Type", contentType);
        }
        if (StringUtils.isNotBlank(cookie)) {
            headerMap.put("Cookie", cookie);
        }
        buildHeader(build, headerMap);
        RequestBody body = RequestBody.create(MediaType.parse(contentType), content.trim());
        logger.info("post请求内容：" + url + ":" + convertPassword(content));
        build.post(body);
        Request request = build.build();
        try {
            response = client.newCall(request).execute();
            int code = response.code();
            logger.info("post请求返回code : " + code);
            //if (isSuccessful(response)) {
            String result = response.body().string();
            callBackInfo.setCode(code);
            callBackInfo.setContent(result);
            logger.info("post请求返回内容 : " + result);
            //}
        } catch (IOException e) {
            logger.error("发送 POST 请求出现异常！", e);
        }

        return callBackInfo;
    }

    /**
     * get请求操作
     *
     * @param url
     * @param client
     * @param content
     * @param cookie
     * @return
     */
    private static CallBackInfo doGet(String url, OkHttpClient client, String content, String cookie) throws Exception {
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        CallBackInfo callBackInfo = new CallBackInfo();
        Request.Builder build = new Request.Builder();
        String getUrl;
        if (url.endsWith("?")) {
            getUrl = url + content;
        } else {
            getUrl = url + "?" + content;
        }
        build.url(getUrl);
        logger.info("get请求内容：" + getUrl);
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("accept", "*/*");
        headerMap.put("connection", "Keep-Alive");
        headerMap.put("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        if (StringUtils.isNotBlank(cookie)) {
            headerMap.put("Cookie", cookie);
        }
        buildHeader(build, headerMap);
        Request request = build.build();
        try {
            response = client.newCall(request).execute();
            int code = response.code();
            logger.info("get请求返回code : " + code);
            String result = response.body().string();
            callBackInfo.setCode(code);
            callBackInfo.setContent(result);
            logger.info("get请求返回内容 : " + result);

        } catch (IOException e) {
            logger.error("发送 GET 请求出现异常！", e);
        }

        return callBackInfo;
    }

    /**
     * 发送put请求
     *
     * @param url
     * @param content
     * @return
     */
    private static CallBackInfo doPut(String url, OkHttpClient client, String content, String cookie) throws Exception {
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        CallBackInfo callBackInfo = new CallBackInfo();
        Request.Builder build = new Request.Builder();
        build.url(url);
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("accept", "*/*");
        headerMap.put("connection", "Keep-Alive");
        headerMap.put("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        headerMap.put("Content-Type", "application/json");
        if (StringUtils.isNotBlank(cookie)) {
            headerMap.put("Cookie", cookie);
        }

        buildHeader(build, headerMap);
        RequestBody body = RequestBody.create(null, content);//contentType暂时为空，有特殊需要再改
        build.put(body);
        logger.info("put请求内容：" + convertPassword(content));
        Request request = build.build();
        try {
            response = client.newCall(request).execute();
            int code = response.code();
            logger.info("put请求返回code : " + code);
            String result = response.body().string();
            callBackInfo.setContent(result);
            logger.info("put请求返回内容 : " + result);
            callBackInfo.setCode(code);

        } catch (IOException e) {
            logger.error("发送 PUT 请求出现异常！", e);
        }

        return callBackInfo;
    }

    /**
     * 组装发送请求头
     *
     * @param build
     * @param headerMap
     */
    private static void buildHeader(Request.Builder build, Map<String, String> headerMap) {
        if (headerMap != null && headerMap.size() > 0) {
            Iterator<Map.Entry<String, String>> iter = headerMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                build.header(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 判断返回信息是否成功
     *
     * @param response
     * @return
     */
    private static boolean isSuccessful(Response response) {
        if (response == null) {
            return false;
        }
        return response.isSuccessful();
    }

    /**
     * 隐藏包含用户密码的信息，不打印出明文日志
     *
     * @param content
     * @return
     */
    private static String convertPassword(String content) {
        String result = content;
        if (StringUtils.isNotBlank(result)) {
            if (result.toLowerCase().contains("password".toLowerCase())) {
                result = "******";
            }
        }
        return result;
    }
}
