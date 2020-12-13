import React, { Component } from "react";
import "./SetParams.css";
import { withRouter } from "react-router-dom";
import TextField from "@material-ui/core/TextField";
import Paper from "@material-ui/core/Paper";
import {
  MuiPickersUtilsProvider,
  KeyboardDatePicker,
} from "@material-ui/pickers";
import MomentUtils from "@date-io/moment";
// 名字
//邮件地址
//现在的居住地址
//国际
//电话
//出发日期 、、今天之后的第三天可以选择，范围：今天后第三天~ +28 天
//候机楼及航空公司 -> 最多三个候机楼 每个候机楼数据不一样
//航班号
//目的地 区域 途径地 目的地

class SetParams extends Component {
  constructor(props) {
    super(props);
    this.state = {
      baseParams: {
        accountName: {
          value: "",
        },
        accoutEmail: {
          value: "",
        },
        activeLiveArea: {
          activeLiveValue: "china_locale",
          nationalityValue: "china_locale",
          list: [
            {
              label: "日本",
              value: "japan",
            },
            {
              label: "韩国",
              value: "south_korea_locale",
            },
            {
              label: "中国",
              value: "china_locale",
            },
            {
              label: "台湾",
              value: "taiwan_locale",
            },
            {
              label: "香港",
              value: "hong_kong_locale",
            },
            {
              label: "泰国",
              value: "thailand_locale",
            },
            {
              label: "印度",
              value: "india_locale",
            },
            {
              label: "印度尼西亚",
              value: "indonesia_locale",
            },
            {
              label: "新加坡",
              value: "singapore_locale",
            },
            {
              label: "马来西亚",
              value: "malaysia_locale",
            },
            {
              label: "菲律宾",
              value: "philippines_locale",
            },
            {
              label: "大洋洲",
              value: "oceania_locale",
            },
            {
              label: "美国",
              value: "us_locale",
            },
            {
              label: "加拿大",
              value: "canada_locale",
            },
            {
              label: "欧洲",
              value: "europe_locale",
            },
            {
              label: "其它",
              value: "other_locales",
            },
          ],
        },
        phoneNumber: {
          value: "",
        },
        departmentDate: {
          value: null,
        },
        airComAndTerminal: {
          valueNo1: "",
          valueNo2: "",
          valueNo3: "",
          listNo1: [
            { value: "39", label: "9W　Jet Airways" },
            { value: "19", label: "AB　airberlin" },
            { value: "14", label: "AC　Air Canada" },
            { value: "26", label: "AF　Air France" },
            { value: "3", label: "AM　Aeromexico" },
            { value: "8", label: "AZ　Alitalia" },
            { value: "111", label: "BC　Skymark Airlines" },
            { value: "107", label: "BI　Royal Brunei Airlines" },
            { value: "24", label: "BR　EVA Air" },
            { value: "18", label: "BX　AIR BUSAN" },
            { value: "58", label: "CA　Air China" },
            { value: "60", label: "CZ　China Southern Airlines" },
            { value: "61", label: "DL　Delta Air Lines" },
            { value: "22", label: "ET　Ethiopian Airlines" },
            { value: "23", label: "EY　Etihad Airways" },
            { value: "30", label: "GA　Garuda Indonesia" },
            { value: "102", label: "HX　Hong Kong Airlines" },
            { value: "11", label: "HY　Uzbekistan Airways" },
            { value: "28", label: "HZ　Aurora Airlines" },
            { value: "52", label: "KE　Korean Air" },
            { value: "33", label: "KL　KLM - Royal Dutch Airlines" },
            { value: "34", label: "KQ　KENYA AIRWAYS" },
            { value: "83", label: "LH　Lufthansa German Airlines" },
            { value: "91", label: "LJ　Jin Air" },
            { value: "86", label: "LO　LOT Polish Airlines" },
            { value: "42", label: "LX　Swiss International Air Lines" },
            { value: "113", label: "LY　EL AL Israel Airlines" },
            { value: "84", label: "MF　Xiamen Airlines" },
            { value: "116", label: "MM　Peach Aviation" },
            { value: "20", label: "MS　EGYPTAIR" },
            { value: "48", label: "NH　All Nippon Airways" },
            { value: "16", label: "NQ　Air Japan" },
            { value: "66", label: "NZ　Air New Zealand" },
            { value: "55", label: "OK　Czech Airlines" },
            { value: "78", label: "OM　MIAT Mongolian Airlines" },
            { value: "27", label: "OS　Austrian Airlines" },
            { value: "4", label: "OZ　Asiana Airlines" },
            { value: "93", label: "R3　Yakutia Airlines" },
            { value: "98", label: "RS　Air Seoul" },
            { value: "79", label: "SA　South African Airways" },
            { value: "15", label: "SB　Aircalin" },
            { value: "35", label: "SC　SHANDONG AIRLINES" },
            { value: "43", label: "SK　Scandinavian Airlines System" },
            { value: "104", label: "SL　Thai Lion Air" },
            { value: "36", label: "SQ　Singapore Airlines" },
            { value: "2", label: "SU　Aeroflot Russian Airlines" },
            { value: "53", label: "TG　Thai Airways International" },
            { value: "49", label: "TK　Turkish Airlines" },
            { value: "80", label: "UA　United Airlines" },
            { value: "10", label: "VA　Virgin Australia" },
            { value: "74", label: "VN　Vietnam Airlines" },
            { value: "114", label: "ZG　ZIPAIR" },
            { value: "37", label: "ZH　Shenzhen Airlines" },
          ],
          listNo2: [
            { value: "7", label: "AA　American Airlines" },
            { value: "100", label: "AE　Mandarin Airlines" },
            { value: "13", label: "AI　Air India" },
            { value: "72", label: "AY　Finnair" },
            { value: "73", label: "BA　British Airways" },
            { value: "57", label: "CI　China Airlines" },
            { value: "32", label: "CX　Cathay Pacific Airways" },
            { value: "12", label: "D7　AirAsia X" },
            { value: "6", label: "DM　Asian Air" },
            { value: "110", label: "DV　SCAT Airlines" },
            { value: "25", label: "EK　Emirates" },
            { value: "101", label: "FJ　Fiji Airways" },
            { value: "96", label: "FY　Firefly" },
            { value: "62", label: "GE　TransAsia Airways" },
            { value: "99", label: "HA　Hawaiian Airlines" },
            { value: "5", label: "HB　Asia Atlantic Airlines" },
            { value: "109", label: "HO　Juneyao Airlines" },
            { value: "85", label: "HU　Hainan Airlines" },
            { value: "95", label: "IB　Iberia" },
            { value: "51", label: "IT　Tigerair Taiwan" },
            { value: "38", label: "JF　Jet Asia Airways" },
            { value: "54", label: "JJ　TAM Airlines" },
            { value: "64", label: "JL　Japan Airlines" },
            { value: "117", label: "JX　STARLUX Airlines" },
            { value: "82", label: "LA　LAN Airlines" },
            { value: "90", label: "LV　MEGA Maldives Airlines" },
            { value: "77", label: "MH　Malaysia Airlines" },
            { value: "59", label: "MU　China Eastern Airlines" },
            { value: "76", label: "NX　AIR MACAU" },
            { value: "103", label: "OD　Malindo Air" },
            { value: "68", label: "PG　Bangkok Airways" },
            { value: "69", label: "PK　Pakistan International Airlines" },
            { value: "71", label: "PR　Philippine Airlines" },
            { value: "65", label: "PX　Air Niugini" },
            { value: "31", label: "QF　Qantas Airways" },
            { value: "115", label: "QH　Bamboo Airways" },
            { value: "29", label: "QR　Qatar Airways" },
            { value: "112", label: "RA　Nepal Airlines" },
            { value: "21", label: "S7　S7 Airlines" },
            { value: "17", label: "TN　Air Tahiti Nui" },
            { value: "44", label: "TR　Scoot" },
            { value: "89", label: "TW　t'way Airlines" },
            { value: "46", label: "UL　SriLankan Airlines" },
            { value: "75", label: "UO　HK Express" },
            { value: "81", label: "US　US Airways" },
            { value: "105", label: "VJ　Vietjet Air" },
            { value: "50", label: "XJ　Thai AirAsia X" },
            { value: "97", label: "XT　Indonesia AirAsia X" },
            { value: "106", label: "XW　NOKSCOOT" },
            { value: "9", label: "ZE　EASTAR JET" },
          ],
          listNo3: [
            { value: "88", label: "9C　Spring Japan" },
            { value: "41", label: "GK　Jetstar Japan" },
            { value: "40", label: "JQ　Jetstar Airways" },
            { value: "67", label: "JW　Vanilla Air" },
          ],
        },
      },
      activeList: [{}, {}],
    };
  }

