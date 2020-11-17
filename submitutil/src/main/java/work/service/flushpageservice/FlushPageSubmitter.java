package work.service.flushpageservice;

import java.util.concurrent.Callable;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xx
 */
public class FlushPageSubmitter implements Callable<Boolean> {
    private static final Logger LOG = LoggerFactory.getLogger(FlushPageSubmitter.class);
    private FlushPageWork flushPageWork = null;

    public FlushPageSubmitter(String targetGoodId) {
        flushPageWork = new FlushPageWork(targetGoodId);
    }

    @Override
    public Boolean call() throws Exception {
        if (ObjectUtils.isNotEmpty(flushPageWork)) {
            LOG.info(" begin submit flush page service ");
            return flushPageWork.flushPage();
        } else {
            throw new Exception("flushPageWork is empty");
        }
    }
}
