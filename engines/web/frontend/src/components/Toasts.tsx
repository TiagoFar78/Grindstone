export type Toast = { id: number; text: string };

export default function Toasts({ toasts }: { toasts: Toast[]}) {
    return (
        <div id="toasts">
            {toasts.map((t) => <div key={t.id} className="toast">{t.text}</div>)}
        </div>
    );
}
