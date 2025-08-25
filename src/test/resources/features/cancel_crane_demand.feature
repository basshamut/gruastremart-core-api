Feature: Cancelación de Solicitudes de Grúas
  Como usuario
  Quiero poder cancelar una solicitud de grúa
  Para anular una solicitud que ya no necesito

  Scenario: Cancelar una solicitud de grúa existente
    Given existe una solicitud con el ID "67c897048ac4702e2cd52edc"
    When el usuario cancela la solicitud con ID "67c897048ac4702e2cd52edc"
    Then la solicitud se cancela exitosamente

  Scenario: Intentar cancelar una solicitud que no existe
    Given no existe una solicitud con el ID "invalid-id"
    When el usuario intenta cancelar la solicitud con ID "invalid-id"
    Then se muestra un error de solicitud no encontrada