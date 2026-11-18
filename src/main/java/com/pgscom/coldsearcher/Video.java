package com.pgscom.coldsearcher;

import java.io.File;

public class Video extends Archivo {
    private static final long serialVersionUID = 1L;

    public Video(String name, String path, String content) {
        super(name, path, content);
    }

    public Video(String path) {
        super(path);
    }

    @Override
    public String parsear(MediaService ms) {
        String transcripcion = ms.transcribirVideo(new File(path));
        if (transcripcion == null || transcripcion.isEmpty()) {
            return "";
        }
        return etiquetasString(LlmService.etiquetar(transcripcion));
    }
}
