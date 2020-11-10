package work.util;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
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
    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = new PoolingHttpClientConnectionManager(
            60, TimeUnit.SECONDS);
    private static CloseableHttpClient CLIENT = HttpClientBuilder.create()
            .setConnectionTimeToLive(5000, TimeUnit.SECONDS).setDefaultCookieStore(COOKIE)
            .setUserAgent(BaseParameters.USER_AGENT).setConnectionManager(CONNECTION_MANAGER).build();
    private static volatile boolean hasLogin = false;
    private static ReentrantLock LOCK = new ReentrantLock();
    static {
        BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", RandomJsessionId());
        cookie.setDomain(".duty-free-japan.jp");
        cookie.setPath("/");
        COOKIE.addCookie(cookie);
        CONNECTION_MANAGER.setDefaultMaxPerRoute(50);
        CONNECTION_MANAGER.setMaxTotal(20);
    }

    private static String RandomJsessionId() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return uuid;
    }

    public String defaultRequest(List<NameValuePair> headers, HttpRequestBase httpType) {
        LOG.info("begin request...");
        String resHtml = "";
        if (!CollectionUtils.isEmpty(headers)) {
            for (NameValuePair np : headers) {
                httpType.addHeader(np.getName(), np.getValue());
            }
        }
        try {
            try (CloseableHttpResponse response = CLIENT.execute(httpType)) {
                StatusLine statusLine = response.getStatusLine();
                if (ObjectUtils.isNotEmpty(statusLine) && validationResponseCode(statusLine.getStatusCode())) {
                    LOG.info("response success");
                    resHtml = EntityUtils.toString(response.getEntity());
                }
            }
        } catch (Exception e) {

        }
        return resHtml;
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

    // validation cookie is exists
    public static boolean cookieValidation() {
        boolean flag = false;
        LOCK.lock();
        try {
            List<Cookie> cookies = COOKIE.getCookies();
            for (Cookie c : cookies) {
                if ("ASP.NET_SessionIdV2".equals(c.getName())) {
                    if (StringUtils.isNotBlank(c.getValue())) {
                        flag = true;
                        setHasLogin(true);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("set haslogin error , message:{}", e.getMessage());
        } finally {
            LOCK.unlock();
        }
        return flag;
    }

    public static boolean getHasLogin() {
        return hasLogin;
    }

    private static void setHasLogin(boolean hasLogin) {
        LOCK.lock();
        try {
            HttpClientUtil.hasLogin = hasLogin;
        } catch (Exception e) {
            LOG.error("set haslogin error , message:{}", e.getMessage());
        } finally {
            LOCK.unlock();
        }
    }
}
