document.addEventListener("DOMContentLoaded", function () {
    registerEventHandlers();
});

function registerEventHandlers() {
    document.getElementById("load-consultant-offering-data-button").addEventListener("click", async function () {
        await loadOfferingPageData();
    });

    document.getElementById("create-service-offering-button").addEventListener("click", async function () {
        await createServiceOffering();
    });
}

async function loadOfferingPageData() {
    const consultantId = getConsultantId();

    if (!consultantId) {
        setText("consultant-offering-message", "Consultant ID is required.");
        return;
    }

    try {
        const serviceCatalog = await apiGet("/api/consultant/services");
        renderServiceCatalogTable(serviceCatalog);

        const myOfferings = await apiGet(
            "/api/consultant/" + encodeURIComponent(consultantId) + "/offerings"
        );
        renderConsultantOfferingsTable(myOfferings, consultantId);

        setText("consultant-offering-message", "Service catalog and consultant offerings loaded successfully.");
    } catch (error) {
        clearServiceCatalogTable();
        clearConsultantOfferingsTable();
        setText("consultant-offering-message", "Failed to load consultant offering data: " + error.message);
    }
}

function renderServiceCatalogTable(serviceCatalog) {
    const tableBodyElement = document.getElementById("service-catalog-table-body");
    clearElementChildren(tableBodyElement);

    for (const currentService of serviceCatalog) {
        const rowElement = document.createElement("tr");

        rowElement.appendChild(createCell(currentService.serviceId));
        rowElement.appendChild(createCell(currentService.serviceName));
        rowElement.appendChild(createCell(String(currentService.durationMinutes)));
        rowElement.appendChild(createCell(String(currentService.basePrice)));
        rowElement.appendChild(createCell(currentService.description || ""));

        const actionCell = document.createElement("td");
        const selectButton = document.createElement("button");
        selectButton.textContent = "Select";
        selectButton.addEventListener("click", function () {
            document.getElementById("service-id-input").value = currentService.serviceId;
        });
        actionCell.appendChild(selectButton);
        rowElement.appendChild(actionCell);

        tableBodyElement.appendChild(rowElement);
    }
}

function renderConsultantOfferingsTable(offerings, consultantId) {
    const tableBodyElement = document.getElementById("consultant-offerings-table-body");
    clearElementChildren(tableBodyElement);

    if (!offerings || offerings.length === 0) {
        const rowElement = document.createElement("tr");
        const cellElement = document.createElement("td");
        cellElement.colSpan = 8;
        cellElement.textContent = "No service offerings found.";
        rowElement.appendChild(cellElement);
        tableBodyElement.appendChild(rowElement);
        return;
    }

    let renderedActiveCount = 0;

    for (const currentOffering of offerings) {
        if (currentOffering.status !== "ACTIVE") {
            continue;
        }

        renderedActiveCount++;

        const rowElement = document.createElement("tr");
        rowElement.appendChild(createCell(currentOffering.offeringId));
        rowElement.appendChild(createCell(currentOffering.serviceName));
        rowElement.appendChild(createCell(currentOffering.consultantId));
        rowElement.appendChild(createCell(currentOffering.consultantName));
        rowElement.appendChild(createCell(String(currentOffering.durationMinutes)));
        rowElement.appendChild(createCell(String(currentOffering.basePrice)));
        rowElement.appendChild(createCell(currentOffering.description || ""));

        const actionCell = document.createElement("td");
        const removeButton = document.createElement("button");
        removeButton.textContent = "Remove";
        removeButton.addEventListener("click", async function () {
            await removeServiceOffering(consultantId, currentOffering.offeringId);
        });
        actionCell.appendChild(removeButton);

        rowElement.appendChild(actionCell);
        tableBodyElement.appendChild(rowElement);
    }

    if (renderedActiveCount === 0) {
        const rowElement = document.createElement("tr");
        const cellElement = document.createElement("td");
        cellElement.colSpan = 8;
        cellElement.textContent = "No active service offerings found.";
        rowElement.appendChild(cellElement);
        tableBodyElement.appendChild(rowElement);
    }
}

async function createServiceOffering() {
    const consultantId = getConsultantId();
    const serviceId = document.getElementById("service-id-input").value.trim();
    const customPriceRaw = document.getElementById("custom-price-input").value.trim();

    if (!consultantId) {
        setText("consultant-offering-message", "Consultant ID is required.");
        return;
    }

    if (!serviceId) {
        setText("consultant-offering-message", "Service ID is required.");
        return;
    }

    const requestBody = {
        serviceId: serviceId,
        customPrice: customPriceRaw === "" ? null : Number(customPriceRaw)
    };

    try {
        const response = await apiPost(
            "/api/consultant/" + encodeURIComponent(consultantId) + "/offerings",
            requestBody
        );

        setText(
            "consultant-offering-message",
            "Service offering created successfully: " + (response.offeringId || "")
        );

        document.getElementById("service-id-input").value = "";
        document.getElementById("custom-price-input").value = "";
        await loadOfferingPageData();
    } catch (error) {
        setText("consultant-offering-message", "Failed to create service offering: " + error.message);
    }
}

async function removeServiceOffering(consultantId, offeringId) {
    try {
        const response = await apiDelete(
            "/api/consultant/" + encodeURIComponent(consultantId) + "/offerings/" + encodeURIComponent(offeringId)
        );

        setText(
            "consultant-offering-message",
            response.message || "Service offering removed successfully."
        );

        await loadOfferingPageData();
    } catch (error) {
        setText("consultant-offering-message", "Failed to remove service offering: " + error.message);
    }
}

function clearServiceCatalogTable() {
    clearElementChildren(document.getElementById("service-catalog-table-body"));
}

function clearConsultantOfferingsTable() {
    clearElementChildren(document.getElementById("consultant-offerings-table-body"));
}

function getConsultantId() {
    return document.getElementById("consultant-id-input").value.trim();
}

function createCell(text) {
    const cellElement = document.createElement("td");
    cellElement.textContent = text == null ? "" : text;
    return cellElement;
}