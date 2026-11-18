package com.pgscom.coldsearcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class TikaService {

    private static String URL_API = "http://pc-pgscom:9998/tika";

    public static String extraerTexto(File fichero) {
        if (fichero == null || !fichero.exists()) {
            System.err.println("Fichero no encontrado: " + fichero);
            return "";
        }

        try {
            URL url = new URL(URL_API);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

            conexion.setRequestMethod("PUT");
            conexion.setDoOutput(true);
            conexion.setConnectTimeout(3_000);
            conexion.setReadTimeout(30_000);
            conexion.setRequestProperty("Accept", "text/plain");

            byte[] bytes = Files.readAllBytes(fichero.toPath());
            try (OutputStream os = conexion.getOutputStream()) {
                os.write(bytes);
            }

            String texto = "";
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conexion.getInputStream(), "UTF-8"))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    texto += linea + "\n";
                }
            }

            return texto.trim();

        } catch (Exception e) {
            System.err.println("Error extrayendo texto de "
                    + fichero.getName() + ": " + e.getMessage());
            return "";
        }
    }
}
