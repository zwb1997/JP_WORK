package work.service.orderservice;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import work.constants.BaseParameters;
import work.model.GoodModel;
import work.model.RequireInfo;
import work.util.HttpClientUtil;
import work.util.PageUtil;

@Service("AutoPlaceOrderService")
public class AutoPlaceOrderService {
    private static final Logger LOG = LoggerFactory.getLogger(AutoPlaceOrderService.class);
    private static final Random RANDOM = new Random();
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

    private static final List<String> GOOD_LIST_POST_PARAMS = new ArrayList<>() {
        {
            add("__EVENTTARGET");
            add("__EVENTARGUMENT");
            add("__LASTFOCUS");
            add("__VIEWSTATE");
            add("__VIEWSTATEGENERATOR");
            add("__SCROLLPOSITIONX");
            add("__SCROLLPOSITIONY");
            add("__VIEWSTATEENCRYPTED");
            add("ctl00$inputSpSearchFront");
            add("ctl00$ddlLanguageSP");
            add("ctl00$inputSpSearch");
            add("ctl00$ddlLanguagePC");
            add("ctl00$inputPcSearch");
            add("ctl00$ddlLanguageFooterPC");
        }
    };
    private static final String GOOD_LIST_FORM_PARAM_SCD_NAME = "ctl00$cphMain$lsvCart$ctrl?$hdnScd";
    private static final String GOOD_LIST_FORM_PARAM_PRICE_NAME = "ctl00$cphMain$lsvCart$ctrl?$hdnPrice";
    private static final String GOOD_LIST_FORM_PARAM_WARIBIKITANKA_NAME = "ctl00$cphMain$lsvCart$ctrl?$hdnWaribikiTanka";
    private static final String GOOD_LIST_FORM_PARAM_DDLNUM_NAME = "ctl00$cphMain$lsvCart$ctrl?$ddlNum";
    @Autowired
    @Qualifier("HttpClientUtil")
    private HttpClientUtil clientUtil;

    @Autowired
    @Qualifier("PageUtil")
    private PageUtil pageUtil;

    public void OrderServiceRun() throws Exception {
        // 1.add good to shopping trolley
        addGoodAction();
        // 2.get shopping trolley and confirm go-off day and terminal
        takeOrderAction();
        // 3.confirm Airport information
        // 4.confirm payment
        // 5.finally confirm
    }

    // params sCD
    private void addGoodAction() throws Exception {
        LOG.info("begin add good...");
        if (CollectionUtils.isEmpty(BaseParameters.GOOD_IDS)) {
            LOG.error(" goold list cannot empty ");
            return;
        }
        // get cookie and add to request header
        String userSeesionVal = clientUtil.getFullUserSessionVal();
        for (GoodModel model : BaseParameters.GOOD_IDS) {
            String goodId = model.getGoodId();
            try {
                String uri = "https://duty-free-japan.jp/narita/ch/goodsDetail.aspx?sCD=" + goodId;
                Map<String, String> formValues = createAddGoodFormParams(model);
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
                clientUtil.defaultRequest(headerList, post, false);
            } catch (Exception e) {
                LOG.error("add good error , good id :{} ,message:{}", goodId, e.getMessage());
            }
        }
    }

    // choose department date and terminal
    private void takeOrderAction() {
        LOG.info("begin get goodlist...");
        Map<String, String> gooldListMap = createGoodlListFormParams();
        List<NameValuePair> headerList = new ArrayList<>();
        List<NameValuePair> formParams = new ArrayList<>();
        Set<Map.Entry<String, String>> mapSet = gooldListMap.entrySet();
        for (Map.Entry<String, String> entry : mapSet) {
            formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formParams, Charset.forName("UTF-8"));
        headerList.add(new BasicNameValuePair("referer", "https://duty-free-japan.jp/narita/ch/goodsReserveList.aspx"));
        headerList.add(new BasicNameValuePair("origin", "https://duty-free-japan.jp"));
        String fullCookieParams = clientUtil.getFullUserSessionVal();
        headerList.add(new BasicNameValuePair("cookie", fullCookieParams));
        HttpPost post = new HttpPost(BaseParameters.CREATE_GOOD_LIST_URI);
        post.setEntity(formEntity);
        String html = clientUtil.defaultRequest(headerList, post, true);
        LOG.info("submit date and terminal");
    }

