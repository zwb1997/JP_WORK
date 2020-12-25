import React, { Component } from "react";
import { withRouter } from "react-router-dom";
import { makeStyles, useTheme } from "@material-ui/core/styles";
import Drawer from "@material-ui/core/Drawer";
import List from "@material-ui/core/List";
import Divider from "@material-ui/core/Divider";
import IconButton from "@material-ui/core/IconButton";
import MenuIcon from "@material-ui/icons/Menu";
import ChevronLeftIcon from "@material-ui/icons/ChevronLeft";
import ChevronRightIcon from "@material-ui/icons/ChevronRight";
import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import AddBoxIcon from "@material-ui/icons/AddBox";
import AssessmentIcon from "@material-ui/icons/Assessment";
import "./MainMenu.css";

const drawerWidth = 240;
const useStyles = makeStyles((theme) => ({
  root: {
    display: "flex",
    width: "100%",
  },
  appBarShift: {
    width: `calc(100% - ${drawerWidth}px)`,
    marginLeft: drawerWidth,
    transition: theme.transitions.create(["margin", "width"], {
      easing: theme.transitions.easing.easeOut,
      duration: theme.transitions.duration.enteringScreen,
    }),
  },
  menuButton: {
    marginRight: theme.spacing(2),
  },
  hide: {
    display: "none",
  },
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
  },
  drawerPaper: {
    width: drawerWidth,
    backgroundColor: "#000",
    color: "#fff",
  },
  drawerHeader: {
    display: "flex",
    alignItems: "center",
    padding: theme.spacing(0, 1),
    // necessary for content to be below app bar
    ...theme.mixins.toolbar,
    justifyContent: "flex-end",
    color: "#fff",
  },
  content: {
    flexGrow: 1,
    padding: theme.spacing(3),
    transition: theme.transitions.create("margin", {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
    marginLeft: -drawerWidth,
  },
  contentShift: {
    transition: theme.transitions.create("margin", {
      easing: theme.transitions.easing.easeOut,
      duration: theme.transitions.duration.enteringScreen,
    }),
    marginLeft: 0,
  },
  iconStyle: {
    fontSize: "40px",
    color: "#fff",
  },
  textColor: {
    color: "#fff",
  },
}));
function PersistentDrawerLeft(props) {
  const classes = useStyles();
  const theme = useTheme();
  const [open, setOpen] = React.useState(false);
  const { actions, actionHandler, history } = props;
  const handleDrawerOpen = () => {
    setOpen(true);
    actionHandler(true);
  };

  const handleDrawerClose = (index) => {
    setOpen(false);
    actionHandler(false);
    let path = "";
    switch (index) {
      case 0:
        path = "/takeorder";
        break;
      case 1:
        path = "/watchorder";
        break;
      default:
        path = "/";
        break;
    }
    history.push(path);
  };
  const renderIcon = (index) => {
    switch (index) {
      case 0:
        return <AddBoxIcon className={classes.iconStyle} />;
      case 1:
        return <AssessmentIcon className={classes.iconStyle} />;
      default:
        return <AddBoxIcon className={classes.iconStyle} />;
    }
  };
  return (
    <div className={classes.root}>
      <IconButton
        color="inherit"
        aria-label="open drawer"
        onClick={handleDrawerOpen}
        edge="start"
        // className={clsx(classes.menuButton, open && classes.hide)}
      >
        <MenuIcon />
      </IconButton>
      <Drawer
        className={classes.drawer}
        variant="persistent"
        anchor="left"
        open={open}
        classes={{
          paper: classes.drawerPaper,
        }}
      >
        <div className={classes.drawerHeader}>
          <IconButton onClick={handleDrawerClose}>
            <span className={classes.textColor}>back</span>
            {theme.direction === "ltr" ? (
              <ChevronLeftIcon className={classes.iconStyle} />
            ) : (
              <ChevronRightIcon className={classes.iconStyle} />
            )}
          </IconButton>
        </div>
        <Divider style={{ backgroundColor: "#fff" }} />
        <List>
          {actions.map((text, index) => (
            <ListItem
              button
              key={text}
              onClick={() => {
                handleDrawerClose(index);
              }}
              className="menu-list-item"
            >
              <ListItemIcon>{renderIcon(index)}</ListItemIcon>
              <ListItemText primary={text} className="menu-text" />
            </ListItem>
          ))}
        </List>
      </Drawer>
    </div>
  );
}

class MainMenu extends Component {
  constructor(props) {
    super(props);
    this.state = {
      baseParams: {
        actions: ["takeorder", "watchorder"],
        actionHandler: this.props.menuActionHandler,
      },
    };
  }

  componentDidMount() {}
  componentDidUpdate() {}
  render() {
    const { baseParams } = this.state;
    const { history } = this.props;
    return (
      <PersistentDrawerLeft
        actions={baseParams.actions}
        actionHandler={baseParams.actionHandler}
        history={history}
      />
    );
  }
}

export default withRouter(MainMenu);
