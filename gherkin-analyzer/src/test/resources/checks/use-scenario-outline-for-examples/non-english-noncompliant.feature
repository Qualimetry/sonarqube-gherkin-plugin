# language: de
Funktionalität: Produktkauf
  Als Kunde möchte ich Produkte kaufen

  # Noncompliant
  Szenario: Kauf mit verschiedenen Zahlungsmethoden
    Angenommen ich bin als Kunde angemeldet
    Und ich habe Artikel im Warenkorb
    Wenn ich mit "<zahlungsmethode>" bezahle
    Dann wird die Bestellung erfolgreich aufgegeben

    Beispiele:
      | zahlungsmethode |
      | Kreditkarte     |
      | PayPal          |
