# Gestion de Livres — Android (Java)

Application Android **Java** avec 3 écrans (Login → Liste → Détail), **Room** pour la base de données, **RecyclerView**, images par livre, **inscription**, **session** (connexion/déconnexion) et passage de données entre activités via `Intent`.

---

## 📦 Fonctionnalités

* **Login / Inscription** Fibase
* **Déconnexion** depuis l’écran principal (menu toolbar)
* **Liste de livres** avec `RecyclerView` + `ViewHolder` + `Adapter`
* **Détail d’un livre**
* **Checkbox "Déjà lu"** (persistance dans Room)
* **Ajout rapide** d’un livre via FAB (+)
* **Images** par livre (à faire)

---

## 🧱 Stack & versions

* **Langage** : Java 17
* **AndroidX** : AppCompat, Material, ConstraintLayout
* **RecyclerView** : `androidx.recyclerview:recyclerview:1.3.2`
* **Room** : `2.6.1` (runtime + compiler via `annotationProcessor`)
* **AGP** : 8.5.x / **compileSdk** : 34 / **minSdk** : 24

---

## 🗂️ Structure

```
app/
 ├─ build.gradle.kts
 └─ src/main/
    ├─ AndroidManifest.xml
    ├─ java/com/example/myapplication/
    │  ├─ data/
    │  │  ├─ Book.java
    │  │  ├─ BookDao.java
    │  │  ├─ User.java
    │  │  ├─ UserDao.java
    │  │  ├─ AppDatabase.java
    │  │  └─ DbPrepopulate.java
    │  ├─ ui/
    │  │  ├─ login/
    │  │  │  ├─ LoginActivity.java
    │  │  │  └─ RegisterActivity.java
    │  │  ├─ main/
    │  │  │  ├─ MainActivity.java
    │  │  │  └─ adapter/
    │  │  │     ├─ BookAdapter.java
    │  │  │     └─ BookViewHolder.java
    │  │  └─ detail/DetailActivity.java
    │  └─ util/
    │     ├─ ExecutorsProvider.java
    │     └─ SessionManager.java
    └─ res/
       ├─ layout/
       │  ├─ activity_login.xml
       │  ├─ activity_register.xml
       │  ├─ activity_main.xml
       │  ├─ item_book.xml
       │  └─ activity_detail.xml
       ├─ drawable/
       │  ├─ book_1984.png
       │  ├─ book_etranger.png
       │  ├─ book_petit_prince.png
       │  └─ (etc.)
       └─ menu/
          └─ menu_main.xml
```

---

## Installation

1. **Clonez ce dépôt:**
   ```bash
   git clone https://github.com/DzmitryiKorjik/Gestion-de-Livres.git
   ```
   
2. Accédez au répertoire du projet :
    ```bash
    cd Gestion-de-Livres
    ```

---

## 🔧 Configuration Gradle (rappel)

### `settings.gradle.kts` (racine)

* Contient les repositories (`google()`, `mavenCentral()`), `rootProject.name` et `include(":app")`.

### `build.gradle.kts` (Projet, racine)

```kotlin
plugins { 
    id("com.android.application") version "8.5.2" apply false
    
    id("com.google.gms.google-services") version "4.4.4" apply false
}
```

### `app/build.gradle.kts` (Module app)

* Dépendances AppCompat/Material/RecyclerView/Room/Lifecycle
* `compileSdk = 34`, `minSdk = 24`, `targetSdk = 34`
* `compileOptions` en Java 17
* `com.google.firebase:firebase-bom:34.4.0` + `implementation("com.google.firebase:firebase-auth")`

> En phase TP vous pouvez activer `fallbackToDestructiveMigration()` dans `AppDatabase` pour simplifier les changements de schéma.

---

## 🗃️ Base de données (Room)

### Entités

* **Book** : `id`, `title`, `author`, `description`, `read:boolean`, `imageRes:String`
* **User** : `id`, `email`, `displayName`, `firebaseUid`

### DAO

* `BookDao` : `getAll()`, `getById(id)`, `insert(Book)`, `setRead(id, read)`
* `UserDao` : `findByEmail(email)`, `insert(User)`

### Pré-remplissage

