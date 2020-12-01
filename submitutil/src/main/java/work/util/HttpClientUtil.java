package work.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.protocol.RedirectStrategy;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import work.constants.BaseParameters;
import work.model.HttpClientUtilModel;

@Component("HttpClientUtil")
public class HttpClientUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);

    // private static final HttpHost PROXY_HOST = new
    // HttpHost(BaseParameters.PROXT_IP, BaseParameters.PROXT_IP_PORT);

    private static final RedirectStrategy REDIRECT_STRATEGY = new DefaultRedirectStrategy();

    private static final SocketConfig SOCKET_CONFIG = SocketConfig.custom().setSoTimeout(Timeout.ofSeconds(5))
            .setSoKeepAlive(true).build();

    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = PoolingHttpClientConnectionManagerBuilder
            .create().setConnectionTimeToLive(Timeout.ofSeconds(10)).setDefaultSocketConfig(SOCKET_CONFIG)
            .setMaxConnPerRoute(20).setMaxConnTotal(50).setConnectionTimeToLive(TimeValue.ofSeconds(5)).build();

    // private static final RequestConfig REQUEST_CONFIG =
    // RequestConfig.custom().setResponseTimeout(Timeout.ofSeconds(10))
    // .setConnectTimeout(Timeout.ofSeconds(5)).setConnectionRequestTimeout(Timeout.ofSeconds(3))
    // .setProxy(PROXY_HOST).build();

    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setResponseTimeout(Timeout.ofSeconds(10))
            .setConnectTimeout(Timeout.ofSeconds(5)).setConnectionRequestTimeout(Timeout.ofSeconds(3)).build();

    private static CloseableHttpClient CLIENT = HttpClientBuilder.create().setRedirectStrategy(REDIRECT_STRATEGY)
            .setUserAgent(BaseParameters.USER_AGENT).setConnectionManager(CONNECTION_MANAGER)
            .setDefaultRequestConfig(REQUEST_CONFIG)
            .setRetryStrategy(new DefaultHttpRequestRetryStrategy(3, TimeValue.ofSeconds(2))).build();

    // private static ReentrantLock LOCK = new ReentrantLock();

    /**
     * 
     * @param headers     request headers
     * @param httpType    request type
     * @param httpContext request context
     * @param useHtml     whether need html
     * @param useCookie   whether need extract set-cookie from the response header
     * @param cookieNames the name list, if need extract set-cookie from the
     *                    response header
     * @return HttpClientUtilModel
     */
    public static HttpClientUtilModel defaultRequest(List<NameValuePair> headers, HttpUriRequest httpType,
            HttpContext httpContext, boolean useHtml, boolean useCookie, List<String> cookieNames) {
        LOG.info("begin request...");
        Assert.notNull(httpType, "httpType cannot empty!");
        Assert.notNull(httpContext, "httpContext cannot empty!");

        String resHtml = "";

        HttpClientUtilModel clientUtilModel = null;

        List<NameValuePair> cookies = new ArrayList<>();

        if (!CollectionUtils.isEmpty(headers)) {
            for (NameValuePair np : headers) {
                httpType.addHeader(np.getName(), np.getValue());
            }
        }
        try {
            printRequestHeader(httpType);
            // long beginTime = System.nanoTime();
            try (CloseableHttpResponse response = CLIENT.execute(httpType, httpContext)) {
                if (validationResponseCode(response.getCode())) {
                    // long endTime = System.nanoTime();
                    LOG.info("response success");
                    printResponseHeader(response);
                    resHtml = useHtml ? EntityUtils.toString(response.getEntity()) : "";
                    if (useCookie && !CollectionUtils.isEmpty(cookieNames)) {
                        for (String cookieName : cookieNames) {
                            assembleCookie(response, cookieName, cookies);
                        }
                    } else {
                        LOG.info("no need for cookie or cookieNames list is empty");
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("request error ! , uri :{} message :{}", httpType.getRequestUri(), e.getMessage());
        }

        clientUtilModel = new HttpClientUtilModel(cookies, resHtml, httpType.getRequestUri());
        return clientUtilModel;
    }

    private static void printResponseHeader(CloseableHttpResponse response) {
        Header[] headers = response.getHeaders();
        LOG.debug("=======response header===========");
        for (Header h : headers) {
            LOG.debug(" header :{} ,value :{}", h.getName(), h.getValue());
        }
    }

    /**
     * Assemble cookies
     * 
     * @param response
     * @param cookieName
     * @param cookies
     */
    private static void assembleCookie(CloseableHttpResponse response, String cookieName, List<NameValuePair> cookies) {
        if (!response.containsHeader(cookieName)) {
            return;
        }
        Header[] headers = response.getHeaders(cookieName);
        for (Header h : headers) {
            cookies.add(new BasicNameValuePair(h.getName(), h.getValue()));
        }
    }

    private static void printRequestHeader(HttpUriRequest httpType) {
        LOG.debug("=======request header========");
        Header[] hds = httpType.getHeaders();
        for (Header h : hds) {
            LOG.debug(" header :{} ,value :{}", h.getName(), h.getValue());
        }
    }

    private static boolean validationResponseCode(int statusCode) {
        String codeStr = String.valueOf(statusCode);
        if (codeStr.matches("^2[0-9]{2,2}$") || codeStr.matches("^3[0-9]{2,2}$")) {
            return true;
        }
        return false;
    }

    /**
     * splicing all cookie string by the httpContext
     * 
     * @return
     */
    public static String getFullUserSessionVal(HttpClientContext context) {
        List<Cookie> cookies = getCookieStoreByContext(context).getCookies();
        StringBuilder cookieValue = new StringBuilder();
        for (Cookie c : cookies) {
            if (StringUtils.isNotBlank(c.getValue())) {
                cookieValue.append(c.getName() + "=" + c.getValue() + ";");
            }
        }
        return cookieValue.toString();
    }

    /**
     * watch full cookie by the httpContext
     */
    public static void watchCookieState(HttpClientContext context) {
        LOG.debug("======watch cookie======");
        CookieStore cs = getCookieStoreByContext(context);
        for (Cookie c : cs.getCookies()) {
            LOG.debug(" cookie : {},value:{}", c.getName(), c.getValue());
        }
    }

    /**
     * add cookie into current http context cookieStore
     */
    public static void addParamsIntoCookie(HttpClientContext context, BasicClientCookie cookie) {
        CookieStore cookieStore = context.getCookieStore();
        cookieStore.addCookie(cookie);
    }

    /**
     * return CookieStore by the httpContext
     * 
     * @return
     */
    public static CookieStore getCookieStoreByContext(HttpClientContext context) {
        return context.getCookieStore();
    }

    /**
     * create default header with cookie: , origin: and refer: with specifial value
     * 
     * @param referer
     * @param cookieStr
     * @return
     */
    public static List<NameValuePair> createRequestHeader(String referer, HttpClientContext context) {
        List<NameValuePair> requestHeaderList = new ArrayList<>();

        Collections.addAll(requestHeaderList, new BasicNameValuePair("origin", BaseParameters.ORIGIN),
                new BasicNameValuePair("referer", referer),
                new BasicNameValuePair("cookie", getFullUserSessionVal(context)));
        return requestHeaderList;
    }

}
