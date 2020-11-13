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
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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

    private static final List<String> TAKE_ORDER_POST_PARAMS = new ArrayList<>() {
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
            add("ctl00$cphMain$UC_TerminalInput$TxtDatepicker");
            add("ctl00$cphMain$UC_TerminalInput$DdlTerminal");
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
        // 1.add good to shopping trolley *done
        addGoodAction();
        // 2.get shopping trolley and confirm go-off day and terminal *done
        takeOrderAction();
        // 3.confirm Airport information
        // 4.confirm payment
        // 5.finally confirm
    }

    // params sCD
    private void addGoodAction() throws Exception {
        LOG.info("begin add good...");
        if (CollectionUtils.isEmpty(BaseParameters.G_LISTS)) {
            LOG.error(" goold list cannot empty ");
            return;
        }
        // get cookie and add to request header
        String userSeesionVal = clientUtil.getFullUserSessionVal();
        for (GoodModel model : BaseParameters.G_LISTS.get(0)) {
            String goodId = model.getGoodId();
            try {
                String uri = "https://duty-free-japan.jp/narita/ch/goodsDetail.aspx?sCD=" + goodId;
                Map<String, String> formValues = createAddGoodFormParams(model);
                // header
                List<NameValuePair> headerList = new ArrayList<>();
                headerList.add(new BasicNameValuePair("referer", uri));
                headerList.add(new BasicNameValuePair("origin", "https://duty-free-japan.jp"));
                headerList.add(new BasicNameValuePair("cookie", userSeesionVal));
                // form params
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
        Map<String, String> formMap = null;
        try {
            formMap = createTakeOrderActionFormMap();
        } catch (Exception e) {
            LOG.error("confirm terminal and pick time error ! , message :{}", e);
            return;
        }
        List<NameValuePair> formParam = new ArrayList<>();
        Set<String> keys = formMap.keySet();
        // form params
        for (String key : keys) {
            formParam.add(new BasicNameValuePair(key, formMap.get(key)));
        }
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formParam, Charset.forName("UTF-8"));
        // header
        List<NameValuePair> headers = new ArrayList<>();
        headers.add(new BasicNameValuePair("origin", BaseParameters.ORIGIN));
        headers.add(new BasicNameValuePair("referer", BaseParameters.TAKE_ORDER_ACTION_URI));
        headers.add(new BasicNameValuePair("cookie", clientUtil.getFullUserSessionVal()));

        HttpPost post = new HttpPost(BaseParameters.TAKE_ORDER_ACTION_URI);
        post.setEntity(formEntity);
        // String html = clientUtil.defaultRequest(headers, post, true);
        clientUtil.defaultRequest(headers, post, false);
        LOG.info("1");
    }

    private Map<String, String> createAddGoodFormParams(GoodModel model) throws Exception {
        String goodId = model.getGoodId();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        int size = ADD_GOOD_ACTION_PARAMS_LIST.size();
        String uri = "https://duty-free-japan.jp/narita/ch/goodsDetail.aspx?sCD=" + goodId;
        // header
        List<NameValuePair> headerList = new ArrayList<>();
        headerList.add(new BasicNameValuePair("referer", "https://duty-free-japan.jp/narita/ch/index.aspx"));
        HttpGet get = new HttpGet(uri);
        String html = clientUtil.defaultRequest(headerList, get, true);
        if (StringUtils.isBlank(html)) {
            throw new Exception("goodId place to shopping trolley failed return html is empty");
        }
        // form params
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

    private Map<String, String> createTakeOrderActionFormMap() throws Exception {
        Map<String, String> formMap = new LinkedHashMap<>();
        // headers
        List<NameValuePair> headers = new ArrayList<>();
        headers.add(new BasicNameValuePair("referer", BaseParameters.TAKE_ORDER_ACTION_URI));
        headers.add(new BasicNameValuePair("origin", "https://duty-free-japan.jp"));
        headers.add(new BasicNameValuePair("cookie", clientUtil.getFullUserSessionVal()));
        HttpGet get = new HttpGet(BaseParameters.TAKE_ORDER_ACTION_URI);
        String html = clientUtil.defaultRequest(headers, get, true);
        // form params
        Document doc = Jsoup.parse(html);
        for (String s : TAKE_ORDER_POST_PARAMS) {
            String val = pageUtil.fetchElementValueAttrWithSection(doc, s);
            formMap.put(s, val);
        }
        formMap.put("__EVENTTARGET", "ctl00$cphMain$LBtnGo");
        formMap.put("__SCROLLPOSITIONY", String.valueOf(Math.random() * 1000).substring(0, 16));
        List<GoodModel> targetGoodList = BaseParameters.G_LISTS.get(0);
        int pos = 0;
        int targetGoodListSize = targetGoodList.size();
        while (pos < targetGoodListSize) {
            String cur = String.valueOf(pos);
            String targetScdVal = GOOD_LIST_FORM_PARAM_SCD_NAME.replace("?", cur);
            String targetPriceVal = GOOD_LIST_FORM_PARAM_PRICE_NAME.replace("?", cur);
            String targetWaribikiTankaVal = GOOD_LIST_FORM_PARAM_WARIBIKITANKA_NAME.replace("?", cur);
            String targetDdlNumVal = GOOD_LIST_FORM_PARAM_DDLNUM_NAME.replace("?", cur);
            formMap.put(targetScdVal, pageUtil.getValueAttrWithSection(doc, "name", targetScdVal));
            formMap.put(targetPriceVal, pageUtil.getValueAttrWithSection(doc, "name", targetPriceVal));
            formMap.put(targetWaribikiTankaVal, pageUtil.getValueAttrWithSection(doc, "name", targetWaribikiTankaVal));
            // formMap.put(targetDdlNumVal, pageUtil.getValueAttrWithSection(doc, "name",
            // targetDdlNumVal));
            formMap.put(targetDdlNumVal, "1");
            pos++;
        }

        RequireInfo info = RequireInfo.generateDefaultInfo();
        formMap.put("ctl00$cphMain$UC_TerminalInput$TxtDatepicker", info.getDepartureDate());
        formMap.put("ctl00$cphMain$UC_TerminalInput$DdlTerminal", info.getTerminalState());
        // confirm datepicker and ddlTermial
        confirmTimeAndTerminalChoose(formMap);
        return formMap;
    }

    // confirm ternimal and pick date !! use a long to find this action in website
    private void confirmTimeAndTerminalChoose(Map<String, String> requireMap) throws Exception {
        // header
        List<NameValuePair> headers = new ArrayList<>();
        headers.add(new BasicNameValuePair("cookie", clientUtil.getFullUserSessionVal()));
        headers.add(new BasicNameValuePair("origin", BaseParameters.ORIGIN));
        headers.add(new BasicNameValuePair("referer", BaseParameters.TAKE_ORDER_ACTION_URI));

        // form parmas
        // the form map some key may need dynamic create values is fixed or come from
        // requireMap
        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("ctl00$cphMain$ScriptManager1",
                "ctl00$cphMain$ctl00|ctl00$cphMain$UC_TerminalInput$TxtDatepicker"));
        formParams.add(new BasicNameValuePair("__EVENTTARGET", "ctl00$cphMain$UC_TerminalInput$TxtDatepicker"));
        formParams.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
        formParams.add(new BasicNameValuePair("__LASTFOCUS", ""));
        formParams.add(new BasicNameValuePair("__VIEWSTATE", requireMap.get("__VIEWSTATE")));
        formParams.add(new BasicNameValuePair("__VIEWSTATEGENERATOR", requireMap.get("__VIEWSTATEGENERATOR")));
        formParams.add(new BasicNameValuePair("__SCROLLPOSITIONX", "0"));
        formParams.add(new BasicNameValuePair("__SCROLLPOSITIONY", "0"));
        formParams.add(new BasicNameValuePair("__VIEWSTATEENCRYPTED", ""));
        formParams.add(new BasicNameValuePair("ctl00$inputSpSearchFront", ""));
        formParams.add(new BasicNameValuePair("ctl00$ddlLanguageSP", ""));
        formParams.add(new BasicNameValuePair("ctl00$inputSpSearch", ""));
        formParams.add(new BasicNameValuePair("ctl00$ddlLanguagePC", ""));
        formParams.add(new BasicNameValuePair("ctl00$inputPcSearch", ""));
        formParams.add(new BasicNameValuePair("ctl00$cphMain$lsvCart$ctrl0$hdnScd",
                requireMap.get("ctl00$cphMain$lsvCart$ctrl0$hdnScd")));
        formParams.add(new BasicNameValuePair("ctl00$cphMain$lsvCart$ctrl0$hdnPrice",
                requireMap.get("ctl00$cphMain$lsvCart$ctrl0$hdnPrice")));
        formParams.add(new BasicNameValuePair("ctl00$cphMain$lsvCart$ctrl0$hdnWaribikiTanka",
                requireMap.get("ctl00$cphMain$lsvCart$ctrl0$hdnWaribikiTanka")));
        formParams.add(new BasicNameValuePair("ctl00$cphMain$lsvCart$ctrl0$ddlNum",
                requireMap.get("ctl00$cphMain$lsvCart$ctrl0$ddlNum")));
        formParams.add(new BasicNameValuePair("ctl00$cphMain$UC_TerminalInput$TxtDatepicker",
                requireMap.get("ctl00$cphMain$UC_TerminalInput$TxtDatepicker")));
        formParams.add(new BasicNameValuePair("ctl00$cphMain$UC_TerminalInput$DdlTerminal",
                requireMap.get("ctl00$cphMain$UC_TerminalInput$DdlTerminal")));
        formParams.add(new BasicNameValuePair("ctl00$ddlLanguageFooterPC", ""));
        formParams.add(new BasicNameValuePair("__ASYNCPOST", "true"));
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formParams, Charset.forName("UTF-8"));
        HttpPost post = new HttpPost(BaseParameters.TAKE_ORDER_ACTION_URI);
        post.setEntity(formEntity);
        String html = clientUtil.defaultRequest(headers, post, true);
        String[] new__VIEWSTATEArr = html.substring(html.lastIndexOf("\n")).trim().split("\\|");
        if (new__VIEWSTATEArr.length >= 2) {
            for (int i = 0; i < new__VIEWSTATEArr.length; i++) {
                if ("__VIEWSTATE".equals(new__VIEWSTATEArr[i])) {
                    requireMap.put("__VIEWSTATE", new__VIEWSTATEArr[i + 1]);
                    break;
                }
            }
        } else {
            throw new Exception("cannot find new __VIEWSTATE");
        }
    }

}
