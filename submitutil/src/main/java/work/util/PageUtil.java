package work.util;

import java.util.concurrent.locks.ReentrantLock;

import org.jsoup.Jsoup;
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
        try{
            Element element = Jsoup.parse(html);
            elements = element.select(sectionRegix);
        }catch(Exception e){
            LOG.error(" fetchElementWithSection error , message : ", e);
        }finally{
            LOCK.unlock();  
        }
        return elements;
    }

}
