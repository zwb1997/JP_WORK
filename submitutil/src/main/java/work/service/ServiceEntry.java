package work.service;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import work.model.GoodModel;
import work.model.RequireInfo;
import work.service.orderservice.flushpageservice.FlushPageWork;
import work.util.ThreadProvider;

@Service("ServiceEntry")
public class ServiceEntry {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceEntry.class);

    public void serviceEntry(List<RequireInfo> infoLists) throws Exception {
        // extract data
        for (RequireInfo ri : infoLists) {
            ri.setGoodModels(extractGoodModel(ri));
            beginSubmitTask(ri);
        }

    }

    /**
     * service entry
     * 
     * 1.flush page until the good could buy
     * 
     * 2.place order ,get the order number
     * 
     * @param ri
     */
    private void beginSubmitTask(RequireInfo requireInfo) {
        try {
            for (GoodModel gm : requireInfo.getGoodModels()) {
                FlushPageWork flushPageWork = new FlushPageWork(gm, requireInfo);
                ThreadProvider.submitTask(flushPageWork);
            }
        } catch (Exception e) {
            LOG.error("beginSubmitTask error , exception >>{}", e);
        }
    }

    private List<GoodModel> extractGoodModel(RequireInfo requireInfo) throws Exception {
        Assert.notNull(requireInfo, "requireInfo cannot empty");
        List<GoodModel> goodModels = new LinkedList<>();
        String val = requireInfo.getGoodInfos();
        String[] vals = val.split(";");
        for (String value : vals) {
            String[] values = value.split(":");
            if (values.length < 2) {
                throw new Exception("wrong params >>" + value);
            }
            goodModels.add(new GoodModel(values[0], Integer.valueOf(values[1])));
        }
        return goodModels;
    }
}
