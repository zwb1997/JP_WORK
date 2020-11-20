package work.service.orderservice.autoplaceorder;

import static work.util.HttpClientUtil.createRequestHeader;
import static work.util.HttpClientUtil.defaultRequest;
import static work.util.PageUtil.fetchElementValueAttrWithId;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import work.constants.BaseParameters;
import work.model.GoodModel;
import work.model.HttpClientUtilModel;
import work.model.RequireInfo;

public class AutoLoginService {

    private static final Logger LOG = LoggerFactory.getLogger(AutoLoginService.class);

    private static final List<String> LOGIN_ACTION_PARAMS_LIST = new ArrayList<>();

    private RequireInfo requireInfo;

    private HttpClientUtilModel clientUtilModel;

    private GoodModel goodModel;

    private HttpClientContext currentContext;

    static {

    }

    public AutoLoginService(RequireInfo requireInfo, GoodModel goodModel, HttpClientUtilModel clientUtilModel,
            HttpClientContext currentContext) {
        this.requireInfo = requireInfo;
        this.goodModel = goodModel;
        this.clientUtilModel = clientUtilModel;
        this.currentContext = currentContext;
    }

    public void loginService() throws Exception {

        try {
            LOG.info("begin login service");

        } catch (Exception e) {
            throw new Exception("login error", e);
        }
    }

    /**
     * 
     * 1.request to https://duty-free-japan.jp/narita/ch/index.aspx and get
     * ASP.NET_SessionIdV2
     * 
     * 
     * 2.request to under link and get visitorId
     * https://duty-free-japan.jp/image.jsp?id=13928
     * https://duty-free-japan.jp/image.jsp?id=12293
     * https://duty-free-japan.jp/image.jsp?id=2392
     * https://duty-free-japan.jp/image.jsp?id=2393
     * https://duty-free-japan.jp/image.jsp?id=2396
     * 
     * @throws Exception
     */
    // private void getVisitorId() throws Exception {

    // String img_13928 = "https://duty-free-japan.jp/image.jsp?id=13928";
    // List<NameValuePair> img_13928_headers = createDefaultRequestHeader(referer,
    // cookieStr);

    // HttpGet img_13928_get = new HttpGet(img_13928);
    // defaultRequest(img_13928_headers, img_13928_get, false);
    // }

}
