function toggleCuentaMenu() {
    const menu = document.getElementById("headerCuentaMenu");
    const button = document.querySelector("button[aria-controls=headerCuentaMenu]");
    if (menu) {
        const isHidden = menu.classList.toggle("hidden");
        if (button) {
            button.setAttribute("aria-expanded", String(!isHidden));
        }
    }
}

document.addEventListener("click", function (e) {
    const dropdown = document.querySelector(".header-account-dropdown");
    const menu = document.getElementById("headerCuentaMenu");
    const button = document.querySelector("button[aria-controls=headerCuentaMenu]");

    if (dropdown && menu && !dropdown.contains(e.target)) {
        menu.classList.add("hidden");
        if (button) {
            button.setAttribute("aria-expanded", "false");
        }
    }
});