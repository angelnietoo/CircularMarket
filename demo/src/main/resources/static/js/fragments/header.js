function cerrarOverlayBuscadorHeader() {
    const buscador = document.getElementById("buscadorHeader");
    const contenedorRecientes = document.getElementById("busquedasRecientes");
    const overlayBuscador = document.getElementById("buscadorOverlay");

    if (buscador) {
        buscador.blur();
    }

    if (contenedorRecientes) {
        contenedorRecientes.classList.add("hidden");
    }

    if (overlayBuscador) {
        overlayBuscador.classList.add("hidden");
    }
}

function toggleCategoriasMenu() {
    cerrarOverlayBuscadorHeader();

    const menu = document.getElementById("headerCategoriasMenu");
    const overlay = document.getElementById("categoriasOverlay");

    if (!menu || !overlay) {
        return;
    }

    menu.classList.toggle("-translate-x-full");
    overlay.classList.toggle("hidden");
}

function cerrarCategoriasMenu() {
    const menu = document.getElementById("headerCategoriasMenu");
    const overlay = document.getElementById("categoriasOverlay");

    if (!menu || !overlay) {
        return;
    }

    menu.classList.add("-translate-x-full");
    overlay.classList.add("hidden");
}

function toggleCuentaMenu() {
    const menu = document.getElementById("headerCuentaMenu");

    if (!menu) {
        return;
    }

    menu.classList.toggle("hidden");
}

document.addEventListener("DOMContentLoaded", function () {
    const buscador = document.getElementById("buscadorHeader");
    const contenedorRecientes = document.getElementById("busquedasRecientes");
    const overlayBuscador = document.getElementById("buscadorOverlay");

    if (!buscador || !contenedorRecientes || !overlayBuscador) {
        return;
    }

    const CLAVE_BUSQUEDAS = "circularMarketBusquedasRecientes";

    function obtenerBusquedas() {
        const busquedasGuardadas = localStorage.getItem(CLAVE_BUSQUEDAS);

        if (!busquedasGuardadas) {
            return [];
        }

        try {
            return JSON.parse(busquedasGuardadas);
        } catch (error) {
            return [];
        }
    }

    function guardarBusquedas(busquedas) {
        localStorage.setItem(CLAVE_BUSQUEDAS, JSON.stringify(busquedas));
    }

    function guardarBusqueda(busqueda) {
        const texto = busqueda.trim();

        if (texto.length === 0) {
            return;
        }

        let busquedas = obtenerBusquedas();

        busquedas = busquedas.filter(item => item.toLowerCase() !== texto.toLowerCase());
        busquedas.unshift(texto);
        busquedas = busquedas.slice(0, 5);

        guardarBusquedas(busquedas);
    }

    function eliminarBusqueda(busquedaEliminar) {
        let busquedas = obtenerBusquedas();

        busquedas = busquedas.filter(item => item.toLowerCase() !== busquedaEliminar.toLowerCase());

        guardarBusquedas(busquedas);
        mostrarBusquedas();
    }

    function mostrarOverlayBuscador() {
        overlayBuscador.classList.remove("hidden");
    }

    function ocultarOverlayBuscador() {
        overlayBuscador.classList.add("hidden");
        contenedorRecientes.classList.add("hidden");
    }

    function mostrarBusquedas() {
        const busquedas = obtenerBusquedas();

        contenedorRecientes.innerHTML = "";

        if (busquedas.length === 0) {
            contenedorRecientes.classList.add("hidden");
            return;
        }

        const titulo = document.createElement("div");
        titulo.textContent = "Búsquedas recientes";
        titulo.className = "px-4 py-2 text-xs font-bold uppercase tracking-wide text-slate-400 bg-slate-50";
        contenedorRecientes.appendChild(titulo);

        busquedas.forEach(busqueda => {
            const fila = document.createElement("div");
            fila.className = "flex items-center justify-between gap-2 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-200";

            const enlace = document.createElement("a");
            enlace.href = "/productos?q=" + encodeURIComponent(busqueda);
            enlace.textContent = busqueda;
            enlace.className = "min-w-0 flex-1 truncate text-slate-700 no-underline";

            enlace.addEventListener("click", function () {
                guardarBusqueda(busqueda);
            });

            const botonEliminar = document.createElement("button");
            botonEliminar.type = "button";
            botonEliminar.textContent = "×";
            botonEliminar.title = "Eliminar búsqueda reciente";
            botonEliminar.setAttribute("aria-label", "Eliminar búsqueda reciente");
            botonEliminar.className = "ml-2 inline-flex h-6 w-6 shrink-0 items-center justify-center rounded-full text-lg font-bold leading-none text-slate-400 transition hover:text-red-600";

            botonEliminar.addEventListener("click", function (event) {
                event.preventDefault();
                event.stopPropagation();
                eliminarBusqueda(busqueda);
            });

            fila.appendChild(enlace);
            fila.appendChild(botonEliminar);
            contenedorRecientes.appendChild(fila);
        });

        contenedorRecientes.classList.remove("hidden");
    }

    buscador.addEventListener("focus", function () {
        mostrarOverlayBuscador();
        mostrarBusquedas();
    });

    buscador.addEventListener("input", function () {
        mostrarOverlayBuscador();
        mostrarBusquedas();
    });

    const formulario = buscador.closest("form");

    if (formulario) {
        formulario.addEventListener("submit", function () {
            guardarBusqueda(buscador.value);
        });
    }

    overlayBuscador.addEventListener("click", function () {
        buscador.blur();
        ocultarOverlayBuscador();
    });

    document.addEventListener("click", function (event) {
        const cuentaMenu = document.getElementById("headerCuentaMenu");

        if (!buscador.contains(event.target) &&
            !contenedorRecientes.contains(event.target) &&
            !overlayBuscador.contains(event.target)) {
            ocultarOverlayBuscador();
        }

        if (cuentaMenu &&
            !cuentaMenu.contains(event.target) &&
            !event.target.closest("[aria-controls='headerCuentaMenu']")) {
            cuentaMenu.classList.add("hidden");
        }
    });

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape") {
            buscador.blur();
            ocultarOverlayBuscador();
            cerrarCategoriasMenu();

            const cuentaMenu = document.getElementById("headerCuentaMenu");

            if (cuentaMenu) {
                cuentaMenu.classList.add("hidden");
            }
        }
    });
});