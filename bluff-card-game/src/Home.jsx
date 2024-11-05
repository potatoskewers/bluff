import { useNavigate } from 'react-router-dom';

function Home() {
    const navigate = useNavigate();

    const startNewGame = () => {
        const newGameId = Math.random().toString(36).substr(2, 9);
        navigate(`/game/${newGameId}`);
    };

    return (
        <div>
            <h1>Welcome to the Bluff Card Game</h1>
            <p>Click the button below to start a new game.</p>
            <button onClick={startNewGame}>
                Start New Game
            </button>
        </div>
    );
}

export default Home;
