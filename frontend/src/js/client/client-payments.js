document.addEventListener("DOMContentLoaded", function () {
    registerEventHandlers();
});

function registerEventHandlers() {
    document.getElementById("process-payment-button").addEventListener("click", async function () {
        await processPayment();
    });

    document.getElementById("load-payment-inputs-button").addEventListener("click", async function () {
        await loadPaymentInputs();
    });

    document.getElementById("load-payment-history-button").addEventListener("click", async function () {
        await loadPaymentHistory();
    });
}

async function loadPaymentInputs() {
    const clientId = getClientId();

    if (!clientId) {
        setText("payment-message", "Client ID is required.");
        return;
    }

    try {
        const bookings = await apiGet("/api/client/" + encodeURIComponent(clientId) + "/bookings");
        const pendingPaymentBookings = bookings.filter(function (booking) {
            return booking.bookingStatus === "Pending Payment";
        });
        renderPendingPaymentBookings(pendingPaymentBookings);

        const savedPaymentMethods = await apiGet(
            "/api/client/" + encodeURIComponent(clientId) + "/payment-methods"
        );
        renderPaymentMethodSelectionTable(savedPaymentMethods);

        setText("payment-message", "Pending payment bookings and saved payment methods loaded successfully.");
    } catch (error) {
        clearPendingPaymentBookings();
        clearPaymentMethodSelectionTable();
        setText("payment-message", "Failed to load payment inputs: " + error.message);
    }
}

async function processPayment() {
    const clientId = getClientId();
    const bookingId = document.getElementById("booking-id-input").value.trim();
    const savedPaymentMethodId = document.getElementById("saved-payment-method-id-input").value.trim();

    if (!clientId || !bookingId || !savedPaymentMethodId) {
        setText("payment-message", "Client ID, booking ID, and saved payment method ID are required.");
        return;
    }

    const requestBody = {
        bookingId: bookingId,
        savedPaymentMethodId: savedPaymentMethodId
    };

    try {
        setText("payment-message", "Processing payment... please wait 2-4 seconds.");

        const paymentResult = await apiPost(
            "/api/client/" + encodeURIComponent(clientId) + "/payments",
            requestBody
        );

        renderLatestPaymentResult(paymentResult);
        setText("payment-message", paymentResult.message || "Payment processed successfully.");
        await loadPaymentInputs();
        await loadPaymentHistory();
    } catch (error) {
        clearLatestPaymentResult();
        setText("payment-message", "Failed to process payment: " + error.message);
    }
}

async function loadPaymentHistory() {
    const clientId = getClientId();

    if (!clientId) {
        setText("payment-message", "Client ID is required.");
        return;
    }

    try {
        const paymentHistory = await apiGet("/api/client/" + encodeURIComponent(clientId) + "/payments");
        renderPaymentHistory(paymentHistory);
        setText("payment-message", "Payment history loaded successfully.");
    } catch (error) {
        clearPaymentHistory();
        setText("payment-message", "Failed to load payment history: " + error.message);
    }
}

function renderPendingPaymentBookings(bookings) {
    const tableBodyElement = document.getElementById("pending-payment-bookings-table-body");
    clearElementChildren(tableBodyElement);

    for (const currentBooking of bookings) {
        const rowElement = document.createElement("tr");
        rowElement.appendChild(createCell(currentBooking.bookingId));
        rowElement.appendChild(createCell(currentBooking.serviceName));
        rowElement.appendChild(createCell(currentBooking.consultantName));
        rowElement.appendChild(createCell(currentBooking.startDateTime));
        rowElement.appendChild(createCell(currentBooking.endDateTime));
        rowElement.appendChild(createCell(currentBooking.bookingStatus));
        rowElement.appendChild(createCell(String(currentBooking.price)));

        const actionCell = document.createElement("td");
        const selectButton = document.createElement("button");
        selectButton.textContent = "Select";
        selectButton.addEventListener("click", function () {
            document.getElementById("booking-id-input").value = currentBooking.bookingId;
        });
        actionCell.appendChild(selectButton);
        rowElement.appendChild(actionCell);

        tableBodyElement.appendChild(rowElement);
    }
}

function renderPaymentMethodSelectionTable(savedPaymentMethods) {
    const tableBodyElement = document.getElementById("payment-method-selection-table-body");
    clearElementChildren(tableBodyElement);

    for (const currentMethod of savedPaymentMethods) {
        const rowElement = document.createElement("tr");
        rowElement.appendChild(createCell(currentMethod.savedPaymentMethodId));
        rowElement.appendChild(createCell(currentMethod.paymentMethodType));
        rowElement.appendChild(createCell(currentMethod.nickname));
        rowElement.appendChild(createCell(currentMethod.maskedDisplayValue));

        const actionCell = document.createElement("td");
        const selectButton = document.createElement("button");
        selectButton.textContent = "Select";
        selectButton.addEventListener("click", function () {
            document.getElementById("saved-payment-method-id-input").value = currentMethod.savedPaymentMethodId;
        });
        actionCell.appendChild(selectButton);
        rowElement.appendChild(actionCell);

        tableBodyElement.appendChild(rowElement);
    }
}

function renderLatestPaymentResult(paymentResult) {
    const tableBodyElement = document.getElementById("payment-result-table-body");
    clearElementChildren(tableBodyElement);

    const rowElement = document.createElement("tr");
    rowElement.appendChild(createCell(paymentResult.paymentTransactionId));
    rowElement.appendChild(createCell(paymentResult.bookingId));
    rowElement.appendChild(createCell(paymentResult.paymentMethodType));
    rowElement.appendChild(createCell(String(paymentResult.amount)));
    rowElement.appendChild(createCell(paymentResult.paymentStatus));
    rowElement.appendChild(createCell(paymentResult.transactionType));
    rowElement.appendChild(createCell(paymentResult.processedAt));

    tableBodyElement.appendChild(rowElement);
}

function renderPaymentHistory(paymentHistory) {
    const tableBodyElement = document.getElementById("payment-history-table-body");
    clearElementChildren(tableBodyElement);

    for (const currentPaymentTransaction of paymentHistory) {
        const rowElement = document.createElement("tr");

        rowElement.appendChild(createCell(currentPaymentTransaction.paymentTransactionId));
        rowElement.appendChild(createCell(currentPaymentTransaction.bookingId));
        rowElement.appendChild(createCell(currentPaymentTransaction.paymentMethodType));
        rowElement.appendChild(createCell(String(currentPaymentTransaction.amount)));
        rowElement.appendChild(createCell(currentPaymentTransaction.paymentStatus));
        rowElement.appendChild(createCell(currentPaymentTransaction.transactionType));
        rowElement.appendChild(createCell(currentPaymentTransaction.processedAt));

        tableBodyElement.appendChild(rowElement);
    }
}

function clearPendingPaymentBookings() {
    clearElementChildren(document.getElementById("pending-payment-bookings-table-body"));
}

function clearPaymentMethodSelectionTable() {
    clearElementChildren(document.getElementById("payment-method-selection-table-body"));
}

function clearLatestPaymentResult() {
    clearElementChildren(document.getElementById("payment-result-table-body"));
}

function clearPaymentHistory() {
    clearElementChildren(document.getElementById("payment-history-table-body"));
}

function getClientId() {
    return document.getElementById("client-id-input").value.trim();
}

function createCell(text) {
    const cellElement = document.createElement("td");
    cellElement.textContent = text == null ? "" : text;
    return cellElement;
}
