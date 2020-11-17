package work.service.orderservice;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import work.constants.BaseParameters;
import work.util.HttpClientUtil;
import work.util.PageUtil;

@Service("AutoLoginService")
public class AutoLoginService {
    private static final Logger LOG = LoggerFactory.getLogger(AutoLoginService.class);
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final List<String> LOGIN_ACTION_PARAMS_LIST = new ArrayList<>();
    @Autowired
    @Qualifier("HttpClientUtil")
    private HttpClientUtil clientUtil;

    @Autowired
    @Qualifier("PageUtil")
    private PageUtil pageUtil;

    static {
        Collections.addAll(LOGIN_ACTION_PARAMS_LIST, "__VIEWSTATE", "__VIEWSTATEGENERATOR", "__EVENTARGUMENT",
                "__LASTFOCUS", "__EVENTVALIDATION", "__EVENTTARGET", "ctl00$inputSpSearchFront", "ctl00$ddlLanguageSP",
                "ctl00$inputSpSearch", "ctl00$ddlLanguagePC", "ctl00$inputPcSearch", "ctl00$cphMain$TxtMail",
                "ctl00$cphMain$TxtPASS", "ctl00$ddlLanguageFooterPC");
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

    private void getCookieParams() throws Exception {
        // 1.request to https://duty-free-japan.jp/narita/ch/index.aspx and get
        // ASP.NET_SessionIdV2
        String indexUrl = "https://duty-free-japan.jp/narita/ch/index.aspx";
        HttpGet indexGet = new HttpGet(indexUrl);
        clientUtil.defaultRequest(null, indexGet, false);
        String asp_net_session_id = signSessionValue();
        clientUtil.watchCookieState();
        if (StringUtils.isBlank(asp_net_session_id)) {
            throw new Exception("ASP.NET_SessionIdV2 not found.");
        }
        // 2.request to under link and get visitorId
        // https://duty-free-japan.jp/image.jsp?id=13928
        // https://duty-free-japan.jp/image.jsp?id=12293
        // https://duty-free-japan.jp/image.jsp?id=2392
        // https://duty-free-japan.jp/image.jsp?id=2393
        // https://duty-free-japan.jp/image.jsp?id=2396
        String img_13928 = "https://duty-free-japan.jp/image.jsp?id=13928";
        List<NameValuePair> img_13928_headers = new ArrayList<>();
        img_13928_headers
                .add(new BasicNameValuePair("cookie", BaseParameters.SESSION_PARAMS_NAME + "=" + asp_net_session_id));
        img_13928_headers.add(new BasicNameValuePair("referer", "https://duty-free-japan.jp/narita/ch/index.aspx"));
        HttpGet img_13928_get = new HttpGet(img_13928);
        clientUtil.defaultRequest(img_13928_headers, img_13928_get, false);
        clientUtil.watchCookieState();
    }

    // find ASP.NET_SessionIdV2
    private String signSessionValue() {
        CookieStore cs = clientUtil.getCookie();
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
        List<NameValuePair> loginHeaders = new ArrayList<>();
        loginHeaders.add(new BasicNameValuePair("origin", "https://duty-free-japan.jp"));
        loginHeaders.add(new BasicNameValuePair("referer", "https://duty-free-japan.jp/narita/ch/memberLogin.aspx"));
        loginHeaders.add(new BasicNameValuePair("cookie", clientUtil.getFullUserSessionVal()));
        List<NameValuePair> formParams = new ArrayList<>();
        Map<String, String> loginFormParamsMap = createLoginFormMap();
        for (String s : LOGIN_ACTION_PARAMS_LIST) {
            formParams.add(new BasicNameValuePair(s, loginFormParamsMap.get(s)));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Charset.forName("UTF-8"));
        HttpPost loginPost = new HttpPost(BaseParameters.LOGIN_URL);
        loginPost.setEntity(entity);
        clientUtil.defaultRequest(loginHeaders, loginPost, false);
        LOG.warn("login end and here the ASP.NET_SessionIdV2 and visitorid should be available");
    }

    private Map<String, String> createLoginFormMap() {
        Map<String, String> paramsMap = new LinkedHashMap<>();
        List<NameValuePair> headers = new ArrayList<>();
        headers.add(new BasicNameValuePair("origin", "https://duty-free-japan.jp"));
        headers.add(new BasicNameValuePair("referer", "https://duty-free-japan.jp/narita/ch/memberLogin.aspx"));
        headers.add(new BasicNameValuePair("cookie", clientUtil.getFullUserSessionVal()));
        HttpGet get = new HttpGet(BaseParameters.LOGIN_URL);
        String html = clientUtil.defaultRequest(headers, get, true);
        Document doc = Jsoup.parse(html);
        int pos = 0;
        int size = LOGIN_ACTION_PARAMS_LIST.size();
        while (pos < size && pos < 6) {
            String parasName = LOGIN_ACTION_PARAMS_LIST.get(pos);
            paramsMap.put(parasName, pageUtil.fetchElementValueAttrWithId(doc, parasName));
            pos++;
        }
        while (pos < size) {
            paramsMap.put(LOGIN_ACTION_PARAMS_LIST.get(pos++), "");
        }
        paramsMap.put("__EVENTTARGET", "ctl00$cphMain$LBtnLogin");
        paramsMap.put("ctl00$cphMain$TxtMail", BaseParameters.DEMO_USER);
        paramsMap.put("ctl00$cphMain$TxtPASS", BaseParameters.DEMO_USER_PASS);
        return paramsMap;
    }

}
