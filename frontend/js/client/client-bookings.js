document.addEventListener("DOMContentLoaded", function () {
    registerEventHandlers();
});

function registerEventHandlers() {
    document.getElementById("load-bookings-button").addEventListener("click", async function () {
        await loadBookingHistory();
    });

    document.getElementById("check-cancellation-summary-button").addEventListener("click", async function () {
        await loadCancellationSummary();
    });
}

async function loadBookingHistory() {
    const clientId = document.getElementById("client-id-input").value.trim();

    if (!clientId) {
        setText("booking-history-message", "Client ID is required.");
        return;
    }

    try {
        const bookings = await apiGet("/api/client/" + encodeURIComponent(clientId) + "/bookings");
        renderBookingsTable(bookings);
        setText("booking-history-message", "Booking history loaded successfully.");
    } catch (error) {
        clearBookingsTable();
        setText("booking-history-message", "Failed to load booking history: " + error.message);
    }
}

function renderBookingsTable(bookings) {
    const tableBodyElement = document.getElementById("bookings-table-body");
    clearElementChildren(tableBodyElement);

    if (!bookings || bookings.length === 0) {
        const rowElement = document.createElement("tr");
        const cellElement = document.createElement("td");
        cellElement.colSpan = 9;
        cellElement.textContent = "No bookings found.";
        rowElement.appendChild(cellElement);
        tableBodyElement.appendChild(rowElement);
        return;
    }

    for (const currentBooking of bookings) {
        const rowElement = document.createElement("tr");

        rowElement.appendChild(createCell(currentBooking.bookingId));
        rowElement.appendChild(createCell(currentBooking.serviceName));
        rowElement.appendChild(createCell(currentBooking.consultantName));
        rowElement.appendChild(createCell(currentBooking.slotId));
        rowElement.appendChild(createCell(currentBooking.startDateTime));
        rowElement.appendChild(createCell(currentBooking.endDateTime));
        rowElement.appendChild(createCell(currentBooking.bookingStatus));
        rowElement.appendChild(createCell(String(currentBooking.price)));

        const actionCell = document.createElement("td");

        const summaryButton = document.createElement("button");
        summaryButton.textContent = "Check Summary";
        summaryButton.addEventListener("click", async function () {
            selectBookingForCancellationSummary(currentBooking.bookingId);
            await loadCancellationSummary();
        });
        actionCell.appendChild(summaryButton);

        const cancelButton = document.createElement("button");
        cancelButton.textContent = "Cancel";
        cancelButton.style.marginLeft = "8px";
        cancelButton.addEventListener("click", async function () {
            await cancelBooking(currentBooking.clientId, currentBooking.bookingId);
        });
        actionCell.appendChild(cancelButton);

        rowElement.appendChild(actionCell);
        tableBodyElement.appendChild(rowElement);
    }
}

function selectBookingForCancellationSummary(bookingId) {
    document.getElementById("selected-booking-id-input").value = bookingId;
}

async function loadCancellationSummary() {
    const clientId = document.getElementById("client-id-input").value.trim();
    const bookingId = document.getElementById("selected-booking-id-input").value.trim();

    if (!clientId) {
        setText("cancellation-summary-message", "Client ID is required.");
        return;
    }

    if (!bookingId) {
        setText("cancellation-summary-message", "Please click Check Summary on a booking first.");
        return;
    }

    try {
        const response = await apiGet(
            "/api/client/" + encodeURIComponent(clientId) +
            "/bookings/" + encodeURIComponent(bookingId) +
            "/cancellation-summary"
        );

        setText("cancellation-summary-message", response.summaryMessage || "Cancellation summary loaded.");
    } catch (error) {
        setText("cancellation-summary-message", "Failed to load cancellation summary: " + error.message);
    }
}

async function cancelBooking(clientId, bookingId) {
    try {
        const response = await apiPost(
            "/api/client/" + encodeURIComponent(clientId) +
            "/bookings/" + encodeURIComponent(bookingId) +
            "/cancel",
            {}
        );

        setText("booking-history-message", response.message || "Booking cancelled successfully.");

        if (document.getElementById("selected-booking-id-input").value === bookingId) {
            setText("cancellation-summary-message", "Selected booking was cancelled successfully.");
        }

        await loadBookingHistory();
    } catch (error) {
        setText("booking-history-message", "Failed to cancel booking: " + error.message);
    }
}

function clearBookingsTable() {
    clearElementChildren(document.getElementById("bookings-table-body"));
}

function createCell(text) {
    const cellElement = document.createElement("td");
    cellElement.textContent = text == null ? "" : text;
    return cellElement;
}