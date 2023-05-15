package org.lpw.photon.util;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("photon.util.http")
public class HttpImpl implements Http {
    @Inject
    private Context context;
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Codec codec;
    @Inject
    private Numeric numeric;
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Value("${photon.http.pool.max:256}")
    private int max;
    @Value("${photon.http.connect.time-out:5000}")
    private int connectTimeout;
    @Value("${photon.http.read.time-out:20000}")
    private int readTimeout;
    private final ThreadLocal<Integer> statusCode = new ThreadLocal<>();

    @Override
    public String get(String url, Map<String, String> requestHeaders, Map<String, String> parameters) {
        return get(url, requestHeaders, parameters, null);
    }

    @Override
    public String get(String url, Map<String, String> requestHeaders, Map<String, String> parameters, String charset) {
        return get(url, requestHeaders, toStringParameters(parameters, charset));
    }

    private String toStringParameters(Map<String, String> parameters, String charset) {
        if (validator.isEmpty(parameters))
            return "";

        StringBuilder sb = new StringBuilder();
        parameters.forEach(
                (name, value) -> sb.append('&').append(name).append('=').append(codec.encodeUrl(value, charset)));

        return sb.substring(1);
    }

    @Override
    public String get(String url, Map<String, String> requestHeaders, String parameters) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        get(url, requestHeaders, parameters, null, outputStream);
        String content = outputStream.toString();

        if (logger.isDebugEnable())
            logger.debug("使用GET访问[{}]结果[{}]。", url, content);

