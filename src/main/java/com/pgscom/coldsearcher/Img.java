package com.pgscom.coldsearcher;

import java.io.File;

public class Img extends Archivo {
    private static final long serialVersionUID = 1L;

    public Img(String name, String path, String content) {
        super(name, path, content);
    }

    public Img(String path) {
        super(path);
    }

    @Override
    public String parsear(MediaService ms) {
        return etiquetasString(LlmService.etiquetar(new File(path)));
    }
}
