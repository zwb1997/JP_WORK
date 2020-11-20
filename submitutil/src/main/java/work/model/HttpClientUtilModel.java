package work.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * 
 * storage html or cookies ,whether they have value depends on whether they are
 * used
 */
public class HttpClientUtilModel {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtilModel.class);
    private List<NameValuePair> cookies;
    private String html;

    private String referer;

    public HttpClientUtilModel() {

    }

    public HttpClientUtilModel(List<NameValuePair> cookies, String html, String referer) {
        this.cookies = cookies;
        this.html = html;
        this.referer = referer;
    }

    public HttpClientUtilModel(String html, String referer) {
        this.html = html;
        this.referer = referer;
    }

    public String getHtml() {
        return html;
    }

    public List<NameValuePair> getCookies() {
        return cookies;
    }

    public String getReferer() {
        return referer;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public void setCookies(List<NameValuePair> cookies) {
        this.cookies = cookies;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public void watchClientModelState() {
        String hasHtml = StringUtils.isBlank(this.html) ? "yes" : "no";
        String hasReferer = StringUtils.isBlank(this.referer) ? "yes" : "no";
        String hasCookie = CollectionUtils.isEmpty(this.cookies) ? "yes" : "no";
        LOG.info(" whether html empty >>{}\twhether cookies empty >>{}\twhether referer empty>>{}", hasHtml, hasCookie,
                hasReferer);
    }
}
