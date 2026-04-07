document.addEventListener("DOMContentLoaded", function () {
    registerEventHandlers();
});

function registerEventHandlers() {
    document.getElementById("load-schedule-button").addEventListener("click", async function () {
        await loadConsultantSchedule();
    });
}

async function loadConsultantSchedule() {
    const consultantId = document.getElementById("consultant-id-input").value.trim();

    if (!consultantId) {
        setText("schedule-message", "Consultant ID is required.");
        return;
    }

    try {
        const scheduleEntries = await apiGet(
            "/api/consultant/" + encodeURIComponent(consultantId) + "/schedule"
        );

        renderScheduleTable(scheduleEntries);
        setText("schedule-message", "Consultant schedule loaded successfully.");
    } catch (error) {
        clearScheduleTable();
        setText("schedule-message", "Failed to load consultant schedule: " + error.message);
    }
}

function renderScheduleTable(scheduleEntries) {
    const tableBodyElement = document.getElementById("schedule-table-body");
    clearElementChildren(tableBodyElement);

    for (const currentEntry of scheduleEntries) {
        const rowElement = document.createElement("tr");

        rowElement.appendChild(createCell(currentEntry.bookingId));
        rowElement.appendChild(createCell(currentEntry.clientId));
        rowElement.appendChild(createCell(currentEntry.clientName));
        rowElement.appendChild(createCell(currentEntry.offeringId));
        rowElement.appendChild(createCell(currentEntry.serviceName));
        rowElement.appendChild(createCell(currentEntry.slotId));
        rowElement.appendChild(createCell(currentEntry.startDateTime));
        rowElement.appendChild(createCell(currentEntry.endDateTime));
        rowElement.appendChild(createCell(currentEntry.bookingStatus));
        rowElement.appendChild(createCell(String(currentEntry.price)));
        rowElement.appendChild(createActionsCell(currentEntry));

        tableBodyElement.appendChild(rowElement);
    }
}

function createActionsCell(scheduleEntry) {
    const cellElement = document.createElement("td");

    if (scheduleEntry.bookingStatus === "Paid") {
        const completeButtonElement = document.createElement("button");
        completeButtonElement.textContent = "Complete";
        completeButtonElement.addEventListener("click", async function () {
            await completeBooking(scheduleEntry.bookingId);
        });
        cellElement.appendChild(completeButtonElement);
    } else {
        cellElement.textContent = "";
    }

    return cellElement;
}

async function completeBooking(bookingId) {
    const consultantId = document.getElementById("consultant-id-input").value.trim();

    if (!consultantId) {
        setText("schedule-message", "Consultant ID is required.");
        return;
    }

    try {
        const response = await apiPost(
            "/api/consultant/" + encodeURIComponent(consultantId)
            + "/schedule/"
            + encodeURIComponent(bookingId)
            + "/complete",
            {}
        );

        setText("schedule-message", response.message || "Booking completed successfully.");
        await loadConsultantSchedule();
    } catch (error) {
        setText("schedule-message", "Failed to complete booking: " + error.message);
    }
}

function clearScheduleTable() {
    const tableBodyElement = document.getElementById("schedule-table-body");
    clearElementChildren(tableBodyElement);
}

function createCell(text) {
    const cellElement = document.createElement("td");
    cellElement.textContent = text == null ? "" : text;
    return cellElement;
}