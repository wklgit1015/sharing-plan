package com.sohu.mp.sharingplan.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class HttpClientUtil {

    private HttpClientUtil() {
    }

    private static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final int CON_TIME_OUT = 500;
    private static final int READ_TIME_OUT = 10000;
    private static final String CHARSET = "UTF-8";

    private static CloseableHttpClient getClient() {
        return HttpClients.createDefault();
    }

    public static int postWithFile(String url, Map<String, String> params, String name, File... files) {
        CloseableHttpClient httpclient = getClient();
        try {
            HttpPost httppost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(READ_TIME_OUT).setConnectTimeout(CON_TIME_OUT).build();
            httppost.setConfig(requestConfig);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE).setContentType(ContentType.MULTIPART_FORM_DATA.withCharset(CHARSET));
            for (File file : files) {
                //name 为请求的文件流的参数名称
                builder.addPart(name, new FileBody(file));
            }
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addTextBody(entry.getKey(), entry.getValue(), ContentType.MULTIPART_FORM_DATA.withCharset(CHARSET));
            }
            HttpEntity entity = builder.build();
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_OK) {
                log.info("[send file email success]: {}", getResult(response));
            } else {
                log.error("[send file email error]: {}", getResult(response));
            }
            return code;
        } catch (Exception e) {
            log.error("[post with file error]: error={}", e.getMessage());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("close client" + url + " error.", e.getMessage());
            }
        }
        return -1;
    }

    private static String getResult(HttpResponse httpResponse) {
        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            try {
                return EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                //ignore
            }
        }
        return null;
    }

}
