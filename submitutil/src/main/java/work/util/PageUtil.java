package work.util;

import java.util.concurrent.locks.ReentrantLock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("PageUtil")
public class PageUtil {
    private static final Logger LOG = LoggerFactory.getLogger(PageUtil.class);
    private static final ReentrantLock LOCK = new ReentrantLock();

    public Elements fetchElementWithSection(String html, String sectionRegix) {

        Elements elements = null;
        LOCK.lock();
        try {
            Element element = Jsoup.parse(html);
            elements = element.select(sectionRegix);
        } catch (Exception e) {
            LOG.error(" fetchElementWithSection error , message : ", e);
        } finally {
            LOCK.unlock();
        }
        return elements;
    }

    /**
     * use id to select element and return value by the value attribute
     * 
     * @param html
     * @param sectionRegix
     * @return
     */
    public String fetchElementValueAttrWithSection(Document doc, String id) {
        String value = "";
        LOCK.lock();
        try {
            Element ele = doc.getElementById(id);
            value = ele.attr("value");
        } catch (Exception e) {
            LOG.error(" fetchElementWithSection error ,id: {}, message : ", id, e);
        } finally {
            LOCK.unlock();
        }
        return value;
    }

}