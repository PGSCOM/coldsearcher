package com.pgscom.coldsearcher;

import java.io.File;

public class Doc extends Archivo {
    private static final long serialVersionUID = 1L;

    public Doc(String name, String path, String content) {
        super(name, path, content);
    }

    public Doc(String path) {
        super(path);
    }

    @Override
    public String parsear(MediaService ms) {
        String texto = TikaService.extraerTexto(new File(path));
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        return etiquetasString(LlmService.etiquetar(texto));
    }
}
