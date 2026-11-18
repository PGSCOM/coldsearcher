# Coldsearcher
Este proyecto es un motor de búsqueda de archivos impulsado por IA.

# Escanear archivos
Para escanear archivos se necesita un servidor con los siguientes servicios:
 - Docker (A partir del archivo [docker-compose.yaml](https://github.com/PGSCOM/coldsearcher/blob/main/docker-compose.yml))
   * Whisper (TTS)
   * tika (Extracción de texto de documentos)
 - Ollama (Con el modelo [ministral-3:3b](https://ollama.com/library/ministral-3:3b))

En mi caso yo uso Tailscale para conectarme, pero en el caso que no tengas un servidor, puedes probar el proyecto con el .dat que hay en la carpeta raiz (ya funcionaría directamente).