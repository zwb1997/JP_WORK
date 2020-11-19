package work.service.orderservice.autoplaceorder;

import static work.util.HttpClientUtil.*;
import static work.util.PageUtil.fetchElementValueAttrWithId;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieSpec;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import work.constants.BaseParameters;
import work.model.RequireInfo;

public class AutoLoginService {

    private static final Logger LOG = LoggerFactory.getLogger(AutoLoginService.class);

    private static final ReentrantLock LOCK = new ReentrantLock();

    private static final List<String> LOGIN_ACTION_PARAMS_LIST = new ArrayList<>();

    private RequireInfo requireInfo;

    private RequestConfig singleRequestConfig;

    static {
        Collections.addAll(LOGIN_ACTION_PARAMS_LIST, "__VIEWSTATE", "__VIEWSTATEGENERATOR", "__EVENTARGUMENT",
                "__LASTFOCUS", "__EVENTVALIDATION", "__EVENTTARGET", "ctl00$inputSpSearchFront", "ctl00$ddlLanguageSP",
                "ctl00$inputSpSearch", "ctl00$ddlLanguagePC", "ctl00$inputPcSearch", "ctl00$cphMain$TxtMail",
                "ctl00$cphMain$TxtPASS", "ctl00$ddlLanguageFooterPC");
    }

    public AutoLoginService(RequireInfo requireInfo, RequestConfig singleRequestConfig) {
        this.requireInfo = requireInfo;
        this.singleRequestConfig = singleRequestConfig;
    }

    // 做个post提交 获取cookie
    public void loginService() throws Exception {
        LOCK.lock();
        try {
            LOG.info("begin login service");
            // part1 get visitorid and ASP.NET_SessionIdV2
            getCookieParams();
            // part2 login
            login();
        } catch (Exception e) {
            throw new Exception("login error", e);
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 
     * 1.request to https://duty-free-japan.jp/narita/ch/index.aspx and get
     * ASP.NET_SessionIdV2
     * 
     * 
     * 2.request to under link and get visitorId
     * https://duty-free-japan.jp/image.jsp?id=13928
     * https://duty-free-japan.jp/image.jsp?id=12293
     * https://duty-free-japan.jp/image.jsp?id=2392
     * https://duty-free-japan.jp/image.jsp?id=2393
     * https://duty-free-japan.jp/image.jsp?id=2396
     * 
     * @throws Exception
     */
    private void getCookieParams() throws Exception {

        String indexUrl = "https://duty-free-japan.jp/narita/ch/index.aspx";
        HttpGet indexGet = new HttpGet(indexUrl);
        indexGet.setConfig(singleRequestConfig);
        defaultRequest(null, indexGet, false);
        String asp_net_session_id = signSessionValue();
        watchCookieState();
        if (StringUtils.isBlank(asp_net_session_id)) {
            throw new Exception("ASP.NET_SessionIdV2 not found.");
        }

        String img_13928 = "https://duty-free-japan.jp/image.jsp?id=13928";
        List<NameValuePair> img_13928_headers = new ArrayList<>();
        img_13928_headers
                .add(new BasicNameValuePair("cookie", BaseParameters.SESSION_PARAMS_NAME + "=" + asp_net_session_id));
        img_13928_headers.add(new BasicNameValuePair("referer", "https://duty-free-japan.jp/narita/ch/index.aspx"));
        HttpGet img_13928_get = new HttpGet(img_13928);
        img_13928_get.setConfig(singleRequestConfig);
        defaultRequest(img_13928_headers, img_13928_get, false);
        watchCookieState();
    }

    // find ASP.NET_SessionIdV2
    private String signSessionValue() {
        CookieStore cs = getCookie();
        String sessionId = "";
        for (Cookie c : cs.getCookies()) {
            if ("ASP.NET_SessionIdV2".equals(c.getName())) {
                sessionId = c.getValue();
                break;
            }
        }
        return sessionId;
    }

    private void login() {
        List<NameValuePair> loginHeaders = createDefaultRequestHeader(BaseParameters.LOGIN_URL);
        List<NameValuePair> formParams = new ArrayList<>();
        Map<String, String> loginFormParamsMap = createLoginFormMap();
        for (String s : LOGIN_ACTION_PARAMS_LIST) {
            formParams.add(new BasicNameValuePair(s, loginFormParamsMap.get(s)));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Charset.forName("UTF-8"));
        HttpPost loginPost = new HttpPost(BaseParameters.LOGIN_URL);
        loginPost.setConfig(singleRequestConfig);
        loginPost.setEntity(entity);
        defaultRequest(loginHeaders, loginPost, false);
        LOG.warn("login end and here the ASP.NET_SessionIdV2 and visitorid should be available");
    }

    private Map<String, String> createLoginFormMap() {
        Map<String, String> paramsMap = new LinkedHashMap<>();
        List<NameValuePair> headers = createDefaultRequestHeader(BaseParameters.LOGIN_URL);
        HttpGet get = new HttpGet(BaseParameters.LOGIN_URL);
        get.setConfig(singleRequestConfig);
        String html = defaultRequest(headers, get, true);
        Document doc = Jsoup.parse(html);
        int pos = 0;
        int size = LOGIN_ACTION_PARAMS_LIST.size();
        while (pos < size && pos < 6) {
            String parasName = LOGIN_ACTION_PARAMS_LIST.get(pos);
            paramsMap.put(parasName, fetchElementValueAttrWithId(doc, parasName));
            pos++;
        }
        while (pos < size) {
            paramsMap.put(LOGIN_ACTION_PARAMS_LIST.get(pos++), "");
        }
        paramsMap.put("__EVENTTARGET", "ctl00$cphMain$LBtnLogin");
        paramsMap.put("ctl00$cphMain$TxtMail", requireInfo.getEmail());
        paramsMap.put("ctl00$cphMain$TxtPASS", requireInfo.getPassword());
        paramsMap.put("ctl00$cphMain$autoLogin", "on");
        return paramsMap;
    }

    public RequestConfig getLoginRequestConfig() {
        return this.singleRequestConfig;
    }
}
