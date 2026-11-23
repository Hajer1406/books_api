# Fiche Récapitulative 

**Projet :** Books API (Catalogue de livres et d'auteurs)   
**Dépôt GitHub :** [https://github.com/Hajer1406/books_api](https://github.com/Hajer1406/books_api)  
**Registre de conteneurs (GHCR) :** `ghcr.io/hajer1406/books_api`  

---

## 1. Architecture de la Solution

L'architecture repose sur une pile multi-conteneurs sécurisée, isolée et persistante :

```text
             Client HTTP / Navigateur
                        │
                        ▼ (Port 8080 exposé)
          ┌───────────────────────────┐
          │   Conteneur 'api'         │
          │   Spring Boot 3 (Java 17) │
          │   Utilisateur: appuser    │
          │   Healthcheck: /health    │
          └─────────────┬─────────────┘
                        │
                        ▼ (Réseau privé interne Compose)
          ┌───────────────────────────┐
          │   Conteneur 'db'          │
          │   MySQL 8.0               │
          │   Port 3306 NON publié    │
          └─────────────┬─────────────┘
                        │
                        ▼ (Persistance)
          ┌───────────────────────────┐
          │   Volume Docker nommé     │
          │   'mysqldata_staging'     │
          └───────────────────────────┘
```

- **API REST :** Application Spring Boot 3 / Java 17 conteneurisée via un `Dockerfile` multi-stage (`maven` pour le build, `eclipse-temurin:17-jre-jammy` pour le runtime). L'exécution est confiée à un utilisateur non-privilégié `appuser` (UID 1001), et la disponibilité est surveillée par un `HEALTHCHECK` interne interrogeant `/health`.
- **Base de données :** MySQL 8.0 isolé du réseau hôte extérieur (port 3306 non exposé), communicant uniquement avec l'API à travers le réseau privé de Docker Compose.
- **Persistance :** Volume Docker nommé `mysqldata_staging` garantissant la pérennité intégrale des données après arrêt ou mise à jour de la pile.

---

## 2. Version Actuellement Déployée

- **Environnement de test / Staging :** `v1.0.0`
- **Image officielle du registre :** `ghcr.io/hajer1406/books_api:v1.0.0`
- **Tag Git immuable :** `v1.0.0`
- **État du service :** Opérationnel et sain (`HTTP 200` sur `GET /health` renvoyant `{"service":"books_api","status":"ok"}`).
- **Vérification automatique :** Le script `./scripts/wait-healthy.sh` renvoie le code `0` et affiche `staging sain`.

---

## 3. Procédure de Retour Arrière (Rollback)

En cas de mise en service d'une version défectueuse, la procédure de rollback s'exécute en quelques secondes sans aucune recompilation :

1. **Constat automatique de l'incident :**
   ```bash
   bash scripts/wait-healthy.sh
   # Échec automatique : code 1, affichage "staging non sain"
   ```

2. **Identification de la cause dans les journaux :**
   ```bash
   docker compose -f compose.staging.yaml logs --tail=50 api
   ```

3. **Bascule vers la dernière version stable connue (`v1.0.0`) :**
   ```bash
   printf 'IMAGE_TAG=v1.0.0\n' > .env.deploy
   ```

4. **Déploiement instantané de la version saine (sans rebuild) :**
   ```bash
   docker compose --env-file .env.deploy -f compose.staging.yaml up -d
   ```

5. **Confirmation de la restauration :**
   ```bash
   bash scripts/wait-healthy.sh
   # Succès immédiat : code 0, affichage "staging sain"
   ```

---

## 4. Preuves d'Exécution du Pipeline CI/CD

- **Pipeline Réussi (Tests Java 17 & 21 + Artefacts Surefire + Docker Build) :**  
  [https://github.com/Hajer1406/books_api/actions/runs/45044047815](https://github.com/Hajer1406/books_api/actions/runs/45044047815)
- **Pipeline en Échec (Erreur unitaire utile bloquant le job de build Docker) :**  
  [https://github.com/Hajer1406/books_api/actions/runs/45042166838](https://github.com/Hajer1406/books_api/actions/runs/45042166838)
- **Pipeline de Publication et Déploiement Staging sur Tag v* (Bonus +2 pts) :**  
  [https://github.com/Hajer1406/books_api/actions/runs/45081258773](https://github.com/Hajer1406/books_api/actions/runs/45081258773)  
  Publication sur GHCR suivie du déploiement automatisé en staging et validation par `scripts/wait-healthy.sh` (code 0).
