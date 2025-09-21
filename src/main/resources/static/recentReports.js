document.addEventListener('DOMContentLoaded', async () => {
    const tableBody = document.getElementById('reportsTable');
    const API_BASE_URL = "http://localhost:8080/api/analyst"; // use full URL if opening file directly

    try {
        const response = await fetch(`${API_BASE_URL}/reports`);
        if (!response.ok) throw new Error(`HTTP error: ${response.status}`);
        const reports = await response.json();

        if (reports.length === 0) {
            tableBody.innerHTML = `
              <tr>
                <td colspan="5" class="p-4 text-center text-gray-600">No reports found.</td>
              </tr>`;
            return;
        }

        tableBody.innerHTML = reports.map(report => `
            <tr class="border-b hover:bg-gray-50">
                <td class="p-3">${report.latitude?.toFixed(4) || 'N/A'}</td>
                <td class="p-3">${report.longitude?.toFixed(4) || 'N/A'}</td>
                <td class="p-3">${report.eventType || 'N/A'}</td>
                <td class="p-3">${report.submittedBy || 'N/A'}</td>
                <td class="p-3">${report.status || 'N/A'}</td>
            </tr>
        `).join('');
    } catch (error) {
        tableBody.innerHTML = `
          <tr>
            <td colspan="5" class="p-4 text-red-600 text-center">Failed to load reports.</td>
          </tr>`;
        console.error(error);
    }
});
