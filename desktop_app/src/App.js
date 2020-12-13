import { withRouter } from "react-router";
import React, { Component } from "react";
import CommonMenu from "./components/meun/CommonMenu.js";
import { BrowserRouter as Router, Switch, Route, Link } from "react-router-dom";
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import Paper from '@material-ui/core/Paper';
import Drawer from '@material-ui/core/Drawer';
import Button from '@material-ui/core/Button';
import AssistantIcon from '@material-ui/icons/Assistant';
import AssessmentIcon from '@material-ui/icons/Assessment';
import Divider from '@material-ui/core/Divider';
import WatchTask from "./components/contents/watchpanel/WatchTask.js";
import SetParams from "./components/contents/setconfig/SetParams.js";
import "./App.css";
const menuDetailsStyle = {
  width: '260px',
}
const menuList = [
  {
    index: "/showConfig",
    label: "set_config"
  },
  {
    index: "/showTask",
    label: "show_work"
  },
]
class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      baseParams: {
        menuIndex: 0,
        anchorAction: false,
        anchorPos: 'left',
        menuActions: ["set_config", "show_work"],
      },
    };
  }

  showAction = (is_open) => {
    let { baseParams } = this.state;
    this.setState((prev) => {
      baseParams.anchorAction = is_open;
      return {
        baseParams
      }
    })
  }
  closeAction = (clazz) => {
    let { baseParams } = this.state;
    this.setState((prev) => {
      baseParams.anchorAction = false;
      return {
        baseParams
      }
    })
  }
  renderMenuActionList = () => {
    const { baseParams } = this.state;
    return (
      <div style={menuDetailsStyle}
        onClick={this.closeAction}>
        {
          baseParams.menuActions.map((text, index) => {
            let linkTo = "";
            for (let i of menuList) {
              if (text === i.label) {
                linkTo = i.index;
                break;
              }
            }
            return (
              <div key={"menu_action_index_" + index}>
                <List>
                  <ListItem button key={index} component={Link} to={linkTo}>
                    <ListItemIcon>{index === 0 ? <AssistantIcon /> : <AssessmentIcon />}</ListItemIcon>
                    <ListItemText primary={text.replace("_", " ")} />
                  </ListItem>
                </List>
                <Divider className="list_item_divider" />
              </div>
            );
          })
        }
      </div>
    )
  }


  renderAppContent = () => {
    return (
      <div className="appContent_container">
        <div className="appContent_word">please click <strong className="strong_word">show actions</strong> button to choose page</div>
      </div>
    )
  }
  render() {
    const { baseParams } = this.state;
    return (
      <Router>
        <div className="root-container">
          {/* <div className="root-meun">
            <CommonMenu />
          </div> */}
          <div className="root-content">
            <div className="content-list-menu">
              <Button
                variant="contained" color="primary"
                onClick={() => {
                  this.showAction(true);
                }}
              >Show Actions</Button>
            </div>
            <Drawer
              anchor={baseParams.anchorPos}
              open={baseParams.anchorAction}
              onClose={(clazz) => {
                this.closeAction(clazz);
              }}
            >
              {
                this.renderMenuActionList()
              }
            </Drawer>
            <Paper className="content-list-components"
              elevation={3}>
                <Switch>
                  <Route exact path="/" children={this.renderAppContent} />
                  <Route exact path="/showConfig" children={SetParams} />
                  <Route exact path="/showTask" component={WatchTask} />
                </Switch>
            </Paper>
          </div>
        </div>

      </Router >
    );
  }
}

export default withRouter(App);
