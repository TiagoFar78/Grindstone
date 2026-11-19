export default function Disconnected() {
    return (
        <div className="center">
            <div className="hero">
                <h1>Disconnected</h1>
                <p>The connection to the server was lost.</p>
                <button className="btn" onClick={() => location.reload()}>Reconnect</button>
            </div>
        </div>
    );
}
