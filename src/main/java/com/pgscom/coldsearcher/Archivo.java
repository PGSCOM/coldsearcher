package com.pgscom.coldsearcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Archivo implements Serializable, Comparable<Archivo> {
    private static final long serialVersionUID = 1L;

    transient String content;
    ArrayList<String> etiquetas;
    String path;
    String name;
    Disco disco;
    String type;
    long size;

    public Archivo(String name, String path, String content) {
        this.name = name;
        this.path = path;
        setContent(content);
        this.type = extraerExtension(name);
    }

    public Archivo(String path) {
        this.path = path;
        String[] partes = path.replace("\\", "/").split("/");
        this.name = partes[partes.length - 1];
        this.type = extraerExtension(this.name);
        setContent("");
    }

    private String extraerExtension(String nombre) {
        if (nombre != null && nombre.contains(".")) {
            return nombre.substring(nombre.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }

    public ArrayList<String> getDatos() {
        ArrayList<String> datos = new ArrayList<>();
        datos.add("Nombre: " + name);
        datos.add("Ruta: " + path);
        datos.add("Tamaño: " + size + " bytes");
        ArrayList<String> etiquetas = getEtiquetas();
        if (!etiquetas.isEmpty()) {
            String lista = etiquetasString(etiquetas);
            datos.add("Etiquetas: " + lista);
        }
        return datos;
    }

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		if (etiquetas == null) {
			etiquetas = new ArrayList<>();
		} else {
			etiquetas.clear();
		}
		if (content != null && !content.isEmpty()) {
			String[] etiquetasArray = content.split(",");
			for (String etiqueta : etiquetasArray) {
				String etiquetaLimpia = etiqueta.trim();
				if (!etiquetaLimpia.isEmpty()) {
					etiquetas.add(etiquetaLimpia);
				}
			}
		}
	}

	public ArrayList<String> getEtiquetas() {
		if (etiquetas == null) {
			etiquetas = new ArrayList<>();
		}
		return etiquetas;
	}

	public void setEtiquetas(ArrayList<String> etiquetas) {
		this.etiquetas = etiquetas;
		this.content = etiquetasString(etiquetas);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Disco getDisco() {
		return disco;
	}

	public void setDisco(Disco disco) {
		this.disco = disco;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

    @Override
    public int compareTo(Archivo o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Archivo)) {
            return false;
        }
        return Objects.equals(path, ((Archivo) o).path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return name + " [" + type + ", " + size + " bytes]";
    }

    public String parsear(MediaService ms) {
        return "";
    }

    static String etiquetasString(ArrayList<String> etiquetas) {
        if (etiquetas == null || etiquetas.isEmpty()) {
            return "";
        }
        String resultado = "";
        for (int i = 0; i < etiquetas.size(); i++) {
            resultado += etiquetas.get(i);
            if (i < etiquetas.size() - 1) {
                resultado += ", ";
            }
        }
        return resultado;
    }
}
