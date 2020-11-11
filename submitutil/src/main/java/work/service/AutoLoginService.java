package work.service;

import java.nio.charset.Charset;
import java.util.ArrayList;
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
    private static final List<String> LOGIN_ACTION_PARAMS_LIST = new ArrayList<>() {
        {
            add("__EVENTTARGET");
            add("__EVENTARGUMENT");
            add("__LASTFOCUS");
            add("__VIEWSTATE");
            add("__VIEWSTATEGENERATOR");
            add("__SCROLLPOSITIONX");
            add("__SCROLLPOSITIONY");
            add("__EVENTVALIDATION");
            add("ctl00$inputSpSearchFront");
            add("ctl00$ddlLanguageSP");
            add("ctl00$inputSpSearch");
            add("ctl00$ddlLanguagePC");
            add("ctl00$inputPcSearch");
            add("ctl00$ddlLanguageFooterPC");
            add("ctl00$cphMain$GoodsVariationList$ctrl0$ctl00$ddlNum");
        }
    };
    @Autowired
    @Qualifier("HttpClientUtil")
    private HttpClientUtil clientUtil;

    @Autowired
    @Qualifier("PageUtil")
    private PageUtil pageUtil;

    // 做个post提交 获取cookie
    public void loginService() throws Exception {

        LOCK.lock();
        try {
            LOG.info("begin login service");
            // part1 get visitorid and ASP.NET_SessionIdV2
            getCookieParams();
            // part2
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
        clientUtil.defaultRequest(null, indexGet);
        String asp_net_session_id = signSessionValue();
        clientUtil.watchCookieState();
        if (StringUtils.isBlank(asp_net_session_id)) {
            throw new Exception("ASP.NET_SessionIdV2 not found.");
        }
        // 2.request to
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
        clientUtil.defaultRequest(img_13928_headers, img_13928_get);
        clientUtil.watchCookieState();
    }

    private void login() {
    }

    // find ASP.NET_SessionIdV2 value and return
    private String signSessionValue() {
        CookieStore cs = clientUtil.getCookie();
        String asp_net_session_id = "";
        for (Cookie c : cs.getCookies()) {
            if (BaseParameters.SESSION_PARAMS_NAME.equals(c.getName())) {
                asp_net_session_id = c.getValue();
                break;
            }
        }
        return asp_net_session_id;
    }

}
