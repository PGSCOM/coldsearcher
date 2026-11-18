package com.pgscom.coldsearcher;

import java.io.File;

public class Audio extends Archivo {
    private static final long serialVersionUID = 1L;

    public Audio(String name, String path, String content) {
        super(name, path, content);
    }

    public Audio(String path) {
        super(path);
    }

    @Override
    public String parsear(MediaService ms) {
        String transcripcion = ms.transcribirAudio(new File(path));
        if (transcripcion == null || transcripcion.isEmpty()) {
            return "";
        }
        return etiquetasString(LlmService.etiquetar(transcripcion));
    }
}
