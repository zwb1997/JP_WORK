package work.constants;

import java.util.ArrayList;
import java.util.List;

import work.model.GoodModel;

public class BaseParameters {
    // public static final String DEMO_USER = "IIIIInc@163.com";
    public static final List<GoodModel> GOOD_IDS = new ArrayList<>();
    public static final String DEMO_USER = "ddid91238@protonmail.com";
    public static final String DEMO_USER_PASS = "123456a";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36";
    public static final String LOGIN_URL = "https://duty-free-japan.jp/narita/ch/memberLogin.aspx";
    // shopping trolley
    public static final String CREATE_GOOD_LIST_URI = "https://duty-free-japan.jp/narita/ch/goodsReserveList.aspx";
    public static final String BOARDING_INFO_INPUT_URI = "https://duty-free-japan.jp/narita/ch/boardingInfoInput.aspx";
    // good id sCD
    public static final String ORIGIN = "https://duty-free-japan.jp";
    public static final String LOGIN_VIEWSTATE = "LOGIN_VIEWSTATE";

    public static final String SESSION_PARAMS_NAME = "ASP.NET_SessionIdV2";
    public static final String VISITORID_PARAMS_NAME = "visitorid";
    public static final String JSESSIONID_PARAMS_NAME = "JSESSIONID";
}
