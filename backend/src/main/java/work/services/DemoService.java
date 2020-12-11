package work.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import work.models.ClientResponseModel;
import work.utils.HttpClientUtil;

@Service("DemoService")
public class DemoService {

    private static final Logger LOG = LoggerFactory.getLogger(DemoService.class);

    public void doDemo() {
        List<NameValuePair> headers = new ArrayList<>();
        String uri = "https://www.fasola-shop.com/locale/change?utf8=%E2%9C%93&change_locale=zh-CN";
        headers.add(new BasicNameValuePair("Referer", "https://www.fasola-shop.com/products/SUNTORY-HIBIKI-30Y-LED"));
        while (true) {
            try {
                LOG.info("begin detect good could buy");
                HttpContext context = new BasicHttpContext();
                HttpGet get = new HttpGet(uri);
                Long st = System.nanoTime();
                ClientResponseModel responseModel = HttpClientUtil.requestWithContext(headers, get, context, true,
                        false, null);
                LOG.info("response end , use time :{} nanotime", (System.nanoTime() - st));
                LOG.info("begin parse html");
                if (StringUtils.isBlank(responseModel.getHtml())) {
                    Thread.sleep(5 * 1000);
                    LOG.info("current request html is empty, will sleep 5 sec");
                }
                Document doc = Jsoup.parse(responseModel.getHtml());
                Element e = doc.getElementById("add-to-cart-button");
                LOG.info(e.text());
            } catch (Exception e) {
                LOG.error("current request error ,message", e);
            }
        }
    }
}
