import { useState } from "react";
import { LogoMark } from "../components/Logo";

const STORAGE_KEY = "grindstone.profile";

function loadProfile() {
    try {
        return JSON.parse(localStorage.getItem(STORAGE_KEY)) ?? {};
    } catch {
        return {};
    }
}

function saveProfile(name, emoji) {
    try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify({ name, emoji }));
    } catch {
        // storage unavailable, ignore
    }
}

export default function Login({ msg, send }) {
    const [name, setName] = useState(() => loadProfile().name ?? "");
    const [emoji, setEmoji] = useState(() => loadProfile().emoji ?? null);

    const join = () => {
        const trimmed = name.trim();
        saveProfile(trimmed, emoji);
        send({ type: "profile", name: trimmed, emoji });
    };

    return (
        <div className="center">
            <div className="hero">
                <LogoMark />
                <h1>Grindstone Playground</h1>
                <p>Pick a nickname to jump in</p>
                <input
                    className="input"
                    placeholder="Your nickname"
                    maxLength={16}
                    value={name}
                    autoFocus
                    onChange={(e) => setName(e.target.value)}
                    onKeyDown={(e) => e.key === "Enter" && join()}
                />
                <div className="label">Choose an emoji (or get a random one)</div>
                <div className="emoji-grid">
                    {msg.emojis.map((e) => (
                        <button
                            key={e}
                            type="button"
                            className={"emoji-option" + (e === emoji ? " selected" : "")}
                            onClick={() => setEmoji(emoji === e ? null : e)}
                        >
                            {e}
                        </button>
                    ))}
                </div>
                <div className="error">{msg.error ?? ""}</div>
                <button className="btn" onClick={join}>Join</button>
            </div>
        </div>
    );
}
