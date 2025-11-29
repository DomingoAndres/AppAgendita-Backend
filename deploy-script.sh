#!/bin/bash

# Script de despliegue para Cloud Run con caracteres especiales
DB_PASSWORD='<Vsx%P;y1;.$Eu1H'

# Desplegar msvc-user
gcloud run deploy msvc-user \
  --image gcr.io/starlit-tube-479621-g3/msvc-user \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --memory 512Mi \
  --timeout 300 \
  --set-env-vars "SPRING_CONFIG_IMPORT=configserver:https://msvc-config-74jugglhoa-uc.a.run.app,DB_HOST=136.119.250.44,DB_NAME=agendita_users_dev,DB_USER=duoc_uc,DB_PASSWORD=${DB_PASSWORD}"

# Desplegar msvc-task  
gcloud run deploy msvc-task \
  --image gcr.io/starlit-tube-479621-g3/msvc-task \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --memory 512Mi \
  --timeout 300 \
  --set-env-vars "SPRING_CONFIG_IMPORT=configserver:https://msvc-config-74jugglhoa-uc.a.run.app,DB_HOST=136.119.250.44,DB_NAME=agendita_tasks_dev,DB_USER=duoc_uc,DB_PASSWORD=${DB_PASSWORD}"

# Desplegar msvc-note
gcloud run deploy msvc-note \
  --image gcr.io/starlit-tube-479621-g3/msvc-note \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --memory 512Mi \
  --timeout 300 \
  --set-env-vars "SPRING_CONFIG_IMPORT=configserver:https://msvc-config-74jugglhoa-uc.a.run.app,DB_HOST=136.119.250.44,DB_NAME=agendita_notes_dev,DB_USER=duoc_uc,DB_PASSWORD=${DB_PASSWORD}"

# Desplegar msvc-event
gcloud run deploy msvc-event \
  --image gcr.io/starlit-tube-479621-g3/msvc-event \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --memory 512Mi \
  --timeout 300 \
  --set-env-vars "SPRING_CONFIG_IMPORT=configserver:https://msvc-config-74jugglhoa-uc.a.run.app,DB_HOST=136.119.250.44,DB_NAME=agendita_events_dev,DB_USER=duoc_uc,DB_PASSWORD=${DB_PASSWORD}"