import React, { useMemo, useState, useEffect } from "react";

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
                    drinkDataSet(data.drinks);
                    isFetchingSet(false);
                });
            };
            data();
        }, [drink]);

        console.log(drinkData)

        useEffect(() => {
            if (Object.keys(drinkData).length > 0) {
                drinkData?.map((item) => {
                console.log(item.strDrink)
            })
            }
        }, [drinkData])


    return (
        <div>
            {isFetching ? ( 
                <><div className="loader"/></>
            ) : (
                <>
                hello
                </>
            )
            }
        </div>
    )
}

export default ApiVisualizer