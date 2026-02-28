# language: fr
Fonctionnalité: Gestion des retours
  Les clients peuvent retourner des produits dans le délai imparti.

  Scénario: Retourner un produit non endommagé
    Etant donné que le client a acheté un produit dans les 30 derniers jours
    Quand le client initie un retour
    Alors le retour est approuvé
