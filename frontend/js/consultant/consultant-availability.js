document.addEventListener("DOMContentLoaded", function () {
    registerEventHandlers();
});

function registerEventHandlers() {
    document.getElementById("load-availability-button").addEventListener("click", async function () {
        await loadAvailabilitySlots();
    });

    document.getElementById("add-availability-button").addEventListener("click", async function () {
        await addAvailabilitySlot();
    });
}

async function loadAvailabilitySlots() {
    const consultantId = document.getElementById("consultant-id-input").value.trim();

    if (!consultantId) {
        setText("availability-message", "Consultant ID is required.");
        return;
    }

    try {
        const availabilitySlots = await apiGet(
            "/api/consultant/" + encodeURIComponent(consultantId) + "/availability"
        );

        renderAvailabilityTable(availabilitySlots);
        setText("availability-message", "Availability slots loaded successfully.");
    } catch (error) {
        clearAvailabilityTable();
        setText("availability-message", "Failed to load availability slots: " + error.message);
    }
}

async function addAvailabilitySlot() {
    const consultantId = document.getElementById("consultant-id-input").value.trim();
    const startDateTime = document.getElementById("start-datetime-input").value.trim();
    const endDateTime = document.getElementById("end-datetime-input").value.trim();

    if (!consultantId) {
        setText("availability-message", "Consultant ID is required.");
        return;
    }

    if (!startDateTime || !endDateTime) {
        setText("availability-message", "Start and end date/time are required.");
        return;
    }

    const requestBody = {
        startDateTime: startDateTime,
        endDateTime: endDateTime
    };

    try {
        const createdSlot = await apiPost(
            "/api/consultant/" + encodeURIComponent(consultantId) + "/availability",
            requestBody
        );

        setText(
            "availability-message",
            "Availability slot created successfully: " + (createdSlot.slotId || "")
        );

        document.getElementById("start-datetime-input").value = "";
        document.getElementById("end-datetime-input").value = "";

        await loadAvailabilitySlots();
    } catch (error) {
        setText("availability-message", "Failed to create availability slot: " + error.message);
    }
}

function renderAvailabilityTable(availabilitySlots) {
    const tableBodyElement = document.getElementById("availability-table-body");
    clearElementChildren(tableBodyElement);

    for (const currentSlot of availabilitySlots) {
        const rowElement = document.createElement("tr");

        rowElement.appendChild(createCell(currentSlot.slotId));
        rowElement.appendChild(createCell(currentSlot.consultantId));
        rowElement.appendChild(createCell(currentSlot.startDateTime));
        rowElement.appendChild(createCell(currentSlot.endDateTime));
        rowElement.appendChild(createCell(currentSlot.status));

        tableBodyElement.appendChild(rowElement);
    }
}

function clearAvailabilityTable() {
    const tableBodyElement = document.getElementById("availability-table-body");
    clearElementChildren(tableBodyElement);
}

function createCell(text) {
    const cellElement = document.createElement("td");
    cellElement.textContent = text == null ? "" : text;
    return cellElement;
}