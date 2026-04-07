function clearElementChildren(element) {
    while (element.firstChild) {
        element.removeChild(element.firstChild);
    }
}

function setText(elementId, text) {
    document.getElementById(elementId).textContent = text;
}