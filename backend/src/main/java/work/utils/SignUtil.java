package work.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import work.constants.BaseParameters;

@Component("SignUtil")
public class SignUtil {
    private static final Logger LOG = LoggerFactory.getLogger(SignUtil.class);

    public static boolean validateSecret(String encodes, String curStamp) {
        LOG.info("begin secret validation");
        String localEncodes = DigestUtils.md5Hex(BaseParameters.SECRET + curStamp).toLowerCase();

        return localEncodes.equals(encodes);
    }
}
