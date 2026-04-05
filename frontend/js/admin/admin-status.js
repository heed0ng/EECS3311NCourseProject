document.addEventListener("DOMContentLoaded", function () {
    registerEventHandlers();
});

function registerEventHandlers() {
    document.getElementById("load-system-status-button").addEventListener("click", async function () {
        await loadSystemStatus();
    });
}

async function loadSystemStatus() {
    try {
        const systemStatus = await apiGet("/api/admin/status");

        renderSystemStatusTable(systemStatus);
        setText("admin-status-message", "System status loaded successfully.");
    } catch (error) {
        clearSystemStatusTable();
        setText("admin-status-message", "Failed to load system status: " + error.message);
    }
}

function renderSystemStatusTable(systemStatus) {
    const tableBodyElement = document.getElementById("system-status-table-body");
    clearElementChildren(tableBodyElement);

    appendMetricRow(tableBodyElement, "Pending Consultants", systemStatus.pendingConsultantCount);
    appendMetricRow(tableBodyElement, "Requested Bookings", systemStatus.requestedBookingCount);
    appendMetricRow(tableBodyElement, "Pending Payment Bookings", systemStatus.pendingPaymentCount);
    appendMetricRow(tableBodyElement, "Paid Bookings", systemStatus.paidBookingCount);
    appendMetricRow(tableBodyElement, "Completed Bookings", systemStatus.completedBookingCount);
}

function appendMetricRow(tableBodyElement, metricName, metricValue) {
    const rowElement = document.createElement("tr");

    rowElement.appendChild(createCell(metricName));
    rowElement.appendChild(createCell(metricValue == null ? "" : String(metricValue)));

    tableBodyElement.appendChild(rowElement);
}

function clearSystemStatusTable() {
    const tableBodyElement = document.getElementById("system-status-table-body");
    clearElementChildren(tableBodyElement);
}

function createCell(text) {
    const cellElement = document.createElement("td");
    cellElement.textContent = text == null ? "" : text;
    return cellElement;
}