package com.pgscom.coldsearcher;

import java.io.*;
import java.util.ArrayList;

public class DataManager {

    private static String RUTA_DAT = "coldsearcher.dat";

    public static void guardar(ArrayList<Disco> discos) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(RUTA_DAT))) {
            oos.writeObject(discos);
            System.out.println("Datos guardados en " + RUTA_DAT);
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
	public static ArrayList<Disco> cargar() {
        File file = new File(RUTA_DAT);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            ArrayList<Disco> discos = (ArrayList<Disco>) ois.readObject();
            return discos;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}