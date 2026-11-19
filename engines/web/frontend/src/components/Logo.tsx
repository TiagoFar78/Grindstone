export function LogoMark() {
    return <img className="logo-mark" src="/../favicon.ico" alt="" />;
}

export default function Logo({ playgroundName }: { playgroundName: string }) {
    return (
        <div className="logo">
            <LogoMark />
            <div className="logo-text">Grindstone<small>{playgroundName ?? ""}</small></div>
        </div>
    );
}
