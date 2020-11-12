package work.model;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

public class RequireInfo {

    // formate -> YYYY/MM/DD
    private String departureDate;
    // 21->terminal1
    // 23->ternimal2
    private String terminalState;

    public String getDepartureDate() {
        return departureDate;
    }

    public String getTerminalState() {
        return terminalState;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public void setTerminalState(String terminalState) {
        this.terminalState = terminalState;
    }

    public static RequireInfo generateDefaultInfo() {
        RequireInfo model = new RequireInfo();
        // model.setDepartureDate(DateFormatUtils.format(new Date(), "yyyy/MM/dd"));
        model.setDepartureDate("2020/11/14");
        model.setTerminalState("21");
        return model;
    }

    @Override
    public String toString() {
        return "{" + " departureDate='" + getDepartureDate() + "'" + ", terminalState='" + getTerminalState() + "'"
                + "}";
    }
}
