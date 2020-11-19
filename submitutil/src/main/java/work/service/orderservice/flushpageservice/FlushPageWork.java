package work.service.orderservice.flushpageservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import work.constants.BaseParameters;
import work.model.GoodModel;
import work.model.RequireInfo;
import work.service.orderservice.autoplaceorder.AutoServiceEntry;
import static work.util.HttpClientUtil.*;
import static work.util.PageUtil.*;

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
        while (!flag) {
            try {
                LOG.info("begin detect gooid >>{} whether could buy", goodIdStr);
                String uri = BaseParameters.GOOD_DETAIL_INFO + "?sCD=" + goodIdStr;
                HttpGet get = new HttpGet(uri);
                String html = defaultRequest(HEADERS, get, true);
                Document doc = Jsoup.parse(html);
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
                LOG.error("flushPage error , message :{}", e);
            }
        }
        LOG.info("flush page end good could buy now");
        AutoServiceEntry autoServiceEntry = new AutoServiceEntry(goodModel, requireInfo);
        autoServiceEntry.run();
    }
}
