package work.service.flushpageservice;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import work.util.HttpClientUtil;
import work.util.PageUtil;

@Service
public class FlushTargetPageService implements Callable<Boolean> {
    private boolean couldBuy = false;

    private HttpClientUtil clientUtil = new HttpClientUtil();

    private PageUtil pageUtil = new PageUtil();

    @Override
    public Boolean call() throws Exception {
        // TODO flush page to detect good whether could buy
        return couldBuy;
    }
}
