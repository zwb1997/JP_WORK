package work.service.orderservice.autoplaceorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import work.model.GoodModel;
import work.model.RequireInfo;

/**
 * order service entry
 */
public class AutoServiceEntry {
    private static final Logger LOG = LoggerFactory.getLogger(AutoServiceEntry.class);

    private AutoLoginService loginService;

    private AutoPlaceOrderService orderService;

    private GoodModel goodModel;

    private RequireInfo requireInfo;

    public AutoServiceEntry(GoodModel goodModel, RequireInfo requireInfo) {
        this.goodModel = goodModel;
        this.requireInfo = requireInfo;
        orderService = new AutoPlaceOrderService();
        loginService = new AutoLoginService();
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
