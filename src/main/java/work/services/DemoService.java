package work.services;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import work.models.ClientResponseModel;
import work.utils.HttpClientUtil;

@Service("DemoService")
public class DemoService {

    private static final Logger LOG = LoggerFactory.getLogger(DemoService.class);

    public void doDemo() {
        String uri = "https://www.baidu.com";
        HttpGet get = new HttpGet(uri);
        ClientResponseModel resModel = HttpClientUtil.requestSimply(null, get, true);
    }
}
