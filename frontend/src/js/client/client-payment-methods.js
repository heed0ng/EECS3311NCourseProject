document.addEventListener("DOMContentLoaded", function () {
    registerEventHandlers();
});

function registerEventHandlers() {
    document.getElementById("load-payment-methods-button").addEventListener("click", async function () {
        await loadSavedPaymentMethods();
    });

    document.getElementById("add-payment-method-button").addEventListener("click", async function () {
        await addSavedPaymentMethod();
    });

    document.getElementById("update-payment-method-button").addEventListener("click", async function () {
        await updateSavedPaymentMethod();
    });

    document.getElementById("clear-update-form-button").addEventListener("click", function () {
        clearUpdateForm();
    });
}

async function loadSavedPaymentMethods() {
    const clientId = document.getElementById("client-id-input").value.trim();

    if (!clientId) {
        setText("payment-method-message", "Client ID is required.");
        return;
    }

    try {
        const savedPaymentMethods =
            await apiGet("/api/client/" + encodeURIComponent(clientId) + "/payment-methods");

        renderSavedPaymentMethodsTable(savedPaymentMethods);
        setText("payment-method-message", "Saved payment methods loaded successfully.");
    } catch (error) {
        setText("payment-method-message", "Failed to load payment methods: " + error.message);
    }
}

function renderSavedPaymentMethodsTable(savedPaymentMethods) {
    const tableBodyElement = document.getElementById("payment-methods-table-body");
    clearElementChildren(tableBodyElement);

    for (const currentSavedPaymentMethod of savedPaymentMethods) {
        const rowElement = document.createElement("tr");

        rowElement.appendChild(createCell(currentSavedPaymentMethod.savedPaymentMethodId));
        rowElement.appendChild(createCell(currentSavedPaymentMethod.paymentMethodType));
        rowElement.appendChild(createCell(currentSavedPaymentMethod.nickname));
        rowElement.appendChild(createCell(currentSavedPaymentMethod.maskedDisplayValue));
        rowElement.appendChild(createActionsCell(currentSavedPaymentMethod));

        tableBodyElement.appendChild(rowElement);
    }
}

function createActionsCell(savedPaymentMethod) {
    const cellElement = document.createElement("td");

    const editButtonElement = document.createElement("button");
    editButtonElement.textContent = "Edit";
    editButtonElement.addEventListener("click", function () {
        populateUpdateForm(savedPaymentMethod);
    });

    const deleteButtonElement = document.createElement("button");
    deleteButtonElement.textContent = "Delete";
    deleteButtonElement.style.marginLeft = "8px";
    deleteButtonElement.addEventListener("click", async function () {
        await deleteSavedPaymentMethod(savedPaymentMethod.savedPaymentMethodId);
    });

    cellElement.appendChild(editButtonElement);
    cellElement.appendChild(deleteButtonElement);

    return cellElement;
}

function populateUpdateForm(savedPaymentMethod) {
    document.getElementById("update-saved-method-id-input").value =
        savedPaymentMethod.savedPaymentMethodId || "";

    document.getElementById("update-payment-method-type-input").value =
        savedPaymentMethod.paymentMethodType || "";

    document.getElementById("update-nickname-input").value =
        savedPaymentMethod.nickname || "";

    document.getElementById("update-payment-details-input").value = "";
    document.getElementById("update-payment-metadata-input").value = "";

    setText(
        "payment-method-message",
        "Update form loaded for saved payment method '" + savedPaymentMethod.savedPaymentMethodId + "'. Enter new payment details before updating."
    );
}

function clearUpdateForm() {
    document.getElementById("update-saved-method-id-input").value = "";
    document.getElementById("update-payment-method-type-input").value = "";
    document.getElementById("update-nickname-input").value = "";
    document.getElementById("update-payment-details-input").value = "";
    document.getElementById("update-payment-metadata-input").value = "";
}

async function addSavedPaymentMethod() {
    const clientId = document.getElementById("client-id-input").value.trim();
    const paymentMethodType = document.getElementById("payment-method-type-input").value;
    const nickname = document.getElementById("nickname-input").value.trim();
    const paymentDetails = document.getElementById("payment-details-input").value.trim();
    const paymentMetadata = document.getElementById("payment-metadata-input").value.trim();

    if (!clientId || !paymentMethodType || !nickname || !paymentDetails) {
        setText("payment-method-message", "Client ID, type, nickname, and payment details are required.");
        return;
    }

    const requestBody = {
        paymentMethodType: paymentMethodType,
        nickname: nickname,
        paymentDetails: paymentDetails,
        paymentMetadata: paymentMetadata
    };

    try {
        const response = await apiPost(
            "/api/client/" + encodeURIComponent(clientId) + "/payment-methods",
            requestBody
        );

        setText("payment-method-message", response.message || "Saved payment method added successfully.");
        await loadSavedPaymentMethods();
    } catch (error) {
        setText("payment-method-message", "Failed to add payment method: " + error.message);
    }
}

async function updateSavedPaymentMethod() {
    const clientId = document.getElementById("client-id-input").value.trim();
    const savedPaymentMethodId = document.getElementById("update-saved-method-id-input").value.trim();
    const nickname = document.getElementById("update-nickname-input").value.trim();
    const paymentDetails = document.getElementById("update-payment-details-input").value.trim();
    const paymentMetadata = document.getElementById("update-payment-metadata-input").value.trim();

    if (!clientId) {
        setText("payment-method-message", "Client ID is required.");
        return;
    }

    if (!savedPaymentMethodId) {
        setText("payment-method-message", "Select a saved payment method to update.");
        return;
    }

    if (!nickname || !paymentDetails) {
        setText("payment-method-message", "Updated nickname and payment details are required.");
        return;
    }

    const requestBody = {
        nickname: nickname,
        paymentDetails: paymentDetails,
        paymentMetadata: paymentMetadata
    };

    try {
        const response = await apiPut(
            "/api/client/" + encodeURIComponent(clientId)
            + "/payment-methods/"
            + encodeURIComponent(savedPaymentMethodId),
            requestBody
        );

        setText("payment-method-message", response.message || "Saved payment method updated successfully.");
        clearUpdateForm();
        await loadSavedPaymentMethods();
    } catch (error) {
        setText("payment-method-message", "Failed to update payment method: " + error.message);
    }
}

async function deleteSavedPaymentMethod(savedPaymentMethodId) {
    const clientId = document.getElementById("client-id-input").value.trim();

    if (!clientId) {
        setText("payment-method-message", "Client ID is required.");
        return;
    }

    const confirmed = window.confirm(
        "Delete saved payment method '" + savedPaymentMethodId + "'?"
    );

    if (!confirmed) {
        return;
    }

    try {
        const response = await apiDelete(
            "/api/client/" + encodeURIComponent(clientId)
            + "/payment-methods/"
            + encodeURIComponent(savedPaymentMethodId)
        );

        setText("payment-method-message", response.message || "Saved payment method deleted successfully.");
        clearUpdateForm();
        await loadSavedPaymentMethods();
    } catch (error) {
        setText("payment-method-message", "Failed to delete payment method: " + error.message);
    }
}

function createCell(text) {
    const cellElement = document.createElement("td");
    cellElement.textContent = text;
    return cellElement;
}