Feature: Appointment scheduling
  Healthcare providers can manage patient appointments.

  Rule: Standard appointments

    Scenario: Schedule a standard appointment
      Given the provider has available time slots
      When the receptionist books a standard appointment
      Then the appointment is confirmed
