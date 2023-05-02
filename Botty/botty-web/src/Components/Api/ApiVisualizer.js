import React, { useMemo, useState, useEffect } from "react";
import ApiJSON from "./ApiJSON";

const ApiVisualizer = ({drink}) => {

    const [isFetching, isFetchingSet] = useState()
    const [drinkData, drinkDataSet] = useState({});

    useMemo(() => {
        const data = () => {
            isFetchingSet(true)
            fetch(
                `https://www.thecocktaildb.com/api/json/v1/1/search.php?s=${drink}`
              )
                .then((response) => response.json())
                .then((data) => {
                    if (data) {
                        drinkDataSet(data.drinks);
                        isFetchingSet(false);
                    } else {
                        console.log("error")
                    }
                });
            };
            data();
        }, [drink]);

        console.log(drinkData)

        useEffect(() => {
            if (drinkData) {
                if (Object.keys(drinkData).length > 0) {
                drinkData?.map((item) => {
                console.log(item.strDrink)
            })
            }
            }
        }, [drinkData])


    return (
        <div>
            {isFetching ? ( 
                <><div className="loader"/></>
            ) : (
                <>
                <ApiJSON data={drinkData}/>
                </>
            )
            }
        </div>
    )
}

export default ApiVisualizer