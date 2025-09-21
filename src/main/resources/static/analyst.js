document.addEventListener('DOMContentLoaded', () => {

    // --- API and Element Constants ---
    const API_BASE_URL = '/api/analyst';
    const statsTotalEl = document.getElementById('stats-total');
    const statsPendingEl = document.getElementById('stats-pending');
    const statsApprovedEl = document.getElementById('stats-approved');
    const statsRejectedEl = document.getElementById('stats-rejected');
    const statsTodayEl = document.getElementById('stats-today');
    let map; // Map instance

    // --- Core Functions ---

    // Fetch and update numerical stats
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

    // Render hazard distribution pie chart
    const renderHazardDistributionChart = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/hazard-distribution`);
            const data = await response.json();
            const ctx = document.getElementById('hazardChart').getContext('2d');
            new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: Object.keys(data),
                    datasets: [{
                        data: Object.values(data),
                        backgroundColor: [
                            'rgba(54, 162, 235, 0.6)', // Flood
                            'rgba(255, 99, 132, 0.6)', // Cyclone
                            'rgba(255, 206, 86, 0.6)', // Tsunami
                            'rgba(75, 192, 192, 0.6)'  // Earthquake
                        ]
                    }]
                },
                options: {
                    responsive: false,
                    maintainAspectRatio: false
                }
            });
        } catch (error) {
            console.error("Failed to load hazard distribution chart", error);
        }
    };

    // Initialize Leaflet map and render hotspot circles
    const renderHotspotMap = async () => {
        if (map) map.remove(); // Remove previous map
        map = L.map('heatmap').setView([20.5937, 78.9629], 5); // India view
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
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
                    radius: hotspot.intensity * 500
                }).addTo(map).bindPopup(`<b>Intensity: ${hotspot.intensity} reports</b>`);
            });
        } catch (error) {
            console.error("Could not fetch hotspots:", error);
        }
    };

    // --- Initialize Dashboard ---
    const initDashboard = async () => {
        updateDashboardStats();
        renderHazardDistributionChart();
        renderHotspotMap();
    };

    initDashboard();

});