    private Map<String, String> createAddGoodFormParams(GoodModel model) throws Exception {
        String goodId = model.getGoodId();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        int size = ADD_GOOD_ACTION_PARAMS_LIST.size();
        String uri = "https://duty-free-japan.jp/narita/ch/goodsDetail.aspx?sCD=" + goodId;
        List<NameValuePair> headerList = new ArrayList<>();
        headerList.add(new BasicNameValuePair("referer", "https://duty-free-japan.jp/narita/ch/index.aspx"));
        HttpGet get = new HttpGet(uri);
        String html = clientUtil.defaultRequest(headerList, get, true);
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
        // map.put(ADD_GOOD_ACTION_PARAMS_LIST.get(14), confirmGoodCount(document,
        // model));
        map.put(ADD_GOOD_ACTION_PARAMS_LIST.get(14), "1");
        return map;
    }

    private String confirmGoodCount(Document document, GoodModel model) {
        String count = String.valueOf(model.getGoodCount());
        return count;
    }

    private Map<String, String> createGoodlListFormParams() {
        Map<String, String> formMap = new LinkedHashMap<>();
        List<NameValuePair> headers = new ArrayList<>();
        headers.add(new BasicNameValuePair("referer", "https://duty-free-japan.jp/narita/ch/goodsReserveList.aspx"));
        String fullCookieParams = clientUtil.getFullUserSessionVal();
        headers.add(new BasicNameValuePair("cookie", fullCookieParams));
        HttpGet get = new HttpGet(BaseParameters.CREATE_GOOD_LIST_URI);
        String gooldListHtml = clientUtil.defaultRequest(headers, get, true);
        Document doc = Jsoup.parse(gooldListHtml);
        int goodSize = BaseParameters.GOOD_IDS.size();
        // get fixed info
        for (String s : GOOD_LIST_POST_PARAMS) {
            if ("__EVENTTARGET".equals(s)) {
                formMap.put(s, "ctl00$cphMain$LBtnGo");
                continue;
            }
            String value = pageUtil.fetchElementValueAttrWithSection(doc, s);
            formMap.put(s, value);
        }

        for (int i = 0; i < goodSize; i++) {
            // get dynamic create params and value
            String pos = String.valueOf(i);
            String scdVal = GOOD_LIST_FORM_PARAM_SCD_NAME.replace("?", pos).replace("$", "_");
            String priceVal = GOOD_LIST_FORM_PARAM_PRICE_NAME.replace("?", pos).replace("$", "_");
            String waribikitankaVal = GOOD_LIST_FORM_PARAM_WARIBIKITANKA_NAME.replace("?", pos).replace("$", "_");
            formMap.put(GOOD_LIST_FORM_PARAM_SCD_NAME.replace("?", pos),
                    pageUtil.fetchElementValueAttrWithSection(doc, scdVal));
            formMap.put(GOOD_LIST_FORM_PARAM_PRICE_NAME.replace("?", pos),
                    pageUtil.fetchElementValueAttrWithSection(doc, priceVal));
            formMap.put(GOOD_LIST_FORM_PARAM_WARIBIKITANKA_NAME.replace("?", pos),
                    pageUtil.fetchElementValueAttrWithSection(doc, waribikitankaVal));
            // formMap.put(GOOD_LIST_FORM_PARAM_DDLNUM_NAME.replace("?", pos),
            // getGoodDDlCount(doc, GOOD_LIST_FORM_PARAM_DDLNUM_NAME.replace("?", pos)));
            formMap.put(GOOD_LIST_FORM_PARAM_DDLNUM_NAME.replace("?", pos), "1");
        }
        RequireInfo model = RequireInfo.generateDefaultInfo();
        formMap.put("ctl00$cphMain$UC_TerminalInput$TxtDatepicker", model.getDepartureDate());
        formMap.put("ctl00$cphMain$UC_TerminalInput$DdlTerminal", model.getTerminalState());
        return formMap;
    }

    // random create good count
    private String getGoodDDlCount(Document d, String ddlName) {
        String count = "1";
        // Elements es = d.getElementsByAttributeValue("name", ddlName);
        // String[] countCount = es.first().text().split(" ");
        // count = StringUtils.isNotBlank(countCount[RANDOM.nextInt(countCount.length)])
        // ? countCount[RANDOM.nextInt(countCount.length)]
        // : count;
        return count;
    }

}
