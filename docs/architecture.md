# Architecture - Books API

## Présentation générale

Books API est un microservice REST développé avec **Spring Boot 3** et **Java 17**, conçu pour la gestion d'un catalogue de livres et de leurs auteurs (écrivains). L'application s'appuie sur **Spring Data JPA** pour la persistance dans une base de données relationnelle **MySQL**.

## Composants du système

```
   Client (Web / HTTP / curl)
               │
               ▼  HTTP:8080
   ┌──────────────────────────────────────────────┐
   │ Books API (Spring Boot)                      │
   │                                              │
   │  ┌────────────────────────────────────────┐  │
   │  │ REST Controllers                       │  │
   │  │  - HealthController (GET /health)      │  │
   │  │  - LivreRESTController (/api)          │  │
   │  └───────────────────┬────────────────────┘  │
   │                      ▼                       │
   │  ┌────────────────────────────────────────┐  │
   │  │ Services (LivreService)                │  │
   │  └───────────────────┬────────────────────┘  │
   │                      ▼                       │
   │  ┌────────────────────────────────────────┐  │
   │  │ Repositories (Spring Data JPA)         │  │
   │  │  - LivreRepository                     │  │
   │  └───────────────────┬────────────────────┘  │
   └──────────────────────┼───────────────────────┘
                          │ TCP:3306
                          ▼
   ┌──────────────────────────────────────────────┐
   │ Base de données MySQL                        │
   │  - Table: livre                              │
   │  - Table: ecrivain                           │
   └──────────────────────────────────────────────┘
```

## Points d'accès (Endpoints REST)

| Méthode | Route | Rôle |
|---|---|---|
| `GET` | `/health` | Contrôle de santé de l'API (renvoie `{"status": "ok"}`) |
| `GET` | `/api` | Liste l'ensemble des livres |
| `GET` | `/api/{id}` | Récupère le détail d'un livre par son identifiant |
| `POST` | `/api` | Crée un nouveau livre |
| `PUT` | `/api` | Met à jour un livre existant |
| `DELETE` | `/api/{id}` | Supprime un livre par son identifiant |
| `GET` | `/api/livrsecriv/{idEcrivain}` | Liste les livres associés à un écrivain donné |

## Sécurité et gestion des secrets

- Aucun secret, mot de passe ou jeton d'accès n'est inscrit dans le code source ou versionné dans Git.
- Les paramètres de connexion sont injectés par variables d'environnement (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`).
- Le fichier `.gitignore` bloque strictement l'indexation de tout fichier `.env` ou fichier de log.
