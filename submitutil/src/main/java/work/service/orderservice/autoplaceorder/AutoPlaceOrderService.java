package work.service.orderservice.autoplaceorder;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import work.constants.BaseParameters;
import work.model.GoodModel;
import work.model.HttpClientUtilModel;
import work.model.RequireInfo;
import work.util.HttpClientUtil;
import work.util.PageUtil;

/**
 * @author xxx
 * 
 */
public class AutoPlaceOrderService {

        private static final Logger LOG = LoggerFactory.getLogger(AutoPlaceOrderService.class);

        private RequireInfo requireInfo;

        private GoodModel goodModel;

        private HttpClientUtilModel clientUtilModel;

        private HttpClientContext currentContext;

        private Map<String, String> cookieParamsMap;

        // need dynamic replaced with '?' when order place one more good
        private static final String GOOD_LIST_FORM_PARAM_SCD_NAME = "ctl00$cphMain$lsvCart$ctrl?$hdnScd";
        // need dynamic replaced with '?' when order place one more good
        private static final String GOOD_LIST_FORM_PARAM_PRICE_NAME = "ctl00$cphMain$lsvCart$ctrl?$hdnPrice";
        // need dynamic replaced with '?' when order place one more good
        private static final String GOOD_LIST_FORM_PARAM_WARIBIKITANKA_NAME = "ctl00$cphMain$lsvCart$ctrl?$hdnWaribikiTanka";
        // need dynamic replaced with '?' when order place one more good
        private static final String GOOD_LIST_FORM_PARAM_DDLNUM_NAME = "ctl00$cphMain$lsvCart$ctrl?$ddlNum";

        public AutoPlaceOrderService(RequireInfo requireInfo, GoodModel goodModel, HttpClientUtilModel clientUtilModel,
                        HttpClientContext curreContext) {
                this.requireInfo = requireInfo;
                this.goodModel = goodModel;
                this.clientUtilModel = clientUtilModel;
                this.currentContext = curreContext;
                this.cookieParamsMap = new LinkedHashMap<>();
        }

