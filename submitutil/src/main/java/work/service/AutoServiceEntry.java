package work.service;

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

    public boolean run(List<String> goodIds) {
        boolean flag = false;
        try {
            // 1.login get cookie
            loginService.login();
            // 2.place order
            orderService.OrderServiceRun(goodIds);
        } catch (Exception e) {
            LOG.error("service error,message :{}", e.getMessage());
        }
        return flag;
    }
}
