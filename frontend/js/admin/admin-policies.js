document.addEventListener("DOMContentLoaded", function () {
    registerEventHandlers();
});

function registerEventHandlers() {
    document.getElementById("load-policy-summary-button").addEventListener("click", async function () {
        await loadPolicySummary();
    });

    document.getElementById("update-cancellation-policy-button").addEventListener("click", async function () {
        await updateCancellationPolicy();
    });

    document.getElementById("update-refund-policy-button").addEventListener("click", async function () {
        await updateRefundPolicy();
    });

    document.getElementById("update-pricing-policy-button").addEventListener("click", async function () {
        await updatePricingPolicy();
    });

    document.getElementById("update-notification-policy-button").addEventListener("click", async function () {
        await updateNotificationPolicy();
    });
}

async function loadPolicySummary() {
    try {
        const response = await apiGet("/api/admin/policies");

        document.getElementById("cancellation-policy-summary").textContent =
            response.cancellationPolicySummary || "";
        document.getElementById("pricing-policy-summary").textContent =
            response.pricingPolicySummary || "";
        document.getElementById("notification-policy-summary").textContent =
            response.notificationPolicySummary || "";
        document.getElementById("refund-policy-summary").textContent =
            response.refundPolicySummary || "";

        setText("admin-policy-message", "Policy summary loaded successfully.");
    } catch (error) {
        clearPolicySummary();
        setText("admin-policy-message", "Failed to load policy summary: " + error.message);
    }
}

async function updateCancellationPolicy() {
    const adminId = getAdminId();
    const cancellationDeadlineHours = document.getElementById("cancellation-deadline-hours-input").value.trim();

    if (!adminId) {
        setText("admin-policy-message", "Admin ID is required.");
        return;
    }

    if (cancellationDeadlineHours === "") {
        setText("admin-policy-message", "Cancellation deadline hours is required.");
        return;
    }

    try {
        const response = await apiPost("/api/admin/policies/cancellation", {
            adminId: adminId,
            cancellationDeadlineHours: Number(cancellationDeadlineHours)
        });

        setText("admin-policy-message", response.message || "Cancellation policy updated successfully.");
        await loadPolicySummary();
    } catch (error) {
        setText("admin-policy-message", "Failed to update cancellation policy: " + error.message);
    }
}

async function updateRefundPolicy() {
    const adminId = getAdminId();
    const refundBeforeDeadline = document.getElementById("refund-before-deadline-input").value.trim();
    const refundAfterDeadline = document.getElementById("refund-after-deadline-input").value.trim();

    if (!adminId) {
        setText("admin-policy-message", "Admin ID is required.");
        return;
    }

    if (refundBeforeDeadline === "" || refundAfterDeadline === "") {
        setText("admin-policy-message", "Both refund percentage fields are required.");
        return;
    }

    try {
        const response = await apiPost("/api/admin/policies/refund", {
            adminId: adminId,
            refundPercentBeforeDeadline: Number(refundBeforeDeadline),
            refundPercentAfterDeadline: Number(refundAfterDeadline)
        });

        setText("admin-policy-message", response.message || "Refund policy updated successfully.");
        await loadPolicySummary();
    } catch (error) {
        setText("admin-policy-message", "Failed to update refund policy: " + error.message);
    }
}

async function updatePricingPolicy() {
    const adminId = getAdminId();

    if (!adminId) {
        setText("admin-policy-message", "Admin ID is required.");
        return;
    }

    try {
        const response = await apiPost("/api/admin/policies/pricing", {
            adminId: adminId,
            allowConsultantCustomPrice: document.getElementById("allow-consultant-custom-price-input").checked
        });

        setText("admin-policy-message", response.message || "Pricing policy updated successfully.");
        await loadPolicySummary();
    } catch (error) {
        setText("admin-policy-message", "Failed to update pricing policy: " + error.message);
    }
}

async function updateNotificationPolicy() {
    const adminId = getAdminId();

    if (!adminId) {
        setText("admin-policy-message", "Admin ID is required.");
        return;
    }

    try {
        const response = await apiPost("/api/admin/policies/notifications", {
            adminId: adminId,
            notifyOnBookingRequested: document.getElementById("notify-on-booking-requested-input").checked,
            notifyOnBookingAccepted: document.getElementById("notify-on-booking-accepted-input").checked,
            notifyOnBookingRejected: document.getElementById("notify-on-booking-rejected-input").checked,
            notifyOnPaymentProcessed: document.getElementById("notify-on-payment-processed-input").checked,
            notifyOnBookingCancelled: document.getElementById("notify-on-booking-cancelled-input").checked,
            notifyOnConsultantApprovalDecision: document.getElementById("notify-on-consultant-approval-decision-input").checked
        });

        setText("admin-policy-message", response.message || "Notification policy updated successfully.");
        await loadPolicySummary();
    } catch (error) {
        setText("admin-policy-message", "Failed to update notification policy: " + error.message);
    }
}

function getAdminId() {
    return document.getElementById("admin-id-input").value.trim();
}

function clearPolicySummary() {
    document.getElementById("cancellation-policy-summary").textContent = "";
    document.getElementById("pricing-policy-summary").textContent = "";
    document.getElementById("notification-policy-summary").textContent = "";
    document.getElementById("refund-policy-summary").textContent = "";
}