  componentDidMount() {}
  componentDidUpdate() {}
  addActive = () => {
    const { activeList } = this.state;
    this.setState((prev) => {
      activeList.push({});
      return { activeList };
    });
  };
  changeHandler = (event, value, attrName) => {};
  render() {
    const { baseParams, activeList } = this.state;
    baseParams.airComAndTerminal.listNo1.sort((a, b) => {
      return a.value - b.value;
    });
    baseParams.airComAndTerminal.listNo2.sort((a, b) => {
      return a.value - b.value;
    });
    baseParams.airComAndTerminal.listNo3.sort((a, b) => {
      return a.value - b.value;
    });
    return (
      <div className="setparams-container">
        {activeList.map((v, index) => {
          return (
            <Paper elevation={5} className="paper-container">
              <div className="params-style">
                <TextField
                  label="护照姓名"
                  value={baseParams.accountName.value}
                  placeholder="输入用户名字(护照上名字全称)"
                  onChange={(event, value) => {
                    this.changeHandler(event, value, "accountName");
                  }}
                />
              </div>
              <div className="params-style">
                <TextField
                  label="邮箱"
                  value={baseParams.accoutEmail.value}
                  placeholder="输入邮箱"
                  onChange={(event, value) => {
                    this.changeHandler(event, value, "accoutEmail");
                  }}
                />
              </div>
              <div className="params-style">
                <TextField
                  label="居住地址"
                  value={baseParams.activeLiveArea.value}
                  placeholder=""
                  onChange={(event, value) => {
                    this.changeHandler(event, value, "accoutEmail");
                  }}
                />
              </div>
              <div className="params-style">
                <TextField
                  label="国际"
                  value={baseParams.activeLiveArea.value}
                  placeholder=""
                  onChange={(event, value) => {
                    this.changeHandler(event, value, "accoutEmail");
                  }}
                />
              </div>
              <div className="params-style">
                <TextField
                  label="手机号"
                  value={baseParams.activeLiveArea.value}
                  placeholder=""
                  onChange={(event, value) => {
                    this.changeHandler(event, value, "accoutEmail");
                  }}
                />
              </div>

              <div className="params-style">
                <MuiPickersUtilsProvider utils={MomentUtils}>
                  <KeyboardDatePicker
                    disableToolbar
                    variant="inline"
                    format="MM/dd/yyyy"
                    margin="normal"
                    id="date-picker-inline"
                    label="Date picker inline"
                    value={baseParams.departmentDate.value}
                    onChange={(event, value) => {
                      this.changeHandler(event, value, "departmentDate");
                    }}
                    KeyboardButtonProps={{
                      "aria-label": "change date",
                    }}
                  />
                </MuiPickersUtilsProvider>
              </div>

              <div className="params-style"></div>
            </Paper>
          );
        })}
      </div>
    );
  }
}
export default withRouter(SetParams);
