# language: fr
Fonctionnalité: Gestion des commandes

  Scénario: Passer une commande standard
    Soit le client est connecté
    Quand le client valide son panier
    Alors la commande est créée

  Scénario: Annuler une commande
    Soit le client a une commande en cours
    Quand le client annule la commande
    Alors la commande est annulée
