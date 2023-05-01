import React, { useState, useMemo } from "react";
import Header from "../Components/Header";
import InputField from "../Components/InputField";
import ApiVisualizer from "../Components/ApiVisualizer";

const Home = () => {

  const [visualizeApi, visualizeApiSet] = useState(false)
  const [drink, drinkSet] = useState("")
  const [input, inputSet] = useState("")

  return (
    <>
      <Header />
      <InputField input={input} inputSet={inputSet} visualizeApiSet={visualizeApiSet} drinkSet={drinkSet} />
      {visualizeApi && <ApiVisualizer drink={drink}/> }
    </>
  );
};

export default Home;
