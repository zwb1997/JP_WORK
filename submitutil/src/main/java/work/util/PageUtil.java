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

    public static Elements fetchElementWithSection(String html, String sectionRegix) {

        Elements elements = null;
        try {
            Element element = Jsoup.parse(html);
            elements = element.select(sectionRegix);
        } catch (Exception e) {
            LOG.error("fetchElementWithSection error , message >>{}", e);
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
    public static String fetchElementValueAttrWithId(Document doc, String id) {
        String value = "";
        try {
            Element ele = doc.getElementById(id);
            value = ele.attr("value");
        } catch (Exception e) {
            LOG.error("fetchElementValueAttrWithId error ,id: {}, message >>{}", id, e);
        } 
        return value;
    }

    /**
     * use id to select element and return text in the element
     * 
     * @param html
     * @param sectionRegix
     * @return
     */
    public static String fetchElementTextWithId(Document doc, String id) {
        String value = "";
        try {
            Element ele = doc.getElementById(id);
            value = ele.text();
        } catch (Exception e) {
            LOG.error("fetchElementValueAttrWithId error ,id: {}, message >>{}", id, e);
        } 
        return value;
    }

    /**
     * get first element by attr name and return the value corresponding to the
     * value attribute;
     * 
     * @param doc
     * @param name
     * @return
     */
    public static String getValueAttrWithSection(Document doc, String name, String value) {
        String result = "";
        try {
            Element ele = doc.getElementsByAttributeValue(name, value).first();
            result = ele.attr("value");
        } catch (Exception e) {
            LOG.error("getValueAttrWithSection error ,name: {},value :{} message >>{}", name, value, e);
        } 
        return result;
    }

    /**
     * return text value get by className
     * 
     * @param doc
     * @param className
     * @return text value
     */
    public static String getTextWithClassName(Document doc, String className) {
        String result = "";
        try {
            Element ele = doc.getElementsByClass(className).first();
            result = ele.text();
        } catch (Exception e) {
            LOG.error("getTextWithClassName error ,className:{} message >>{}", className, e);
        } 
        return result;
    }

}
