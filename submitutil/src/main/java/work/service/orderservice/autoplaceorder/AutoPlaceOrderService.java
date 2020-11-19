package work.service.orderservice.autoplaceorder;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import work.constants.BaseParameters;
import work.model.GoodModel;
import work.model.RequireInfo;
import work.util.HttpClientUtil;
import work.util.PageUtil;

/**
 * @author xxx
 * 
 */
public class AutoPlaceOrderService {

    private int goodListIndex;

    private static final Logger LOG = LoggerFactory.getLogger(AutoPlaceOrderService.class);

    private static final RequireInfo REQUIRE_INFO = RequireInfo.generateDefualtInfo();

    private static final List<String> ADD_GOOD_ACTION_PARAMS_LIST = new ArrayList<>();

    private static final List<String> TAKE_ORDER_POST_PARAMS = new ArrayList<>();

    private static final List<String> AIR_PORT_POST_PARAMS = new ArrayList<>();

    private static final List<String> FINAL_CHECK_POST_PARAMS = new ArrayList<>();

    // need dynamic replaced with '?' when order place one more good
    private static final String GOOD_LIST_FORM_PARAM_SCD_NAME = "ctl00$cphMain$lsvCart$ctrl?$hdnScd";
    // need dynamic replaced with '?' when order place one more good
    private static final String GOOD_LIST_FORM_PARAM_PRICE_NAME = "ctl00$cphMain$lsvCart$ctrl?$hdnPrice";
    // need dynamic replaced with '?' when order place one more good
    private static final String GOOD_LIST_FORM_PARAM_WARIBIKITANKA_NAME = "ctl00$cphMain$lsvCart$ctrl?$hdnWaribikiTanka";
    // need dynamic replaced with '?' when order place one more good
    private static final String GOOD_LIST_FORM_PARAM_DDLNUM_NAME = "ctl00$cphMain$lsvCart$ctrl?$ddlNum";

    static {
        // Collections.addAll(ADD_GOOD_ACTION_PARAMS_LIST,
        // "__VIEWSTATE", "__VIEWSTATEGENERATOR","__EVENTVALIDATION",
        // "ctl00$cphMain$GoodsVariationList$ctrl0$ctl00$ddlNum");
        Collections.addAll(ADD_GOOD_ACTION_PARAMS_LIST, "__VIEWSTATE", "__VIEWSTATEGENERATOR", "__EVENTVALIDATION");
        Collections.addAll(TAKE_ORDER_POST_PARAMS, "__EVENTTARGET", "__EVENTARGUMENT", "__LASTFOCUS", "__VIEWSTATE",
                "__VIEWSTATEGENERATOR", "__SCROLLPOSITIONX", "__SCROLLPOSITIONY", "__VIEWSTATEENCRYPTED",
                "ctl00$inputSpSearchFront", "ctl00$ddlLanguageSP", "ctl00$inputSpSearch", "ctl00$ddlLanguagePC",
                "ctl00$inputPcSearch", "ctl00$cphMain$UC_TerminalInput$TxtDatepicker",
                "ctl00$cphMain$UC_TerminalInput$DdlTerminal", "ctl00$ddlLanguageFooterPC");
        Collections.addAll(AIR_PORT_POST_PARAMS, "ctl00$cphMain$ScriptManager1", "ctl00$inputSpSearchFront",
                "ctl00$ddlLanguageSP", "ctl00$inputSpSearch", "ctl00$ddlLanguagePC", "ctl00$inputPcSearch",
                "ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiCode",
                "ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiUnit",
                "tl00$cphMain$UC_ReserveGoodsList$hdnWaribikiValue",
                "ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiDiscount",
                "ctl00$cphMain$UC_ReserveGoodsList$hdnBaseDetailPrice",
                "ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiPrice", "ctl00$cphMain$UC_BoardingInput$ChkAirportName",
                "ctl00$cphMain$UC_BoardingInput$DdlAirline", "ctl00$cphMain$UC_BoardingInput$TxtFlightNumber",
                "ctl00$cphMain$UC_BoardingInput$chkAgree", "ctl00$cphMain$UC_BoardingInput$DdlDestination",
                "ctl00$cphMain$UC_BoardingInput$transit", "ctl00$cphMain$UC_BoardingInput$TxtReceiverName",
                "ctl00$cphMain$UC_BoardingInput$txtInq", "ctl00$ddlLanguageFooterPC", "__EVENTTARGET",
                "__EVENTARGUMENT", "__LASTFOCUS", "__VIEWSTATE", "__VIEWSTATEGENERATOR", "__VIEWSTATEENCRYPTED",
                "__EVENTVALIDATION", "__ASYNCPOST");
        Collections.addAll(FINAL_CHECK_POST_PARAMS, "__EVENTTARGET", "__EVENTARGUMENT", "__LASTFOCUS", "__VIEWSTATE",
                "__VIEWSTATEGENERATOR", "__VIEWSTATEENCRYPTED", "__EVENTVALIDATION", "ctl00$inputSpSearchFront",
                "ctl00$ddlLanguageSP", "ctl00$inputSpSearch", "ctl00$ddlLanguagePC", "ctl00$inputPcSearch",
                "ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiCode",
                "ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiUnit",
                "ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiValue",
                "ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiDiscount",
                "ctl00$cphMain$UC_ReserveGoodsList$hdnBaseDetailPrice",
                "ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiPrice", "ctl00$cphMain$hdnCardNoToken",
                "ctl00$cphMain$hdnSecurityCode", "ctl00$ddlLanguageFooterPC");
    }
    @Autowired
    @Qualifier("HttpClientUtil")
    private HttpClientUtil clientUtil;

