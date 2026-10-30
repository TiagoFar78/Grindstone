import { useState } from "react";
import { LogoMark } from "../components/Logo";
import { EMOJIS } from "../emojis";

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

export default function CreateProfile({ onJoin, error }) {
    const [name, setName] = useState(() => loadProfile().name ?? "");
    const [emoji, setEmoji] = useState(() => {
        const saved = loadProfile().emoji;
        return saved in EMOJIS ? saved : null;
    });

    const join = () => {
        const trimmed = name.trim();
        if (!trimmed) return;
        saveProfile(trimmed, emoji);
        onJoin(trimmed, emoji);
    };

    return (<div className="center">
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
                    {Object.entries(EMOJIS).map(([code, char]) => (
                        <button
                            key={code}
                            type="button"
                            className={"emoji-option" + (code === emoji ? " selected" : "")}
                            onClick={() => setEmoji(emoji === code ? null : code)}
                        >
                            {char}
                        </button>
                    ))}
                </div>
                <div className="error">{error ?? ""}</div>
                <button className="btn" onClick={join}>Join</button>
            </div>
        </div>
    );
}
