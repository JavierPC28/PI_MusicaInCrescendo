# PI_MusicaInCrescendo

Aplicación Android desarrollada en Kotlin usando Jetpack Compose, Firebase Realtime Database y arquitectura MVVM para facilitar la organización de eventos musicales y la distribución de partituras.

## Características

- **Creación de eventos**: El director puede crear, editar y eliminar eventos musicales.
- **Confirmación de asistencia**: Los músicos marcan su asistencia a cada evento.
- **Gestión de partituras**: El director sube partituras, y los músicos solo ven las de sus instrumentos.
- **Perfil de usuario**: Los músicos seleccionan sus instrumentos y editan su información personal.
- **Mapa de eventos**: Visualización de la ubicación, duración y hora de inicio de cada evento.

## Stack Tecnológico

- **Lenguaje**: Kotlin
- **UI**: Jetpack Compose
- **Arquitectura**: Model-View-ViewModel (MVVM)
- **Backend**: Firebase Authentication, Realtime Database
- **IDE**: Android Studio

## Arquitectura

Se sigue el patrón MVVM:

1. **Model**: Clases de datos y repositorios para la conexión con Firebase.
2. **ViewModel**: Lógica de negocio y LiveData para la UI.
3. **View**: Composables de Jetpack Compose que observan el estado del ViewModel.

## Comenzando

### Instalación

1. Clona el repositorio:
   ```bash
   git clone https://github.com/JavierPC28/PI_MusicaInCrescendo.git
   ```
2. Abre el proyecto en Android Studio.
3. Compila y ejecuta en un emulador o dispositivo.

## Uso

1. Regístrate o inicia sesión con Firebase Auth.
2. Si eres director, crea un nuevo evento y sube partituras.
3. Si eres músico, configura tu perfil y confirma asistencia.
4. Consulta el mapa para ver la ubicación y detalles de los eventos.

## Contribuciones

¡Las contribuciones son bienvenidas! Por favor, abre un _pull request_ o issue para sugerir mejoras.

## Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.
