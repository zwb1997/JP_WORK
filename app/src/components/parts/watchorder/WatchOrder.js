import React, { Component } from "react";
import { withRouter } from "react-router-dom";
import Paper from "@material-ui/core/Paper";
import "./WatchOrder.css";
class WatchOrder extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {}
  componentDidUpdate() {}

  render() {
    return <Paper className="watch-order-container">WatchOrder</Paper>;
  }
}

export default withRouter(WatchOrder);
