package work.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("ServiceEntry")
public class ServiceEntry {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceEntry.class);

    /**
     * service entry
     */
    public void serviceEntry() {
        // 1.flush page until the good could buy
        // 2.place order ,get the order number
    }
}
