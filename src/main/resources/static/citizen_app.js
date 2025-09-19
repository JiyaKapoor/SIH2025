const API_BASE = "http://localhost:8080/citizen";

// ----------------------- Dynamic Auth -----------------------
let AUTH_HEADER = null; // Will be set after login

// Wrapper to add Authorization header
async function authFetch(url, options = {}) {
    if (!AUTH_HEADER) {
        throw new Error("Not logged in");
    }

    const headers =
        options.headers instanceof Headers
            ? options.headers
            : new Headers(options.headers || {});

    headers.set("Authorization", AUTH_HEADER);

    return fetch(url, { ...options, headers });
}

// ----------------------- Login -----------------------
document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();

    AUTH_HEADER = "Basic " + btoa(username + ":" + password);

    try {
        // Test login with a protected endpoint
        const res = await fetch(`${API_BASE}/dashboard-feed`, {
            method: "POST",
            headers: {
                "Authorization": AUTH_HEADER,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ maxDaysOld: 5, radiusKm: 30 })
        });

        if (res.ok) {
            document.getElementById("loginStatus").innerText = "✅ Login successful!";
            loadFeed();
            loadNearby();
        } else {
            document.getElementById("loginStatus").innerText = "❌ Invalid username or password";
            AUTH_HEADER = null;
        }
    } catch (err) {
        document.getElementById("loginStatus").innerText = "❌ Error: " + err.message;
        AUTH_HEADER = null;
    }
});

// ----------------------- Submit Hazard Report -----------------------
document.getElementById("reportForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    if (!AUTH_HEADER) {
        alert("⚠️ Please login first.");
        return;
    }

    const rawJson = document.getElementById("reportJson").value;

    let report;
    try {
        report = JSON.parse(rawJson);
    } catch (err) {
        alert("❌ Invalid JSON: " + err.message);
        return;
    }

    const formData = new FormData();
    formData.append("report", JSON.stringify(report));

    const mediaInput = document.getElementById("media");
    if (mediaInput.files.length > 0) {
        formData.append("media", mediaInput.files[0]);
    }

    try {
        const res = await authFetch(`${API_BASE}/report`, {
            method: "POST",
            body: formData
        });

        const data = await res.json();
        document.getElementById("reportResponse").innerText =
            "✅ Report submitted:\n" + JSON.stringify(data, null, 2);

    } catch (err) {
        document.getElementById("reportResponse").innerText =
            "❌ Failed: " + err.message;
    }
});

// ----------------------- Load Dashboard Feed -----------------------
async function loadFeed() {
    try {
        const res = await authFetch(`${API_BASE}/dashboard-feed`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ maxDaysOld: 5, radiusKm: 30 })
        });

        const feed = await res.json();
        const list = document.getElementById("feedList");
        list.innerHTML = "";

        feed.forEach(item => {
            const li = document.createElement("li");
            li.textContent = item.rawText;
            list.appendChild(li);
        });

    } catch (err) {
        console.error("❌ Failed to load feed:", err);
    }
}

// ----------------------- Load Nearby Reports -----------------------
async function loadNearby() {
    const lat = 12.97, lon = 77.59; // example coordinates
    try {
        const res = await authFetch(`${API_BASE}/reports/nearby?lat=${lat}&lon=${lon}&radiusKm=2`);
        const reports = await res.json();
        const list = document.getElementById("nearbyList");
        list.innerHTML = "";

        reports.forEach(r => {
            const li = document.createElement("li");
            li.textContent = `${r.eventType} at (${r.latitude}, ${r.longitude})`;
            list.appendChild(li);
        });

    } catch (err) {
        console.error("❌ Failed to load nearby reports:", err);
    }
}

