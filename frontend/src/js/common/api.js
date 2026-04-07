async function readResponseBody(response) {
    const responseText = await response.text();

    if (!responseText) {
        return null;
    }

    try {
        return JSON.parse(responseText);
    } catch (error) {
        return responseText;
    }
}

function extractErrorMessage(response, responseBody, defaultMessage) {
    if (responseBody && typeof responseBody === "object") {
        if (typeof responseBody.message === "string" && responseBody.message.trim() !== "") {
            return responseBody.message.trim();
        }

        if (typeof responseBody.error === "string" && responseBody.error.trim() !== "") {
            return responseBody.error.trim();
        }
    }

    if (typeof responseBody === "string" && responseBody.trim() !== "") {
        return responseBody.trim();
    }

    return defaultMessage + " (HTTP " + response.status + ")";
}

async function handleApiResponse(response, defaultErrorMessage) {
    const responseBody = await readResponseBody(response);

    if (!response.ok) {
        throw new Error(extractErrorMessage(response, responseBody, defaultErrorMessage));
    }

    return responseBody === null ? {} : responseBody;
}

async function apiGet(path) {
    const response = await fetch(APP_CONFIG.API_BASE_URL + path, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    });

    return await handleApiResponse(response, "GET request failed.");
}

async function apiPost(path, requestBodyObject) {
    const response = await fetch(APP_CONFIG.API_BASE_URL + path, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestBodyObject)
    });

    const responseBody = await handleApiResponse(response, "POST request failed.");
    await refreshPopupNotificationAfterMutation();
    return responseBody;
}

async function apiPut(path, requestBodyObject) {
    const response = await fetch(APP_CONFIG.API_BASE_URL + path, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestBodyObject)
    });

    const responseBody = await handleApiResponse(response, "PUT request failed.");
    await refreshPopupNotificationAfterMutation();
    return responseBody;
}

async function apiDelete(path) {
    const response = await fetch(APP_CONFIG.API_BASE_URL + path, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json"
        }
    });

    const responseBody = await handleApiResponse(response, "DELETE request failed.");
    await refreshPopupNotificationAfterMutation();
    return responseBody;
}

window.apiGet = apiGet;
window.apiPost = apiPost;
window.apiPut = apiPut;
window.apiDelete = apiDelete;

document.addEventListener("DOMContentLoaded", function () {
    initializeLiveNotificationPopupPolling();
});

let liveNotificationPollingIntervalId = null;

function initializeLiveNotificationPopupPolling() {
    const currentPageName = getCurrentPageName();

    if (currentPageName === "notifications.html" || currentPageName === "index.html") {
        return;
    }

    const role = inferActiveRoleFromPage(currentPageName);

    if (!role) {
        return;
    }

    pollLatestNotificationForPopup(role, {
        suppressPopup: true,
        allowRecentInitialPopup: true,
        forcePopupOnFirstSeen: false
    });

    if (liveNotificationPollingIntervalId !== null) {
        window.clearInterval(liveNotificationPollingIntervalId);
    }

    liveNotificationPollingIntervalId = window.setInterval(function () {
        pollLatestNotificationForPopup(role, {
            suppressPopup: false,
            allowRecentInitialPopup: false,
            forcePopupOnFirstSeen: false
        });
    }, 5000);
}

function getCurrentPageName() {
    const pathParts = window.location.pathname.split("/");
    return pathParts[pathParts.length - 1] || "";
}

function inferActiveRoleFromPage(currentPageName) {
    if (currentPageName.startsWith("client-") || currentPageName === "client-home.html") {
        return "client";
    }

    if (currentPageName.startsWith("consultant-") || currentPageName === "consultant-home.html") {
        return "consultant";
    }

    if (currentPageName.startsWith("admin-") || currentPageName === "admin-home.html") {
        return "admin";
    }

    return null;
}

function resolveActiveUserIdForPopup(role) {
    const inputElement = document.getElementById(role + "-id-input");

    if (inputElement && inputElement.value.trim() !== "") {
        return inputElement.value.trim();
    }

    if (role === "client") {
        return "client-1";
    }

    if (role === "consultant") {
        return "consultant-1";
    }

    return "admin-1";
}

function buildNotificationPopupPath(role, userId) {
    if (role === "client") {
        return "/api/client/" + encodeURIComponent(userId) + "/notifications";
    }

    if (role === "consultant") {
        return "/api/consultant/" + encodeURIComponent(userId) + "/notifications";
    }

    return "/api/admin/" + encodeURIComponent(userId) + "/notifications";
}

async function refreshPopupNotificationAfterMutation() {
    const role = inferActiveRoleFromPage(getCurrentPageName());

    if (!role) {
        return;
    }

    try {
        await pollLatestNotificationForPopup(role, {
            suppressPopup: false,
            allowRecentInitialPopup: false,
            forcePopupOnFirstSeen: true
        });
    } catch (error) {
        // Keep mutation flows uninterrupted if popup refresh fails.
    }
}

async function pollLatestNotificationForPopup(role, options) {
    const settings = options || {};
    const userId = resolveActiveUserIdForPopup(role);

    if (!userId) {
        return;
    }

    try {
        const notifications = await apiGet(buildNotificationPopupPath(role, userId));

        if (!notifications || notifications.length === 0) {
            return;
        }

        const newestNotification = notifications[0];
        const storageKey = "latest-popup-notification:" + role + ":" + userId;
        const previousEventId = sessionStorage.getItem(storageKey);

        if (!previousEventId) {
            sessionStorage.setItem(storageKey, newestNotification.eventId);

            if (settings.forcePopupOnFirstSeen || shouldShowRecentInitialPopup(newestNotification, 15000)) {
                showPopupNotification(newestNotification);
            }

            return;
        }

        if (previousEventId !== newestNotification.eventId) {
            sessionStorage.setItem(storageKey, newestNotification.eventId);

            if (!settings.suppressPopup) {
                showPopupNotification(newestNotification);
            }
        }
    } catch (error) {
    }
}

function shouldShowRecentInitialPopup(notification, maxAgeMilliseconds) {
    if (!notification || !notification.occurredAt) {
        return false;
    }

    const occurredAtTime = Date.parse(notification.occurredAt);

    if (Number.isNaN(occurredAtTime)) {
        return false;
    }

    const ageMilliseconds = Date.now() - occurredAtTime;
    return ageMilliseconds >= 0 && ageMilliseconds <= maxAgeMilliseconds;
}

function showPopupNotification(notification) {
    window.alert("New notification:\n" + (notification.message || ""));
}