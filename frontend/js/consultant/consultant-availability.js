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

    document.getElementById("update-availability-button").addEventListener("click", async function () {
        await updateAvailabilitySlot();
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

        renderAvailabilityTable(availabilitySlots, consultantId);
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

async function updateAvailabilitySlot() {
    const consultantId = document.getElementById("consultant-id-input").value.trim();
    const slotId = document.getElementById("selected-slot-id-input").value.trim();
    const startDateTime = document.getElementById("update-start-datetime-input").value.trim();
    const endDateTime = document.getElementById("update-end-datetime-input").value.trim();

    if (!consultantId) {
        setText("availability-update-message", "Consultant ID is required.");
        return;
    }

    if (!slotId) {
        setText("availability-update-message", "Please select a slot first.");
        return;
    }

    if (!startDateTime || !endDateTime) {
        setText("availability-update-message", "New start and end date/time are required.");
        return;
    }

    const requestBody = {
        startDateTime: startDateTime,
        endDateTime: endDateTime
    };

    try {
        const updatedSlot = await apiPut(
            "/api/consultant/" + encodeURIComponent(consultantId) +
            "/availability/" + encodeURIComponent(slotId),
            requestBody
        );

        setText(
            "availability-update-message",
            "Availability slot updated successfully: " + (updatedSlot.slotId || slotId)
        );

        await loadAvailabilitySlots();
    } catch (error) {
        setText("availability-update-message", "Failed to update availability slot: " + error.message);
    }
}

async function removeAvailabilitySlot(consultantId, slotId) {
    try {
        const response = await apiDelete(
            "/api/consultant/" + encodeURIComponent(consultantId) + "/availability/" + encodeURIComponent(slotId)
        );

        setText(
            "availability-message",
            response.message || "Availability slot removed successfully."
        );

        if (document.getElementById("selected-slot-id-input").value === slotId) {
            clearAvailabilityEditor();
            setText("availability-update-message", "Selected slot was removed.");
        }

        await loadAvailabilitySlots();
    } catch (error) {
        setText("availability-message", "Failed to remove availability slot: " + error.message);
    }
}

function renderAvailabilityTable(availabilitySlots, consultantId) {
    const tableBodyElement = document.getElementById("availability-table-body");
    clearElementChildren(tableBodyElement);

    if (!availabilitySlots || availabilitySlots.length === 0) {
        const rowElement = document.createElement("tr");
        const cellElement = document.createElement("td");
        cellElement.colSpan = 6;
        cellElement.textContent = "No availability slots found.";
        rowElement.appendChild(cellElement);
        tableBodyElement.appendChild(rowElement);
        return;
    }

    for (const currentSlot of availabilitySlots) {
        const rowElement = document.createElement("tr");

        rowElement.appendChild(createCell(currentSlot.slotId));
        rowElement.appendChild(createCell(currentSlot.consultantId));
        rowElement.appendChild(createCell(currentSlot.startDateTime));
        rowElement.appendChild(createCell(currentSlot.endDateTime));
        rowElement.appendChild(createCell(currentSlot.status));

        const actionCell = document.createElement("td");

        if (currentSlot.status === "AVAILABLE") {
            const selectButton = document.createElement("button");
            selectButton.textContent = "Select";
            selectButton.addEventListener("click", function () {
                selectAvailabilitySlotForUpdate(currentSlot);
            });
            actionCell.appendChild(selectButton);

            const removeButton = document.createElement("button");
            removeButton.textContent = "Remove";
            removeButton.style.marginLeft = "8px";
            removeButton.addEventListener("click", async function () {
                await removeAvailabilitySlot(consultantId, currentSlot.slotId);
            });
            actionCell.appendChild(removeButton);
        } else {
            actionCell.textContent = "-";
        }

        rowElement.appendChild(actionCell);
        tableBodyElement.appendChild(rowElement);
    }
}

function selectAvailabilitySlotForUpdate(slot) {
    document.getElementById("selected-slot-id-input").value = slot.slotId;
    document.getElementById("update-start-datetime-input").value = toDateTimeLocalValue(slot.startDateTime);
    document.getElementById("update-end-datetime-input").value = toDateTimeLocalValue(slot.endDateTime);
    setText("availability-update-message", "Slot selected for update.");
}

function clearAvailabilityEditor() {
    document.getElementById("selected-slot-id-input").value = "";
    document.getElementById("update-start-datetime-input").value = "";
    document.getElementById("update-end-datetime-input").value = "";
}

function toDateTimeLocalValue(dateTimeText) {
    if (!dateTimeText) {
        return "";
    }

    return dateTimeText.replace(" ", "T").slice(0, 16);
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