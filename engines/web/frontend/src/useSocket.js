import { useCallback, useEffect, useRef, useState } from "react";

export function useSocket() {
    const socketRef = useRef(null);
    const toastId = useRef(0);

    const [status, setStatus] = useState("connecting"); // connecting | open | closed
    const [view, setView] = useState(null);             // last nickname / lobby / game message
    const [board, setBoard] = useState(null);
    const [chat, setChat] = useState([]);
    const [toasts, setToasts] = useState([]);

    useEffect(() => {
        let active = true;
        const socket = new WebSocket(import.meta.env.VITE_WS_URL);
        socketRef.current = socket;

        socket.onopen = () => active && setStatus("open");
        socket.onclose = () => active && setStatus("closed");
        socket.onmessage = (event) => {
            if (!active) return;
            const msg = JSON.parse(event.data);
            switch (msg.type) {
                case "nickname":
                case "lobby":
                    setView(msg);
                    break;
                case "game":
                    setView(msg);
                    setBoard(null);
                    setChat([]);
                    break;
                case "board":
                    setBoard(msg);
                    break;
                case "chat":
                    setChat((lines) => [...lines, msg.text]);
                    break;
                case "error": {
                    const id = ++toastId.current;
                    setToasts((t) => [...t, { id, text: msg.text }]);
                    setTimeout(() => setToasts((t) => t.filter((x) => x.id !== id)), 3500);
                    break;
                }
                default:
                    console.log(msg);
            }
        };

        return () => {
            active = false;
            socket.close();
        };
    }, []);

    const send = useCallback((message) => {
        socketRef.current?.send(JSON.stringify(message));
    }, []);

    return { status, view, board, chat, toasts, send };
}
