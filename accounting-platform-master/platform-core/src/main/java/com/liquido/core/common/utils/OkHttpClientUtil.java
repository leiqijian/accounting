package com.liquido.core.common.utils;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.liquido.core.common.exception.CommonException;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.logger.LogConstant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

/**
 * Encapsulates OKHttp3 HTTP request tool class
 */
@Slf4j
public class OkHttpClientUtil {

    private static final MediaType TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private static long connectTimeout = 60;
    private static long writeTimeout = 120;
    private static long readTimeout = 120;

    private static OkHttpClient okHttpClient;

    static {
        okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory(), x509TrustManager())
                .retryOnConnectionFailure(false)
                .connectionPool(connectionPool())
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(writeTimeout, TimeUnit.SECONDS)
                .writeTimeout(readTimeout, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    String traceId = MDC.get(LogConstant.TRACE_ID);
                    if (StringUtils.isNotBlank(traceId)) {
                        log.debug("OKHttpClient interceptor request set logger from MDC traceId={}",
                                traceId);
                    } else {
                        traceId = UUID.randomUUID().toString().replaceAll("-", "");
                        log.debug(
                                "OKHttpClient interceptor request set traceId from MDC is empty, "
                                        + "and create new traceId={}", traceId);
                    }

                    final Request original = chain.request();
                    final Request request = original.newBuilder()
                            .headers(original.headers())
                            .header("cache-control", "no-cache")
                            .header(LogConstant.TRACE_ID, traceId)
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }).build();
    }

    private static ConnectionPool connectionPool() {
        return new ConnectionPool(200, 5, TimeUnit.MINUTES);
    }

    /**
     * execute new call
     *
     * @param client
     * @param request
     * @return
     */
    public static String execute(final OkHttpClient client, final Request request) {
        try (Response response = (client == null ? okHttpClient : client).newCall(request)
                .execute()) {
            log.debug("http response url={} statusCode={}", request.url().url(), response.code());
            if (response.isSuccessful()) {
                return response.body().string();
            }

            throw new CommonException(response.code(),
                    "http request fail, errorCode:" + response.code());
        } catch (Exception e) {
            log.error("http request error: {}", e.getMessage(), e);
            throw new CommonException(CommonExceptionCode.SYSTEM_ERROR.getCode(),
                    "http execute error", e);
        }
    }

    public static String executeReturnBody(final OkHttpClient client, final Request request) {
        try (Response response = (client == null ? okHttpClient : client).newCall(request)
                .execute()) {
            log.debug("http response url={} statusCode={}", request.url().url(), response.code());
            return response.body().string();
        } catch (Exception e) {
            log.error("http request error: {}", e.getMessage(), e);
            throw new CommonException(CommonExceptionCode.SYSTEM_ERROR.getCode(),
                    "http execute error", e);
        }
    }

    /**
     * execute new call
     *
     * @param request
     * @return
     */
    public static String execute(final Request request) {
        return execute(null, request);
    }

    public static String executeReturnBody(final Request request) {
        return executeReturnBody(null, request);
    }

    /**
     * do get
     *
     * @param url
     * @param paramsMap
     * @param headers
     * @return
     */
    public static String doGet(final String url,
                               final Map<String, Object> paramsMap,
                               final Map<String, String> headers) {
        String params = "";
        if (MapUtils.isEmpty(paramsMap)) {
            params = buildParams(paramsMap);
        }

        return execute(new Request.Builder()
                .headers(Headers.of(MapUtils.isEmpty(headers) ? Maps.newHashMap() : headers))
                .url(url + params)
                .build());
    }

    /**
     * do get
     *
     * @param url
     * @param headers
     * @return
     */
    public static <T> T doGet(final String url,
                              final Map<String, Object> paramsMap,
                              final Map<String, String> headers,
                              final TypeReference<T> typeReference) {

        final String result = doGet(url, paramsMap, headers);
        return JsonUtil.toBean(result, typeReference);
    }

    /**
     * do get
     *
     * @param url
     * @param headers
     * @return
     */
    public static <T> T doGet(final String url,
                              final Map<String, String> headers,
                              final TypeReference<T> typeReference) {

        final String result = doGet(url, null, headers);
        return JsonUtil.toBean(result, typeReference);
    }

    /**
     * do get
     *
     * @param url
     * @param headers
     * @return
     */
    public static <T> T doGet(final String url,
                              final Map<String, Object> paramsMap,
                              final Map<String, String> headers,
                              final Class<T> clazz) {

        final String result = doGet(url, paramsMap, headers);
        return JsonUtil.toBean(result, clazz);
    }


    /**
     * do get
     *
     * @param url
     * @param headers
     * @return
     */
    public static <T> T doGet(final String url,
                              final Map<String, String> headers,
                              final Class<T> clazz) {

        final String result = doGet(url, null, headers);
        return JsonUtil.toBean(result, clazz);
    }

    /**
     * build the full request path: ?k1=v1&k2=v2&.....
     *
     * @param parameters
     * @return
     */
    private static String buildParams(final Map<String, Object> parameters) {
        if (MapUtils.isEmpty(parameters)) {
            return "";
        }

        final StringJoiner joiner = new StringJoiner("&", "?", "");
        for (final Map.Entry<String, Object> entry : parameters.entrySet()) {
            joiner.add(entry.getKey().trim() + "=" + entry.getValue());
        }

        return joiner.toString();
    }

    /**
     * POST Form
     *
     * @param url
     * @param formParams
     * @return
     */
    public static String postWithForm(final String url,
                                      final Map<String, String> formParams) {
        return postWithForm(url, formParams, null);
    }

    /**
     * POST Form
     *
     * @param url
     * @param formParams
     * @param headers
     * @return
     */
    public static String postWithForm(final String url,
                                      final Map<String, String> formParams,
                                      Map<String, String> headers) {
        final FormBody.Builder formBody = new FormBody.Builder(CHARSET);
        if (MapUtils.isNotEmpty(formParams)) {
            for (final Map.Entry<String, String> entry : formParams.entrySet()) {
                if (StringUtils.isNotBlank(entry.getKey())) {
                    formBody.add(entry.getKey().trim(), entry.getValue());
                }
            }
        }

        if (MapUtils.isEmpty(headers)) {
            headers = Maps.newHashMap();
        }

        return execute(new Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .post(formBody.build()).build());
    }

    /**
     * POST json
     *
     * @param url
     * @param jsonParams
     * @return
     */
    public static String postWithJson(final String url, final Object jsonParams) {
        return postWithJson(url, jsonParams, null, String.class);
    }

    /**
     * POST json
     *
     * @param url
     * @param jsonParams
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T postWithJson(final String url,
                                     final Object jsonParams,
                                     final Class<T> clazz) {
        return postWithJson(url, jsonParams, null, clazz);
    }

    /**
     * POST json
     *
     * @param url
     * @param params
     * @param headers
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T postWithJson(final String url,
                                     final Object params,
                                     final Map<String, String> headers,
                                     final Class<T> clazz) {

        final String jsonParams = JsonUtil.toJson(params);
        log.info("http request: url={} params={}, headers={}, clazz={}", url, jsonParams,
                JsonUtil.toJson(headers), clazz);

        final String dataStr = postWithJson(url, JsonUtil.toJson(params), headers);
        final T bean = JsonUtil.toBean(dataStr, clazz);
        log.info("http response: url={} params={}, headers={}, responseData={}, returnObj={}", url,
                jsonParams, JsonUtil.toJson(headers), dataStr, bean.getClass());

        return bean;
    }

    /**
     * POST json
     *
     * @param url
     * @param jsonParams
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T postWithJson(final String url,
                                     final Object jsonParams,
                                     final TypeReference<T> typeReference) {
        return postWithJson(url, jsonParams, null, typeReference);
    }

    public static <T> T postWithJsonReturnBody(final String url,
                                     final Object jsonParams,
                                     final TypeReference<T> typeReference) {
        return postWithJsonReturnBody(url, jsonParams, null, typeReference);
    }

    /**
     * POST json
     *
     * @param url
     * @param params
     * @param headers
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T postWithJson(final String url,
                                     final Object params,
                                     final Map<String, String> headers,
                                     final TypeReference<T> typeReference) {

        final String jsonParams = JsonUtil.toJson(params);
        log.info("http request: url={} params={}, headers={}, typeReference={}", url, jsonParams,
                JsonUtil.toJson(headers), typeReference);

        final String dataStr = postWithJson(url, JsonUtil.toJson(params), headers);
        final T bean = JsonUtil.toBean(dataStr, typeReference);
        log.info("http response: url={} params={}, headers={}, responseData={}, returnObj={}", url,
                jsonParams, JsonUtil.toJson(headers), dataStr, bean.getClass());

        return bean;
    }

    public static <T> T postWithJsonReturnBody(final String url,
                                     final Object params,
                                     final Map<String, String> headers,
                                     final TypeReference<T> typeReference) {

        final String jsonParams = JsonUtil.toJson(params);
        log.info("http request: url={} params={}, headers={}, typeReference={}", url, jsonParams,
                JsonUtil.toJson(headers), typeReference);

        final String dataStr = postWithJsonReturnBody(url, JsonUtil.toJson(params), headers);
        final T bean = JsonUtil.toBean(dataStr, typeReference);
        log.info("http response: url={} params={}, headers={}, responseData={}, returnObj={}", url,
                jsonParams, JsonUtil.toJson(headers), dataStr, bean.getClass());

        return bean;
    }

    /**
     * POST json
     *
     * @param url
     * @param jsonParams
     * @param headers
     * @return
     */
    private static String postWithJson(final String url,
                                       final String jsonParams,
                                       Map<String, String> headers) {

        final RequestBody requestBody = RequestBody.create(TYPE_JSON, jsonParams);
        if (MapUtils.isEmpty(headers)) {
            headers = Maps.newHashMap();
        }

        return execute(new Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .post(requestBody)
                .build());
    }

    private static String postWithJsonReturnBody(final String url,
                                       final String jsonParams,
                                       Map<String, String> headers) {

        final RequestBody requestBody = RequestBody.create(TYPE_JSON, jsonParams);
        if (MapUtils.isEmpty(headers)) {
            headers = Maps.newHashMap();
        }

        return executeReturnBody(new Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .post(requestBody)
                .build());
    }

    public static X509TrustManager x509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    public static SSLSocketFactory sslSocketFactory() {
        try {
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] {x509TrustManager()}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            log.error("OkHttpClientUtil sslSocketFactory init error:", e);
        } catch (KeyManagementException e) {
            log.error("OkHttpClientUtil sslSocketFactory init error:", e);
        }

        return null;
    }
}
