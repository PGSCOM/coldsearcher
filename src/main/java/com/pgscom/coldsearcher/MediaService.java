package com.pgscom.coldsearcher;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class MediaService {

    private static String MODELO_WHISPER = "turbo";
    private String URL_WHISPER = "http://pc-pgscom:9000";

    public String transcribirVideo(File video) {
        if (video == null || !video.exists()) {
            System.err.println("Fichero de video no encontrado: " + video);
            return "";
        }
        System.out.println("Extrayendo audio de: " + video.getName());
        File audioTmp = extraerAudioTemp(video);
        if (audioTmp == null) {
            System.err.println("Error al extraer el audio de " + video.getName());
            return "";
        }
        String transcripcion = transcribirAudio(audioTmp);
        audioTmp.delete();
        return transcripcion;
    }

    private File extraerAudioTemp(File video) {
        try {
            File audio = File.createTempFile("cs_audio_", ".wav");

            ProcessBuilder builder = new ProcessBuilder(
                    "ffmpeg",
                    "-y",
                    "-i", video.getAbsolutePath(),
                    "-vn",
                    "-ar", "16000",
                    "-ac", "1",
                    audio.getAbsolutePath()
            );

            builder.redirectErrorStream(true);
            Process proceso = builder.start();

            // vaciar buffer
            while (proceso.getInputStream().read() != -1) {}

            int exitCode = proceso.waitFor();
            if (exitCode == 0) {
                return audio;
            } else {
                System.err.println("El proceso falló con el código: " + exitCode);
                return null;
            }

        } catch (Exception e) {
            System.err.println("Ocurrió un error: " + e.getMessage());
            return null;
        }
    }

    public String transcribirAudio(File audio) {
    	System.out.println("Transcribiendo con Whisper: " + audio.getName());
        String separador = "----Separador" + System.currentTimeMillis();

        try {
            URL url = new URL(URL_WHISPER + "/asr?task=transcribe&language=es&output=txt&model=" + MODELO_WHISPER);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

            conexion.setRequestMethod("POST");
            conexion.setDoOutput(true);
            conexion.setReadTimeout(120_000);

            conexion.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + separador);

            byte[] datosAudio = Files.readAllBytes(audio.toPath());

            try (DataOutputStream writer = new DataOutputStream(conexion.getOutputStream())) {
                writer.writeBytes("--" + separador + "\r\n");
                writer.writeBytes("Content-Disposition: form-data; name=\"audio_file\"; filename=\"" + audio.getName() + "\"\r\n");
                writer.writeBytes("Content-Type: audio/wav\r\n\r\n");
                writer.write(datosAudio);
                writer.writeBytes("\r\n");
                writer.writeBytes("--" + separador + "--\r\n");
            }

            // Respuesta de whisper
            String textoFinal = "";
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "UTF-8"))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    textoFinal += linea + " ";
                }
            }
            
            return textoFinal.trim();

        } catch (Exception e) {
            System.err.println("Error enviando el audio a Whisper: " + e.getMessage());
            return null;
        }
    }
}
