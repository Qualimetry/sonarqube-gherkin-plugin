# language: de
Funktionalität: Kontoverwaltung

  Szenario: Konto deaktivieren
    Angenommen das Konto ist aktiv
    # Noncompliant
    # Angenommen das Konto wurde gestern erstellt
    Wenn das Konto deaktiviert wird
    Dann ist das Konto inaktiv
