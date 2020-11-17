package work.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import work.constants.BaseParameters;

@Component("HttpClientUtil")
public class HttpClientUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final BasicCookieStore COOKIE = new BasicCookieStore();

    private static final HttpHost PROXY_HOST = new HttpHost("149.28.21.114", 55367);

    private static final LaxRedirectStrategy REDIRECT_STRATEGY = new LaxRedirectStrategy();

    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = new PoolingHttpClientConnectionManager(
            60, TimeUnit.SECONDS);

    private static final RequestConfig REQUEST_REQUEST_CONFIG = RequestConfig.custom().setConnectTimeout(3000)
            .setSocketTimeout(5000).setConnectionRequestTimeout(5000).setProxy(PROXY_HOST).build();

    private static final ConnectionConfig CONNECTION_CONFIG = ConnectionConfig.custom().build();

    private static final SocketConfig SOCKET_CONFIG_CONFIG = SocketConfig.custom().setSoTimeout(5000).build();

    private static final int REY_TRY_COUNTS = 3;
    private static CloseableHttpClient CLIENT = HttpClientBuilder.create()
            .setConnectionTimeToLive(5000, TimeUnit.SECONDS).setRedirectStrategy(REDIRECT_STRATEGY)
            .setDefaultCookieStore(COOKIE).setUserAgent(BaseParameters.USER_AGENT)
            .setConnectionManager(CONNECTION_MANAGER).setDefaultConnectionConfig(CONNECTION_CONFIG)
            .setDefaultRequestConfig(REQUEST_REQUEST_CONFIG).setDefaultSocketConfig(SOCKET_CONFIG_CONFIG)
            .setRetryHandler(new HttpRequestRetryHandler() {
                public boolean retryRequest(java.io.IOException exception, int executionCount,
                        org.apache.http.protocol.HttpContext context) {
                    return executionCount <= REY_TRY_COUNTS;
                };
            }).setServiceUnavailableRetryStrategy(new ServiceUnavailableRetryStrategy() {
                int waitPeriod = 100;

                @Override
                public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
                    waitPeriod *= 2;
                    return executionCount <= REY_TRY_COUNTS && response.getStatusLine().getStatusCode() >= 500; // important!
                }

                @Override
                public long getRetryInterval() {
                    return waitPeriod;
                }
            }).build();

    private static ReentrantLock LOCK = new ReentrantLock();

    static {
        CONNECTION_MANAGER.setDefaultMaxPerRoute(50);
        CONNECTION_MANAGER.setMaxTotal(20);
    }

    public String defaultRequest(List<NameValuePair> headers, HttpRequestBase httpType, boolean useHtml) {
        LOG.info("begin request...");
        String resHtml = "";
        if (!CollectionUtils.isEmpty(headers)) {
            for (NameValuePair np : headers) {
                httpType.addHeader(np.getName(), np.getValue());
            }
        }
        try {
            printRequestHeader(httpType);
            try (CloseableHttpResponse response = CLIENT.execute(httpType)) {
                StatusLine statusLine = response.getStatusLine();

                if (ObjectUtils.isNotEmpty(statusLine) && validationResponseCode(statusLine.getStatusCode())) {
                    LOG.info("response success");
                    resHtml = useHtml ? EntityUtils.toString(response.getEntity()) : "";
                }
            }
        } catch (Exception e) {
            LOG.error(" request error ! , uri :{} message :{} ", httpType.getURI(), e.getMessage());
        }
        return resHtml;
    }

    private void printRequestHeader(HttpRequestBase httpType) {
        LOG.info("=======request header========");
        Header[] hds = httpType.getAllHeaders();
        for (Header h : hds) {
            LOG.info(" header :{} ,value :{}", h.getName(), h.getValue());
        }
    }

    private boolean validationResponseCode(int statusCode) {
        String codeStr = String.valueOf(statusCode);
        if (codeStr.matches("^2[0-9]{2,2}$") || codeStr.matches("^3[0-9]{2,2}$")) {
            return true;
        }
        return false;
    }

    // GET cookie when log in
    public CookieStore getCookie() {
        return COOKIE;
    }

    public String getFullUserSessionVal() {
        List<Cookie> cookies = COOKIE.getCookies();
        StringBuilder cookieValue = new StringBuilder();
        for (Cookie c : cookies) {
            if (StringUtils.isNotBlank(c.getValue())) {
                cookieValue.append(c.getName() + "=" + c.getValue() + ";");
            }
        }
        return cookieValue.toString();
    }

    public void watchCookieState() {
        LOG.info("======watch cookie======");
        CookieStore cs = getCookie();
        for (Cookie c : cs.getCookies()) {
            LOG.info(" cookie : {},value:{}", c.getName(), c.getValue());
        }
    }

    /**
     * create default header with cookie: , origin: and refer: with specifial value
     * 
     * @param referer
     * @return
     */
    public List<NameValuePair> createDefaultRequestHeader(String referer) {
        List<NameValuePair> requestHeaderList = new ArrayList<>();
        Collections.addAll(requestHeaderList, new BasicNameValuePair("origin", BaseParameters.ORIGIN),
                new BasicNameValuePair("referer", referer),
                new BasicNameValuePair("cookie", this.getFullUserSessionVal()));
        return requestHeaderList;
    }
}
