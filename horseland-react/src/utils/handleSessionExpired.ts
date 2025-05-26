export function handleSessionExpired() {
    const existing = document.getElementById("session-expired-modal");
    if (existing) return; // Prevent multiple instances

    const modal = document.createElement("div");
    modal.id = "session-expired-modal";
    modal.innerHTML = `
        <div style="
            position: fixed; top: 0; left: 0; right: 0; bottom: 0;
            background: rgba(0, 0, 0, 0.5); display: flex;
            align-items: center; justify-content: center; z-index: 9999;">
            <div style="background: white; padding: 24px; border-radius: 8px; text-align: center;">
                <h2>Session Expired</h2>
                <p>Your session has expired. Please log in again.</p>
                <button id="session-expired-ok" style="margin-top: 16px; padding: 8px 16px;">OK</button>
            </div>
        </div>
    `;
    document.body.appendChild(modal);

    document.getElementById("session-expired-ok")?.addEventListener("click", () => {
        sessionStorage.clear();
        window.location.href = "/login";
    });
}
