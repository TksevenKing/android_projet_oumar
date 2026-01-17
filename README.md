# WariTrack

Mini-projet Android Studio (Java) pour suivre des dépenses avec une architecture MVVM + Repository, Room et RecyclerView.

## Prérequis
- Android Studio Hedgehog (ou plus récent)
- JDK 17
- Android SDK 34

## Lancer le projet
1. Ouvrir le dossier du projet dans Android Studio.
2. Laisser Gradle synchroniser les dépendances.
3. Lancer l'app sur un émulateur ou un appareil physique.

## Fonctionnalités MVP
- CRUD de dépenses (montant, catégorie, date, note).
- Liste triée par date décroissante avec recherche par note/catégorie.
- Filtre par catégorie via Spinner.
- Statistiques : total du mois courant, total global, top 3 catégories.
- Validation : montant > 0, catégorie non vide.
- UX : FAB pour ajouter, Snackbar pour annuler une suppression.

## Captures attendues (texte)
- Écran principal : recherche, filtre, stats, liste des dépenses, bouton "+".
- Écran ajout/modification : champs montants/catégorie/date/note et bouton enregistrer.

## Scénarios de test
1. Ajouter une dépense valide.
2. Modifier la dépense.
3. Supprimer la dépense puis annuler la suppression via le Snackbar.
4. Rechercher par note ou catégorie.
5. Filtrer une catégorie spécifique.
6. Vérifier la mise à jour des totaux et du top 3 catégories.

## Structure du projet
- `com.waritrack.data` : Entity, DAO, Database, Repository.
- `com.waritrack.ui` : Activities, ViewModels, adapter RecyclerView.
- `com.waritrack.util` : utilitaires (format date).
