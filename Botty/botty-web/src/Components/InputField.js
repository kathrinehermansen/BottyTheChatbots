import React, { useMemo } from "react";
import { FaSearch } from "react-icons/fa";

const InputField = ({ input, inputSet, visualizeApiSet, drinkSet }) => {

    const buttonClick = () => {
        visualizeApiSet(true)
        drinkSet(input)
    }

    return (
    <div className="navbar">
        <div className="navbar-p">
            <input
                type="text"
                placeholder="search api..."
                value={input}
                onInput={(e) => inputSet(e.target.value)}
            ></input>
            <button onClick={() => buttonClick()}>
                <FaSearch />
            </button>
        </div>
    </div>
    )
}

export default InputField