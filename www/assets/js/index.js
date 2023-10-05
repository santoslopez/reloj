const canvas = document.getElementById("canvas");
const ctx = canvas.getContext("2d");

//const rutaImagen = "assets/img/";

const imagenes = [
    "assets/img/josh-hild-zWCKJjPCl0s-unsplash.jpg",
    "assets/img/jonathan-notay-e_hq1euh5lI-unsplash.jpg",
    "assets/img/330GigaPixeles_217MB.jpg",
    "assets/img/fabrizio-coco-O2MKB1Pf6hE-unsplash.jpg",
    "assets/img/360.jpg",
    "assets/img/330GigaPixeles.png",
    "assets/img/208GigaPixeles.jpg",
    "assets/img/432GigaPixeles.jpg"
];
const textos = [
    "Imagen 1: 3 megas",
    "Imagen 2: 2.2 megas",
    "Imagen 3: 217.9 megas",
    "Imagen 4: 3.4 megas",
    "Imagen 5: 24.9 megas",
    "Imagen 6: 330 megas",
    "Imagen 7: 435.7 megas",
    "Imagen 8: 993.8 megas"
];

let indice = 0;
// Función para cargar y mostrar una imagen en el canvas
function cargarImagen(indice) {
    const url = imagenes[indice]; // Obtén la URL de la imagen por índice
    const image = new Image();
    image.src = url;

    image.onload = function () {
        // Limpia el canvas antes de dibujar la nueva imagen
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        // Calcula el tamaño de la imagen en el canvas
        let canvasWidth = canvas.width;
        let canvasHeight = (canvasWidth / image.width) * image.height;

        // Asegura que la imagen no se estire más allá del límite de 300x300
        if (canvasHeight > canvas.height) {
            canvasHeight = canvas.height;
            canvasWidth = (canvasHeight / image.height) * image.width;
        }

        // Centra la imagen en el canvas
        const x = (canvas.width - canvasWidth) / 2;
        const y = (canvas.height - canvasHeight) / 2;

        // Dibuja la imagen en el canvas
        ctx.drawImage(image, x, y, canvasWidth, canvasHeight);

        // Muestra el texto debajo de la imagen
        document.getElementById("imagen-texto").textContent = textos[indice];
    };

}

// Cargar la imagen inicial (en este caso, la imagen en el índice 0)
cargarImagen(indice);
// Evento para el botón "Anterior"
document.getElementById("anterior").addEventListener("click", function () {
    if (indice > 0) {
        indice--;
        cargarImagen(indice); // Cargar la imagen anterior
    }
    // Desbloquear el botón "Siguiente" ya que no estamos en la última imagen
    document.getElementById("siguiente").disabled = false;
});

// Evento para el botón "Siguiente"
document.getElementById("siguiente").addEventListener("click", function () {
    if (indice < imagenes.length - 1) {
        indice++;
        cargarImagen(indice); // Cargar la siguiente imagen
    }
    // Si estamos en la última imagen, bloquear el botón "Siguiente"
    if (indice === imagenes.length - 1) {
        document.getElementById("siguiente").disabled = true;
    }
});

// Evento para el botón "Ampliar Imagen"
document.getElementById("ampliar").addEventListener("click", function () {
    // Abre la imagen en una nueva ventana o pestaña del navegador
    window.open(imagenes[indice], "_blank");
});