document.addEventListener("DOMContentLoaded", function () {
    initializeNotificationPage();
    registerNotificationEventHandlers();
});

function initializeNotificationPage() {
    const roleFromQueryString = new URLSearchParams(window.location.search).get("role");
    const roleSelectElement = document.getElementById("notification-role-select");

    if (roleFromQueryString === "client"
            || roleFromQueryString === "consultant"
            || roleFromQueryString === "admin") {
        roleSelectElement.value = roleFromQueryString;
    }

    applyRoleDefaults();
    updateRoleHelpText();
    updatePageHeading();
}

function registerNotificationEventHandlers() {
    document.getElementById("notification-role-select").addEventListener("change", function () {
        applyRoleDefaults();
        updateRoleHelpText();
        updatePageHeading();
    });

    document.getElementById("load-notifications-button").addEventListener("click", async function () {
        await loadNotifications();
    });
}

function applyRoleDefaults() {
    const role = document.getElementById("notification-role-select").value;
    document.getElementById("notification-user-id-input").value = getDefaultUserId(role);
}

function getDefaultUserId(role) {
    if (role === "client") {
        return "client-1";
    }

    if (role === "consultant") {
        return "consultant-1";
    }

    return "admin-1";
}

function updateRoleHelpText() {
    const role = document.getElementById("notification-role-select").value;

    if (role === "client") {
        setText(
            "notification-role-help",
            "Client feed includes booking requested, accepted, rejected, cancelled,and  payment processed events."
        );
        return;
    }

    if (role === "consultant") {
        setText(
            "notification-role-help",
            "Consultant feed includes booking requested, cancelled, payment processed, and policy update events."
        );
        return;
    }

    setText(
        "notification-role-help",
        "Admin feed includes consultant approval decision events and policy update events."
    );
}

function updatePageHeading() {
    const role = document.getElementById("notification-role-select").value;

    if (role === "client") {
        setText("notification-page-heading", "Client Notification Center");
        return;
    }

    if (role === "consultant") {
        setText("notification-page-heading", "Consultant Notification Center");
        return;
    }

    setText("notification-page-heading", "Admin Notification Center");
}

async function loadNotifications() {
    const role = document.getElementById("notification-role-select").value;
    const userId = document.getElementById("notification-user-id-input").value.trim();

    if (!userId) {
        setText("notifications-message", "User ID is required.");
        return;
    }

    try {
        const notifications = await apiGet(buildNotificationsPath(role, userId));
        renderNotificationsTable(notifications);
        setText("notifications-message", "Notifications loaded successfully.");
    } catch (error) {
        renderNotificationsTable([]);
        setText("notifications-message", "Failed to load notifications: " + error.message);
    }
}

function buildNotificationsPath(role, userId) {
    if (role === "client") {
        return "/api/client/" + encodeURIComponent(userId) + "/notifications";
    }

    if (role === "consultant") {
        return "/api/consultant/" + encodeURIComponent(userId) + "/notifications";
    }

    return "/api/admin/" + encodeURIComponent(userId) + "/notifications";
}

function renderNotificationsTable(notifications) {
    const tableBodyElement = document.getElementById("notifications-table-body");
    clearElementChildren(tableBodyElement);

    if (!notifications || notifications.length === 0) {
        const rowElement = document.createElement("tr");
        const cellElement = document.createElement("td");
        cellElement.colSpan = 4;
        cellElement.textContent = "No notifications available yet for this application run.";
        rowElement.appendChild(cellElement);
        tableBodyElement.appendChild(rowElement);
        return;
    }

    for (const currentNotification of notifications) {
        const rowElement = document.createElement("tr");
        rowElement.appendChild(createNotificationCell(currentNotification.eventId));
        rowElement.appendChild(createNotificationCell(currentNotification.occurredAt));
        rowElement.appendChild(createNotificationCell(currentNotification.eventType));
        rowElement.appendChild(createNotificationCell(currentNotification.message));
        tableBodyElement.appendChild(rowElement);
    }
}

function createNotificationCell(text) {
    const cellElement = document.createElement("td");
    cellElement.textContent = text || "";
    return cellElement;
}