document.addEventListener('DOMContentLoaded', () => {

    // --- I18n Setup ---
    const languageSwitcher = document.getElementById('language-switcher');
    const currentLang = localStorage.getItem('userLanguage') || 'en';
    languageSwitcher.value = currentLang;

    let uiMessages = {}; // To hold all translated strings

    // Event listener for language change
    languageSwitcher.addEventListener('change', (event) => {
        localStorage.setItem('userLanguage', event.target.value);
        location.reload();
    });

    // --- End I18n Setup ---


    // --- API and Element Constants ---
    const API_BASE_URL = '/official';
    const statsTotalEl = document.getElementById('stats-total');
    const statsPendingEl = document.getElementById('stats-pending');
    const statsApprovedEl = document.getElementById('stats-approved');
    const statsRejectedEl = document.getElementById('stats-rejected');
    const statsTodayEl = document.getElementById('stats-today');
    const feedContainerEl = document.getElementById('feed-container');
    let map; // To hold the map instance


    // --- Core Functions ---

    /**
     * Fetches all UI translations from the backend for the current language.
     */
    const loadTranslations = async () => {
        try {
            const response = await fetch('/api/i18n/messages', {
                headers: {
                    'Accept-Language': currentLang
                }
            });
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            uiMessages = await response.json();
        } catch (error) {
            console.error("Could not fetch translations:", error);
            // In case of error, the UI will show keys instead of crashing.
        }
    };

    /**
     * Applies all static text translations to the page.
     */
    const applyStaticTranslations = () => {
        document.getElementById('page-title').innerText = uiMessages['dashboard.title'] || 'Official Dashboard';
        document.getElementById('dashboard-title').innerText = uiMessages['dashboard.title'] || 'Official Dashboard';
        document.getElementById('hotspots-title').innerText = uiMessages['dashboard.hotspots.title'] || 'Hazard Hotspots';
        document.getElementById('feed-title').innerText = uiMessages['dashboard.feed.title'] || 'Live Report Feed';
        document.getElementById('stats-total-label').innerText = uiMessages['stats.total'] || 'Total Reports';
        document.getElementById('stats-pending-label').innerText = uiMessages['stats.pending'] || 'Pending';
        document.getElementById('stats-approved-label').innerText = uiMessages['stats.approved'] || 'Approved';
        document.getElementById('stats-rejected-label').innerText = uiMessages['stats.rejected'] || 'Rejected';
        document.getElementById('stats-today-label').innerText = uiMessages['stats.today'] || 'Reports Today';
    };

    /**
     * Fetches and updates the numerical stats on the dashboard.
     */
    const updateDashboardStats = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/stats`);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const stats = await response.json();

            statsTotalEl.innerText = stats.totalReports || 0;
            statsPendingEl.innerText = stats.pendingReports || 0;
            statsApprovedEl.innerText = stats.approvedReports || 0;
            statsRejectedEl.innerText = stats.rejectedReports || 0;
            statsTodayEl.innerText = stats.reportsToday || 0;
        } catch (error) {
            console.error("Could not fetch dashboard stats:", error);
        }
    };

    /**
     * Initializes the Leaflet map and draws hotspots from the API.
     */
    const initializeMap = async () => {
        if (map) map.remove(); // Ensure no duplicate maps are created
        map = L.map('map').setView([20.5937, 78.9629], 5);
        L.tileLayer('https://{s}.t' +
            'ile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);

        try {
            const response = await fetch(`${API_BASE_URL}/hotspots`);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const hotspots = await response.json();

            hotspots.forEach(hotspot => {
                L.circle([hotspot.latitude, hotspot.longitude], {
                    color: 'red',
                    fillColor: '#f03',
                    fillOpacity: 0.5,
                    radius: hotspot.intensity * 500 // Radius in meters
                }).addTo(map).bindPopup(`<b>Intensity: ${hotspot.intensity} reports</b>`);
            });
        } catch (error) {
            console.error("Could not fetch hotspots:", error);
        }
    };

    /**
     * Fetches reports and populates the live feed, using translated text.
     */
    const populateLiveFeed = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/reports`);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const reports = await response.json();

            feedContainerEl.innerHTML = '';

            if (reports.length === 0) {
                feedContainerEl.innerHTML = `<p class="text-gray-500">${uiMessages['feed.no_reports'] || 'No reports found.'}</p>`;
                return;
            }

            reports.sort((a, b) => new Date(b.submittedAt) - new Date(a.submittedAt));

            reports.forEach(report => {
                const reportEl = document.createElement('div');
                reportEl.className = 'p-3 bg-gray-50 rounded-lg border';

                const statusColor = {
                    'PENDING': 'text-yellow-600',
                    'APPROVED': 'text-green-600',
                    'REJECTED': 'text-red-600'
                }[report.status.toUpperCase()] || 'text-gray-600';

                const statusText = uiMessages[`report.status.${report.status.toLowerCase()}`] || report.status;

                // Buttons only visible if status is PENDING
                const actionButtons = report.status.toUpperCase() === "PENDING" ? `
        <div class="flex gap-2 mt-2">
            <button class="approve-btn bg-green-500 text-white px-2 py-1 rounded" data-id="${report.id}">
                Approve
            </button>
            <button class="reject-btn bg-red-500 text-white px-2 py-1 rounded" data-id="${report.id}">
                Reject
            </button>
        </div>
    ` : '';

                const mediaContent = report.mediaUrl
                    ? `<div class="mt-2">
         <img src="${report.mediaUrl}" alt="Report Media" class="max-h-48 rounded border"/>
       </div>`
                    : '';
                console.log("Media URL:", report.mediaUrl);


                reportEl.innerHTML = `
    <p class="font-bold">${report.eventType}</p>
    <p class="text-sm text-gray-600">${report.description || 'No description.'}</p>
    ${mediaContent}
    <div class="flex justify-between items-center mt-2 text-xs">
        <span class="font-semibold ${statusColor}">${statusText}</span>
        <span class="text-gray-500">${new Date(report.submittedAt).toLocaleString(currentLang)}</span>
    </div>
    ${actionButtons}
`;


                feedContainerEl.appendChild(reportEl);
            });

// Attach event listeners after rendering
            document.querySelectorAll('.approve-btn').forEach(btn => {
                btn.addEventListener('click', async () => {
                    const id = btn.getAttribute('data-id');
                    await fetch(`${API_BASE_URL}/reports/${id}/approve`, { method: 'PUT' });
                    populateLiveFeed(); // Refresh feed
                });
            });

            document.querySelectorAll('.reject-btn').forEach(btn => {
                btn.addEventListener('click', async () => {
                    const id = btn.getAttribute('data-id');
                    await fetch(`${API_BASE_URL}/reports/${id}/reject`, { method: 'PUT' });
                    populateLiveFeed(); // Refresh feed
                });
            });

        } catch (error) {
            console.error("Could not populate live feed:", error);
        }
    };

    /**
     * Main function to initialize the entire dashboard.
     */
    const initDashboard = async () => {
        await loadTranslations();
        applyStaticTranslations();
        initializeMap();
        updateDashboardStats();
        populateLiveFeed();
    };

    // --- Run the dashboard ---
    initDashboard();

});