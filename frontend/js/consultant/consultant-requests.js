document.addEventListener("DOMContentLoaded", function () {
    registerEventHandlers();
});

function registerEventHandlers() {
    document.getElementById("load-booking-requests-button").addEventListener("click", async function () {
        await loadPendingBookingRequests();
    });
}

async function loadPendingBookingRequests() {
    const consultantId = document.getElementById("consultant-id-input").value.trim();

    if (!consultantId) {
        setText("booking-request-message", "Consultant ID is required.");
        return;
    }

    try {
        const bookingRequests = await apiGet(
            "/api/consultant/" + encodeURIComponent(consultantId) + "/booking-requests"
        );

        renderBookingRequestsTable(bookingRequests);
        setText("booking-request-message", "Pending booking requests loaded successfully.");
    } catch (error) {
        clearBookingRequestsTable();
        setText("booking-request-message", "Failed to load booking requests: " + error.message);
    }
}

function renderBookingRequestsTable(bookingRequests) {
    const tableBodyElement = document.getElementById("booking-requests-table-body");
    clearElementChildren(tableBodyElement);

    for (const currentBookingRequest of bookingRequests) {
        const rowElement = document.createElement("tr");

        rowElement.appendChild(createCell(currentBookingRequest.bookingId));
        rowElement.appendChild(createCell(currentBookingRequest.clientId));
        rowElement.appendChild(createCell(currentBookingRequest.clientName));
        rowElement.appendChild(createCell(currentBookingRequest.offeringId));
        rowElement.appendChild(createCell(currentBookingRequest.serviceName));
        rowElement.appendChild(createCell(currentBookingRequest.slotId));
        rowElement.appendChild(createCell(currentBookingRequest.startDateTime));
        rowElement.appendChild(createCell(currentBookingRequest.endDateTime));
        rowElement.appendChild(createCell(currentBookingRequest.bookingStatus));
        rowElement.appendChild(createCell(String(currentBookingRequest.price)));
        rowElement.appendChild(createActionsCell(currentBookingRequest));

        tableBodyElement.appendChild(rowElement);
    }
}

function createActionsCell(bookingRequest) {
    const cellElement = document.createElement("td");

    const acceptButtonElement = document.createElement("button");
    acceptButtonElement.textContent = "Accept";
    acceptButtonElement.addEventListener("click", async function () {
        await acceptBookingRequest(bookingRequest.bookingId);
    });

    const rejectButtonElement = document.createElement("button");
    rejectButtonElement.textContent = "Reject";
    rejectButtonElement.style.marginLeft = "8px";
    rejectButtonElement.addEventListener("click", async function () {
        await rejectBookingRequest(bookingRequest.bookingId);
    });

    cellElement.appendChild(acceptButtonElement);
    cellElement.appendChild(rejectButtonElement);

    return cellElement;
}

async function acceptBookingRequest(bookingId) {
    const consultantId = document.getElementById("consultant-id-input").value.trim();

    if (!consultantId) {
        setText("booking-request-message", "Consultant ID is required.");
        return;
    }

    try {
        const response = await apiPost(
            "/api/consultant/" + encodeURIComponent(consultantId)
            + "/booking-requests/"
            + encodeURIComponent(bookingId)
            + "/accept",
            {}
        );

        setText("booking-request-message", response.message || "Booking request accepted successfully.");
        await loadPendingBookingRequests();
    } catch (error) {
        setText("booking-request-message", "Failed to accept booking request: " + error.message);
    }
}

async function rejectBookingRequest(bookingId) {
    const consultantId = document.getElementById("consultant-id-input").value.trim();

    if (!consultantId) {
        setText("booking-request-message", "Consultant ID is required.");
        return;
    }

    try {
        const response = await apiPost(
            "/api/consultant/" + encodeURIComponent(consultantId)
            + "/booking-requests/"
            + encodeURIComponent(bookingId)
            + "/reject",
            {}
        );

        setText("booking-request-message", response.message || "Booking request rejected successfully.");
        await loadPendingBookingRequests();
    } catch (error) {
        setText("booking-request-message", "Failed to reject booking request: " + error.message);
    }
}

function clearBookingRequestsTable() {
    const tableBodyElement = document.getElementById("booking-requests-table-body");
    clearElementChildren(tableBodyElement);
}

function createCell(text) {
    const cellElement = document.createElement("td");
    cellElement.textContent = text == null ? "" : text;
    return cellElement;
}