# Books API

Projet de fin de module DevOps — Licence 3 DSI (ISET).
API REST de gestion d'un catalogue de livres et d'auteurs, construite avec **Spring Boot 3** et **MySQL**.

## Table des matières
- [Description](#description)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Démarrage rapide en local](#démarrage-rapide-en-local)
- [Points d'accès de l'API](#points-daccès-de-lapi)
- [Conventions DevOps et Git](#conventions-devops-et-git)

---

## Description

Books API fournit des points d'accès REST pour créer, lire, modifier et supprimer des livres, ainsi que filtrer par auteur. Elle intègre également un endpoint dédié de contrôle de santé `/health`.

## Architecture

Le détail complet de l'architecture est documenté dans [docs/architecture.md](docs/architecture.md).

## Prérequis

- **Java JDK 17** ou supérieur
- **Maven 3.8+** (ou utiliser `./mvnw`)
- **MySQL 8+** (ou conteneurisé via Docker)
- **Git 2.40+**

## Démarrage en une seule commande (Docker Compose)

Pour démarrer toute la pile applicative (l'API Spring Boot et la base de données MySQL) en une seule commande :

```bash
docker compose up -d --build
```

Compose va orchestrer :
1. Le démarrage de la base de données MySQL `db` et de son volume persistant `mysqldata`.
2. L'attente de l'état `healthy` de la base via son healthcheck.
3. Le build et le démarrage automatique de l'API `api` connectée à MySQL.

Pour vérifier que le service est opérationnel :
```bash
curl http://localhost:8080/health
```
Réponse attendue :
```json
{"service":"books_api","status":"ok"}
```

Pour arrêter la pile :
```bash
docker compose down
```

Pour arrêter la pile et purger le volume persistant :
```bash
docker compose down -v
```

## Démarrage en local (sans Docker)

1. **Cloner le dépôt** :
   ```bash
   git clone https://github.com/Hajer1406/books_api.git
   cd books_api
   ```

2. **Configurer l'environnement** :
   Copier le fichier d'exemple et adapter selon votre configuration :
   ```bash
   cp .env.example .env
   ```

3. **Lancer l'application** :
   Sous Linux / macOS / WSL2 :
   ```bash
   ./mvnw spring-boot:run
   ```
   Sous Windows PowerShell :
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

4. **Vérifier l'état de l'application** :
   ```bash
   curl http://localhost:8080/health
   ```
   Réponse attendue :
   ```json
   {"service":"books_api","status":"ok"}
   ```

## Points d'accès de l'API

- `GET /health` : Statut du service (code HTTP 200).
- `GET /api` : Liste de tous les livres.
- `GET /api/{id}` : Consultation d'un livre par ID.
- `POST /api` : Ajout d'un livre (corps JSON).
- `PUT /api` : Mise à jour d'un livre.
- `DELETE /api/{id}` : Suppression d'un livre.

## Conventions DevOps et Git

1. **Protection de main** : aucun push direct sur la branche `main`.
2. **Branches de fonctionnalités** : chaque ajout passe par une branche dédiée `feature/...`, `fix/...`, `docs/...` ou `chore/...`.
3. **Merge Requests / Pull Requests** : toute modification fait l'objet d'une revue et d'une demande de fusion.
4. **Commits conventionnels** : messages préfixés (`feat:`, `fix:`, `docs:`, `chore:`, `test:`, `ci:`).
5. **Zéro secret** : les fichiers `.env` et données sensibles sont ignorés par Git.
