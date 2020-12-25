import React, { Component } from "react";
import { withRouter } from "react-router-dom";
import Paper from "@material-ui/core/Paper";
import "./TakeOrder.css";
class TakeOrder extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {}
  componentDidUpdate() {}

  render() {
    return <Paper className="take-order-container">TakeOrder</Paper>;
  }
}

export default withRouter(TakeOrder);
