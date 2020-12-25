import "./App.css";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import MainMenu from "./components/menus/MainMenu.js";
import TakeOrder from "./components/parts/takeorder/TakeOrder.js";
import WatchOrder from "./components/parts/watchorder/WatchOrder.js";
import Paper from "@material-ui/core/Paper";
import React, { Component } from "react";
const paper = {
  height: "calc(100% - 20px)",
  width: "calc(100% - 20px)",
  padding: "10px",
};
class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      baseParams: {
        menu_is_open: false,
      },
    };
  }
  componentDidMount() {}
  componentDidUpdate() {}
  menuActionHandler = (boolValue) => {
    const { baseParams } = this.state;
    baseParams.menu_is_open = boolValue;
    this.setState(baseParams);
  };
  render() {
    const { baseParams } = this.state;
    return (
      <div className="App">
        {/* <div className="App-header"></div> */}
        <div className="App-content">
          <Router>
            <div
              className={
                baseParams.menu_is_open ? "main_menu_open" : "main_menu_close"
              }
            >
              <MainMenu
                menuAction={baseParams.menu_is_open}
                menuActionHandler={this.menuActionHandler}
              ></MainMenu>
            </div>
            {/* <Divider
              className={
                baseParams.menu_is_open
                  ? "container_divider_close"
                  : "container_divider"
              }
            /> */}
            <div className="main_container">
              <Switch>
                <Route exact path="/">
                  <Paper style={paper}>
                    <div className="main-page">
                      please click left toolbar and choose your action!
                    </div>
                  </Paper>
                </Route>
                <Route exact path="/takeorder">
                  <TakeOrder></TakeOrder>
                </Route>
                <Route exact path="/watchorder">
                  <WatchOrder></WatchOrder>
                </Route>
              </Switch>
            </div>
          </Router>
        </div>
      </div>
    );
  }
}

export default App;
