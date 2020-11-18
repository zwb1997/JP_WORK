package work.common.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class CommonInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(CommonInterceptor.class);

    //timestamp
    //secret key
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // TODO Auto-generated method stub
        boolean flag = false;
        String rad = request.getRemoteAddr();
        String rHost = request.getRemoteHost();
        String rUser = request.getRemoteUser();
        int rPort = request.getRemotePort();
        LOG.warn("come new request ,remote addr >>{} ,remote port >>{} ,remote host >>{} ,remote user >>{}", rad, rPort,
                rHost, rUser);
        LOG.info("begin validate sign....");
        String curStamp = request.getHeader("curstamp");
        String secret = request.getHeader("secret");
        if(StringUtils.isBlank(curStamp) || StringUtils.isBlank(secret)){
            LOG.info("lack curStamp or secret");
        }
        
        return flag;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        LOG.info("2");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        LOG.info("3");
    }

}
