package com.pgscom.coldsearcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Disco implements Serializable {
    private static final long serialVersionUID = 1L;

    private String discoId;
    private long capacidad;
    private Date fechaUltimaConexion;
    private boolean conectado;
    private ArrayList<Archivo> archivos;

    public Disco(String discoId, long capacidad) {
        this.discoId = discoId;
        this.capacidad = capacidad;
        this.fechaUltimaConexion = new Date();
        this.conectado = true;
        this.archivos = new ArrayList<>();
    }

    public Archivo addArchivo(String name, String path, String content) {
        return addArchivo(name, path, content, 0);
    }

    public Archivo addArchivo(String name, String path, String content, long size) {
        String ext = "";
        if (name.contains(".")) {
            ext = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
        }

        Archivo archivo = null;

        if (ext.equals("mp3") || ext.equals("wav") || ext.equals("flac")
                || ext.equals("aac") || ext.equals("ogg")) {
            archivo = new Audio(name, path, content);
        } else if (ext.equals("mp4") || ext.equals("mov") || ext.equals("avi") || ext.equals("mkv")) {
            archivo = new Video(name, path, content);
        } else if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png")) {
            archivo = new Img(name, path, content);
        } else if (ext.equals("pdf") || ext.equals("txt") || ext.equals("doc") || ext.equals("docx") || ext.equals("md")) {
            archivo = new Doc(name, path, content);
        } else {
            archivo = new Archivo(name, path, content);
        }

        archivo.setDisco(this);
        archivo.setSize(size);
        archivos.add(archivo);
        return archivo;
    }

	public String getDiscoId() {
		return discoId;
	}

	public void setDiscoId(String discoId) {
		this.discoId = discoId;
	}

	public long getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(long capacidad) {
		this.capacidad = capacidad;
	}

	public Date getFechaUltimaConexion() {
		return fechaUltimaConexion;
	}

	public void setFechaUltimaConexion(Date fecha) {
		this.fechaUltimaConexion = fecha;
	}

	public boolean isConectado() {
		return conectado;
	}

	public void setConectado(boolean conectado) {
		this.conectado = conectado;
	}

	public ArrayList<Archivo> getArchivos() {
		return archivos;
	}

	public void setArchivos(ArrayList<Archivo> archivos) {
		this.archivos = archivos;
	}

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Disco)) {
            return false;
        }
        return Objects.equals(discoId, ((Disco) o).discoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(discoId);
    }

    @Override
    public String toString() {
        String estado;
        if (conectado) {
            estado = "conectado";
        } else {
            estado = "desconectado";
        }
        return "Disco{ID=" + discoId
                + ", " + archivos.size() + " archivos, " + estado
                + ", última conexión: " + fechaUltimaConexion + "}";
    }
}
