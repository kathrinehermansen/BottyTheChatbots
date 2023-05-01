import React from "react";
import ReactDOM from "react-dom";
import "./index.css";
import Home from "./pages/Home";

class Mainpage extends React.Component {

  render() {
    return (
      <>
      <Home/>
      </>
    );
  }
}

ReactDOM.render(<Mainpage />, document.getElementById("root"));