* Dans `DbPrepopulate.insertDefaults(...)` : insertion de quelques **livres**.

### Migrations

* Pendant le cours : **facile** → `fallbackToDestructiveMigration()`.
* En prod : écrire des `Migration(x, y)` (ex. ajout de colonnes `read`, `imageRes`).

---

## 🔐 Authentification & session

* **Login** → connexion via **Firebase Authentication** (`LoginActivity`),
  utilisant `FirebaseAuth.signInWithEmailAndPassword(...)`.

* **Inscription** → création d’un **compte Firebase** (`RegisterActivity`)

   * enregistrement optionnel du profil local (`User`) dans la base `Room`
     (email, displayName, firebaseUid).

* **Session locale** → `SessionManager` (basé sur `SharedPreferences`)
  mémorise l’adresse e-mail ou l’UID Firebase de l’utilisateur connecté.

* **Déconnexion** → depuis le menu ⋮ dans `MainActivity`,
  supprime la session locale (`SessionManager.clear()`)
  et revient à `LoginActivity`.

---

## 🚦 Navigation

1. **LoginActivity** *(MAIN / LAUNCHER — `android:exported="true`)*

   * Authentification via **FirebaseAuth** (`signInWithEmailAndPassword`)
   * Bouton **Créer un compte** → `RegisterActivity`
   * Succès → redirection vers **`MainActivity`**
   * `SessionManager` sauvegarde l’utilisateur connecté (email / UID)

2. **RegisterActivity**

   * Crée un compte via **FirebaseAuth.createUserWithEmailAndPassword**
   * Met à jour le `displayName` Firebase
   * (Optionnel) Insère un `User` local (email, displayName, firebaseUid) dans la base Room
   * Retour automatique à l’écran de **Login**

3. **MainActivity**

   * Affiche la **liste des livres** via un `RecyclerView`
   * Clic sur un livre → ouvre **`DetailActivity`**
   * Checkbox **“Lu”** → met à jour le champ `read` dans la DB
   * **FAB “+”** → ouvre un **dialogue d’ajout** :

      * titre / auteur / description
      * (nouveau) sélection d’une **image** depuis la galerie, encodée en **Base64** et stockée dans la DB
   * Menu ⋮ **Déconnexion** → efface la session (`SessionManager.clear()`) et revient à `LoginActivity`

4. **DetailActivity**

   * Toolbar avec flèche de retour
   * Affiche **titre**, **auteur**, **description**, et (si présente) **l’image Base64** décodée
   * Possible évolution : bouton *“Modifier”* ou *“Supprimer”* (si ajouté plus tard)

---

## ▶️ Lancer l’app

1. Ouvrir dans **Android Studio** (Iguana ou récent)
2. Créer un **AVD** (Device Manager) ou brancher un téléphone (USB Debugging)
3. **Sync Gradle** puis **Run ▶️**

> Si vous avez modifié le schéma Room, désinstallez l’app de l’émulateur (ou activez `fallbackToDestructiveMigration()`).

---

## 🛠️ Dépannage rapide

* **Manifest merger failed (Android 12)** : ajoutez `android:exported="true"` à l’activité qui a un `<intent-filter>` (Login)
* **Cannot resolve symbol drawable** : le fichier n’existe pas ou nom invalide (pas d’accents/espaces). Remplacez par un drawable existant ou ajoutez un **vector asset** en placeholder
* **Crash après login (Room)** : migrations manquantes → activez `fallbackToDestructiveMigration()` ou écrivez les `Migration`
* **repositories error** : ne mettez pas `repositories {}` dans `build.gradle.kts` (Projet) si `settings.gradle.kts` utilise `dependencyResolutionManagement`
* **ic_launcher background manquant** : régénérez via **New → Image Asset**

---

## ✅ À faire / améliorations possibles

* Édition / suppression de livres (`@Update`, `@Delete`)
* Recherche / filtre dans la liste
* Architecture `ViewModel` + `LiveData`/`Flow` pour observer la DB
* Upload d’images depuis la galerie (URI) au lieu de `drawable`

---

## 👤 Auteurs

- **Name:** Dzmitryi
- **Education:** DevOps Fullstack
- **Objective:** Validation Android application Java
