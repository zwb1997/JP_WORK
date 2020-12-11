package work.models;

import java.util.Date;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultNamedFactory implements ThreadFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultNamedFactory.class);
    private static final String WORK_NAME = "AUTO-SERVICE-WORKER";
    private static final AtomicInteger WORKER_ID = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable r) {
        int id = WORKER_ID.incrementAndGet();
        String fullTaskName = WORK_NAME + "-" + id;
        LOG.info(" time :{} , create new worker , id >>{}", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"),
                id);
        return new Thread(r, fullTaskName);
    }
}
