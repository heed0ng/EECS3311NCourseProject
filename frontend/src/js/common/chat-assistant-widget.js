document.addEventListener("DOMContentLoaded", function () {
    initializeClientAssistantWidget();
});

function initializeClientAssistantWidget() {
    if (!shouldShowClientAssistantWidget()) {
        return;
    }

    if (document.getElementById("client-assistant-widget-root")) {
        return;
    }

    const rootElement = document.createElement("div");
    rootElement.id = "client-assistant-widget-root";
    rootElement.innerHTML = `
        <button id="client-assistant-floating-button" class="client-assistant-floating-button" type="button" aria-label="Open AI Assistant">
            <span class="client-assistant-floating-icon">✦</span>
            <span class="client-assistant-floating-label">AI Help</span>
        </button>

        <section id="client-assistant-panel" class="client-assistant-panel client-assistant-hidden" aria-label="Client AI Assistant">
            <div class="client-assistant-panel-header">
                <div>
                    <div class="client-assistant-panel-title">AI Customer Assistant</div>
                    <div class="client-assistant-panel-subtitle">General platform help only</div>
                </div>
                <button id="client-assistant-close-button" class="client-assistant-icon-button" type="button" aria-label="Close assistant">×</button>
            </div>

            <div id="client-assistant-chat-log" class="client-assistant-chat-log">
                <div class="client-assistant-bubble client-assistant-bubble-assistant">
                    Hello. Ask about booking flow, services, payments, or public platform policies.
                </div>
            </div>

            <div class="client-assistant-suggestions">
                <button type="button" class="client-assistant-chip" data-question="How do I book a consulting session?">How do I book?</button>
                <button type="button" class="client-assistant-chip" data-question="What payment methods are accepted?">Payment methods</button>
                <button type="button" class="client-assistant-chip" data-question="Can I cancel my booking?">Cancellation</button>
            </div>

            <div class="client-assistant-input-area">
                <textarea
                    id="client-assistant-widget-input"
                    class="client-assistant-textarea"
                    rows="3"
                    placeholder="Ask a question about the platform..."></textarea>
                <div class="client-assistant-actions">
                    <span id="client-assistant-widget-status" class="client-assistant-status"></span>
                    <button id="client-assistant-send-button" type="button">Send</button>
                </div>
            </div>
        </section>
    `;

    document.body.appendChild(rootElement);

    const floatingButton = document.getElementById("client-assistant-floating-button");
    const panel = document.getElementById("client-assistant-panel");
    const closeButton = document.getElementById("client-assistant-close-button");
    const sendButton = document.getElementById("client-assistant-send-button");
    const inputElement = document.getElementById("client-assistant-widget-input");
    const statusElement = document.getElementById("client-assistant-widget-status");

    floatingButton.addEventListener("click", function () {
        panel.classList.toggle("client-assistant-hidden");
        if (!panel.classList.contains("client-assistant-hidden")) {
            inputElement.focus();
        }
    });

    closeButton.addEventListener("click", function () {
        panel.classList.add("client-assistant-hidden");
    });

    sendButton.addEventListener("click", async function () {
        await submitClientAssistantWidgetQuestion();
    });

    inputElement.addEventListener("keydown", async function (event) {
        if (event.key === "Enter" && !event.shiftKey) {
            event.preventDefault();
            await submitClientAssistantWidgetQuestion();
        }
    });

    document.querySelectorAll(".client-assistant-chip").forEach(function (chipButton) {
        chipButton.addEventListener("click", async function () {
            inputElement.value = chipButton.getAttribute("data-question") || "";
            await submitClientAssistantWidgetQuestion();
        });
    });

    statusElement.textContent = "";
}

function shouldShowClientAssistantWidget() {
    const pathParts = window.location.pathname.split("/");
    const currentPageName = pathParts[pathParts.length - 1] || "";

    return currentPageName === "client-home.html"
        || currentPageName === "client-services.html"
        || currentPageName === "client-bookings.html"
        || currentPageName === "client-payment-methods.html"
        || currentPageName === "client-payments.html";
}

async function submitClientAssistantWidgetQuestion() {
    const inputElement = document.getElementById("client-assistant-widget-input");
    const statusElement = document.getElementById("client-assistant-widget-status");
    const question = inputElement.value.trim();

    if (!question) {
        statusElement.textContent = "Enter a question first.";
        return;
    }

    appendClientAssistantBubble(question, "client");
    inputElement.value = "";
    statusElement.textContent = "Thinking...";

    try {
        const response = await apiPost("/api/client/assistant/question", { question: question });
        appendClientAssistantBubble(response.answer || "No answer was returned.", "assistant");
        statusElement.textContent = "Answer loaded.";
    } catch (error) {
        appendClientAssistantBubble("Sorry. The assistant could not answer right now.", "assistant");
        statusElement.textContent = "Request failed: " + error.message;
    }
}

function appendClientAssistantBubble(text, senderType) {
    const chatLogElement = document.getElementById("client-assistant-chat-log");
    const bubbleElement = document.createElement("div");
    bubbleElement.className = "client-assistant-bubble "
        + (senderType === "client"
            ? "client-assistant-bubble-user"
            : "client-assistant-bubble-assistant");
    bubbleElement.textContent = text || "";
    chatLogElement.appendChild(bubbleElement);
    chatLogElement.scrollTop = chatLogElement.scrollHeight;
}