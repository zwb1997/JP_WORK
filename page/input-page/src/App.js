import { SKEY } from "./Secret.js";
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
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Tooltip,
  IconButton,
} from "@material-ui/core";
import ErrorIcon from "@material-ui/icons/Error";
import { Message } from "element-react";
import "element-theme-default";
import DateFnsUtils from "@date-io/date-fns";
import {
  MuiPickersUtilsProvider,
  KeyboardDatePicker,
} from "@material-ui/pickers";
import moment from "moment";
import axios from "axios";
import md5 from "md5";
const SERVICE_ADDR = "http://localhost:32768/auto";
const SERVICE_ADD_PATH = "\\uD)mJ:cY\\nZ,jW}iT";
console.log(SERVICE_ADDR + SERVICE_ADD_PATH);
export default class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      showDialog: false,
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
          goodIds: "",
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
        {
          label: "terminal1",
          value: "21",
        },
        {
          label: "terminal2",
          value: "23",
        },
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
        goodIds: "",
      });
      return {
        data,
      };
    });
  };
  commonChangeHandler = (event, obj, attrName, index) => {
    const { data } = this.state;
    this.setState((prev) => {
      if (event) {
        data[index][attrName] = event.target.value;
      }
      return {
        data,
      };
    });
  };
  dateChangeHandler = (dateObj, formateStr, index) => {
    const { data } = this.state;
    this.setState((prev) => {
      data[index].departmentTime = dateObj;
      return data;
    });
  };
  submitAction = () => {
    let { data, showDialog } = this.state;
    let t = false;
    for (let i = 0; i < data.length; i++) {
      for (let name in data[i]) {
        if (name === "searchText") {
          continue;
        }
        if (
          data[i].goodIds.split(";").length > 3 ||
          data[i][name] === null ||
          data[i][name] === "" ||
          data[i][name] === undefined
        ) {
          t = true;
          break;
        }
      }
    }
    // if any unfilled params return;
    if (t) {
      Message({
        message: "存在未填项 或 单个账号持有商品号超过三个",
        type: "error",
      });
    } else {
      let postData = data.map((v, index) => {
        let obj = {
          gi: v.goodIds,
          dd: moment(v.departmentTime).format("YYYY-MM-DD").replace("-", "\\"),
          ts: v.terminal,
          an: v.airportCompany.split(":")[0].trim(),
          fn: v.flightNumber,
          dt: v.destination.split(":")[0].trim(),
          sw: v.searchText,
          r: v.receiver,
          cf: v.whetherChangeFlight,
        };
        return obj;
      });
      let curStamp = moment().unix();
      let encodingMessage = md5(SKEY + curStamp);
      postData = JSON.stringify(postData);
      axios({
        method: "POST",
        url: SERVICE_ADDR + SERVICE_ADD_PATH,
        data: postData,

        headers: {
          curStamp: curStamp,
          secret: encodingMessage,
          "content-type": "application/json",
        },
      });
    }

    //axios
    this.setState((prev) => {
      showDialog = false;
      return {
        showDialog,
      };
    });
  };
  deleteData = (index) => {
    let { data } = this.state;
    this.setState((prev) => {
      data.splice(index, 1);
      return { data };
    });
  };
  render() {
    const {
      data,
      terminals,
      airportCompanyList,
      destinationList,
      whetherChangeFlight,
      showDialog,
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
            添加用户 +
          </Button>
        </div>
        <div className="demo-text">
          测试版本 单个账号持有商品编号先别超过3个 想先抢的账号与商品编号往前排序 序号小的优先处理 商品编号先出现的先处理
        </div>
        <div className="info-container">
          {data.map((v, index) => {
            return (
              <Card key={"info_index_" + index} className="info-card">
                <CardContent>
                  <div className="info_order_index">
                    <div>序号:{index + 1}</div>
                    <div
                      className="info_delete"
                      onClick={() => {
                        this.deleteData(index);
                      }}
                    >
                      删除当前信息
                    </div>
                  </div>
                  <div className="info_part">
                    <div className="info_part-title"> 账号 </div>
                    <TextField
                      error={!v.email}
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
                    <div className="info_part-title"> 密码 </div>
                    <TextField
                      type="password"
                      error={!v.password}
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
                    <div className="info_part-title">
                      商品编号
                      <Tooltip
                        title={
                          <div className="tooltip-attention">
                            多个商品编号请以<strong>英文输入法</strong>中的
                            <strong>'&nbsp;;&nbsp;'</strong>
                            作为分隔符号,商品编号与商品数量以
                            <strong>英文输入法</strong>中的
                            <strong>'&nbsp;:&nbsp;'</strong>
                            作为分隔符. 输入示范 5201230131:1;5201230132:1
                          </div>
                        }
                      >
                        <IconButton>
                          <ErrorIcon />
                        </IconButton>
                      </Tooltip>
                    </div>
                    <TextField
                      className="good_id_count_text"
                      variant="outlined"
                      multiline
                      rows={5}
                      rowsMax={7}
                      error={!v.goodIds || v.goodIds.split(";").length > 3}
                      value={v.goodIds}
                      onChange={(event) => {
                        this.commonChangeHandler(
                          event,
                          undefined,
                          "goodIds",
                          index
                        );
                      }}
                    />
                  </div>
                  <div className="info_part">
                    <div className="info_part-title"> 出发日期 </div>
                    <MuiPickersUtilsProvider utils={DateFnsUtils}>
                      <KeyboardDatePicker
                        error={!v.departmentTime}
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
                    <div className="info_part-title"> 航站 </div>
                    <Select
                      className="info_select_style"
                      error={!v.terminal}
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
                    <div className="info_part-title"> 航空公司名称</div>
                    <Select
                      className="info_select_style"
                      error={!v.airportCompany}
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
                            <div className="menu_item_style"> {aricomv} </div>
                          </MenuItem>
                        );
                      })}
                    </Select>
                  </div>
                  <div className="info_part">
                    <div className="info_part-title"> 航班号 </div>
                    <TextField
                      error={!v.flightNumber}
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
                    <div className="info_part-title"> 目的地 </div>
                    <Select
                      error={!v.destination}
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
                            <div className="menu_item_style"> {destv} </div>
                          </MenuItem>
                        );
                      })}
                    </Select>
                  </div>
                  <div className="info_part">
                    <div className="info_part-title"> 收货人 </div>
                    <TextField
                      error={!v.receiver}
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
                    <div className="info_part-title"> 是否转机 </div>
                    {whetherChangeFlight.map((changev, insideIndex) => {
                      return (
                        <div
                          key={"whetherChangeFlight_radio_index_" + insideIndex}
                        >
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
                    <div className="info_part-title"> 查询(若有) </div>
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
              let { showDialog } = this.state;
              this.setState((prev) => {
                showDialog = true;
                return {
                  showDialog,
                };
              });
            }}
          >
            提交工作
          </Button>
          <Dialog open={showDialog}>
            <DialogTitle
              // key={"asdasd_asd_index_" + Math.random() * 100}
              id="alert-dialog-title"
            >
              {"账号与商品id核对"}
            </DialogTitle>
            <DialogContent>
              {data.map((v, index) => {
                return (
                  <div
                    key={"check_good_index_" + index}
                    className="email-goodis-check-style"
                  >
                    <div>账号:{v.email}</div>
                    <div className="email-goodis-check-style-line">
                      抢购商品id:{v.goodIds}
                    </div>
                  </div>
                );
              })}
            </DialogContent>
            <DialogActions>
              <Button
                onClick={() => {
                  let { showDialog } = this.state;
                  this.setState((prev) => {
                    showDialog = false;
                    return {
                      showDialog,
                    };
                  });
                }}
                color="primary"
              >
                不提交
              </Button>
              <Button
                onClick={() => {
                  this.submitAction();
                }}
                color="primary"
                autoFocus
              >
                提交
              </Button>
            </DialogActions>
          </Dialog>
        </div>
      </div>
    );
  }
}
