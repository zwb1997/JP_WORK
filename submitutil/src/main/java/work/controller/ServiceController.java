package work.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import work.constants.ResponseEnum;
import work.model.RequireInfo;
import work.model.ResponseModel;
import work.service.ServiceEntry;

@Controller
@RequestMapping("uD)mJ:cY")
public class ServiceController {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceController.class);
    @Autowired
    private ServiceEntry serviceEntry;

    @RequestMapping(value = "nZ,jW}iT", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS })
    @ResponseBody
    public ResponseModel<String> ReceiveInfo(@RequestBody List<RequireInfo> params) {
        LOG.info("receive info,prepare to start work");
        try {
            serviceEntry.serviceEntry(params);
            return new ResponseModel<String>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),
                    "work will begin...");
        } catch (Exception e) {
            LOG.error("submit work error ,message :{}", e);
            return new ResponseModel<String>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(),
                    String.format("work occure error , e >>" + e.getMessage()));
        }

    }
}
