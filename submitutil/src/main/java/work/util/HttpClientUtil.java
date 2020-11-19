package work.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.RedirectStrategy;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
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

    private static final RedirectStrategy REDIRECT_STRATEGY = new DefaultRedirectStrategy();

    private static final SocketConfig SOCKET_CONFIG = SocketConfig.custom().setSoTimeout(Timeout.ofSeconds(5))
            .setSoKeepAlive(true).build();

    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = PoolingHttpClientConnectionManagerBuilder
            .create().setConnectionTimeToLive(Timeout.ofSeconds(10)).setDefaultSocketConfig(SOCKET_CONFIG)
            .setMaxConnPerRoute(20).setMaxConnTotal(50).setConnectionTimeToLive(TimeValue.ofSeconds(5)).build();

    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setConnectTimeout(Timeout.ofSeconds(3))
            .setConnectionRequestTimeout(Timeout.ofSeconds(3)).setProxy(PROXY_HOST).build();
    // private static final RequestConfig REQUEST_REQUEST_CONFIG =
    // RequestConfig.custom()
    // .setConnectTimeout(Timeout.ofSeconds(3)).setConnectionRequestTimeout(Timeout.ofSeconds(3)).build();

    private static CloseableHttpClient CLIENT = HttpClientBuilder.create().setRedirectStrategy(REDIRECT_STRATEGY)
            .setDefaultCookieStore(COOKIE).setUserAgent(BaseParameters.USER_AGENT)
            .setConnectionManager(CONNECTION_MANAGER).setDefaultRequestConfig(REQUEST_CONFIG)
            .setRetryStrategy(new DefaultHttpRequestRetryStrategy(3, TimeValue.ofSeconds(2))).build();

    // private static ReentrantLock LOCK = new ReentrantLock();

    public static String defaultRequest(List<NameValuePair> headers, HttpUriRequest httpType, boolean useHtml) {
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

                if (validationResponseCode(response.getCode())) {
                    LOG.info("response success");
                    resHtml = useHtml ? EntityUtils.toString(response.getEntity()) : "";
                }
            }
        } catch (Exception e) {
            LOG.error(" request error ! , uri :{} message :{} ", httpType.getRequestUri(), e.getMessage());
        }
        return resHtml;
    }

    private static void printRequestHeader(HttpUriRequest httpType) {
        LOG.info("=======request header========");
        Header[] hds = httpType.getHeaders();
        for (Header h : hds) {
            LOG.info(" header :{} ,value :{}", h.getName(), h.getValue());
        }
    }

    private static boolean validationResponseCode(int statusCode) {
        String codeStr = String.valueOf(statusCode);
        if (codeStr.matches("^2[0-9]{2,2}$") || codeStr.matches("^3[0-9]{2,2}$")) {
            return true;
        }
        return false;
    }

    // GET cookie when log in
    public static CookieStore getCookie() {
        return COOKIE;
    }

    /**
     * splicing all cookie string
     * 
     * @return
     */
    public static String getFullUserSessionVal() {
        List<Cookie> cookies = COOKIE.getCookies();
        StringBuilder cookieValue = new StringBuilder();
        for (Cookie c : cookies) {
            if (StringUtils.isNotBlank(c.getValue())) {
                cookieValue.append(c.getName() + "=" + c.getValue() + ";");
            }
        }
        return cookieValue.toString();
    }

    /**
     * watch full cookie
     */
    public static void watchCookieState() {
        LOG.info("======watch cookie======");
        CookieStore cs = getCookie();
        for (Cookie c : cs.getCookies()) {
            LOG.info(" cookie : {},value:{}", c.getName(), c.getValue());
        }
    }

    /**
     * get default CookieStore object
     * 
     * @return
     */
    public static BasicCookieStore getDefaultCookieStore() {
        return COOKIE;
    }

    /**
     * get default RequestConfig object
     * @return
     */
    public static RequestConfig getDefauConfig() {
        return REQUEST_CONFIG;
    }

    /**
     * create default header with cookie: , origin: and refer: with specifial value
     * 
     * @param referer
     * @return
     */
    public static List<NameValuePair> createDefaultRequestHeader(String referer) {
        List<NameValuePair> requestHeaderList = new ArrayList<>();
        Collections.addAll(requestHeaderList, new BasicNameValuePair("origin", BaseParameters.ORIGIN),
                new BasicNameValuePair("referer", referer), new BasicNameValuePair("cookie", getFullUserSessionVal()));
        return requestHeaderList;
    }
}
