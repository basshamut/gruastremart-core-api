Feature: Completar Solicitudes de Grúas
  Como operador
  Quiero poder completar una solicitud de grúa
  Para marcar como finalizado un servicio de grúa

  Scenario: Completar una solicitud de grúa existente
    Given existe una solicitud con el ID "67c897048ac4702e2cd52edc"
    When el operador completa la solicitud con ID "67c897048ac4702e2cd52edc"
    Then la solicitud se completa exitosamente

  Scenario: Intentar completar una solicitud que no existe
    Given no existe una solicitud con el ID "invalid-id"
    When el operador intenta completar la solicitud con ID "invalid-id"
    Then se muestra un error de solicitud no encontrada