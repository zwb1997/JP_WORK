package work.service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import work.constants.BaseParameters;
import work.constants.InitViewState;
import work.util.HttpClientUtil;

@Service("AutoLoginService")
public class AutoLoginService {
    private static final Logger LOG = LoggerFactory.getLogger(AutoLoginService.class);
    private boolean hasLogin = false;
    @Autowired
    @Qualifier("HttpClientUtil")
    private HttpClientUtil clientUtil;

    // 做个post提交 获取cookie
    public void login() {
        LOG.info("begin login service");
        List<NameValuePair> headerList = new ArrayList<>();
        headerList.add(new BasicNameValuePair("referer", "https://duty-free-japan.jp/narita/ch/memberLogin.aspx"));
        headerList.add(new BasicNameValuePair("origin", "https://duty-free-japan.jp"));
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("__EVENTTARGET", "ctl00$cphMain$LBtnLogin"));
        formparams.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
        formparams.add(new BasicNameValuePair("__LASTFOCUS", ""));
        formparams.add(new BasicNameValuePair("__VIEWSTATEGENERATOR", "B7BAF78D"));
        formparams.add(new BasicNameValuePair("__EVENTVALIDATION",
                "/wEdAB65yneuzThfjiKUlW7Oj911pK3qULQQy7WVONK0QNLludqb/WScVLTOPJyPjfX7fqhYHauxUWxj6iJUCpLxjCuG8rTiM6s0s5HepNPt1TVYECCjJC4P37GB/plFbcwrSVowPrKhs3YisidZ5aqcqRaMlNFpM3eB6i1l4wzvLw3i3DwiVEJB3rUBlU1H6mA5y9S5jThhdzOwG3HSTOwNZjUWTHQw44UVodRyG4X+MuGnij5LxGY9XkUojuFZ1drTt34Km1ILVnX9dPhe3YiOZsB4za2M7TOR/04rX+2ZoIIuiMuQrGs7q+I/BFu1bMHmYNrakCaacXCDYvJEEjRoNZVcnKxNtlQ2R3cv2VmJzDHSOlbJDU1Vxc7JlI5Yqq/qGq0V+w31d6ai4v4YOHoW7q1npgWWQ6bvLlKFmMkH/sDKvzrEi/hhptXSQ7Nq0JIZ8nOSIcUVPlo612lkaW3LzCKN3lDHd9GLkWzxnGa84dLjxCTSKsS3qDbiPjBou+cogQEBmAP5+s4VpjccujAfqWrvirBMJkVdqSSfsLIxT0BYgO19yzS3DtAM9hW7SeHLgJJ6P/zOUiMSfNZbZnoyWWdiYuVxUtHvfY6ePPK26xaHEJvv6A35ua5/g/c5etAXKQuZa8hPOHsiaW5xaSdiFmxnsZSURGQGdUzRf43wWdfAlg=="));
        formparams.add(new BasicNameValuePair("ctl00$inputSpSearchFront", ""));
        formparams.add(new BasicNameValuePair("ctl00$ddlLanguageSP", ""));
        formparams.add(new BasicNameValuePair("ctl00$inputSpSearch", ""));
        formparams.add(new BasicNameValuePair("ctl00$ddlLanguagePC", ""));
        formparams.add(new BasicNameValuePair("ctl00$inputPcSearch", ""));
        formparams.add(new BasicNameValuePair("ctl00$cphMain$TxtMail", BaseParameters.DEMO_USER));
        formparams.add(new BasicNameValuePair("ctl00$cphMain$TxtPASS", BaseParameters.DEMO_USER_PASS));
        formparams.add(new BasicNameValuePair("ctl00$ddlLanguageFooterPC", ""));
        UrlEncodedFormEntity formParams = new UrlEncodedFormEntity(formparams, Charset.forName("UTF-8"));
        HttpPost post = new HttpPost(BaseParameters.LOGIN_URL);
        post.setEntity(formParams);
        String html = clientUtil.defaultRequest(headerList, post);
        List<Cookie> cookies = clientUtil.getCookie().getCookies();
        if (!CollectionUtils.isEmpty(cookies)) {
            hasLogin = true;
        }
    }
}
