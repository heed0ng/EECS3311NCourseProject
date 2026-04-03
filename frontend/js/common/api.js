async function apiGet(path) {
    const response = await fetch(APP_CONFIG.API_BASE_URL + path, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (!response.ok) {
        throw new Error("GET request failed: " + response.status);
    }

    return await response.json();
}

async function apiPost(path, requestBodyObject) {
    const response = await fetch(APP_CONFIG.API_BASE_URL + path, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestBodyObject)
    });

    const responseBody = await response.json();

    if (!response.ok) {
        throw new Error(responseBody.message || "POST request failed.");
    }

    return responseBody;
}