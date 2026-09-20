# Evidencia visual v7

Fecha de captura: 2026-07-25.

Dispositivo: Motorola conectado por ADB (`ZY22GRJ5TD`).

Propósito: registrar la pasada final de UI/UX del MVP con foco en Dashboard, ventana horaria de secado, Nueva carga, Historial, Ajustes y contraste claro/oscuro.

| Archivo | Pantalla | Uso sugerido |
|---|---|---|
| `01-v7-dashboard-window-light.png` | Dashboard en modo claro | Evidenciar clima, score, ventana recomendada y carga activa. |
| `02-v7-new-load-decision-light.png` | Nueva carga en modo claro | Evidenciar tarjeta compacta de decisión, selección y formulario. |
| `03-v7-history-light.png` | Historial en modo claro | Evidenciar carga activa, estados y cargas anteriores. |
| `04-v7-settings-light.png` | Ajustes en modo claro | Evidenciar idioma, tema, permisos y preferencias. |
| `05-v7-settings-dark.png` | Ajustes en modo oscuro | Evidenciar soporte visual de tema oscuro. |
| `06-v7-dashboard-window-dark.png` | Dashboard en modo oscuro | Evidenciar contraste de ventana horaria y Dashboard. |
| `07-v7-push-notification-ready.png` | Notificación push real | Evidenciar recepción de alerta automática en dispositivo físico. |
| `07-v7-push-notification-ready-redacted.png` | Notificación push real redactada | Usar en tesis/anexos para evitar exponer SSID, multimedia u otros datos del sistema. |

Notas:

- Las capturas son evidencia manual del estado visual de la app instalada en dispositivo físico.
- No reemplazan pruebas automatizadas ni validación funcional completa.
- La captura de notificación push corresponde a una validación manual sobre backend desplegado, dispositivo registrado por FCM y evento automático recibido.
- La pantalla Ajustes mantiene como observación pendiente la coherencia del bloque Cuenta con el estado real de sesión.
- La etiqueta de ubicación visible en Historial puede requerir pulido de copy si se usa como figura final.
