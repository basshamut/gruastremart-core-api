Feature: Consulta de Solicitudes de Gruas
  Como usuario
  Quiero poder consultar los detalles de una solicitud de grua
  Para obtener información relevante sobre esta

  Scenario: Obtener los detalles de una solicitud de grua
    Given existe una solicitud con el ID "67c897048ac4702e2cd52edc"
    When el usuario consulta los detalles de la solicitud con ID "67c897048ac4702e2cd52edc"
    Then se muestran los detalles de la solicitud
