package work.util;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
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
    private static final LaxRedirectStrategy REDIRECT_STRATEGY = new LaxRedirectStrategy();
    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = new PoolingHttpClientConnectionManager(
            60, TimeUnit.SECONDS);
    private static CloseableHttpClient CLIENT = HttpClientBuilder.create()
            .setConnectionTimeToLive(5000, TimeUnit.SECONDS).setRedirectStrategy(REDIRECT_STRATEGY)
            .setDefaultCookieStore(COOKIE).setUserAgent(BaseParameters.USER_AGENT)
            .setConnectionManager(CONNECTION_MANAGER).build();
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
}
