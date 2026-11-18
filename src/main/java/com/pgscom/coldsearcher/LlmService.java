package com.pgscom.coldsearcher;

import com.google.gson.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;

public class LlmService {
	private static String MODELO = "ministral-3:3b";
	
	private static String URL_API = "http://pc-pgscom:11434/api/generate";
	
	public static void main( String[] args ) {
		System.out.println(etiquetar("En un lugar de la Mancha de cuyo\r\n"
				+ "nombre no quiero acordarme,\r\n"
				+ "no ha mucho tiempo que vivía\r\n"
				+ "un hidalgo' de los de lanza en\r\n"
				+ "astillero', adarga antigua, rocín\r\n"
				+ "flaco y galgo corredor. U na olla\r\n"
				+ "de algo más vaca que carnero, salpicón' las más\r\n"
				+ "noches, duelos y quebrantos' los sábados, lintejas los viernes, algún palomino de añadidura\r\n"
				+ "los domingos, consumían las tres partes' de su\r\n"
				+ "hacienda. El resto della concluían sayo de\r\n"
				+ "velarte', calzas de velludo' para las fiestas con\r\n"
				+ "sus pantuflos de lo mismo, y los días de entre\r\n"
				+ "semana se honraba con su vellod' de lo más\r\n"
				+ "fino."));
	}
	
	public static ArrayList<String> etiquetar(String texto) {
		if (texto.isEmpty()) {
			return new ArrayList<>();
		}
		String prompt = "Analiza el siguiente texto y devuelve ÚNICAMENTE un objeto JSON " +
                "con una clave 'etiquetas' que contiene un array de strings. " +
                "Ejemplo de respuesta: {\"etiquetas\": [\"tecnología\", \"java\", \"programación\"]}. " +
                "No incluyas ningún texto fuera del JSON.\n\nTexto: " + texto;
		
		String requestBody = "{"
                + "\"model\":\"" + MODELO + "\","
                + "\"prompt\":\"" + limpiarJson(prompt) + "\","
                + "\"format\":\"json\","
                + "\"stream\":false"
                + "}";
		
		String respuesta = enviar(requestBody);
		
		if (respuesta == null || respuesta.isEmpty()) {
			return new ArrayList<>();
		}
		
		return parsearJson(respuesta);
	}
	
	public static ArrayList<String> etiquetar(File archivoImagen) {
		try {
			// El modelo no puede leer imágenes de menos de 336
			BufferedImage imagen = ImageIO.read(archivoImagen);
			if (imagen == null || imagen.getWidth() < 336 || imagen.getHeight() < 336) {
				System.out.println("Imagen demasiado pequeña para ollama");
				return new ArrayList<>();
			}

			String base64Image = Base64.getEncoder().encodeToString(Files.readAllBytes(archivoImagen.toPath()));
			
			String prompt = "Analiza la siguiente imagen y devuelve ÚNICAMENTE un objeto JSON " +
	                "con una clave 'etiquetas' que contiene un array de strings. " +
	                "Ejemplo de respuesta: {\"etiquetas\": [\"tecnología\", \"java\", \"programación\"]}. " +
	                "No incluyas ningún texto fuera del JSON.";
			
		String requestBody = "{"
	                + "\"model\":\"" + MODELO + "\","
	                + "\"prompt\":\"" + limpiarJson(prompt) + "\","
	                + "\"format\":\"json\","
	                + "\"stream\":false,"
	                + "\"images\": [\"" + base64Image + "\"]"
	                + "}";
		String respuesta = enviar(requestBody);
			
		return parsearJson(respuesta);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	private static String enviar(String requestBody) {
		for (int intento = 0; intento < 5; intento++) {
			try {
				URL url = new URL(URL_API);
				HttpURLConnection api = (HttpURLConnection) url.openConnection();
				
				api.setRequestMethod("POST");
				
				api.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				
				api.setReadTimeout(180_000); // Modelos pueden tardar
				api.setConnectTimeout(30_000);
				
				api.setDoOutput(true);
				
				// Mandar solicitud
				try (OutputStream stream = api.getOutputStream()) {
				    stream.write(requestBody.getBytes("UTF-8"));		// Formateo a UTF-8 y mandar
				}
				
			String respuesta = "";
			try (BufferedReader br = new BufferedReader(new InputStreamReader(api.getInputStream(), "UTF-8"))) {
			    String linea;
			    while ((linea = br.readLine()) != null) {
			        respuesta += linea;
			        if (respuesta.contains("\"done\":true")) {	// Respuesta completa
			        	break;
			        }
			    }
			}
				
				System.out.println("request: " + requestBody);
				System.out.println("respuesta: " + respuesta);
				
				return respuesta;
				
			} catch(Exception e) {
				e.printStackTrace();
				try { Thread.sleep(2000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); } // Error => timeout
			}
		}
		
		System.err.println("Error de LLM después de 5 intentos");
		return "";
	}

	// Formatear para adaptar sintaxis de Json
	private static String limpiarJson(String texto) {
		return texto.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n",  "\\n").replace("\r",  "\\r").replace("\t",  "\\t");
	}
	
	private static ArrayList<String> parsearJson(String jsonString) {
		try {
			// Parsear JSON https://google.github.io/gson/UserGuide.html
			// ¿Vacío?
			if (jsonString == null || jsonString.trim().isEmpty()) {
				return new ArrayList<>();
			}

			// ¿Diccionario json (No un JsonArray)?
			JsonElement parsed = JsonParser.parseString(jsonString);
			if (!parsed.isJsonObject()) {
				return new ArrayList<>();
			}

			// ¿Response vacía?
			JsonObject jsonObject = parsed.getAsJsonObject();
			JsonElement responseElement = jsonObject.get("response");
			if (responseElement == null || responseElement.isJsonNull()) {
				return new ArrayList<>();
			}

			/*
			String responseString = responseElement.getAsString();
			if (responseString == null || responseString.trim().isEmpty()) {
				return new ArrayList<>();
			}

			JsonElement innerParsed = JsonParser.parseString(responseString);
			if (!innerParsed.isJsonObject()) {
				return new ArrayList<>();
			}

			JsonObject innerObject = innerParsed.getAsJsonObject();
			if (!innerObject.has("etiquetas") || innerObject.get("etiquetas").isJsonNull()) {
				return new ArrayList<>();
			}
			*/

			String responseString = JsonParser.parseString(jsonString).getAsJsonObject().get("response").getAsString();
			JsonArray etiquetasJson = JsonParser.parseString(responseString).getAsJsonObject().getAsJsonArray("etiquetas");
			ArrayList<String> etiquetas = new ArrayList<>();
			
			for (JsonElement jElement : etiquetasJson) {
				etiquetas.add(jElement.getAsString());
			}

			return etiquetas;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
}
