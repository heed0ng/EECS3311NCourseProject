document.addEventListener("DOMContentLoaded", function () {
    registerEventHandlers();
});

function registerEventHandlers() {
    document.getElementById("load-booking-requests-button").addEventListener("click", async function () {
        await loadBookingRequests();
    });
}

async function loadBookingRequests() {
    const consultantId = document.getElementById("consultant-id-input").value.trim();

    if (!consultantId) {
        setText("booking-request-message", "Consultant ID is required.");
        return;
    }

    try {
        const bookingRequests = await apiGet(
            "/api/consultant/" + encodeURIComponent(consultantId) + "/booking-requests"
        );

        renderBookingRequestsTable(bookingRequests, consultantId);
        setText("booking-request-message", "Pending booking requests loaded successfully.");
    } catch (error) {
        clearBookingRequestsTable();
        setText("booking-request-message", "Failed to load booking requests: " + error.message);
    }
}

function renderBookingRequestsTable(bookingRequests, consultantId) {
    const tableBodyElement = document.getElementById("booking-requests-table-body");
    clearElementChildren(tableBodyElement);

    if (!bookingRequests || bookingRequests.length === 0) {
        const rowElement = document.createElement("tr");
        const cellElement = document.createElement("td");
        cellElement.colSpan = 11;
        cellElement.textContent = "No pending booking requests found.";
        rowElement.appendChild(cellElement);
        tableBodyElement.appendChild(rowElement);
        return;
    }

    for (const currentRequest of bookingRequests) {
        const rowElement = document.createElement("tr");

        rowElement.appendChild(createCell(currentRequest.bookingId));
        rowElement.appendChild(createCell(currentRequest.clientId));
        rowElement.appendChild(createCell(currentRequest.clientName));
        rowElement.appendChild(createCell(currentRequest.offeringId));
        rowElement.appendChild(createCell(currentRequest.serviceName));
        rowElement.appendChild(createCell(currentRequest.slotId));
        rowElement.appendChild(createCell(currentRequest.startDateTime));
        rowElement.appendChild(createCell(currentRequest.endDateTime));
        rowElement.appendChild(createCell(currentRequest.bookingStatus));
        rowElement.appendChild(createCell(String(currentRequest.price)));

        const actionCell = document.createElement("td");

        const acceptButton = document.createElement("button");
        acceptButton.textContent = "Accept";
        acceptButton.addEventListener("click", async function () {
            await acceptBookingRequest(consultantId, currentRequest.bookingId);
        });
        actionCell.appendChild(acceptButton);

        const rejectButton = document.createElement("button");
        rejectButton.textContent = "Reject";
        rejectButton.style.marginLeft = "8px";
        rejectButton.addEventListener("click", async function () {
            await rejectBookingRequest(consultantId, currentRequest.bookingId);
        });
        actionCell.appendChild(rejectButton);

        rowElement.appendChild(actionCell);
        tableBodyElement.appendChild(rowElement);
    }
}

async function acceptBookingRequest(consultantId, bookingId) {
    try {
        const response = await apiPost(
            "/api/consultant/" + encodeURIComponent(consultantId) +
            "/booking-requests/" + encodeURIComponent(bookingId) +
            "/accept",
            {}
        );

        setText("booking-request-message", response.message || "Booking request accepted successfully.");
        await loadBookingRequests();
    } catch (error) {
        setText("booking-request-message", "Failed to accept booking request: " + error.message);
    }
}

async function rejectBookingRequest(consultantId, bookingId) {
    try {
        const response = await apiPost(
            "/api/consultant/" + encodeURIComponent(consultantId) +
            "/booking-requests/" + encodeURIComponent(bookingId) +
            "/reject",
            {}
        );

        setText("booking-request-message", response.message || "Booking request rejected successfully.");
        await loadBookingRequests();
    } catch (error) {
        setText("booking-request-message", "Failed to reject booking request: " + error.message);
    }
}

function clearBookingRequestsTable() {
    clearElementChildren(document.getElementById("booking-requests-table-body"));
}

function createCell(text) {
    const cellElement = document.createElement("td");
    cellElement.textContent = text == null ? "" : text;
    return cellElement;
}