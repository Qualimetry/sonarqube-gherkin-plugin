Feature: Appointment scheduling
  Healthcare providers can manage patient appointments.

  Scenario: Schedule a new appointment
    Given the provider has available time slots
    When the receptionist books an appointment for tomorrow at 10:00 AM
    Then the appointment appears in the provider's calendar
