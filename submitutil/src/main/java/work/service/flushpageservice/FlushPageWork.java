package work.service.flushpageservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import work.constants.BaseParameters;
import work.util.HttpClientUtil;
import work.util.PageUtil;

public class FlushPageWork {
    private static final Logger LOG = LoggerFactory.getLogger(FlushPageWork.class);
    private static final List<NameValuePair> HEADERS = new ArrayList<>();

    private static final Random RANDOM = new Random();
    static {
        HEADERS.add(new BasicNameValuePair("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.193 Safari/537.36"));
    }
    private String goodId;
    @Autowired
    private HttpClientUtil clientUtil;
    @Autowired
    private PageUtil pageUtil;

    public FlushPageWork(String goodId) {
        this.goodId = goodId;
    }

    // ctl00_cphMain_lblAddCart
    // detail_text
    public boolean flushPage() {
        boolean flag = false;
        while (!flag) {
            try {
                LOG.info("begin detect gooid >>{} whether could buy", goodId);
                String uri = BaseParameters.GOOD_DETAIL_INFO + "?sCD=" + this.goodId;
                HttpGet get = new HttpGet(uri);
                String html = clientUtil.defaultRequest(HEADERS, get, true);
                Document doc = Jsoup.parse(html);
                String val1 = pageUtil.getTextWithClassName(doc, "detail_text");
                String val2 = pageUtil.fetchElementTextWithId(doc, "ctl00_cphMain_lblAddCart");
                if (StringUtils.isNotBlank(val2) && StringUtils.isBlank(val1)) {
                    LOG.info("goodid :{} could buy now, will begin order service...", this.goodId);
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
        LOG.info("flushPage end");
        return flag;
    }
}
