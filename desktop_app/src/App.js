import { withRouter } from "react-router";
import React, { Component } from "react";
import CommonMenu from "./components/meun/CommonMenu.js";
import { BrowserRouter as Router, Switch, Route, Link } from "react-router-dom";
import "./App.css";
class App extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (
      <Router>
        <div className="root-container">
          <div className="root-meun">
            <CommonMenu />
          </div>
          <div className="root-content">App!</div>
        </div>
        <Switch>
            
        </Switch>
      </Router>
    );
  }
}

export default App;
