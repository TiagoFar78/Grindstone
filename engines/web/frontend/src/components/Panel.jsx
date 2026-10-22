export default function Panel({ title, count, children }) {
    return (
        <section className="panel">
            <h2>
                {title}
                {count != null && <span className="count">{count}</span>}
            </h2>
            {children}
        </section>
    );
}
