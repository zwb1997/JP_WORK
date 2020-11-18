package work.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import work.model.RequireInfo;

@Controller
@RequestMapping("uD)mJ:cY")
public class ServiceController {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceController.class);


    @PostMapping("nZ,jW}iT\\")
    @ResponseBody
    public void ReceiveInfo(@RequestBody List<RequireInfo> params){
        LOG.info("receive info,prepare to start work");
    }
}
