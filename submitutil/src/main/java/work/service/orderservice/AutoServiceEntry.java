package work.service.orderservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * order service entry
 */
@Service("AutoServiceEntry")
public class AutoServiceEntry {
    private static final Logger LOG = LoggerFactory.getLogger(AutoServiceEntry.class);

    @Autowired
    private AutoLoginService loginService;
    @Autowired
    private AutoPlaceOrderService orderService;

    public boolean run() {
        boolean flag = false;
        try {
            // 1.login get cookie
            long startTime = System.currentTimeMillis();
            loginService.loginService();
            LOG.info("login service finish");
            // 2.place order
            orderService.OrderServiceRun();
            long endTime = System.currentTimeMillis();
            LOG.info(" use time : {}", (endTime - startTime) / 1000);
            flag = true;
        } catch (Exception e) {
            LOG.error("service error,message :{}", e.getMessage());
        }
        return flag;
    }
}