        return content;
    }

    @Override
    public void get(String url, Map<String, String> requestHeaders, String parameters,
                    Map<String, String> responseHeaders, OutputStream outputStream) {
        if (validator.isEmpty(url))
            return;

        if (!validator.isEmpty(parameters))
            url = url + (url.indexOf('?') == -1 ? '?' : '&') + parameters;

        if (logger.isDebugEnable())
            logger.debug("使用GET访问[{}]。", url);

        HttpGet get = new HttpGet(url);
        get.setConfig(getRequestConfig());
        execute(get, requestHeaders, responseHeaders, outputStream);
    }

    @Override
    public String post(String url, Map<String, String> requestHeaders, Map<String, String> parameters) {
        return post(url, requestHeaders, parameters, null);
    }

    @Override
    public String post(String url, Map<String, String> requestHeaders, Map<String, String> parameters, String charset) {
        return post(url, requestHeaders, toEntity(parameters, charset));
    }

    @Override
    public void post(String url, Map<String, String> requestHeaders, Map<String, String> parameters, String charset,
                     Map<String, String> responseHeaders, OutputStream outputStream) {
        postByEntity(url, requestHeaders, toEntity(parameters, charset), responseHeaders, outputStream);
    }

    @Override
    public String post(String url, Map<String, String> requestHeaders, String content) {
        return post(url, requestHeaders, content, null);
    }

    @Override
    public String post(String url, Map<String, String> requestHeaders, String content, String charset) {
        return post(url, requestHeaders, toEntity(content, charset));
    }

    private String post(String url, Map<String, String> requestHeaders, HttpEntity entity) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        postByEntity(url, requestHeaders, entity, null, outputStream);
        String content = outputStream.toString();
        if (logger.isDebugEnable())
            logger.debug("使用POST访问[{}]结果[{}]。", url, content);

        return content;
    }

    @Override
    public void post(String url, Map<String, String> requestHeaders, String content, String charset,
                     Map<String, String> responseHeaders, OutputStream outputStream) {
        postByEntity(url, requestHeaders, toEntity(content, charset), responseHeaders, outputStream);
    }

    @Override
    public String post(String url, Map<String, String> requestHeaders, InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        postByEntity(url, requestHeaders, toEntity(inputStream), null, outputStream);
        String content = outputStream.toString();
        if (logger.isDebugEnable())
            logger.debug("使用POST访问[{}]结果[{}]。", url, content);

        return content;
    }

    @Override
    public void post(String url, Map<String, String> requestHeaders, InputStream inputStream,
                     Map<String, String> responseHeaders, OutputStream outputStream) {
        postByEntity(url, requestHeaders, toEntity(inputStream), responseHeaders, outputStream);
    }

    private HttpEntity toEntity(InputStream inputStream) {
        return inputStream == null ? null : new InputStreamEntity(inputStream);
    }

    @Override
    public String upload(String url, Map<String, String> requestHeaders, Map<String, String> parameters,
                         Map<String, File> files) {
        return upload(url, requestHeaders, parameters, files, null);
    }

    @Override
    public String upload(String url, Map<String, String> requestHeaders, Map<String, String> parameters,
                         Map<String, File> files, String charset) {
        if (validator.isEmpty(files))
            return post(url, requestHeaders, parameters, charset);

        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        ContentType contentType = ContentType.create("text/plain", context.getCharset(charset));
        if (!validator.isEmpty(parameters))
            parameters.forEach((key, value) -> entity.addTextBody(key, value, contentType));
        files.forEach(entity::addBinaryBody);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        postByEntity(url, requestHeaders, entity.build(), null, outputStream);

        return outputStream.toString();
    }

    private HttpEntity toEntity(String parameters, String charset) {
        if (validator.isEmpty(parameters))
            return null;

        return new StringEntity(parameters, context.getCharset(charset));
    }

    private HttpEntity toEntity(Map<String, String> map, String charset) {
        if (validator.isEmpty(map))
            return null;

        List<NameValuePair> nvps = new ArrayList<>();
        map.forEach((key, value) -> nvps.add(new BasicNameValuePair(key, value)));

        try {
            return new UrlEncodedFormEntity(nvps, context.getCharset(charset));
        } catch (UnsupportedEncodingException e) {
            logger.warn(e, "转化参数[{}:{}]时发生异常！", converter.toString(map), charset);

            return null;
        }
    }

    private void postByEntity(String url, Map<String, String> requestHeaders, HttpEntity entity,
                              Map<String, String> responseHeaders, OutputStream outputStream) {
        if (validator.isEmpty(url))
            return;

        if (logger.isDebugEnable())
            logger.debug("使用POST访问{}[{}]。", url, entity);

        HttpPost post = new HttpPost(url);
        post.setConfig(getRequestConfig());
        if (entity != null)
            post.setEntity(entity);
        execute(post, requestHeaders, responseHeaders, outputStream);
    }

    private RequestConfig getRequestConfig() {
        return RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout).build();
    }

    private void execute(HttpUriRequest request, Map<String, String> requestHeaders,
                         Map<String, String> responseHeaders, OutputStream outputStream) {
        if (!validator.isEmpty(requestHeaders))
            requestHeaders.keySet().stream().filter(key -> !key.equalsIgnoreCase("content-length"))
                    .forEach(key -> request.addHeader(key, requestHeaders.get(key)));
        try (CloseableHttpResponse response = HttpClients.custom().setConnectionManager(null)
                .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build().execute(request, HttpClientContext.create())) {
            int statusCode = response.getStatusLine().getStatusCode();
            this.statusCode.set(statusCode);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity == null) {
                request.abort();
                outputStream.close();
                logger.warn(null, "执行HTTP请求[{}:{}:{}]未返回数据！", request.getMethod(), request.getURI(), statusCode);

                return;
            }

            if (responseHeaders != null)
                for (Header header : response.getAllHeaders())
                    responseHeaders.put(header.getName(), header.getValue());
            copy(request, httpEntity, outputStream);
            logger.info("执行HTTP请求[{}:{}:{}]！", request.getMethod(), request.getURI(), statusCode);
        } catch (Throwable throwable) {
            request.abort();
            logger.warn(throwable, "执行HTTP请求时发生异常！");
        }
    }

    private void copy(HttpUriRequest request, HttpEntity httpEntity, OutputStream outputStream) {
        try (InputStream inputStream = httpEntity.getContent()) {
            io.copy(inputStream, outputStream);
            outputStream.close();
        } catch (IOException e) {
            request.abort();
            logger.warn(null, "输出HTTP执行结果时发生异常[{}]！", e.getMessage());
        }
    }

    @Override
    public int getStatusCode() {
        return numeric.toInt(statusCode.get());
    }
}
