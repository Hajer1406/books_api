# ==============================================================================
# Étape 1 : Build (compilation du code Java avec Maven)
# ==============================================================================
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copie du descripteur de dépendances en premier pour optimiser le cache Docker
COPY pom.xml .

# Téléchargement des dépendances pour bénéficier du cache des couches
RUN mvn dependency:go-offline -B

# Copie des sources et compilation sans exécuter les tests (les tests tournent dans la CI)
COPY src/ ./src/
RUN mvn clean package -DskipTests

# ==============================================================================
# Étape 2 : Runtime (image d'exécution allégée et sécurisée)
# ==============================================================================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Installation de curl pour le HEALTHCHECK
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Création d'un utilisateur non-root pour la sécurité (Atelier 3 - Étape 5)
RUN useradd --create-home appuser && chown -R appuser /app

# Copie de l'artefact jar compilé depuis l'étape de build
COPY --from=builder --chown=appuser:appuser /app/target/*.jar app.jar

# Exécution sous l'utilisateur non-root
USER appuser

# Documentation du port d'écoute
EXPOSE 8080

# Contrôle de santé interne au conteneur (Atelier 3 - Étape 5)
HEALTHCHECK --interval=10s --timeout=3s --retries=3 \
  CMD curl -fsS http://127.0.0.1:8080/health || exit 1

# Commande de démarrage
CMD ["java", "-jar", "app.jar"]
