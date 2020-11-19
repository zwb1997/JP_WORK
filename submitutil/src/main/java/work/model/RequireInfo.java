package work.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RequireInfo {

    @JsonProperty("em")
    private String email;

    @JsonProperty("ppw")
    private String password;

    @JsonProperty("gi")
    private String goodInfos;
    // formate -> YYYY/MM/DD
    @JsonProperty("dd")
    private String departureDate;

    // 21->terminal1
    // 23->ternimal2
    @JsonProperty("ts")
    private String terminalState;

    // airline company
    // 3U : 四川航空
    // 5J : 宿务太平洋航空
    // 5K : Hi Fly
    // 7C : 济州航空
    // 9C : Spring Airlines
    // 9W : 印度捷特航空公司
    // AA : 美国航空公司
    // AC : 加拿大航空
    // AE : 華信航空
    // AF : 法国航空公司
    // AI : 印度航空公司
    // AM : 墨西哥航空公司
    // AY : 芬兰航空公司
    // AZ : 意大利航空
    // BA : 英国航空
    // BC : Skymark Airlines
    // BI : 文莱皇家航空
    // BK : 奥凯航空
    // BR : 长荣航空
    // BX : 釜山航空
    // CA : 中国国际航空公司
    // CI : 中华航空公司
    // CX : 国泰航空
    // CZ : 中国南方航空公司
    // D7 : Air Asia X
    // DL : 达美航空公司
    // DV : SCAT Airlines
    // EK : 阿联酋航空
    // ET : 埃塞俄比亚航空
    // EY : 阿提哈德航空
    // FJ : Fiji Airways
    // FM : 上海航空公司
    // FY : Firefly
    // GA : 印尼嘉鲁达航空
    // GK : 捷星日本
    // GS : 天津航空
    // HA : 夏威夷航空公司
    // HB : 亚洲大西洋航空公司
    // HG : NIKI
    // HO : Juneyao Airlines
    // HU : 海南航空
    // HX : 香港航空
    // HY : 乌兹别克航空
    // HZ : Aurora Airlines
    // IB : 西班牙国家航空
    // IT : 台湾虎航
    // JF : JetAsia Airways
    // JJ : LATAM Airlines Brazil
    // JL : 日本航空公司
    // JQ : 捷星航空
    // JW : 香草航空
    // KA : 国泰港龙航空
    // KE : 大韩航空
    // KL : 荷兰皇家航空公司
    // KQ : 肯尼亚航空
    // LH : 德国汉莎航空
    // LJ : 真航空
    // LO : 波兰航空
    // LX : 瑞士国际航空
    // MF : 厦门航空
    // MH : 马来西亚航空
    // MM : 乐桃航空
    // MS : 埃及航空
    // MU : 中国东方航空公司
    // NH : 全日空航空
    // NQ : 全日空AIR JAPAN航空公司
    // NX : 澳门航空
    // NZ : 新西兰航空
    // OK : CSA Czech Airlines
    // OM : 蒙古航空
    // OS : Austrian Airlines
    // OZ : 韩亚航空公司
    // PG : 曼谷航空
    // PK : 巴基斯坦国际航空公司
    // PR : 菲律宾航空公司
    // PX : 新几内亚航空
    // QF : 澳洲航空
    // QR : 卡塔尔航空
    // R3 : Yakutia Airlines
    // RS : 首尔航空
    // S7 : S7航空
    // SA : 南非航空
    // SB : 喀里多尼亚航空
    // SC : 山東航空
    // SK : 北欧航空公司
    // SL : 泰国狮航
    // SQ : 新加坡航空
    // SU : 俄罗斯国际航空公司
    // TG : 泰国国际航空公司
    // TK : 土耳其航空
    // TN : 大溪地航空
    // TR : 酷航（Scoot）
    // TW : 韩国德威航空
    // UA : 美国联合航空
    // UL : 斯里兰卡航空公司
    // UO : 香港快运航空
    // VA : Virgin australia
    // VJ : Vietjet Air
    // VN : 越南航空公司
    // XJ : 泰国亚航 X
    // XT : 印尼全亚洲航空
    // XW : NokScoot
    // ZE : Eastar Jet
    // ZH : 深圳航空
    @JsonProperty("an")
    private String airlineName;

    // flight number
    @JsonProperty("fn")
    private String flightNumber;

    // archibe destination
    // 阿拉伯联合酋长国
    // 意大利
    // 印度
    // 印尼
    // 英国
    // 越南
    // Dubai
    // 奥地利
    // 加拿大
    // 夏威夷
    // 荷兰
    // 俄国
    // 希腊
    // 肯尼亚
    // 香港
    // 塞班岛
    // 斯里兰卡
    // 捷克
    // 新加坡
    // 新西兰
    // 新喀里多尼亚
    // 瑞典
    // 西班牙
    // 泰国
    // 台湾
    // 大溪地
    // 丹麦
    // 中国
    // 土耳其
    // 巴西
    // 巴布亚新几内亚
    // 斐济群岛
    // 比利时
    // 美国
    // 葡萄牙
    // 法国
    // 墨西哥
    // 蒙古
    // 匈牙利
    // 埃及
    // 帛琉
    // 澳大利亚
    // 澳门
    // 芬兰
    // 菲律宾
    // 德国
    // 关岛
    // 爱尔兰
    // 萨特阿拉伯
    // 韩国
    // 马来西亚
    // 马尔代夫
    // Swiss Confederation
    // 其他亚洲地区
    // 其他欧洲地区
    // 其他
    @JsonProperty("dt")
    private String destination;

    // search words (if have)
    @JsonProperty("sw")
    private String searchWords;

    // the receiver
    @JsonProperty("r")
    private String receiver;

    // whether change flight
    // rdoNo rdoYes
    @JsonProperty("cf")
    private String changeFlight;

    @JsonProperty("can")
    private String chkAirportName = "on";

    @JsonProperty("cag")
    private String chkAgree = "on";

    private List<GoodModel> goodModels;

    public RequireInfo() {
    }

    public RequireInfo(String departureDate, String terminalState, String airlineName, String flightNumber,
            String destination, String searchWords, String receiver, String changeFlight, String chkAirportName,
            String chkAgree, String goodInfos) {
        this.departureDate = departureDate;
        this.terminalState = terminalState;
        this.airlineName = airlineName;
        this.flightNumber = flightNumber;
        this.destination = destination;
        this.searchWords = searchWords;
        this.receiver = receiver;
        this.changeFlight = changeFlight;
        this.chkAirportName = chkAirportName;
        this.chkAgree = chkAgree;
        this.goodInfos = goodInfos;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<GoodModel> getGoodModels() {
        return goodModels;
    }

    public String getDepartureDate() {
        return this.departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getTerminalState() {
        return this.terminalState;
    }

    public void setTerminalState(String terminalState) {
        this.terminalState = terminalState;
    }

    public String getAirlineName() {
        return this.airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getFlightNumber() {
        return this.flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSearchWords() {
        return this.searchWords;
    }

    public void setSearchWords(String searchWords) {
        this.searchWords = searchWords;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getChangeFlight() {
        return this.changeFlight;
    }

    public void setChangeFlight(String changeFlight) {
        this.changeFlight = changeFlight;
    }

    public String getChkAirportName() {
        return this.chkAirportName;
    }

    public void setChkAirportName(String chkAirportName) {
        this.chkAirportName = chkAirportName;
    }

    public String getChkAgree() {
        return this.chkAgree;
    }

    public void setChkAgree(String chkAgree) {
        this.chkAgree = chkAgree;
    }

    public void setGoodInfos(String goodInfos) {
        this.goodInfos = goodInfos;
    }

    public void setGoodModels(List<GoodModel> goodModels) {
        this.goodModels = goodModels;
    }

    public String getGoodInfos() {
        return goodInfos;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        RequireInfo ri = (RequireInfo) obj;
        return new EqualsBuilder().append(receiver, ri.receiver).append(email, ri.email).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.receiver).append(this.email).toHashCode();
    }

    @Override
    public String toString() {
        return "RequireInfo{" + "email='" + email + '\'' + ", goodInfos='" + goodInfos + '\'' + ", departureDate='"
                + departureDate + '\'' + ", terminalState='" + terminalState + '\'' + ", airlineName='" + airlineName
                + '\'' + ", flightNumber='" + flightNumber + '\'' + ", destination='" + destination + '\''
                + ", searchWords='" + searchWords + '\'' + ", receiver='" + receiver + '\'' + ", changeFlight='"
                + changeFlight + '\'' + ", chkAirportName='" + chkAirportName + '\'' + ", chkAgree='" + chkAgree + '\''
                + ", goodModels=" + goodModels + '}';
    }

    public static RequireInfo generateDefualtInfo() {
        return null;
    }

}
