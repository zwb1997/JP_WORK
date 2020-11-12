package work.service.orderservice;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        } catch (Exception e) {
            LOG.error("service error,message :{}", e.getMessage());
        }
        return flag;
    }
}
