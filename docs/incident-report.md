# Compte-Rendu d'Incident et Démonstration du Rollback (Atelier 7)

1. **Que s'est-il passé ?**  
   Lors du déploiement en environnement de staging de la version `IMAGE_TAG=v0.9.0`, l'image déployée ne contenait pas l'application Spring Boot attendue, refusant toutes les connexions sur le port applicatif.

2. **À quel moment l'avons-nous su ?**  
   Immédiatement après la mise en service, lors de l'exécution automatique du script de contrôle de santé `./scripts/wait-healthy.sh`, qui a constaté 12 échecs successifs de connexion (`curl: (56) Recv failure: Connection reset by peer`) et a levé une alerte avec le code de sortie non nul `1` (`staging non sain`).

3. **Ce que nous avons fait :**  
   Nous avons inspecté les journaux du conteneur avec `docker compose -f compose.staging.yaml logs --tail=50 api` pour relever la première erreur utile, puis nous avons réassigné la variable `IMAGE_TAG=v1.0.0` dans `.env.deploy` et réappliqué la pile instantanément via `docker compose --env-file .env.deploy -f compose.staging.yaml up -d` sans reconstruire l'image.

4. **Combien de temps le service a été indisponible ?**  
   L'indisponibilité a duré environ 1 minute et 20 secondes (60 secondes de détection d'anomalie par le script de santé + 20 secondes pour la recréation du conteneur et la restauration complète de la version saine `v1.0.0`).

5. **Ce qui empêcherait que cela se reproduise :**  
   Rendre obligatoire dans le pipeline CI/CD un smoke test automatisé du conteneur après le build, et conditionner tout étiquetage (tag) de version ou déploiement à la validation préalable de ce contrôle de santé.
