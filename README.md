# Rang Moy Gaaw — Backend

Service backend de **Rang Moy Gaaw**, une plateforme de mobilité urbaine pour le transport sénégalais (bus, BRT, navettes maritimes) : suivi des bus en temps réel, achat de tickets par paiement mobile, signalement d'incidents et assistant conversationnel français/wolof.

- Interface web : [RangMouyGaaw-Frontend](https://github.com/Ndeye-Fatou-SOW/RangMouyGaaw-Frontend)
- Démonstration vidéo : [voir la vidéo](https://ucadedu-my.sharepoint.com/:v:/g/personal/ndeyefatou20_sow_ucad_edu_sn/IQDv5U1DwAalRaEkq-WVOAlZATMTkjBp15FNe6y_w9BDXWY?e=w7iGvX)

## Fonctionnalités

- **Suivi GPS en temps réel** : les positions des bus arrivent par Kafka, sont mises en cache dans Redis et diffusées aux clients par WebSocket (STOMP).
- **Gestion du réseau** : bus, lignes, chauffeurs et incidents (API REST, statistiques d'incidents).
- **Billetterie** : achat de tickets avec adaptateurs de paiement PayTech, Wave et Orange Money.
- **Assistant « Dama dem »** : répond aux questions sur le transport en français et en wolof, à l'écrit comme à l'oral, à partir des données de la base (API Google Gemini).
- **Notifications** : Firebase et SMS Orange.
- **Sécurité** : Spring Security, authentification par JWT, rôles administrateur et citoyen.

## Architecture

Architecture hexagonale (ports et adaptateurs) :

```
domain/          modèles, événements, objets-valeurs et ports
application/     requêtes applicatives
infrastructure/  adaptateurs : web (REST, WebSocket), persistance JPA,
                 messagerie Kafka, paiement, notification, IA, sécurité
```

## Technologies

Java 21 · Spring Boot 4 · Spring Security · JWT · Spring Data JPA · PostgreSQL (pgvector) · Kafka · Redis · WebSocket STOMP · Docker Compose · OpenAPI (springdoc)

## Lancer le projet

Prérequis : Java 21, Docker.

```bash
# 1. Démarrer PostgreSQL, Redis et Kafka
docker compose up -d

# 2. Définir les variables d'environnement listées dans .env.example
#    (base de données, secret JWT, clés PayTech et Gemini)

# 3. Lancer l'application
./mvnw spring-boot:run
```

Aucune clé n'est stockée dans le dépôt : tous les secrets passent par des variables d'environnement.

## Principales routes

| Route | Rôle |
|---|---|
| `/api/v1/auth` | inscription et connexion |
| `/api/v1/transport` | bus, lignes, chauffeurs, incidents, positions |
| `/api/v1/transport/tickets` | achat et consultation des tickets |
| `/api/v1/transport/assistant` | assistant texte et vocal |

## Auteure

Ndéye Fatou SOW — Master 2 Systèmes d'Information Répartis, Université Cheikh Anta Diop de Dakar
Portfolio : https://ndeye-fatou-sow.github.io/ndeyefatousow.github.io/
