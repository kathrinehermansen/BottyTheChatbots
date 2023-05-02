import React, { useEffect } from "react";
import 'xatkit-chat-widget/lib/xatkit.css';
import {renderXatkitWidget} from 'xatkit-chat-widget'

const Botty = () => {

    useEffect(() => {
        renderXatkitWidget({
            server: 'http://localhost:5001',
            username: "Kathrine",
            elementId: 'xatkit-chat'
        });
    }, []);

    return (
        <div id="xatkit-chat"></div>
    )
};

export default Botty;
