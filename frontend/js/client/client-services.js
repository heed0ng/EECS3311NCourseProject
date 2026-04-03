document.addEventListener("DOMContentLoaded", function () {
    registerEventHandlers();
});

function registerEventHandlers() {
    document.getElementById("load-offerings-button").addEventListener("click", async function () {
        await loadOfferings();
    });

    document.getElementById("load-slots-button").addEventListener("click", async function () {
        await loadSlotsForSelectedOffering();
    });

    document.getElementById("slot-select").addEventListener("change", function () {
        const selectedSlotId = this.value;
        document.getElementById("request-slot-id-input").value = selectedSlotId;
    });

    document.getElementById("request-booking-button").addEventListener("click", async function () {
        await submitBookingRequest();
    });
}

async function loadOfferings() {
    try {
        const offerings = await apiGet("/api/client/offerings");
        renderOfferingsTable(offerings);
        setText("request-booking-message", "");
    } catch (error) {
        setText("request-booking-message", "Failed to load offerings: " + error.message);
    }
}

function renderOfferingsTable(offerings) {
    const tableBodyElement = document.getElementById("offerings-table-body");
    clearElementChildren(tableBodyElement);

    for (const currentOffering of offerings) {
        const rowElement = document.createElement("tr");

        rowElement.appendChild(createCell(currentOffering.offeringId));
        rowElement.appendChild(createCell(currentOffering.serviceName));
        rowElement.appendChild(createCell(currentOffering.consultantName));
        rowElement.appendChild(createCell(String(currentOffering.durationMinutes)));
        rowElement.appendChild(createCell(String(currentOffering.basePrice)));
        rowElement.appendChild(createCell(currentOffering.description || ""));

        const selectButtonCell = document.createElement("td");
        const selectButton = document.createElement("button");
        selectButton.textContent = "Select";

        selectButton.addEventListener("click", function () {
            document.getElementById("selected-offering-id").value = currentOffering.offeringId;
            document.getElementById("request-offering-id-input").value = currentOffering.offeringId;
            document.getElementById("request-slot-id-input").value = "";
            clearSlotSelect();
        });

        selectButtonCell.appendChild(selectButton);
        rowElement.appendChild(selectButtonCell);

        tableBodyElement.appendChild(rowElement);
    }
}

async function loadSlotsForSelectedOffering() {
    const selectedOfferingId = document.getElementById("selected-offering-id").value;

    if (!selectedOfferingId) {
        setText("request-booking-message", "Please select an offering first.");
        return;
    }

    try {
        const slots = await apiGet("/api/client/offerings/" + encodeURIComponent(selectedOfferingId) + "/slots");
        renderSlotSelect(slots);
        setText("request-booking-message", "");
    } catch (error) {
        setText("request-booking-message", "Failed to load slots: " + error.message);
    }
}

function renderSlotSelect(slots) {
    const slotSelectElement = document.getElementById("slot-select");
    clearElementChildren(slotSelectElement);

    const defaultOptionElement = document.createElement("option");
    defaultOptionElement.value = "";
    defaultOptionElement.textContent = "-- Select a slot --";
    slotSelectElement.appendChild(defaultOptionElement);

    for (const currentSlot of slots) {
        const optionElement = document.createElement("option");
        optionElement.value = currentSlot.slotId;
        optionElement.textContent = currentSlot.slotId + " | " + currentSlot.startDateTime + " -> " + currentSlot.endDateTime;
        slotSelectElement.appendChild(optionElement);
    }
}

function clearSlotSelect() {
    const slotSelectElement = document.getElementById("slot-select");
    clearElementChildren(slotSelectElement);

    const defaultOptionElement = document.createElement("option");
    defaultOptionElement.value = "";
    defaultOptionElement.textContent = "-- Select a slot --";
    slotSelectElement.appendChild(defaultOptionElement);
}

async function submitBookingRequest() {
    const clientId = document.getElementById("client-id-input").value.trim();
    const offeringId = document.getElementById("request-offering-id-input").value.trim();
    const slotId = document.getElementById("request-slot-id-input").value.trim();

    if (!clientId || !offeringId || !slotId) {
        setText("request-booking-message", "Client ID, offering ID, and slot ID are all required.");
        return;
    }

    const requestBody = {
        clientId: clientId,
        offeringId: offeringId,
        slotId: slotId
    };

    try {
        const response = await apiPost("/api/client/bookings", requestBody);
        setText("request-booking-message", response.message || "Booking request submitted successfully.");
    } catch (error) {
        setText("request-booking-message", "Booking request failed: " + error.message);
    }
}

function createCell(text) {
    const cellElement = document.createElement("td");
    cellElement.textContent = text;
    return cellElement;
}