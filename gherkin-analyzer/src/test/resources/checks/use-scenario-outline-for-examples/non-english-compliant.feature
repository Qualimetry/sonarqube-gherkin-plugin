# language: de
Funktionalität: Benutzeranmeldung
  Als Benutzer möchte ich mich anmelden

  Szenariogrundriss: Anmeldung mit verschiedenen Benutzertypen
    Angenommen ich bin auf der Anmeldeseite
    Wenn ich "<benutzername>" und "<passwort>" eingebe
    Dann bin ich als "<benutzertyp>" angemeldet

    Beispiele:
      | benutzername    | passwort  | benutzertyp   |
      | admin@test.com  | admin123  | Administrator |
      | user@test.com   | user123   | Kunde         |
