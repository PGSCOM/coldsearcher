package com.pgscom.coldsearcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class UI {

    private ArrayList<Disco> discos;
    private Scanner sc;
    private HashMap<String, ArrayList<Archivo>> indice;

    public UI() {
        this.sc = new Scanner(System.in);
        this.discos = DataManager.cargar();
        this.indice = new HashMap<>();
        construirIndice();
    }

    public void iniciar() {
        int opcion = 0;
        while (opcion != 5) {
            mostrarMenu();
            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                opcion = -1;
            }
            if (opcion == 1) {
                verDiscos();
            } else if (opcion == 2) {
                listarArchivos();
            } else if (opcion == 3) {
                nuevoEscaneo();
            } else if (opcion == 4) {
                buscarArchivo();
            } else if (opcion == 5) {
                System.out.println("Saliendo...");
            } else {
                System.out.println("Opción no válida.");
            }
        }
        sc.close();
    }

    private void mostrarMenu() {
        System.out.println("\n=== ColdSearcher ===");
        System.out.println("1. Ver discos registrados");
        System.out.println("2. Ver archivos de un disco");
        System.out.println("3. Escanear nuevo disco");
        System.out.println("4. Buscar archivo");
        System.out.println("5. Salir");
        System.out.print("Opción: ");
    }

    private void verDiscos() {
        if (discos.isEmpty()) {
            System.out.println("No hay discos registrados.");
            return;
        }
        System.out.println("\n--- Discos ---");
        for (int i = 0; i < discos.size(); i++) {
            System.out.println((i + 1) + ". " + discos.get(i));
        }
    }

    private void listarArchivos() {
        if (discos.isEmpty()) {
            System.out.println("No hay discos registrados.");
            return;
        }
        verDiscos();
        System.out.print("ID del disco: ");
        try {
                int indiceDisco = Integer.parseInt(sc.nextLine()) - 1;
                if (indiceDisco < 0 || indiceDisco >= discos.size()) {
                    System.out.println("ID del disco no válido.");
                    return;
                }
                Disco disco = discos.get(indiceDisco);
            ArrayList<Archivo> archivos = disco.getArchivos();
            if (archivos.isEmpty()) {
                System.out.println("Este disco no tiene archivos registrados.");
                return;
            }
            System.out.println("\n--- Archivos en disco con ID \"" + disco.getDiscoId() + "\" ---");
                for (Archivo archivo : archivos) {
                    System.out.println("  - " + archivo.getName());
                    for (String dato : archivo.getDatos()) {
                        System.out.println("    " + dato);
                    }
                    System.out.println();
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada no válida.");
        }
    }

    private void nuevoEscaneo() {
        System.out.print("Ruta raíz del disco (D:\\): ");
                        String ruta = sc.nextLine();
                        System.out.print("ID del disco: ");
                        String idDisco = sc.nextLine();

        System.out.println("Escaneando...");
        Disco disco = DiskScanner.escanear(ruta, idDisco);
        if (disco == null) {
            System.out.println("Error al escanear.");
            return;
        }

        boolean actualizado = false;
        for (int i = 0; i < discos.size(); i++) {
            if (discos.get(i).getDiscoId().equals(idDisco)) {
                discos.set(i, disco);
                actualizado = true;
                break;
            }
        }
        if (!actualizado) {
            discos.add(disco);
        }

        DataManager.guardar(discos);
        construirIndice();
        System.out.println("\nEscaneo terminado: " + disco.getArchivos().size() + " archivos");
    }

    private void construirIndice() {
        indice.clear();
        for (Disco disco : discos) {
            for (Archivo archivo : disco.getArchivos()) {
                for (String etiqueta : archivo.getEtiquetas()) {
                    String clave = etiqueta.toLowerCase();
                    if (!indice.containsKey(clave)) {
                        indice.put(clave, new ArrayList<>());
                    }
                    indice.get(clave).add(archivo);
                }
            }
        }
    }

    private void buscarArchivo() {
        System.out.print("Término a buscar: ");
        String busqueda = sc.nextLine().toLowerCase();
        boolean encontrado = false;

        // Buscar por etiquetas
        for (String clave : indice.keySet()) {
            if (clave.contains(busqueda)) {
                for (Archivo archivo : indice.get(clave)) {
                    Disco disco = archivo.getDisco();
                    System.out.println("  [ID: " + disco.getDiscoId() + "] " + archivo.getEtiquetas() + " " + archivo.getPath());
                    encontrado = true;
                }
            }
        }

        // Buscar por nombre (substring)
        for (Disco disco : discos) {
            for (Archivo archivo : disco.getArchivos()) {
                if (archivo.getName().toLowerCase().contains(busqueda)) {
                    System.out.println("  [ID: " + disco.getDiscoId() + "][nombre] " + archivo);
                    encontrado = true;
                }
            }
        }

        if (!encontrado) {
            System.out.println("No se encontró ningún archivo.");
        }
    }
}
