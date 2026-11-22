# Books API

Projet de fin de module DevOps — Licence 3 DSI (ISET).
API REST de gestion d'un catalogue de livres et d'auteurs, construite avec **Spring Boot 3** et **MySQL**.

## Table des matières
- [Description](#description)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Démarrage rapide en local](#démarrage-rapide-en-local)
- [Points d'accès de l'API](#points-daccès-de-lapi)
- [Pipeline CI/CD](#pipeline-cicd)
- [Conventions DevOps et Git](#conventions-devops-et-git)

---

## Description

Books API fournit des points d'accès REST pour créer, lire, modifier et supprimer des livres, ainsi que filtrer par auteur. Elle intègre également un endpoint dédié de contrôle de santé `/health`.

## Architecture et Documentation

- [Détail de l'architecture logicielle et réseau](docs/architecture.md)
- [Fiche Récapitulative du Projet](docs/fiche-recap.md)
- [Rapport d'incident et démonstration de Rollback](docs/incident-report.md)

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

## Déploiement en environnement de recette (Staging)

L'environnement de staging réutilise l'image immuable publiée sur GitHub Container Registry (`ghcr.io/hajer1406/books_api:${IMAGE_TAG}`) sans **jamais la reconstruire** :

1. **Définir la version à déployer** :
   ```bash
   printf 'IMAGE_TAG=v1.0.0\n' > .env.deploy
   ```

2. **Télécharger l'image depuis le registre** :
   ```bash
   docker compose --env-file .env.deploy -f compose.staging.yaml pull
   ```

3. **Mettre en service la pile Staging** :
   ```bash
   docker compose --env-file .env.deploy -f compose.staging.yaml up -d
   ```

4. **Contrôler automatiquement la santé du service** :
   ```bash
   ./scripts/wait-healthy.sh
   ```
   *(Le script vérifie l'endpoint `/health` avec 12 tentatives de 5 secondes, affiche `staging sain` et renvoie le code 0 en succès, ou `staging non sain` et le code 1 en cas de timeout).*

### Procédure de Retour Arrière (Rollback)

Si une version déployée est défectueuse (ex: `IMAGE_TAG=v0.9.0`) et fait échouer `wait-healthy.sh` :
1. Consulter les logs pour identifier la cause :
   ```bash
   docker compose -f compose.staging.yaml logs --tail=50 api
   ```
2. Revenir immédiatement à la dernière version saine :
   ```bash
   printf 'IMAGE_TAG=v1.0.0\n' > .env.deploy
   docker compose --env-file .env.deploy -f compose.staging.yaml up -d
   ```
3. Confirmer la restauration :
   ```bash
   ./scripts/wait-healthy.sh
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

## Pipeline CI/CD

Le projet intègre un pipeline d'intégration continue GitHub Actions (`.github/workflows/ci.yml`) déclenché à chaque Pull Request vers la branche `main` ainsi que sur les tags de release (`v*`). Il orchestre :
1. **Contrôle de conformité** automatique via le script `scripts/check-project.sh`.
2. **Matrice de tests** multi-versions Java (`Java 17` et `Java 21`).
3. **Publication d'artefacts** : rapports de tests Surefire XML téléchargeables.
4. **Construction de l'image Docker** (`docker-build`) strictement conditionnée par la réussite des tests (`needs: test`).
5. **Déploiement automatique en Staging et contrôle de santé** (`deploy-staging`) déclenché sur les tags `v*` (Bonus +2 pts).

### Preuves d'exécution du pipeline (Livrables demandés)

- **Exécution Réussie (Succès) :**
  - **Lien de l'exécution :** [https://github.com/Hajer1406/books_api/actions/runs/45044047815](https://github.com/Hajer1406/books_api/actions/runs/45044047815)
  - **Statut :** Tous les tests unitaires et d'intégration validés sur Java 17 et Java 21, artefacts générés et téléchargés, image Docker construite avec succès.

- **Exécution en Échec (Démonstration d'erreur bloquante) :**
  - **Lien de l'exécution :** [https://github.com/Hajer1406/books_api/actions/runs/45042166838](https://github.com/Hajer1406/books_api/actions/runs/45042166838)
  - **Premier message d'erreur utile :** `IllegalStateException: Failed to load ApplicationContext` dans la suite de tests de contrôleur, provoquant `exit code 1` dans Maven.
  - **Comportement DevOps validé :** Grâce à la clause `needs: test`, le job `docker-build` a été immédiatement bloqué (`skipped`). La Pull Request a été marquée en rouge, empêchant tout déploiement ou fusion de code défaillant sur `main`.

## Conventions DevOps et Git

1. **Protection de main** : aucun push direct sur la branche `main`.
2. **Branches de fonctionnalités** : chaque ajout passe par une branche dédiée `feature/...`, `fix/...`, `docs/...` ou `chore/...`.
3. **Merge Requests / Pull Requests** : toute modification fait l'objet d'une revue et d'une demande de fusion.
4. **Commits conventionnels** : messages préfixés (`feat:`, `fix:`, `docs:`, `chore:`, `test:`, `ci:`).
5. **Zéro secret** : les fichiers `.env` et données sensibles sont ignorés par Git.
