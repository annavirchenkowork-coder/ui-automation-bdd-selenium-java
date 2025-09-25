@regression @cura @appointment
Feature: Book an appointment on CURA Healthcare Service

  Background:
    Given the user is on the CURA home page
    And the user logs into CURA with username "John Doe" and password "ThisIsNotAPassword"

  @happy_path
  Scenario: Book an appointment and verify confirmation details
    When the user opens the Make Appointment form
    And the user fills the appointment form with:
      | facility         | Hongkong CURA Healthcare Center |
      | applyReadmission | true                            |
      | program          | Medicaid                        |
      | visitDate        | 05/10/2025                      |
      | comment          | Automation booking via Cucumber |
    And the user submits the appointment
    Then the confirmation page should show:
      | facility         | Hongkong CURA Healthcare Center |
      | applyReadmission | Yes                             |
      | program          | Medicaid                        |
      | visitDate        | 05/10/2025                      |
      | comment          | Automation booking via Cucumber |

  @validation
  Scenario: Date is required to book an appointment
    When the user opens the Make Appointment form
    And the user fills the appointment form with:
      | facility         | Tokyo CURA Healthcare Center |
      | applyReadmission | false                        |
      | program          | Medicare                     |
      | visitDate        |                              |
      | comment          | Missing date test            |
    And the user submits the appointment
    Then the appointment should not be submitted
    And the user should remain on the Make Appointment page