        // params sCD
        public void addGoodAction(UrlEncodedFormEntity formEntity) throws Exception {
                LOG.info("begin add good...");
                if (ObjectUtils.isEmpty(formEntity)) {
                        LOG.error("addGoodAction , params formEntity is empty");
                        throw new Exception("addGoodActionparams formEntity is empty");
                }
                long s1 = System.nanoTime();
                List<NameValuePair> headers = HttpClientUtil.createRequestHeader(clientUtilModel.getReferer(),
                                currentContext);
                String uri = BaseParameters.GOOD_DETAIL_INFO + "?sCD=" + goodModel.getGoodId();
                HttpPost post = new HttpPost(uri);
                post.setEntity(formEntity);
                // use current cookie to add good to shopping trolley
                HttpClientUtil.defaultRequest(headers, post, currentContext, false, false, null);

                formEntity = null;
                LOG.info("add good end");

                List<NameValuePair> trolleyHeaders = HttpClientUtil.createRequestHeader(clientUtilModel.getReferer(),
                                currentContext);
                // request to shopping trolley
                HttpGet visitTrolley = new HttpGet(BaseParameters.TAKE_ORDER_ACTION_URI);
                HttpClientUtilModel visitTrolleyModel = HttpClientUtil.defaultRequest(trolleyHeaders, visitTrolley,
                                currentContext, true, false, null);
                // need stroage 'JAT-EC-Cart'
                // storageCookieParams();
                // extract params from visitTrolleyModel
                Document doc = Jsoup.parse(visitTrolleyModel.getHtml());

                List<NameValuePair> confirmTernimalAndDateFromParams = new ArrayList<>(20);
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("ctl00$cphMain$ScriptManager1",
                                "ctl00$cphMain$ctl00|ctl00$cphMain$UC_TerminalInput$TxtDatepicker"));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("__EVENTTARGET",
                                "ctl00$cphMain$UC_TerminalInput$TxtDatepicker"));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("__LASTFOCUS", ""));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("__VIEWSTATE",
                                PageUtil.fetchElementValueAttrWithId(doc, "__VIEWSTATE")));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("__VIEWSTATEGENERATOR",
                                PageUtil.fetchElementValueAttrWithId(doc, "__VIEWSTATEGENERATOR")));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("__SCROLLPOSITIONX", "0"));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("__SCROLLPOSITIONY", "0"));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("__VIEWSTATEENCRYPTED", ""));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("ctl00$inputSpSearchFront", ""));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("ctl00$ddlLanguageSP", ""));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("ctl00$inputPcSearch", ""));
                confirmTernimalAndDateFromParams.add(
                                new BasicNameValuePair("ctl00$cphMain$lsvCart$ctrl0$hdnScd", goodModel.getGoodId()));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("ctl00$cphMain$lsvCart$ctrl0$hdnPrice",
                                PageUtil.getValueAttrWithSection(doc, "name", "ctl00$cphMain$lsvCart$ctrl0$hdnPrice")));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair(
                                "ctl00$cphMain$lsvCart$ctrl0$hdnWaribikiTanka", PageUtil.getValueAttrWithSection(doc,
                                                "name", "ctl00$cphMain$lsvCart$ctrl0$hdnWaribikiTanka")));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("ctl00$cphMain$lsvCart$ctrl0$ddlNum",
                                String.valueOf(goodModel.getGoodCount())));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair(
                                "ctl00$cphMain$UC_TerminalInput$TxtDatepicker", requireInfo.getDepartureDate()));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair(
                                "ctl00$cphMain$UC_TerminalInput$DdlTerminal", requireInfo.getTerminalState()));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("ctl00$ddlLanguageFooterPC", ""));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("__ASYNCPOST", "true"));

                UrlEncodedFormEntity confirnTernimalAndDateFormEntity = new UrlEncodedFormEntity(
                                confirmTernimalAndDateFromParams, Charset.forName("utf-8"));

                HttpPost confirnTernimalAndDatePost = new HttpPost(BaseParameters.TAKE_ORDER_ACTION_URI);

                confirnTernimalAndDatePost.setEntity(confirnTernimalAndDateFormEntity);

                List<NameValuePair> confirnTernimalAndDateHeaders = HttpClientUtil
                                .createRequestHeader(BaseParameters.TAKE_ORDER_ACTION_URI, currentContext);
                HttpClientUtilModel confirnTernimalAndDateModel = HttpClientUtil.defaultRequest(
                                confirnTernimalAndDateHeaders, confirnTernimalAndDatePost, currentContext, true, false,
                                null);

                LOG.info("confirm terminal and department date end");
                subAndReplaceParams(confirnTernimalAndDateModel.getHtml(), confirmTernimalAndDateFromParams,
                                Arrays.asList("__VIEWSTATE", "__VIEWSTATEGENERATOR", "__EVENTVALIDATION"));
                confirmTernimalAndDateFromParams.set(1,
                                new BasicNameValuePair("__EVENTTARGET", "ctl00$cphMain$LBtnGo"));
                confirmTernimalAndDateFromParams.remove(0);
                confirmTernimalAndDateFromParams.remove(confirmTernimalAndDateFromParams.size() - 1);
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("ctl00$inputSpSearch", ""));
                confirmTernimalAndDateFromParams.add(new BasicNameValuePair("ctl00$ddlLanguagePC", ""));

                List<NameValuePair> addGoodActionHeaders = HttpClientUtil
                                .createRequestHeader(BaseParameters.TAKE_ORDER_ACTION_URI, currentContext);

                HttpPost addGoodActionPost = new HttpPost(BaseParameters.TAKE_ORDER_ACTION_URI);
                addGoodActionPost.setEntity(
                                new UrlEncodedFormEntity(confirmTernimalAndDateFromParams, Charset.forName("utf-8")));

                HttpClientUtilModel addGoodActionModel = HttpClientUtil.defaultRequest(addGoodActionHeaders,
                                addGoodActionPost, currentContext, true, false, null);

                confirmTernimalAndDateFromParams = null;
                LOG.info("submit terminal and department date end");
                // next login

                String loginUrl = BaseParameters.LOGIN_URL + "?btnNm=LBtnGo";

                List<NameValuePair> loginHeaders = HttpClientUtil.createRequestHeader(loginUrl, currentContext);

                List<NameValuePair> loginFormParams = new ArrayList<>(15);
                Document loginPageDoc = Jsoup.parse(addGoodActionModel.getHtml());
                loginFormParams.add(new BasicNameValuePair("__EVENTTARGET", "ctl00$cphMain$LBtnLogin"));
                loginFormParams.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
                loginFormParams.add(new BasicNameValuePair("__LASTFOCUS", ""));
                loginFormParams.add(new BasicNameValuePair("__VIEWSTATE",
                                PageUtil.fetchElementValueAttrWithId(loginPageDoc, "__VIEWSTATE")));
                loginFormParams.add(new BasicNameValuePair("__VIEWSTATEGENERATOR",
                                PageUtil.fetchElementValueAttrWithId(loginPageDoc, "__VIEWSTATEGENERATOR")));
                loginFormParams.add(new BasicNameValuePair("__EVENTVALIDATION",
                                PageUtil.fetchElementValueAttrWithId(loginPageDoc, "__EVENTVALIDATION")));
                loginFormParams.add(new BasicNameValuePair("ctl00$inputSpSearchFront", ""));
                loginFormParams.add(new BasicNameValuePair("ctl00$ddlLanguageSP", ""));
                loginFormParams.add(new BasicNameValuePair("ctl00$inputSpSearch", ""));
                loginFormParams.add(new BasicNameValuePair("ctl00$ddlLanguagePC", ""));
                loginFormParams.add(new BasicNameValuePair("ctl00$inputPcSearch", ""));
                loginFormParams.add(new BasicNameValuePair("ctl00$cphMain$TxtMail", requireInfo.getEmail()));
                loginFormParams.add(new BasicNameValuePair("ctl00$cphMain$TxtPASS", requireInfo.getPassword()));
                loginFormParams.add(new BasicNameValuePair("ctl00$ddlLanguageFooterPC", ""));

                UrlEncodedFormEntity logiEntity = new UrlEncodedFormEntity(loginFormParams, Charset.forName("utf-8"));
                loginFormParams = null;
                HttpPost loginPost = new HttpPost(loginUrl);
                loginPost.setEntity(logiEntity);
                // logined ,get confirm airline info page
                HttpClientUtilModel afterLoginInModel = HttpClientUtil.defaultRequest(loginHeaders, loginPost,
                                currentContext, true, false, null);
                LOG.info("after login end");
                // HttpClientUtil.watchCookieState(currentContext);
                // String currentFullCookieStr =
                // HttpClientUtil.getFullUserSessionVal(currentContext);
                // if(currentFullCookieStr.indexOf(BaseParameters.GOOD_ADD_ACTION_GOOD_COUNT) ==
                // -1){

                // }currentContext

                HttpClientUtil.addParamsIntoCookie(currentContext,
                                new BasicClientCookie("JAT-EC-Cart", cookieParamsMap.get("JAT-EC-Cart")));
                Document confirmAirPosDoc = Jsoup.parse(afterLoginInModel.getHtml());
                List<NameValuePair> confirmAirInfoPostParams = new ArrayList<>(30);
                confirmAirInfoPostParams.add(new BasicNameValuePair("ctl00$cphMain$ScriptManager1",
                                "ctl00$cphMain$ScriptManager1|ctl00$cphMain$UC_BoardingInput$TxtFlightNumber"));
                confirmAirInfoPostParams.add(new BasicNameValuePair("__EVENTTARGET",
                                "ctl00$cphMain$UC_BoardingInput$TxtFlightNumber"));
                confirmAirInfoPostParams.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
                confirmAirInfoPostParams.add(new BasicNameValuePair("__LASTFOCUS", ""));
                confirmAirInfoPostParams.add(new BasicNameValuePair("__VIEWSTATE",
                                PageUtil.fetchElementValueAttrWithId(confirmAirPosDoc, "__VIEWSTATE")));
                confirmAirInfoPostParams.add(new BasicNameValuePair("__VIEWSTATEGENERATOR",
                                PageUtil.fetchElementValueAttrWithId(confirmAirPosDoc, "__VIEWSTATEGENERATOR")));
                confirmAirInfoPostParams.add(new BasicNameValuePair("__VIEWSTATEENCRYPTED", ""));
                confirmAirInfoPostParams.add(new BasicNameValuePair("__EVENTVALIDATION",
                                PageUtil.fetchElementValueAttrWithId(confirmAirPosDoc, "__EVENTVALIDATION")));
                confirmAirInfoPostParams.add(new BasicNameValuePair("ctl00$inputSpSearchFront", ""));
                confirmAirInfoPostParams.add(new BasicNameValuePair("ctl00$ddlLanguageSP", ""));
                confirmAirInfoPostParams.add(new BasicNameValuePair("ctl00$inputSpSearch", ""));
                confirmAirInfoPostParams.add(new BasicNameValuePair("ctl00$ddlLanguagePC", ""));
                confirmAirInfoPostParams.add(new BasicNameValuePair("ctl00$inputPcSearch", ""));
                confirmAirInfoPostParams
                                .add(new BasicNameValuePair("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiCode", ""));
                confirmAirInfoPostParams
                                .add(new BasicNameValuePair("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiUnit", ""));
                confirmAirInfoPostParams
                                .add(new BasicNameValuePair("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiValue", ""));
                confirmAirInfoPostParams.add(
                                new BasicNameValuePair("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiDiscount", ""));
                confirmAirInfoPostParams.add(
                                new BasicNameValuePair("ctl00$cphMain$UC_ReserveGoodsList$hdnBaseDetailPrice", ""));
                confirmAirInfoPostParams
                                .add(new BasicNameValuePair("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiPrice", ""));
                confirmAirInfoPostParams.add(new BasicNameValuePair("ctl00$cphMain$UC_BoardingInput$ChkAirportName",
                                requireInfo.getChkAirportName()));
                confirmAirInfoPostParams.add(new BasicNameValuePair("ctl00$cphMain$UC_BoardingInput$DdlAirline",
                                requireInfo.getAirlineName()));
                confirmAirInfoPostParams.add(new BasicNameValuePair("ctl00$cphMain$UC_BoardingInput$TxtFlightNumber",
                                requireInfo.getFlightNumber()));
                confirmAirInfoPostParams.add(new BasicNameValuePair("ctl00$cphMain$UC_BoardingInput$chkAgree",
                                requireInfo.getChkAgree()));
                confirmAirInfoPostParams.add(new BasicNameValuePair("ctl00$cphMain$UC_BoardingInput$DdlDestination",
                                requireInfo.getDestination()));
                confirmAirInfoPostParams.add(new BasicNameValuePair("ctl00$cphMain$UC_BoardingInput$transit",
                                requireInfo.getChangeFlight()));
                confirmAirInfoPostParams.add(new BasicNameValuePair("ctl00$cphMain$UC_BoardingInput$TxtReceiverName",
                                requireInfo.getReceiver()));
                confirmAirInfoPostParams.add(new BasicNameValuePair("ctl00$cphMain$UC_BoardingInput$txtInq",
                                requireInfo.getSearchWords()));
                confirmAirInfoPostParams.add(new BasicNameValuePair("ctl00$ddlLanguageFooterPC", ""));
                confirmAirInfoPostParams.add(new BasicNameValuePair("__ASYNCPOST", "true"));

                goConfirmAirInfo(s1, confirmAirInfoPostParams);
        }

        /**
         * storage cookies
         */
        private void storageCookieParams() {
                CookieStore cookieStore = currentContext.getCookieStore();
                List<Cookie> cookies = cookieStore.getCookies();
                for (Cookie c : cookies) {
                        cookieParamsMap.put(c.getName(), c.getValue());
                }
        }

        /**
         * confirm airport info
         * 
         * @param confirmAirFormEntity
         */
        private void goConfirmAirInfo(long s1, List<NameValuePair> confirmAirParams) throws Exception {
                LOG.info("begin confirm airPort info");

                List<NameValuePair> confirmAirPortInfoHeaders = HttpClientUtil
                                .createRequestHeader(BaseParameters.BOARDING_INFO_INPUT_URI, currentContext);

                HttpPost confirmAirPortInfoPost = new HttpPost(BaseParameters.BOARDING_INFO_INPUT_URI);

                confirmAirPortInfoPost.setEntity(new UrlEncodedFormEntity(confirmAirParams, Charset.forName("utf-8")));

                HttpClientUtilModel afterConfirmAirportModel = HttpClientUtil.defaultRequest(confirmAirPortInfoHeaders,
                                confirmAirPortInfoPost, currentContext, true, false, null);

                LOG.info("after confirm air line info end");
                // reuse the paramsList
                confirmAirParams.remove(0);
                confirmAirParams.remove(confirmAirParams.size() - 1);
                confirmAirParams.set(0, new BasicNameValuePair("__EVENTTARGET", "ctl00$cphMain$LBtnConfirmation"));

                subAndReplaceParams(afterConfirmAirportModel.getHtml(), confirmAirParams,
                                Arrays.asList("__VIEWSTATE", "__VIEWSTATEGENERATOR", "__EVENTVALIDATION"));

                HttpPost submitAirPortPost = new HttpPost(BaseParameters.BOARDING_INFO_INPUT_URI);
                submitAirPortPost.setEntity(new UrlEncodedFormEntity(confirmAirParams, Charset.forName("utf-8")));
                confirmAirParams = null;
                HttpClientUtilModel afterSubmitAirPortModel = HttpClientUtil.defaultRequest(confirmAirPortInfoHeaders,
                                submitAirPortPost, currentContext, true, false, null);
                // end
                LOG.info("submit confirm air line info end");
                // then go to select payment page
                Document afterSubmitAirPortDoc = Jsoup.parse(afterSubmitAirPortModel.getHtml());
                List<NameValuePair> beforePaymentFormParams = new ArrayList<>(20);

                beforePaymentFormParams.add(new BasicNameValuePair("__EVENTTARGET", "ctl00$cphMain$LBtnNext"));
                beforePaymentFormParams.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
                beforePaymentFormParams.add(new BasicNameValuePair("__LASTFOCUS", ""));
                beforePaymentFormParams.add(new BasicNameValuePair("__VIEWSTATE",
                                PageUtil.fetchElementValueAttrWithId(afterSubmitAirPortDoc, "__VIEWSTATE")));
                beforePaymentFormParams.add(new BasicNameValuePair("__VIEWSTATEGENERATOR",
                                PageUtil.fetchElementValueAttrWithId(afterSubmitAirPortDoc, "__VIEWSTATEGENERATOR")));
                beforePaymentFormParams.add(new BasicNameValuePair("__VIEWSTATEENCRYPTED", ""));
                beforePaymentFormParams.add(new BasicNameValuePair("__EVENTVALIDATION",
                                PageUtil.fetchElementValueAttrWithId(afterSubmitAirPortDoc, "__EVENTVALIDATION")));
                beforePaymentFormParams.add(new BasicNameValuePair("ctl00$inputSpSearchFront", ""));
                beforePaymentFormParams.add(new BasicNameValuePair("ctl00$ddlLanguageSP", ""));
                beforePaymentFormParams.add(new BasicNameValuePair("ctl00$inputSpSearch", ""));
                beforePaymentFormParams.add(new BasicNameValuePair("ctl00$ddlLanguagePC", ""));
                beforePaymentFormParams.add(new BasicNameValuePair("ctl00$inputPcSearch", ""));
                beforePaymentFormParams
                                .add(new BasicNameValuePair("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiCode", ""));
                beforePaymentFormParams
                                .add(new BasicNameValuePair("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiUnit", ""));
                beforePaymentFormParams
                                .add(new BasicNameValuePair("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiValue", ""));
                beforePaymentFormParams.add(
                                new BasicNameValuePair("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiDiscount", ""));
                beforePaymentFormParams.add(
                                new BasicNameValuePair("ctl00$cphMain$UC_ReserveGoodsList$hdnBaseDetailPrice", ""));
                beforePaymentFormParams
                                .add(new BasicNameValuePair("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiPrice", ""));
                beforePaymentFormParams.add(new BasicNameValuePair("ctl00$ddlLanguageFooterPC", ""));

                HttpPost beforePaymentPost = new HttpPost(BaseParameters.BOARDING_INFO_CHECK_URI);
                beforePaymentPost
                                .setEntity(new UrlEncodedFormEntity(beforePaymentFormParams, Charset.forName("utf-8")));
                beforePaymentFormParams = null;
                List<NameValuePair> beforePaymentHeaders = HttpClientUtil
                                .createRequestHeader(BaseParameters.BOARDING_INFO_CHECK_URI, currentContext);
                // 这一步挂了
                HttpClientUtilModel beforePaymentModel = HttpClientUtil.defaultRequest(beforePaymentHeaders,
                                beforePaymentPost, currentContext, true, false, null);
                LOG.info("before payment end");
                // end

                // then choose reserve
                Document payselectDoc = Jsoup.parse(beforePaymentModel.getHtml());
                List<NameValuePair> payselectFormParams = new ArrayList<>(27);
                payselectFormParams.add(new BasicNameValuePair("ctl00$cphMain$ScriptManager1",
                                "ctl00$cphMain$udpPayMode|ctl00$cphMain$rdoLocal"));
                payselectFormParams.add(new BasicNameValuePair("__EVENTTARGET", "ctl00$cphMain$rdoLocal"));
                payselectFormParams.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
                payselectFormParams.add(new BasicNameValuePair("__LASTFOCUS", ""));
                payselectFormParams.add(new BasicNameValuePair("__VIEWSTATE",
                                PageUtil.fetchElementValueAttrWithId(payselectDoc, "__VIEWSTATE")));
                payselectFormParams.add(new BasicNameValuePair("__VIEWSTATEGENERATOR",
                                PageUtil.fetchElementValueAttrWithId(payselectDoc, "__VIEWSTATEGENERATOR")));
                payselectFormParams.add(new BasicNameValuePair("__EVENTVALIDATION",
                                PageUtil.fetchElementValueAttrWithId(payselectDoc, "__EVENTVALIDATION")));
                payselectFormParams.add(new BasicNameValuePair("ctl00$inputSpSearchFront", ""));
                payselectFormParams.add(new BasicNameValuePair("ctl00$ddlLanguageSP", ""));
                payselectFormParams.add(new BasicNameValuePair("ctl00$inputSpSearch", ""));
                payselectFormParams.add(new BasicNameValuePair("ctl00$ddlLanguagePC", ""));
                payselectFormParams.add(new BasicNameValuePair("ctl00$inputPcSearch", ""));
                payselectFormParams.add(new BasicNameValuePair("ctl00$cphMain$payment", "rdoLocal"));

                BasicNameValuePair n1 = new BasicNameValuePair("ctl00$cphMain$regist", "rdoNewCard");
                BasicNameValuePair n2 = new BasicNameValuePair("ctl00$cphMain$txtCardNo", "");
                BasicNameValuePair n3 = new BasicNameValuePair("ctl00$cphMain$ddlMonth", "00");
                BasicNameValuePair n4 = new BasicNameValuePair("ctl00$cphMain$ddlYear", "0000");
                BasicNameValuePair n5 = new BasicNameValuePair("ctl00$cphMain$txtCardHolder", "");
                BasicNameValuePair n6 = new BasicNameValuePair("ctl00$cphMain$txtSecurityCode", "");

                payselectFormParams.add(n1);
                payselectFormParams.add(n2);
                payselectFormParams.add(n3);
                payselectFormParams.add(n4);
                payselectFormParams.add(n5);
                payselectFormParams.add(n6);

                payselectFormParams.add(new BasicNameValuePair("ctl00$cphMain$hdnCardNo", ""));
                payselectFormParams.add(new BasicNameValuePair("ctl00$cphMain$hdnCardNoToken", ""));
                payselectFormParams.add(new BasicNameValuePair("ctl00$cphMain$hdnResultCode", ""));
                payselectFormParams.add(new BasicNameValuePair("ctl00$cphMain$hdn3DToken", ""));
                payselectFormParams.add(new BasicNameValuePair("ctl00$cphMain$hdnHash", ""));
                payselectFormParams.add(new BasicNameValuePair("ctl00$ddlLanguageFooterPC", ""));
                payselectFormParams.add(new BasicNameValuePair("__ASYNCPOST", "true"));

                List<NameValuePair> payselectHeaders = HttpClientUtil.createRequestHeader(BaseParameters.PAY_SELECT_URI,
                                currentContext);

                HttpPost payselectPost = new HttpPost(BaseParameters.PAY_SELECT_URI);

                payselectPost.setEntity(new UrlEncodedFormEntity(payselectFormParams, Charset.forName("utf-8")));

                HttpClientUtilModel payselectModel = HttpClientUtil.defaultRequest(payselectHeaders, payselectPost,
                                currentContext, true, false, null);

                payselectFormParams.remove(0);
                payselectFormParams.remove(payselectFormParams.size() - 1);
                payselectFormParams.set(0, new BasicNameValuePair("__EVENTTARGET", "ctl00$cphMain$lbtnHdnReg"));
                subAndReplaceParams(payselectModel.getHtml(), payselectFormParams,
                                Arrays.asList("__VIEWSTATE", "__VIEWSTATEGENERATOR", "__EVENTVALIDATION"));

                payselectFormParams.remove(n1);
                payselectFormParams.remove(n2);
                payselectFormParams.remove(n3);
                payselectFormParams.remove(n4);
                payselectFormParams.remove(n5);
                payselectFormParams.remove(n6);

                HttpPost paymentConfirmPost = new HttpPost(BaseParameters.PAY_SELECT_URI);

                paymentConfirmPost.setEntity(new UrlEncodedFormEntity(payselectFormParams, Charset.forName("utf-8")));

                payselectFormParams = null;

                HttpClientUtilModel afterPaymentModel = HttpClientUtil.defaultRequest(payselectHeaders,
                                paymentConfirmPost, currentContext, true, false, null);
                LOG.info("after payment end");

                Document afterPaymentPage = Jsoup.parse(afterPaymentModel.getHtml());

                List<NameValuePair> finalConfirmFormParams = new ArrayList<>(22);
                finalConfirmFormParams.add(new BasicNameValuePair("__EVENTTARGET", "ctl00$cphMain$lbtnRegist"));
                finalConfirmFormParams.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
                finalConfirmFormParams.add(new BasicNameValuePair("__LASTFOCUS", ""));
                finalConfirmFormParams.add(new BasicNameValuePair("__VIEWSTATE",
                                PageUtil.fetchElementValueAttrWithId(afterPaymentPage, "__VIEWSTATE")));
                finalConfirmFormParams.add(new BasicNameValuePair("__VIEWSTATEGENERATOR",
                                PageUtil.fetchElementValueAttrWithId(afterPaymentPage, "__VIEWSTATEGENERATOR")));
                finalConfirmFormParams.add(new BasicNameValuePair("__VIEWSTATEENCRYPTED", ""));
                finalConfirmFormParams.add(new BasicNameValuePair("__EVENTVALIDATION",
                                PageUtil.fetchElementValueAttrWithId(afterPaymentPage, "__EVENTVALIDATION")));
                finalConfirmFormParams.add(new BasicNameValuePair("ctl00$inputSpSearchFront", ""));
                finalConfirmFormParams.add(new BasicNameValuePair("ctl00$ddlLanguageSP", ""));
                finalConfirmFormParams.add(new BasicNameValuePair("ctl00$inputSpSearch", ""));
                finalConfirmFormParams.add(new BasicNameValuePair("ctl00$ddlLanguagePC", ""));
                finalConfirmFormParams.add(new BasicNameValuePair("ctl00$inputPcSearch", ""));
                finalConfirmFormParams.add(new BasicNameValuePair("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiCode",
                                PageUtil.fetchElementValueAttrWithId(afterPaymentPage,
                                                "ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiCode")));
                finalConfirmFormParams.add(new BasicNameValuePair("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiUnit",
                                PageUtil.fetchElementValueAttrWithId(afterPaymentPage,
                                                "ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiUnit")));
                finalConfirmFormParams.add(new BasicNameValuePair("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiValue",
                                PageUtil.fetchElementValueAttrWithId(afterPaymentPage,
                                                "ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiUnit")));
                finalConfirmFormParams.add(new BasicNameValuePair(
                                "ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiDiscount",
                                PageUtil.fetchElementValueAttrWithId(afterPaymentPage,
                                                "ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiDiscount")));
                finalConfirmFormParams.add(new BasicNameValuePair(
                                "ctl00$cphMain$UC_ReserveGoodsList$hdnBaseDetailPrice",
                                PageUtil.fetchElementValueAttrWithId(afterPaymentPage,
                                                "ctl00$cphMain$UC_ReserveGoodsList$hdnBaseDetailPrice")));
                finalConfirmFormParams.add(new BasicNameValuePair("ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiPrice",
                                PageUtil.fetchElementValueAttrWithId(afterPaymentPage,
                                                "ctl00$cphMain$UC_ReserveGoodsList$hdnWaribikiPrice")));
                finalConfirmFormParams.add(new BasicNameValuePair("ctl00$cphMain$hdnCardNoToken", ""));
                finalConfirmFormParams.add(new BasicNameValuePair("ctl00$cphMain$hdnSecurityCode", ""));
                finalConfirmFormParams.add(new BasicNameValuePair("ctl00$ddlLanguageFooterPC", ""));

                List<NameValuePair> finalConfirmHeaders = HttpClientUtil
                                .createRequestHeader(BaseParameters.FINAL_CHECK_URI, currentContext);

                HttpPost finalConfirmPost = new HttpPost(BaseParameters.FINAL_CHECK_URI);
                finalConfirmPost.setEntity(new UrlEncodedFormEntity(finalConfirmFormParams, Charset.forName("utf-8")));
                finalConfirmFormParams = null;
                HttpClientUtilModel finalConfirmModel = HttpClientUtil.defaultRequest(finalConfirmHeaders,
                                finalConfirmPost, currentContext, true, false, null);

                LOG.info("after final confirm end");

                LOG.info(" use time >> {}", (System.nanoTime() - s1));

                Document finalConfirmDoc = Jsoup.parse(finalConfirmModel.getHtml());

                String completeText = PageUtil.getTextWithClassName(finalConfirmDoc,
                                BaseParameters.COMPLETE_TEXT_CLASS_NAME);
                LOG.info("success, text >>\"{}\"", completeText);
        }

        /**
         * 
         * @param html
         * @param list
         * @param needReplacedParams
         * @throws Exception
         */
        private void subAndReplaceParams(String html, List<NameValuePair> list, List<String> needReplacedParams)
                        throws Exception {
                if (StringUtils.isBlank(html) || CollectionUtils.isEmpty(needReplacedParams)) {
                        throw new Exception("require params is empty >>{html} >>{Map<String, String> map}");
                }

                String[] newArr = html.substring(html.lastIndexOf("\n")).trim().split("\\|");

                if (newArr.length < 3) {
                        throw new Exception("the length is too short after sub the html");
                }

                int size = list.size();

                int newArrLength = newArr.length;

                for (int i = 0; i < size; i++) {
                        NameValuePair np = list.get(i);
                        if (needReplacedParams.contains(np.getName())) {
                                try {
                                        for (int j = 0; j < newArrLength; j++) {
                                                if (newArr[j].equals(np.getName())) {
                                                        list.set(i, new BasicNameValuePair(np.getName(),
                                                                        newArr[j + 1]));
                                                        break;
                                                }
                                        }
                                } catch (Exception e) {
                                        LOG.error(" pair name :{} could not find", np.getName());
                                        throw new Exception("pair could not find", e);
                                }

                        }
                }
        }
}
