# Induztek Protocolos

Aplicación Android para registrar **pruebas eléctricas en terreno** (resistencia de aislación, resistencia de devanados y relación de transformación) sobre equipos como transformadores, celdas y cables de media tensión. El técnico registra el equipo, ingresa las mediciones, toma una foto de la placa y la app genera el **protocolo en PDF** para ver o compartir, comparando cada medición con la prueba anterior del mismo equipo.

Proyecto de la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles** (Evaluación Parcial 2). Los datos de clientes y equipos son ficticios.

## Integrantes

- Uciell Madrid
- Katalina Mora

## Funcionalidades implementadas

| Pantalla | Qué hace |
|---|---|
| Login | Ingreso con correo y contraseña, validación por campo y mensaje de credenciales incorrectas |
| Equipos | Lista de equipos con buscador por código, cliente o instalación |
| Nuevo / editar equipo | Formulario con campos obligatorios validados (código, tipo, cliente, instalación) |
| Ficha del equipo | Datos del equipo, historial de pruebas, editar y eliminar |
| Nueva prueba | Mediciones AT–tierra, BT–tierra y AT–BT con validación, comparación con la prueba anterior y alerta si el valor bajó |
| Cámara | Foto de la placa con CameraX (flash y cambio de cámara) |
| Protocolo | Resumen de la prueba y PDF generado en el teléfono para ver o compartir |
| Protocolos | Lista de protocolos generados |
| Sincronizar | Estado de los protocolos pendientes de enviar |

**Requisitos de la evaluación cubiertos**

- **Formularios validados:** cada campo marca el error en rojo, con ícono y mensaje bajo el campo. Las validaciones están en los ViewModel, no en las pantallas.
- **Animaciones:** mensajes de error que aparecen con animación, borde que cambia de color cuando una medición bajó, botón Guardar → Guardando… → Guardado y foto que aparece con animación.
- **Persistencia local:** base de datos Room (`induztek.db`) con las tablas `equipos` y `pruebas` relacionadas por clave foránea.
- **Recursos nativos:**
  - **Cámara** (CameraX), con solicitud de permiso en tiempo de ejecución.
  - **Vibración**: una vibración corta al guardar correctamente y dos cortas cuando el formulario tiene errores.
  - Generación de PDF y uso de la hoja de compartir de Android (FileProvider).
- **Diseño adaptable:** barra de navegación inferior en celular y riel lateral en tablet (WindowSizeClass).

## Arquitectura (MVVM)

```
app/src/main/java/cl/duoc/induztek/
├── data/          Model: entidades Room, DAO, AppDatabase y repositorios
├── ui/            ViewModel y estados de formulario (FormState)
│   ├── navigation/   NavHost y rutas
│   ├── screen/       Pantallas en Jetpack Compose
│   └── theme/        Colores y tipografía
├── util/          PDF, imágenes y vibración
└── MainActivity.kt
```

## Tecnologías

Kotlin · Jetpack Compose · Material 3 · Navigation Compose · Room (KSP) · CameraX · ViewModel + StateFlow

## Pasos para ejecutar

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/KatUci/Induztek.git
   ```
2. Abrir la carpeta `Induztek` en **Android Studio** (File → Open).
3. Esperar a que termine el **Gradle sync**.
4. Elegir un emulador o un teléfono con depuración USB (Android 8.0 o superior) y presionar **Run ▶**.
5. Ingresar con el usuario de prueba:
   - Correo: `tecnico1@induztek.test`
   - Contraseña: `1234`
