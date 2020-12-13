import React, { Component } from "react";
import "./CommonMenu.css";

class CommonMenu extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return <div className="menu-container">
      <div className="menu-text">
        Order Tool 0.1v
      </div>
    </div>;
  }
}

export default CommonMenu;
