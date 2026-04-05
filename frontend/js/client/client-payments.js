document.addEventListener("DOMContentLoaded", function () {
    registerEventHandlers();
});

function registerEventHandlers() {
    document.getElementById("process-payment-button").addEventListener("click", async function () {
        await processPayment();
    });

    document.getElementById("load-payment-history-button").addEventListener("click", async function () {
        await loadPaymentHistory();
    });
}

async function processPayment() {
    const clientId = document.getElementById("client-id-input").value.trim();
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
        await loadPaymentHistory();
    } catch (error) {
        clearLatestPaymentResult();
        setText("payment-message", "Failed to process payment: " + error.message);
    }
}

async function loadPaymentHistory() {
    const clientId = document.getElementById("client-id-input").value.trim();

    if (!clientId) {
        setText("payment-message", "Client ID is required.");
        return;
    }

    try {
        const paymentHistory =
            await apiGet("/api/client/" + encodeURIComponent(clientId) + "/payments");

        renderPaymentHistory(paymentHistory);
        setText("payment-message", "Payment history loaded successfully.");
    } catch (error) {
        clearPaymentHistory();
        setText("payment-message", "Failed to load payment history: " + error.message);
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

function clearLatestPaymentResult() {
    const tableBodyElement = document.getElementById("payment-result-table-body");
    clearElementChildren(tableBodyElement);
}

function clearPaymentHistory() {
    const tableBodyElement = document.getElementById("payment-history-table-body");
    clearElementChildren(tableBodyElement);
}

function createCell(text) {
    const cellElement = document.createElement("td");
    cellElement.textContent = text == null ? "" : text;
    return cellElement;
}