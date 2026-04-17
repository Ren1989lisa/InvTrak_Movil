# Backend Error Handling para QR Scanner

## Endpoint: `/api/resguardo/verificar/{id}`

### Respuestas Esperadas:

#### ✅ **Éxito (200)** - Resguardo válido y pendiente
```json
{
  "idResguardo": 123,
  "confirmado": false,
  "fechaAsignacion": "2024-01-15",
  "activo": {
    "idActivo": 456,
    "etiquetaBien": "LAP001",
    "producto": {
      "nombre": "Laptop Dell Inspiron"
    }
  },
  "usuario": {
    "idUsuario": 789,
    "nombre": "Juan Pérez"
  }
}
```

#### ❌ **Error 400** - Validación de negocio
```json
{
  "success": false,
  "message": "Este bien no está asignado a tu cuenta o ya fue confirmado",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### ❌ **Error 404** - Activo no encontrado
```json
{
  "success": false,
  "message": "No se encontró un activo con el ID proporcionado",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### ❌ **Error 401** - No autorizado
```json
{
  "success": false,
  "message": "Sesión expirada. Inicia sesión nuevamente",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### ❌ **Error 403** - Sin permisos
```json
{
  "success": false,
  "message": "No tienes permisos para acceder a este activo",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### ❌ **Error 500** - Error interno real
```json
{
  "success": false,
  "message": "Error interno del servidor. Contacta al administrador",
  "error": "DatabaseConnectionException",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Ejemplo de Controller Spring Boot:

```java
@RestController
@RequestMapping("/api/resguardo")
public class ResguardoController {

    @GetMapping("/verificar/{activoId}")
    public ResponseEntity<?> verificarResguardoQR(@PathVariable Long activoId) {
        try {
            // Obtener usuario actual del token JWT
            Long usuarioId = getCurrentUserId();
            
            // Buscar resguardo
            Optional<Resguardo> resguardo = resguardoService
                .findByActivoIdAndUsuarioId(activoId, usuarioId);
            
            if (resguardo.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse(false, "Este bien no está asignado a tu cuenta")
                );
            }
            
            if (resguardo.get().isConfirmado()) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse(false, "Este resguardo ya fue confirmado")
                );
            }
            
            // Éxito - devolver resguardo
            return ResponseEntity.ok(resguardo.get());
            
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(
                new ErrorResponse(false, e.getMessage())
            );
        } catch (Exception e) {
            log.error("Error interno verificando resguardo", e);
            return ResponseEntity.status(500).body(
                new ErrorResponse(false, "Error interno del servidor")
            );
        }
    }
}
```

## Casos de Uso:

1. **QR válido** → 200 + datos del resguardo → Abre checklist
2. **QR no asignado** → 400 + mensaje → Snackbar con error
3. **QR ya confirmado** → 400 + mensaje → Snackbar con error  
4. **QR inexistente** → 404 + mensaje → Snackbar con error
5. **Sin permisos** → 403 + mensaje → Snackbar con error
6. **Error real** → 500 + mensaje → Snackbar con error técnico