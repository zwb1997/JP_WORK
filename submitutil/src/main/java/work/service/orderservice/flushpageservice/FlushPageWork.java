package work.service.orderservice.flushpageservice;

import static work.util.HttpClientUtil.defaultRequest;
import static work.util.PageUtil.fetchElementTextWithId;
import static work.util.PageUtil.getTextWithClassName;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import work.constants.BaseParameters;
import work.model.GoodModel;
import work.model.HttpClientUtilModel;
import work.model.RequireInfo;
import work.service.orderservice.autoplaceorder.AutoLoginService;
import work.service.orderservice.autoplaceorder.AutoPlaceOrderService;
import work.util.PageUtil;

/**
 * @author xx
 */
public class FlushPageWork implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(FlushPageWork.class);

    private static final List<NameValuePair> HEADERS = new ArrayList<>();

    private static final Random RANDOM = new Random();

    static {
        HEADERS.add(new BasicNameValuePair("user-agent", BaseParameters.USER_AGENT));
    }

    private GoodModel goodModel;

    private RequireInfo requireInfo;

    public FlushPageWork(GoodModel goodModel, RequireInfo requireInfo) {
        this.goodModel = goodModel;
        this.requireInfo = requireInfo;
    }

    // ctl00_cphMain_lblAddCart
    // detail_text
    public void run() {

        boolean flag = false;

        String goodIdStr = goodModel.getGoodId();

        String goodCount = String.valueOf(goodModel.getGoodCount());

        String uri = "";

        HttpClientUtilModel clientUtilModel = null;

        Document doc = null;

        HttpClientContext currentContext = new HttpClientContext();

        currentContext.setCookieStore(new BasicCookieStore());

        while (!flag) {
            try {
                LOG.info("begin detect gooid >>{} whether could buy", goodIdStr);
                uri = BaseParameters.GOOD_DETAIL_INFO + "?sCD=" + goodIdStr;
                HttpGet get = new HttpGet(uri);
                clientUtilModel = defaultRequest(HEADERS, get, currentContext, true, true, Arrays.asList("set-cookie"));
                doc = Jsoup.parse(clientUtilModel.getHtml());
                String val1 = getTextWithClassName(doc, "detail_text");
                String val2 = fetchElementTextWithId(doc, "ctl00_cphMain_lblAddCart");
                if (StringUtils.isNotBlank(val2) && StringUtils.isBlank(val1)) {
                    LOG.info("goodid :{} could buy now, will begin order service...", goodIdStr);
                    flag = true;
                    break;
                }
                int timeCount = RANDOM.nextInt(3);
                Thread.sleep(timeCount * 100);
                LOG.info("flush work sleep :{} milliseconds", timeCount * 100);
            } catch (Exception e) {
                LOG.error("flushPage error", e);
            }
        }

        LOG.info("flush page end good could buy now");
        clientUtilModel.setReferer(uri);
        AutoPlaceOrderService placeOrderService = new AutoPlaceOrderService(requireInfo, goodModel, clientUtilModel,
                currentContext);
        AutoLoginService loginService = new AutoLoginService(requireInfo, goodModel, clientUtilModel, currentContext);
        try {
            // directly add good to trolley
            List<NameValuePair> addGoodParamsLists = new ArrayList<>();
            addGoodParamsLists.add(new BasicNameValuePair("__EVENTTARGET", "ctl00$cphMain$LBtnAddCart"));
            addGoodParamsLists.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
            addGoodParamsLists.add(new BasicNameValuePair("__LASTFOCUS", ""));
            addGoodParamsLists.add(
                    new BasicNameValuePair("__VIEWSTATE", PageUtil.getValueAttrWithSection(doc, "id", "__VIEWSTATE")));
            addGoodParamsLists.add(new BasicNameValuePair("__VIEWSTATEGENERATOR",
                    PageUtil.getValueAttrWithSection(doc, "id", "__VIEWSTATEGENERATOR")));
            addGoodParamsLists.add(new BasicNameValuePair("__SCROLLPOSITIONX", "0"));
            addGoodParamsLists.add(new BasicNameValuePair("__SCROLLPOSITIONY", "0"));
            addGoodParamsLists.add(new BasicNameValuePair("__EVENTVALIDATION",
                    PageUtil.getValueAttrWithSection(doc, "id", "__EVENTVALIDATION")));
            addGoodParamsLists.add(new BasicNameValuePair("ctl00$inputSpSearchFront", ""));
            addGoodParamsLists.add(new BasicNameValuePair("ctl00$ddlLanguageSP", ""));
            addGoodParamsLists.add(new BasicNameValuePair("ctl00$inputSpSearch", ""));
            addGoodParamsLists.add(new BasicNameValuePair("ctl00$ddlLanguagePC", ""));
            addGoodParamsLists.add(new BasicNameValuePair("ctl00$inputPcSearch", ""));
            addGoodParamsLists
                    .add(new BasicNameValuePair("ctl00$cphMain$GoodsVariationList$ctrl0$ctl00$ddlNum", goodCount));
            addGoodParamsLists.add(new BasicNameValuePair("ctl00$ddlLanguageFooterPC", ""));

            UrlEncodedFormEntity addGoodEntity = new UrlEncodedFormEntity(addGoodParamsLists, Charset.forName("utf-8"));

            placeOrderService.addGoodAction(addGoodEntity);

            // login
            // confirm info service

        } catch (Exception e) {
            LOG.error("auto place order service error", e);
        }

    }
}
