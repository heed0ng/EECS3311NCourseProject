document.addEventListener("DOMContentLoaded", function () {
    registerEventHandlers();
});

function registerEventHandlers() {
    document.getElementById("load-bookings-button").addEventListener("click", async function () {
        await loadBookingHistory();
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
        setText("booking-history-message", "Failed to load booking history: " + error.message);
    }
}

function renderBookingsTable(bookings) {
    const tableBodyElement = document.getElementById("bookings-table-body");
    clearElementChildren(tableBodyElement);

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

        tableBodyElement.appendChild(rowElement);
		const actionCell = document.createElement("td");
		const cancelButton = document.createElement("button");
		cancelButton.textContent = "Cancel";

		cancelButton.addEventListener("click", async function () {
		    await cancelBooking(currentBooking.clientId, currentBooking.bookingId);
		});

		actionCell.appendChild(cancelButton);
		rowElement.appendChild(actionCell);
    }
}

async function cancelBooking(clientId, bookingId) {
    try {
        const response = await apiPost(
            "/api/client/" + encodeURIComponent(clientId) + "/bookings/" + encodeURIComponent(bookingId) + "/cancel",
            {}
        );

        setText("booking-history-message", response.message || "Booking cancelled successfully.");
        await loadBookingHistory();
    } catch (error) {
        setText("booking-history-message", "Failed to cancel booking: " + error.message);
    }
}

function createCell(text) {
    const cellElement = document.createElement("td");
    cellElement.textContent = text;
    return cellElement;
}