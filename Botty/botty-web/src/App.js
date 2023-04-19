import logo from './logo.svg';
import './App.css';
import {renderXatkitWidget} from 'xatkit-chat-widget'
import 'xatkit-chat-widget/lib/xatkit.css';
import React from 'react';
import Botty from './Components/Bot/Botty';

const App = () => {

  return (
    <div className="App">
      <header className="App-header">
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
        <Botty/>
      </header>
    </div>
  )
}


export default App;
