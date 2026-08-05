# Huella Digital - Recepción Veterinaria

**Huella Digital** es una aplicación móvil nativa para Android desarrollada en **Kotlin** y **Jetpack Compose**, orientada a la gestión eficiente de recepciones en clínicas veterinarias. Permite administrar expedientes médicos de pacientes (perros, gatos, conejos), controlar datos de contacto de propietarios y gestionar el agendamiento e historial de citas en tiempo real con Firebase Firestore.

------

## Enlaces del Proyecto

* **Prototipo Interactivo en Figma:** (https://www.figma.com/design/Pusy4pmku2opw3ETqiaiQL/HUELLAS?node-id=0-1&t=LDCpeC0qyOIiIpM8-1)*
* **Sitio Web para descargar App** 
* **Repositorio GitHub:** (https://github.com/eledwinin/HuellaDigital)

---

## Tecnologías y Arquitectura

* **Lenguaje:** Kotlin
* **Interfaz de Usuario:** Jetpack Compose (Material Design 3)
* **Arquitectura:** MVVM (Model-View-ViewModel)
* **Base de Datos:** Firebase Cloud Firestore (Sincronización en tiempo real)
* **Navegación:** Jetpack Navigation Compose
* **Asincronía:** Corrutinas de Kotlin y `StateFlow`

----

## Manual de Usuario

### 1. Inicio de Sesión
* Al abrir la aplicación, ingrese con las credenciales asignadas para el personal de recepción de la clínica.
* Al autenticarse correctamente, el sistema redirigirá al **Panel de Control**.
* ////
* De otro modo, puede iniciar sesión con una cuenta oficial de la veterinaria

### 2. Panel de Control (Pantalla Principal)
* **Búsqueda:** Utilice la barra superior para buscar expedientes ingresando el nombre de la mascota, el nombre del dueño o el código correlativo (`EXP-XXX`).
* **Filtros rápidos:** Filtre los pacientes por especie seleccionando los chips: *Todos*, *Perros*, *Gatos* o *Conejos*.
* **Agenda Diaria:** Presione el icono de calendario en la esquina superior derecha para ver todas las citas del día actual.

### 3. Registro de Nuevo Expediente
1. Presione el botón flotante con el icono **`+`**.
2. Seleccione la especie correspondiente (*Perro*, *Gato* o *Conejo*).
3. Complete la información obligatoria: Nombre de la mascota, Raza, Nombre del dueño y Teléfono de contacto (validado a 8 dígitos).
4. Ingrese opcionalmente la edad (seleccionando la unidad en **Años** o **Meses**) y el peso (restringido a valores numéricos con hasta 2 decimales en **lbs**).
5. Presione **Guardar Expediente**.

### 4. Expediente Clínico e Historial de Citas
* Al presionar cualquier tarjeta del paciente, accederá a su **Expediente Clínico**.
* **Edición / Eliminación:** En el panel superior puede editar los datos o eliminar el expediente mediante los iconos de lápiz y basurero.
* **Historial:** Visualice las citas clasificadas automáticamente como **PENDIENTE** o **FINALIZADO** según la fecha y hora programada.

### 5. Agendamiento de Citas
1. Desde la tarjeta de la mascota o dentro de su expediente, presione **Agendar Cita**.
2. Seleccione el tipo de servicio (*Consulta Médica*, *Vacunación*, *Desparasitación*, *Corte de Uñas*, *Baño y Estética*, etc.).
3. Si selecciona *Baño*, elija la modalidad (*Básico*, *Medicada*, *Antipulgas*, *Corte Sanitario*).
4. Seleccione la fecha y la hora deseada y confirme el agendamiento.

----

##Recursos y Pasos para la Ejecución

### Requisitos Previos:
* **Android Studio:** Hedgehog (2023.1.1) o superior.
* **JDK:** versión 17 o superior.
* **Dispositivo físico o emulador:** Android 8.0 (API Nivel 26) o superior.
* Conexión a Internet (necesaria para la sincronización con Firebase Firestore).

### Pasos para clonar y ejecutar:

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/eledwinin/HuellaDigital.git](https://github.com/eledwinin/HuellaDigital.git)
