document.addEventListener("DOMContentLoaded", function () {
    registerEventHandlers();
});

function registerEventHandlers() {
    document.getElementById("ask-assistant-button").addEventListener("click", async function () {
        await askAssistantQuestion();
    });
}

async function askAssistantQuestion() {
    const question = document.getElementById("assistant-question-input").value.trim();

    if (!question) {
        setText("assistant-message", "Question is required.");
        document.getElementById("assistant-answer-output").textContent = "";
        return;
    }

    try {
        setText("assistant-message", "Getting assistant answer...");

        const response = await apiPost("/api/client/assistant/question", {
            question: question
        });

        document.getElementById("assistant-answer-output").textContent = response.answer || "";
        setText("assistant-message", "Assistant answer loaded successfully.");
    } catch (error) {
        document.getElementById("assistant-answer-output").textContent = "";
        setText("assistant-message", "Failed to get assistant answer: " + error.message);
    }
}