package work.service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import work.util.HttpClientUtil;
import work.util.PageUtil;

@Service("AutoPlaceOrderService")
public class AutoPlaceOrderService {
    private static final Logger LOG = LoggerFactory.getLogger(AutoPlaceOrderService.class);
    private static final List<String> ADD_GOOD_ACTION_PARAMS_LIST = new ArrayList<>() {
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

    public void OrderServiceRun(List<String> goodIds) throws Exception {
        // 1.add good to shopping trolley
        addGoodAction(goodIds);
        // 2.get shopping trolley
        getGoodList();
        // 3.confirm go-off day and terminal
        // 4.confirm Airport information
        // 5.confirm payment
        // 6.finally confirm
    }

    // params sCD
    public void addGoodAction(List<String> goodIds) throws Exception {
        LOG.info("begin add good...");
        if (CollectionUtils.isEmpty(goodIds)) {
            LOG.error(" goold list cannot empty ");
            return;
        }
        // get cookie and add to request header
        String userSeesionVal = clientUtil.getUserSessionVal();
        for (String goodId : goodIds) {
            try {
                String uri = "https://duty-free-japan.jp/narita/ch/goodsDetail.aspx?sCD=" + goodId;
                Map<String, String> formValues = createAddGoodFormParams(goodId);
                List<NameValuePair> headerList = new ArrayList<>();
                headerList.add(new BasicNameValuePair("referer", uri));
                headerList.add(new BasicNameValuePair("origin", "https://duty-free-japan.jp"));
                headerList.add(new BasicNameValuePair("cookie", userSeesionVal));
                List<NameValuePair> formparams = new ArrayList<NameValuePair>();
                for (String s : ADD_GOOD_ACTION_PARAMS_LIST) {
                    formparams.add(new BasicNameValuePair(s, formValues.get(s)));
                }
                UrlEncodedFormEntity formParams = new UrlEncodedFormEntity(formparams, Charset.forName("UTF-8"));
                HttpPost post = new HttpPost(uri);

                post.setEntity(formParams);
                String html = clientUtil.defaultRequest(headerList, post);
                System.out.println(html);
            } catch (Exception e) {
                LOG.error("add good error , good id :{} ,message:{}", goodIds, e.getMessage());
            }

        }
        // validation good count
    }

    private Map<String, String> createAddGoodFormParams(String goodId) throws Exception {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        int size = ADD_GOOD_ACTION_PARAMS_LIST.size();
        String uri = "https://duty-free-japan.jp/narita/ch/goodsDetail.aspx?sCD=" + goodId;
        List<NameValuePair> headerList = new ArrayList<>();
        headerList.add(new BasicNameValuePair("referer", "https://duty-free-japan.jp/narita/ch/index.aspx"));
        HttpGet get = new HttpGet(uri);
        String html = clientUtil.defaultRequest(headerList, get);
        if (StringUtils.isBlank(html)) {
            throw new Exception("goodId place to shopping trolley failed return html is empty");
        }
        Document document = Jsoup.parse(html);
        for (int i = 0; i < size; i++) {
            String params = ADD_GOOD_ACTION_PARAMS_LIST.get(i);
            String value = "";
            value = i < 8 ? pageUtil.fetchElementValueAttrWithSection(document, params) : "";
            map.put(params, value);
        }
        map.put(ADD_GOOD_ACTION_PARAMS_LIST.get(0), "ctl00$cphMain$LBtnAddCart");
        map.put(ADD_GOOD_ACTION_PARAMS_LIST.get(14), "1");
        return map;
    }

    public void getGoodList() {
        LOG.info("begin get goodlist...");
    }
}
