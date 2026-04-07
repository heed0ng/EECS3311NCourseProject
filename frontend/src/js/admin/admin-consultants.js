document.addEventListener("DOMContentLoaded", function () {
    registerEventHandlers();
});

function registerEventHandlers() {
    document.getElementById("load-pending-consultants-button").addEventListener("click", async function () {
        await loadPendingConsultants();
    });
}

async function loadPendingConsultants() {
    try {
        const pendingConsultants = await apiGet("/api/admin/consultants/pending");

        renderPendingConsultantsTable(pendingConsultants);
        setText("admin-consultant-message", "Pending consultants loaded successfully.");
    } catch (error) {
        clearPendingConsultantsTable();
        setText("admin-consultant-message", "Failed to load pending consultants: " + error.message);
    }
}

function renderPendingConsultantsTable(pendingConsultants) {
    const tableBodyElement = document.getElementById("pending-consultants-table-body");
    clearElementChildren(tableBodyElement);

    for (const currentConsultant of pendingConsultants) {
        const rowElement = document.createElement("tr");

        rowElement.appendChild(createCell(currentConsultant.consultantId));
        rowElement.appendChild(createCell(currentConsultant.consultantName));
        rowElement.appendChild(createCell(currentConsultant.email));
        rowElement.appendChild(createCell(currentConsultant.approvalStatus));
        rowElement.appendChild(createActionsCell(currentConsultant));

        tableBodyElement.appendChild(rowElement);
    }
}

function createActionsCell(consultant) {
    const cellElement = document.createElement("td");

    const approveButtonElement = document.createElement("button");
    approveButtonElement.textContent = "Approve";
    approveButtonElement.addEventListener("click", async function () {
        await approveConsultant(consultant.consultantId);
    });

    const rejectButtonElement = document.createElement("button");
    rejectButtonElement.textContent = "Reject";
    rejectButtonElement.style.marginLeft = "8px";
    rejectButtonElement.addEventListener("click", async function () {
        await rejectConsultant(consultant.consultantId);
    });

    cellElement.appendChild(approveButtonElement);
    cellElement.appendChild(rejectButtonElement);

    return cellElement;
}

async function approveConsultant(consultantId) {
    const adminId = document.getElementById("admin-id-input").value.trim();

    if (!adminId) {
        setText("admin-consultant-message", "Admin ID is required.");
        return;
    }

    try {
        const response = await apiPost(
            "/api/admin/consultants/" + encodeURIComponent(consultantId) + "/approve",
            { adminId: adminId }
        );

        setText("admin-consultant-message", response.message || "Consultant approved successfully.");
        await loadPendingConsultants();
    } catch (error) {
        setText("admin-consultant-message", "Failed to approve consultant: " + error.message);
    }
}

async function rejectConsultant(consultantId) {
    const adminId = document.getElementById("admin-id-input").value.trim();

    if (!adminId) {
        setText("admin-consultant-message", "Admin ID is required.");
        return;
    }

    try {
        const response = await apiPost(
            "/api/admin/consultants/" + encodeURIComponent(consultantId) + "/reject",
            { adminId: adminId }
        );

        setText("admin-consultant-message", response.message || "Consultant rejected successfully.");
        await loadPendingConsultants();
    } catch (error) {
        setText("admin-consultant-message", "Failed to reject consultant: " + error.message);
    }
}

function clearPendingConsultantsTable() {
    const tableBodyElement = document.getElementById("pending-consultants-table-body");
    clearElementChildren(tableBodyElement);
}

function createCell(text) {
    const cellElement = document.createElement("td");
    cellElement.textContent = text == null ? "" : text;
    return cellElement;
}