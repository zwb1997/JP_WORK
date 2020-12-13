import React, { Component } from "react";
import "./WatchTask.css";
import { withRouter } from "react-router-dom";

class WatchTask extends Component{
    constructor(props) {
        super(props);
        this.state = {};
      }
    
      componentDidMount() {}
      componentDidUpdate() {}
    
      render() {
        return <div>watch task!</div>;
      }
}

export default withRouter(WatchTask);