    @Autowired
    @Qualifier("PageUtil")
    private PageUtil pageUtil;

    public AutoPlaceOrderService() {

    }

    public AutoPlaceOrderService(int goodListIndex) {
        this.goodListIndex = goodListIndex;
    }

    public void OrderServiceRun() throws Exception {
        try {
            // add good to trolley
            addGoodAction();
            // trolley confirm
            Map<String, String> airPortFormMap = takeOrderAction();
            // airline info confirm
            Map<String, String> checkInfoFormMap = confirmAirportInfo(airPortFormMap);
            // payment info confirm
            Map<String, String> finalInfoFormMap = confirmPaymentInfo(checkInfoFormMap);
            // final info confirm
            finalConfirm(finalInfoFormMap);
        } catch (Exception e) {
            LOG.error("order service error ,message :{}", e.getMessage());
            throw new Exception("order service error ,message >>" + e.getMessage());
        }
    }

    // params sCD
    private void addGoodAction() throws Exception {
        LOG.info("begin add good...");
        if (CollectionUtils.isEmpty(BaseParameters.G_LISTS)) {
            throw new Exception(" addGoodAction() , good list is empty ");
        }
        // get cookie and add to request header
        for (GoodModel model : BaseParameters.G_LISTS.get(0)) {
            String goodId = model.getGoodId();
            try {
                String uri = "https://duty-free-japan.jp/narita/ch/goodsDetail.aspx?sCD=" + goodId;
                List<NameValuePair> getHeaders = clientUtil.createDefaultRequestHeader("uri");
                HttpGet goodInfoGet = new HttpGet(uri);
                String goodInfoPage = clientUtil.defaultRequest(getHeaders, goodInfoGet, true);
                Document doc = Jsoup.parse(goodInfoPage);

                List<NameValuePair> formParams = new ArrayList<>();
                for (String str : ADD_GOOD_ACTION_PARAMS_LIST) {
                    formParams.add(new BasicNameValuePair(str, pageUtil.getValueAttrWithSection(doc, "id", str)));
                }
                formParams.add(new BasicNameValuePair("__EVENTTARGET", "ctl00$cphMain$LBtnAddCart"));
                formParams.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
                formParams.add(new BasicNameValuePair("__LASTFOCUS", ""));
                formParams.add(new BasicNameValuePair("__SCROLLPOSITIONX", "0"));
                formParams.add(new BasicNameValuePair("__SCROLLPOSITIONY", "0"));
                formParams.add(new BasicNameValuePair("ctl00$inputSpSearchFront", ""));
                formParams.add(new BasicNameValuePair("ctl00$ddlLanguageSP", ""));
                formParams.add(new BasicNameValuePair("ctl00$inputSpSearch", ""));
                formParams.add(new BasicNameValuePair("ctl00$ddlLanguagePC", ""));
                formParams.add(new BasicNameValuePair("ctl00$inputPcSearch", ""));
                formParams.add(new BasicNameValuePair("ctl00$cphMain$GoodsVariationList$ctrl0$ctl00$ddlNum", "1"));
                formParams.add(new BasicNameValuePair("ctl00$ddlLanguageFooterPC", ""));
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formParams, Charset.forName("utf-8"));
                HttpPost goodInfoPost = new HttpPost(uri);
                goodInfoPost.setEntity(formEntity);

                long s1 = System.currentTimeMillis();
                clientUtil.defaultRequest(getHeaders, goodInfoPost, false);
                LOG.info("ddGoodAction goodId :\"{}\" end , send request use time :{}", goodId,
                        (System.currentTimeMillis() - s1));
            } catch (Exception e) {
                throw new Exception("add good error , good id >>" + goodId + " ,message: >>" + e.getMessage());
            }
        }
    }

    // choose department date and terminal
    private Map<String, String> takeOrderAction() throws Exception {
        LOG.info("take order action begin");
        Map<String, String> formMap = null;
        Map<String, String> nextformMap = new LinkedHashMap<>();
        try {
            formMap = createTakeOrderActionFormMap();
            UrlEncodedFormEntity formEntity = mapToFormEntity(formMap);
            // header
            List<NameValuePair> headers = clientUtil.createDefaultRequestHeader(BaseParameters.TAKE_ORDER_ACTION_URI);
            HttpPost post = new HttpPost(BaseParameters.TAKE_ORDER_ACTION_URI);
            post.setEntity(formEntity);
            // String html = clientUtil.defaultRequest(headers, post, true);
            String html = clientUtil.defaultRequest(headers, post, true);
            Document doc = Jsoup.parse(html);
            for (String s : AIR_PORT_POST_PARAMS) {
                nextformMap.put(s, pageUtil.fetchElementValueAttrWithId(doc, s));
            }
            LOG.info("take order action end");
            return nextformMap;
        } catch (Exception e) {
            throw new Exception("confirm terminal and pick time error ! , message >>" + e.getMessage());
        }
    }

    /**
     * confirm airline info page
     */
    private Map<String, String> confirmAirportInfo(Map<String, String> formMap) throws Exception {
        LOG.info(" confirm air port info begin ");
        // generate formMap
        Map<String, String> nextformMap = new LinkedHashMap<>();
        if (ObjectUtils.isEmpty(formMap) || formMap.isEmpty()) {
            throw new Exception(
                    " position >>confirmAirportInfo  prior formMap is empty ,maybe prior response html is empty ");
        }
        try {
            formMap.put("ctl00$cphMain$ScriptManager1",
                    "ctl00$cphMain$ScriptManager1|ctl00$cphMain$UC_BoardingInput$TxtFlightNumber");
            formMap.put("ctl00$cphMain$UC_BoardingInput$ChkAirportName", REQUIRE_INFO.getChkAirportName());
            formMap.put("ctl00$cphMain$UC_BoardingInput$chkAgree", REQUIRE_INFO.getChkAgree());
            formMap.put("ctl00$cphMain$UC_BoardingInput$DdlAirline", REQUIRE_INFO.getAirlineName());
            formMap.put("ctl00$cphMain$UC_BoardingInput$TxtFlightNumber", REQUIRE_INFO.getFlightNumber());
            formMap.put("ctl00$cphMain$UC_BoardingInput$DdlDestination", REQUIRE_INFO.getDestination());
            formMap.put("ctl00$cphMain$UC_BoardingInput$transit", REQUIRE_INFO.getChangeFlight());
            formMap.put("ctl00$cphMain$UC_BoardingInput$TxtReceiverName", REQUIRE_INFO.getReceiver());
            formMap.put("ctl00$cphMain$UC_BoardingInput$txtInq", REQUIRE_INFO.getSearchWords());
            formMap.put("__EVENTTARGET", "ctl00$cphMain$UC_BoardingInput$TxtFlightNumber");
            formMap.put("__ASYNCPOST", "true");
            List<NameValuePair> headers = clientUtil.createDefaultRequestHeader(BaseParameters.TAKE_ORDER_ACTION_URI);
            UrlEncodedFormEntity confirmPostEntity = mapToFormEntity(formMap);
            HttpPost confirmPost = new HttpPost(BaseParameters.BOARDING_INFO_INPUT_URI);
            confirmPost.setEntity(confirmPostEntity);

            String html = clientUtil.defaultRequest(headers, confirmPost, true);
            // remove the following params in map
            formMap.remove("ctl00$cphMain$ScriptManager1");
            formMap.remove("__ASYNCPOST");
            // replace the params in map
            formMap.put("__EVENTTARGET", "ctl00$cphMain$LBtnConfirmation");

            HashSet<String> replacedParams = new HashSet<>();
            Collections.addAll(replacedParams, "__VIEWSTATE", "__VIEWSTATEGENERATOR", "__EVENTVALIDATION");
            subAndReplaceParams(html, formMap, replacedParams);

            List<NameValuePair> boardingHeaders = clientUtil
                    .createDefaultRequestHeader(BaseParameters.BOARDING_INFO_INPUT_URI);
            UrlEncodedFormEntity boardingPostEntity = mapToFormEntity(formMap);

            HttpPost boardingPost = new HttpPost(BaseParameters.BOARDING_INFO_INPUT_URI);
            boardingPost.setEntity(boardingPostEntity);
            long s1 = System.currentTimeMillis();
            String checkInfoHtml = clientUtil.defaultRequest(boardingHeaders, boardingPost, true);
            LOG.info("confirmAirportInfo send request use time :{}", (System.currentTimeMillis() - s1));

            Document doc = Jsoup.parse(checkInfoHtml);
            nextformMap.put("__EVENTTARGET", "ctl00$cphMain$LBtnNext");
            nextformMap.put("__VIEWSTATE", pageUtil.fetchElementValueAttrWithId(doc, "__VIEWSTATE"));
            nextformMap.put("__EVENTVALIDATION", pageUtil.fetchElementValueAttrWithId(doc, "__EVENTVALIDATION"));
            nextformMap.put("__VIEWSTATEGENERATOR", pageUtil.fetchElementValueAttrWithId(doc, "__VIEWSTATEGENERATOR"));
            nextformMap.put("__EVENTARGUMENT", "");
            nextformMap.put("__LASTFOCUS", "");
            nextformMap.put("__VIEWSTATEENCRYPTED", "");
            nextformMap.put("ctl00$inputSpSearchFront", "");
            nextformMap.put("ctl00$ddlLanguageSP", "");
            nextformMap.put("ctl00$inputSpSearch", "");
            nextformMap.put("ctl00$ddlLanguagePC", "");
            nextformMap.put("ctl00$inputPcSearch", "");
            nextformMap.put("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiCode", "");
            nextformMap.put("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiUnit", "");
            nextformMap.put("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiValue", "");
            nextformMap.put("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiDiscount", "");
            nextformMap.put("ctl00$cphMain$UC_ReserveGoodsList$hdnBaseDetailPrice", "");
            nextformMap.put("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiPrice", "");
            nextformMap.put("ctl00$ddlLanguageFooterPC", "");
            LOG.info(" confirm air port info end ");
            return nextformMap;
        } catch (Exception e) {
            throw new Exception(" confirmAirportInfo error , message >>" + e.getMessage());
        }
    }

    /**
     * confirm payment type
     */
    private Map<String, String> confirmPaymentInfo(Map<String, String> checkInfoFormMap) throws Exception {
        LOG.info("confirm payment begin");
        // first,confirm airport and department and termimal info
        Map<String, String> nextFormMap = new LinkedHashMap<>();
        try {
            List<NameValuePair> configFeforeInfoHeaders = clientUtil
                    .createDefaultRequestHeader(BaseParameters.BOARDING_INFO_CHECK_URI);
            UrlEncodedFormEntity configFeforeInfoEntity = mapToFormEntity(checkInfoFormMap);

            HttpPost configFeforeInfoPost = new HttpPost(BaseParameters.BOARDING_INFO_CHECK_URI);
            configFeforeInfoPost.setEntity(configFeforeInfoEntity);
            String paymentDetailHtml = clientUtil.defaultRequest(configFeforeInfoHeaders, configFeforeInfoPost, true);

            Document doc = Jsoup.parse(paymentDetailHtml);
            Map<String, String> paySelectChooseFormMap = new LinkedHashMap<>();
            paySelectChooseFormMap.put("ctl00$cphMain$ScriptManager1",
                    "ctl00$cphMain$udpPayMode|ctl00$cphMain$rdoLocal");
            paySelectChooseFormMap.put("__EVENTTARGET", "ctl00$cphMain$rdoLocal");
            paySelectChooseFormMap.put("__EVENTARGUMENT", "");
            paySelectChooseFormMap.put("__LASTFOCUS", "");
            paySelectChooseFormMap.put("__VIEWSTATE", pageUtil.fetchElementValueAttrWithId(doc, "__VIEWSTATE"));
            paySelectChooseFormMap.put("__VIEWSTATEGENERATOR",
                    pageUtil.fetchElementValueAttrWithId(doc, "__VIEWSTATEGENERATOR"));
            paySelectChooseFormMap.put("__EVENTVALIDATION",
                    pageUtil.fetchElementValueAttrWithId(doc, "__EVENTVALIDATION"));
            paySelectChooseFormMap.put("ctl00$inputSpSearchFront", "");
            paySelectChooseFormMap.put("ctl00$ddlLanguageSP", "");
            paySelectChooseFormMap.put("ctl00$inputSpSearch", "");
            paySelectChooseFormMap.put("ctl00$ddlLanguagePC", "");
            paySelectChooseFormMap.put("ctl00$inputPcSearch", "");

            paySelectChooseFormMap.put("ctl00$cphMain$payment", "rdoLocal");
            paySelectChooseFormMap.put("ctl00$cphMain$regist", "rdoNewCard");
            paySelectChooseFormMap.put("ctl00$cphMain$txtCardNo", "");
            paySelectChooseFormMap.put("ctl00$cphMain$ddlMonth", "00");
            paySelectChooseFormMap.put("ctl00$cphMain$ddlYear", "0000");
            paySelectChooseFormMap.put("ctl00$cphMain$txtCardHolder", "");
            paySelectChooseFormMap.put("ctl00$cphMain$txtSecurityCode", "");
            paySelectChooseFormMap.put("ctl00$cphMain$hdnCardNo", "");
            paySelectChooseFormMap.put("ctl00$cphMain$hdnCardNoToken", "");
            paySelectChooseFormMap.put("ctl00$cphMain$hdnResultCode", "");
            paySelectChooseFormMap.put("ctl00$cphMain$hdn3DToken", "");
            paySelectChooseFormMap.put("ctl00$cphMain$hdnHash", "");
            paySelectChooseFormMap.put("ctl00$ddlLanguageFooterPC", "");
            paySelectChooseFormMap.put("__ASYNCPOST", "true");
            UrlEncodedFormEntity paySelectChooseEntity = mapToFormEntity(paySelectChooseFormMap);

            List<NameValuePair> paySelectHeaders = clientUtil.createDefaultRequestHeader(BaseParameters.PAY_SELECT_URI);
            HttpPost paySelectPost = new HttpPost(BaseParameters.PAY_SELECT_URI);
            paySelectPost.setEntity(paySelectChooseEntity);
            String paymenSelectPage = clientUtil.defaultRequest(paySelectHeaders, paySelectPost, true);

            Set<String> needReplacedParams = new HashSet<>();
            Collections.addAll(needReplacedParams, "__VIEWSTATE", "__VIEWSTATEGENERATOR", "__EVENTVALIDATION");
            subAndReplaceParams(paymenSelectPage, paySelectChooseFormMap, needReplacedParams);
            paySelectChooseFormMap.put("__EVENTTARGET", "ctl00$cphMain$lbtnHdnReg");
            paySelectChooseFormMap.remove("ctl00$cphMain$ScriptManager1");
            paySelectChooseFormMap.remove("ctl00$cphMain$regist");
            paySelectChooseFormMap.remove("ctl00$cphMain$txtCardNo");
            paySelectChooseFormMap.remove("ctl00$cphMain$ddlMonth");
            paySelectChooseFormMap.remove("ctl00$cphMain$ddlYear");
            paySelectChooseFormMap.remove("ctl00$cphMain$txtCardHolder");
            paySelectChooseFormMap.remove("ctl00$cphMain$txtSecurityCode");
            paySelectChooseFormMap.remove("ctl00$cphMain$txtSecurityCode");
            paySelectChooseFormMap.remove("__ASYNCPOST");
            HttpPost paymenConfirmPost = new HttpPost(BaseParameters.PAY_SELECT_URI);
            paymenConfirmPost.setEntity(mapToFormEntity(paySelectChooseFormMap));
            long s1 = System.currentTimeMillis();
            String finalConfirmPage = clientUtil.defaultRequest(paySelectHeaders, paymenConfirmPost, true);
            LOG.info("confirmPaymentInfo send request use time :{}", (System.currentTimeMillis() - s1));
            Document finalConfirmPageDoc = Jsoup.parse(finalConfirmPage);
            for (String key : FINAL_CHECK_POST_PARAMS) {
                nextFormMap.put(key, pageUtil.getValueAttrWithSection(finalConfirmPageDoc, "name", key));
            }
            LOG.info("confirm payment end");
            return nextFormMap;
        } catch (Exception e) {
            throw new Exception("confirmPaymentInfo error ! ,message >>" + e.getMessage());
        }
    }

    // final info confirm
    private void finalConfirm(Map<String, String> finalInfoFormMap) throws Exception {
        LOG.info("final info confirm begin");
        try {
            finalInfoFormMap.put("__EVENTTARGET", "ctl00$cphMain$lbtnRegist");
            List<NameValuePair> headers = clientUtil.createDefaultRequestHeader(BaseParameters.FINAL_CHECK_URI);
            HttpPost post = new HttpPost(BaseParameters.FINAL_CHECK_URI);
            UrlEncodedFormEntity formEntity = mapToFormEntity(finalInfoFormMap);
            post.setEntity(formEntity);
            long s1 = System.currentTimeMillis();
            String html = clientUtil.defaultRequest(headers, post, true);
            LOG.info("finalConfirm send request use time :{}", (System.currentTimeMillis() - s1));
            String val = pageUtil.getTextWithClassName(Jsoup.parse(html), "completed_screen");
            LOG.info("final info confirm end , order id : {}", val);
        } catch (Exception e) {
            throw new Exception("finalConfirm error ! message >>" + e.getMessage());
        }

    }

    private Map<String, String> createTakeOrderActionFormMap() throws Exception {
        try {
            Map<String, String> formMap = new LinkedHashMap<>();
            // headers
            List<NameValuePair> headers = clientUtil.createDefaultRequestHeader(BaseParameters.TAKE_ORDER_ACTION_URI);
            HttpGet get = new HttpGet(BaseParameters.TAKE_ORDER_ACTION_URI);
            String html = clientUtil.defaultRequest(headers, get, true);
            // form params
            Document doc = Jsoup.parse(html);
            for (String s : TAKE_ORDER_POST_PARAMS) {
                String val = pageUtil.fetchElementValueAttrWithId(doc, s);
                formMap.put(s, val);
            }
            formMap.put("__EVENTTARGET", "ctl00$cphMain$LBtnGo");
            formMap.put("__SCROLLPOSITIONY", String.valueOf(Math.random() * 1000).substring(0, 16));
            List<GoodModel> targetGoodList = BaseParameters.G_LISTS.get(this.goodListIndex);
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
                formMap.put(targetWaribikiTankaVal,
                        pageUtil.getValueAttrWithSection(doc, "name", targetWaribikiTankaVal));
                // for now create one count for one good
                formMap.put(targetDdlNumVal, "1");
                pos++;
            }

            formMap.put("ctl00$cphMain$UC_TerminalInput$TxtDatepicker", REQUIRE_INFO.getDepartureDate());
            formMap.put("ctl00$cphMain$UC_TerminalInput$DdlTerminal", REQUIRE_INFO.getTerminalState());
            // confirm datepicker and ddlTermial
            confirmTimeAndTerminalChoose(formMap);
            return formMap;
        } catch (Exception e) {
            throw new Exception("createTakeOrderActionFormMap() error , message >>" + e.getMessage());
        }

    }

    // confirm ternimal and pick date !! use a long to find this action in website
    private void confirmTimeAndTerminalChoose(Map<String, String> requireMap) throws Exception {
        // header
        List<NameValuePair> headers = clientUtil.createDefaultRequestHeader(BaseParameters.TAKE_ORDER_ACTION_URI);
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
        // here need dynamic signed
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
        HashSet<String> replacedParams = new HashSet<>();
        Collections.addAll(replacedParams, "__VIEWSTATE");
        subAndReplaceParams(html, requireMap, replacedParams);
    }

    /**
     * extract __VIEWSTATE params in page width specific params
     * 
     * @param html
     * @param map
     * @param needReplaceParams
     * @throws Exception
     */
    private void subAndReplaceParams(String html, Map<String, String> map, Set<String> needReplacedParams)
            throws Exception {
        if (StringUtils.isBlank(html) || ObjectUtils.isEmpty(map) || map.isEmpty()) {
            throw new Exception("require params is empty >>{html} >>{Map<String, String> map}");
        }
        String[] new__VIEWSTATEArr = html.substring(html.lastIndexOf("\n")).trim().split("\\|");
        if (new__VIEWSTATEArr.length < 3) {
            throw new Exception("cannot find new __VIEWSTATE");
        }
        for (int i = 0; i < new__VIEWSTATEArr.length; i++) {
            if (needReplacedParams.contains(new__VIEWSTATEArr[i])) {
                map.put(new__VIEWSTATEArr[i], new__VIEWSTATEArr[i + 1]);
                LOG.info("replace {} -> {} success !", new__VIEWSTATEArr[i],
                        new__VIEWSTATEArr[i + 1].length() > 100 ? new__VIEWSTATEArr[i + 1].subSequence(0, 100)
                                : new__VIEWSTATEArr[i + 1]);
            }
        }

    }

    private UrlEncodedFormEntity mapToFormEntity(Map<String, String> formMap) throws Exception {
        if (formMap == null || formMap.isEmpty()) {
            throw new Exception(" the map to form entity ,could not be empty! ");
        }
        List<NameValuePair> formList = new ArrayList<>();
        Set<String> keys = formMap.keySet();
        for (String key : keys) {
            formList.add(new BasicNameValuePair(key, formMap.get(key)));
        }
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formList, Charset.forName("UTF-8"));
        return formEntity;
    }

}
