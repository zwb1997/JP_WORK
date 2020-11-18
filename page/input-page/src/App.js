import "./App.css";
import { React, Component } from "react";
import {
  Button,
  Radio,
  TextField,
  Select,
  MenuItem,
  Card,
  CardActions,
  CardContent,
} from "@material-ui/core";
import DateFnsUtils from "@date-io/date-fns";
import {
  MuiPickersUtilsProvider,
  KeyboardTimePicker,
  KeyboardDatePicker,
} from "@material-ui/pickers";
import moment from "moment";
export default class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      data: [
        {
          email: "",
          password: "",
          departmentTime: moment().add(2, "days"),
          terminal: "",
          airportCompany: "3U : 四川航空",
          flightNumber: "",
          destination: "148:阿拉伯联合酋长国",
          whetherChangeFlight: "rdoNo",
          receiver: "",
          searchText: "",
        },
      ],
      airportCompanyList: [
        "3U : 四川航空",
        "5J : 宿务太平洋航空",
        "5K : Hi Fly",
        "7C : 济州航空",
        "9C : Spring Airlines",
        "9W : 印度捷特航空公司",
        "AA : 美国航空公司",
        "AC : 加拿大航空",
        "AE : 華信航空",
        "AF : 法国航空公司",
        "AI : 印度航空公司",
        "AM : 墨西哥航空公司",
        "AY : 芬兰航空公司",
        "AZ : 意大利航空",
        "BA : 英国航空",
        "BC : Skymark Airlines",
        "BI : 文莱皇家航空",
        "BK : 奥凯航空",
        "BR : 长荣航空",
        "BX : 釜山航空",
        "CA : 中国国际航空公司",
        "CI : 中华航空公司",
        "CX : 国泰航空",
        "CZ : 中国南方航空公司",
        "D7 : Air Asia X",
        "DL : 达美航空公司",
        "DV : SCAT Airlines",
        "EK : 阿联酋航空",
        "ET : 埃塞俄比亚航空",
        "EY : 阿提哈德航空",
        "FJ : Fiji Airways",
        "FM : 上海航空公司",
        "FY : Firefly",
        "GA : 印尼嘉鲁达航空",
        "GK : 捷星日本",
        "GS : 天津航空",
        "HA : 夏威夷航空公司",
        "HB : 亚洲大西洋航空公司",
        "HG : NIKI",
        "HO : Juneyao Airlines",
        "HU : 海南航空",
        "HX : 香港航空",
        "HY : 乌兹别克航空",
        "HZ : Aurora Airlines",
        "IB : 西班牙国家航空",
        "IT : 台湾虎航",
        "JF : JetAsia Airways",
        "JJ : LATAM Airlines Brazil",
        "JL : 日本航空公司",
        "JQ : 捷星航空",
        "JW : 香草航空",
        "KA : 国泰港龙航空",
        "KE : 大韩航空",
        "KL : 荷兰皇家航空公司",
        "KQ : 肯尼亚航空",
        "LH : 德国汉莎航空",
        "LJ : 真航空",
        "LO : 波兰航空",
        "LX : 瑞士国际航空",
        "MF : 厦门航空",
        "MH : 马来西亚航空",
        "MM : 乐桃航空",
        "MS : 埃及航空",
        "MU : 中国东方航空公司",
        "NH : 全日空航空",
        "NQ : 全日空AIR JAPAN航空公司",
        "NX : 澳门航空",
        "NZ : 新西兰航空",
        "OK : CSA Czech Airlines",
        "OM : 蒙古航空",
        "OS : Austrian Airlines",
        "OZ : 韩亚航空公司",
        "PG : 曼谷航空",
        "PK : 巴基斯坦国际航空公司",
        "PR : 菲律宾航空公司",
        "PX : 新几内亚航空",
        "QF : 澳洲航空",
        "QR : 卡塔尔航空",
        "R3 : Yakutia Airlines",
        "RS : 首尔航空",
        "S7 : S7航空",
        "SA : 南非航空",
        "SB : 喀里多尼亚航空",
        "SC : 山東航空",
        "SK : 北欧航空公司",
        "SL : 泰国狮航",
        "SQ : 新加坡航空",
        "SU : 俄罗斯国际航空公司",
        "TG : 泰国国际航空公司",
        "TK : 土耳其航空",
        "TN : 大溪地航空",
        "TR : 酷航（Scoot）",
        "TW : 韩国德威航空",
        "UA : 美国联合航空",
        "UL : 斯里兰卡航空公司",
        "UO : 香港快运航空",
        "VA : Virgin australia",
        "VJ : Vietjet Air",
        "VN : 越南航空公司",
        "XJ : 泰国亚航 X",
        "XT : 印尼全亚洲航空",
        "XW : NokScoot",
        "ZE : Eastar Jet",
        "ZH : 深圳航空",
      ],
      destinationList: [
        "148:阿拉伯联合酋长国",
        "4:意大利",
        "5:印度",
        "147:印尼",
        ":请选择目的地",
        "3:英国",
        "35:越南",
        "149:Dubai",
        "8:奥地利",
        "10:加拿大",
        "27:夏威夷",
        "9:荷兰",
        "150:俄国",
        "11:希腊",
        "13:肯尼亚",
        "96:香港",
        "14:塞班岛",
        "152:斯里兰卡",
        "21:捷克",
        "16:新加坡",
        "26:新西兰",
        "25:新喀里多尼亚",
        "17:瑞典",
        "18:西班牙",
        "19:泰国",
        "43:台湾",
        "20:大溪地",
        "22:丹麦",
        "44:中国",
        "23:土耳其",
        "154:巴西",
        "29:巴布亚新几内亚",
        "31:斐济群岛",
        "36:比利时",
        "2:美国",
        "37:葡萄牙",
        "34:法国",
        "39:墨西哥",
        "41:蒙古",
        "28:匈牙利",
        "6:埃及",
        "30:帛琉",
        "7:澳大利亚",
        "151:澳门",
        "33:芬兰",
        "32:菲律宾",
        "24:德国",
        "12:关岛",
        "1:爱尔兰",
        "15:萨特阿拉伯",
        "42:韩国",
        "38:马来西亚",
        "40:马尔代夫",
        "153:Swiss Confederation",
        "45:其他亚洲地区",
        "46:其他欧洲地区",
        "57:其他",
      ],
      terminals: [
        { label: "terminal1", value: "21" },
        { label: "terminal2", value: "23" },
      ],
      whetherChangeFlight: [
        {
          label: "否",
          value: "rdoNo",
        },
        {
          label: "是",
          value: "rdoYes",
        },
      ],
    };
  }

  componentDidMount = () => {};
  componentDidUpdate = () => {
    console.log(this.state.data);
  };
  createNewAction = () => {
    const { data } = this.state;
    this.setState((prev) => {
      data.push({
        email: "",
        password: "",
        departmentTime: moment().add(2, "days"),
        terminal: "",
        airportCompany: "3U : 四川航空",
        flightNumber: "",
        destination: "148:阿拉伯联合酋长国",
        whetherChangeFlight: "rdoNo",
        receiver: "",
        searchText: "",
      });
      return { data };
    });
  };
  commonChangeHandler = (event, obj, attrName, index) => {
    const { data } = this.state;
    this.setState((prev) => {
      if (event) {
        data[index][attrName] = event.target.value;
      }
      return { data };
    });
  };
  dateChangeHandler = (dateObj, formateStr, index) => {
    const { data } = this.state;
    this.setState((prev) => {
      data[index].departmentTime = dateObj;
      return data;
    });
  };
  submitAction = () => {};
  render() {
    const {
      data,
      terminals,
      airportCompanyList,
      destinationList,
      whetherChangeFlight,
    } = this.state;
    return (
      <div className="container">
        <div className="create-action">
          <Button
            variant="contained"
            color="primary"
            onClick={() => {
              this.createNewAction();
            }}
          >
            添加用户+
          </Button>
        </div>
        <div className="info-container">
          {data.map((v, index) => {
            return (
              <Card key={"info_index_" + index} className="info-card">
                <CardContent>
                  <div className="info_part">
                    <div className="info_part-title">账号:</div>
                    <TextField
                      value={v.email}
                      onChange={(event) => {
                        this.commonChangeHandler(
                          event,
                          undefined,
                          "email",
                          index
                        );
                      }}
                    />
                  </div>
                  <div className="info_part">
                    <div className="info_part-title">密码:</div>
                    <TextField
                      type="password"
                      value={v.password}
                      onChange={(event) => {
                        this.commonChangeHandler(
                          event,
                          undefined,
                          "password",
                          index
                        );
                      }}
                    />
                  </div>
                  <div className="info_part">
                    <div className="info_part-title">出发日期:</div>
                    <MuiPickersUtilsProvider utils={DateFnsUtils}>
                      <KeyboardDatePicker
                        disableToolbar
                        variant="inline"
                        format="MM/dd/yyyy"
                        margin="normal"
                        id="date-picker-inline"
                        value={v.departmentTime}
                        onChange={(dateObj, formateStr) => {
                          this.dateChangeHandler(dateObj, formateStr, index);
                        }}
                        KeyboardButtonProps={{
                          "aria-label": "change date",
                        }}
                      />
                    </MuiPickersUtilsProvider>
                  </div>
                  <div className="info_part">
                    <div className="info_part-title">航站:</div>
                    <Select
                      className="info_select_style"
                      value={v.terminal}
                      onChange={(event, obj) => {
                        this.commonChangeHandler(event, obj, "terminal", index);
                      }}
                    >
                      {terminals.map((terminalv, insideIndex) => {
                        return (
                          <MenuItem
                            key={"termimal_index_" + insideIndex}
                            value={terminalv.value}
                          >
                            <div className="menu_item_style">
                              {terminalv.label}
                            </div>
                          </MenuItem>
                        );
                      })}
                    </Select>
                  </div>
                  <div className="info_part">
                    <div className="info_part-title">航空公司名称:</div>
                    <Select
                      className="info_select_style"
                      value={v.airportCompany}
                      onChange={(event, obj) => {
                        this.commonChangeHandler(
                          event,
                          obj,
                          "airportCompany",
                          index
                        );
                      }}
                    >
                      {airportCompanyList.map((aricomv, insideIndex) => {
                        return (
                          <MenuItem
                            key={"airportCompany_index_" + insideIndex}
                            value={aricomv}
                          >
                            <div className="menu_item_style">{aricomv}</div>
                          </MenuItem>
                        );
                      })}
                    </Select>
                  </div>
                  <div className="info_part">
                    <div className="info_part-title">航班号:</div>
                    <TextField
                      value={v.flightNumber}
                      onChange={(event) => {
                        this.commonChangeHandler(
                          event,
                          undefined,
                          "flightNumber",
                          index
                        );
                      }}
                    />
                  </div>
                  <div className="info_part">
                    <div className="info_part-title">目的地:</div>
                    <Select
                      className="info_select_style"
                      value={v.destination}
                      onChange={(event, obj) => {
                        this.commonChangeHandler(
                          event,
                          obj,
                          "destination",
                          index
                        );
                      }}
                    >
                      {destinationList.map((destv, insideIndex) => {
                        return (
                          <MenuItem
                            key={"destinationList_index_" + insideIndex}
                            value={destv}
                          >
                            <div className="menu_item_style">{destv}</div>
                          </MenuItem>
                        );
                      })}
                    </Select>
                  </div>
                  <div className="info_part">
                    <div className="info_part-title">收货人:</div>
                    <TextField
                      value={v.receiver}
                      onChange={(event) => {
                        this.commonChangeHandler(
                          event,
                          undefined,
                          "receiver",
                          index
                        );
                      }}
                    />
                  </div>
                  <div className="info_part">
                    <div className="info_part-title">是否转机:</div>
                    {whetherChangeFlight.map((changev, insideIndex) => {
                      return (
                        <div key={"whetherChangeFlight_radio_index_" + insideIndex}>
                          {changev.label}
                          <Radio
                            color="primary"
                            checked={v.whetherChangeFlight === changev.value}
                            value={changev.value}
                            onChange={(event, obj) =>
                              this.commonChangeHandler(
                                event,
                                obj,
                                "whetherChangeFlight",
                                index
                              )
                            }
                          ></Radio>
                        </div>
                      );
                    })}
                  </div>
                  <div className="info_part">
                    <div className="info_part-title">查询(若有):</div>
                    <TextField
                      variant="outlined"
                      rows={5}
                      rowsMax={7}
                      value={v.searchText}
                      onChange={(event, obj) => {
                        this.commonChangeHandler(
                          event,
                          obj,
                          "searchText",
                          index
                        );
                      }}
                    />
                  </div>
                </CardContent>
              </Card>
            );
          })}
        </div>
        <div className="submit-action">
          <Button
            variant="contained"
            color="primary"
            onClick={() => {
              this.submitAction();
            }}
          >
            提交工作
          </Button>
        </div>
      </div>
    );
  }
}
