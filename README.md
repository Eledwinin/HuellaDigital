# Huella Digital - Recepción Veterinaria

**Huella Digital** Huella Digital es una aplicación móvil nativa para Android desarrollada en Kotlin y Jetpack Compose, diseñada tanto para la gestión administrativa de clínicas veterinarias como para el autoservicio de los propietarios de mascotas. Permite administrar expedientes clínicos, gestionar citas con control de cupos en tiempo real, filtrar historiales y manejar flujos de aprobación o reprogramación mediante Firebase.

------

## Enlaces del Proyecto

* **Prototipo Interactivo en Figma:** (https://www.figma.com/design/Pusy4pmku2opw3ETqiaiQL/HUELLAS?node-id=0-1&t=LDCpeC0qyOIiIpM8-1)*
* **Sitio Web para descargar App** 
* **Repositorio GitHub:** (https://github.com/eledwinin/HuellaDigital)

---

## Tecnologías y Arquitectura

* **Lenguaje:** Kotlin
* **Interfaz de Usuario:** Jetpack Compose (Material Design 3)
* **Arquitectura:** MVVM (Model-View-ViewModel) + Repository Pattern
* **Autenticación:** Firebase Authentication (Correo/Contraseña y Google Sign-In con Credential Manager)
* **Base de Datos:** Firebase Cloud Firestore (Sincronización en tiempo real)
* **Navegación:** Jetpack Navigation Compose (Transiciones y animaciones personalizadas)
* **Asincronía y Estado:** Corrutinas de Kotlin, `State` y `StateFlow`

----

## Manual de Usuario

### 1. Acceso y Registro
* **Inicio de Sesión:** Ingrese su correo y contraseña o presione **Continuar con Google**. El sistema detectará su rol automáticamente y lo enviará al panel correspondiente.
* **Recuperación de Cuenta:** Si olvidó su contraseña, use la opción *¿Olvidaste tu contraseña?* para recibir un enlace de restablecimiento en su correo.
* **Registro de Cliente:** Presione *Crea tu Cuenta*, complete sus datos y confirme su contraseña para habilitar su acceso.

---

### 2. Módulo de Recepción y Administración (Personal Clínico)

* **Búsqueda y Filtro de Pacientes:** En la pantalla principal, busque pacientes por nombre de la mascota, dueño o correlativo (`EXP-XXX`). También puede filtrar rápidamente por especie tocando los chips (*Todos*, *Perros*, *Gatos*, *Conejos*).
* **Crear un Expediente:**
  1. Presione el botón flotante **`+`**.
  2. Seleccione la especie correspondiente.
  3. Ingrese los datos obligatorios (Nombre, Raza, Dueño y Teléfono de 8 dígitos).
  4. Opcionalmente registre peso (en libras) y edad (meses o años).
  5. Presione **Guardar Expediente**.
* **Gestión del Expediente:** Toque la tarjeta de cualquier paciente para ver su ficha médica completa, editar sus datos o eliminar el registro.
* **Control de Solicitudes Entrantes:**
  1. Ingrese a la pestaña de **Solicitudes**.
  2. Revise las citas en espera y presione **Aceptar** para confirmar o **Rechazar**.
  3. Si rechaza, seleccione el motivo en el diálogo emergente para informar al dueño.
* **Agenda Diaria y Control de Asistencia:**
  1. Desde la barra inferior, acceda a **Agenda**.
  2. Use las flechas de navegación para consultar citas por fecha.
  3. En cada cita aceptada del día, marque **ATENDIDA** si el cliente asistió o **NO ASISTIÓ** si faltó.

---

### 3. Módulo de Clientes (Dueños de Mascotas)

* **Agendar una Cita:**
  1. Seleccione a su mascota desde el inicio y toque **Agendar Cita**.
  2. Elija el servicio requerido (Consulta, Vacunación, Desparasitación, etc.) y la modalidad si aplica (ej. tipo de baño).
  3. Seleccione fecha, horario disponible y confirme el envío.
* **Seguimiento en "Mis Citas":**
  * **Próximas:** Consulte las citas aceptadas para fechas vigentes o futuras.
  * **Pendientes:** Revise las solicitudes en espera de confirmación.
  * **Rechazadas:** Si una cita fue rechazada, revise el motivo del personal y toque **REPROGRAMAR CITA** para elegir un nuevo horario sin llenar todo otra vez.
  * **Historial:** Consulte el registro de consultas pasadas, citas atendidas y ausencias.
* **Filtro por Mascota:** En la parte superior de *Mis Citas*, toque el nombre de cualquiera de sus mascotas para ver solo su historial individual.

---

##Recursos y Pasos para la Ejecución

### Requisitos Previos:
* **Android Studio:** Hedgehog (2023.1.1) o superior.
* **JDK:** Versión 17 o superior.
* **Dispositivo físico o emulador:** Android 8.0 (API Nivel 26) o superior.
* **Servicios de Google:** Archivo `google-services.json` configurado en el directorio `/app`.

### Configuración de Firebase (Para nuevos entornos):
1. Crea un proyecto en la consola de [Firebase](https://console.firebase.google.com/).
2. Habilita **Firebase Authentication** (Email/Password y Google) y **Cloud Firestore**.
3. Descarga el archivo `google-services.json` desde la configuración del proyecto en Firebase.
4. Pega el archivo en la ruta `app/google-services.json` de este proyecto antes de compilar.

### Pasos para clonar y ejecutar:

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/eledwinin/HuellaDigital.git](https://github.com/eledwinin/HuellaDigital.git)
