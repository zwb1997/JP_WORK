package work.service.orderservice.autoplaceorder;

import org.apache.hc.client5.http.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import work.model.GoodModel;
import work.model.RequireInfo;
import static work.util.HttpClientUtil.getDefauConfig;
/**
 * order service entry
 */
public class AutoServiceEntry {
    private static final Logger LOG = LoggerFactory.getLogger(AutoServiceEntry.class);

    private AutoLoginService loginService;

    private AutoPlaceOrderService orderService;

    private RequestConfig singleRequestConfig;

    public AutoServiceEntry(GoodModel goodModel, RequireInfo requireInfo) {
        singleRequestConfig = RequestConfig.copy(getDefauConfig()).setCookieSpec("default").build();
        loginService = new AutoLoginService(requireInfo,singleRequestConfig);
        orderService = new AutoPlaceOrderService(requireInfo, goodModel,singleRequestConfig);
    }

    public boolean run() {
        boolean flag = false;
        try {
            // 1.login get cookie
            loginService.loginService();
            LOG.info("login service finish");
            // 2.place order
            orderService.OrderServiceRun();
            flag = true;
        } catch (Exception e) {
            LOG.error("service error,message :{}", e.getMessage());
        }
        return flag;
    }
}
