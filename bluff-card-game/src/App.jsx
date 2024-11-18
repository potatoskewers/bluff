import { useState } from 'react'
import {redirect, useNavigate} from "react-router-dom"

import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import {Link} from "react-router-dom";

function App() {
    const navigate = useNavigate();
    const [count, setCount] = useState(0)
    const [requestedId, setRequestedId] = useState("")
    const startNewGame = () => {
        const gameId = Math.random().toString(36).substr(2, 9); // Generate a unique game ID
        navigate(`/game/${gameId}`);
    }

  return (
      <>
          {/*<div>*/}
          {/*    <a href="https://vitejs.dev" target="_blank">*/}
          {/*        <img src={viteLogo} className="logo" alt="Vite logo"/>*/}
          {/*    </a>*/}
          {/*    <a href="https://react.dev" target="_blank">*/}
          {/*        <img src={reactLogo} className="logo react" alt="React logo"/>*/}
          {/*    </a>*/}
          {/*</div>*/}
          <h1>Bluff Card Game</h1>
          <div className="card">
              <button onClick={() => setCount((count) => count + 1)}>
                  New Game {count}
              </button>
          </div>
          <p>
              <button onClick={startNewGame}><code>click here to learn how to play</code> and save to test HMR</button>
          </p>
          <p>
              <input type="text" id="private-message" className="form-control" placeholder="Type Here"
                     onChange={(event) => setRequestedId(event.target.value)}/>
              <button id="send" className="btn btn-default" type="button" onClick={() => navigate(`/game/${requestedId}`)}>Send
              </button>
              //add a place where people can join just by entering the game id, and it will redirect them.
          </p>
      </>
  )

}

export default App
