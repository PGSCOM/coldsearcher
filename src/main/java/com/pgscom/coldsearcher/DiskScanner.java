package com.pgscom.coldsearcher;

import java.io.File;
import java.util.ArrayList;


public class DiskScanner {

    public static Disco escanear(String rutaRaiz, String serial) {
        File raiz = new File(rutaRaiz);
        if (!raiz.exists() || !raiz.isDirectory()) {
            System.err.println("Ruta no válida: " + rutaRaiz);
            return null;
        }

        Disco disco = new Disco(serial, raiz.getTotalSpace());
        MediaService mediaService = new MediaService();
        ArrayList<Archivo> archivos = new ArrayList<Archivo>();

        recopilarArchivos(raiz, disco, mediaService, archivos);

        int total = archivos.size();
        //int total = 200;
        int p = 0;

        for (Archivo archivo : archivos) {
            archivo.setContent(archivo.parsear(mediaService));
            p++;
            System.out.print("\rProgreso: " + p + "/" + total + " archivos; actual: " + archivo.getName());
        }

        return disco;
    }

    private static void recopilarArchivos(File directorio, Disco disco, MediaService mediaService, ArrayList<Archivo> lista) {
        File[] contenido = directorio.listFiles();
        if (contenido == null) {
            return;
        }

        for (File f : contenido) {
            if (f.isDirectory()) {
            	// Crea otra rama en cada carpeta
                recopilarArchivos(f, disco, mediaService, lista);
            } else {
                Archivo archivo = disco.addArchivo(f.getName(), f.getAbsolutePath(), "", f.length());
                if (archivo != null) {
                    lista.add(archivo);
                }
            }
        }
    }
}
