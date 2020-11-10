package work.service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import work.constants.BaseParameters;
import work.util.HttpClientUtil;
import work.util.PageUtil;

@Service("AutoPlaceOrderService")
public class AutoPlaceOrderService {
    private static final Logger LOG = LoggerFactory.getLogger(AutoPlaceOrderService.class);
    @Autowired
    @Qualifier("HttpClientUtil")
    private HttpClientUtil clientUtil;

    @Autowired
    @Qualifier("PageUtil")
    private PageUtil pageUtil;



    public void OrderServiceRun(List<String> goodIds){
        //1.place order
        addGoodAction(goodIds);
        //2.get shopping trolley
        getGoodList();
        //3.confirm go-off day and terminal
        //4.confirm Airport information
        //5.confirm payment
        //6.finally confirm
    }


    // params sCD
    public void addGoodAction(List<String> goodIds) {
        LOG.info("begin add good...");
        if (CollectionUtils.isEmpty(goodIds)) {
            LOG.error(" goold list cannot empty ");
            return;
        }
        String __VIEWSTATEGENERATOR = get__VIEWSTATEGENERATOR(goodIds.get(0));
        // 第一个页面取一下__VIEWSTATEGENERATOR 参数值
        for (String goodId : goodIds) {
            try {
                List<NameValuePair> headerList = new ArrayList<>();
                headerList.add(new BasicNameValuePair("referer",
                        "https://duty-free-japan.jp/narita/ch/goodsDetail.aspx?sCD=" + goodId));
                headerList.add(new BasicNameValuePair("origin", "https://duty-free-japan.jp"));
                List<NameValuePair> formparams = new ArrayList<NameValuePair>();
                formparams.add(new BasicNameValuePair("__EVENTTARGET", "ctl00$cphMain$LBtnLogin"));
                formparams.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
                formparams.add(new BasicNameValuePair("__LASTFOCUS", ""));
                formparams.add(new BasicNameValuePair("__VIEWSTATEGENERATOR", __VIEWSTATEGENERATOR));
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

            } catch (Exception e) {
                LOG.error("add good error , good id :{} ,message:{}", e.getMessage());
            }

        }

    }

    private String get__VIEWSTATEGENERATOR(String goodId) {
        String __VIEWSTATEGENERATOR = "";
        String uri = "https://duty-free-japan.jp/narita/ch/goodsDetail.aspx?sCD=" + goodId;
        List<NameValuePair> headerList = new ArrayList<>();
        headerList.add(new BasicNameValuePair("referer", "https://duty-free-japan.jp/narita/ch/index.aspx"));
        HttpGet get = new HttpGet(uri);
        String html = clientUtil.defaultRequest(headerList, get);
        Document document = Jsoup.parse(html);
        Element ele = document.getElementById("__VIEWSTATEGENERATOR");

        return __VIEWSTATEGENERATOR;
    }

    public void getGoodList() {
        LOG.info("begin get goodlist...");
        List<NameValuePair> headerList = new ArrayList<>();
        headerList.add(new BasicNameValuePair("referer", "https://duty-free-japan.jp/narita/ch/memberLogin.aspx"));
        headerList.add(new BasicNameValuePair("origin", "https://duty-free-japan.jp"));
    }
}
