package com.example.fortune.model;

import javax.swing.ImageIcon;

public class FortuneFace {
    private final String name;
    private final String keyword;
    private final String message;
    private final ImageIcon icon;

    public FortuneFace(String name, String keyword, String message, ImageIcon icon) {
        this.name = name;
        this.keyword = keyword;
        this.message = message;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getMessage() {
        return message;
    }

    public ImageIcon getIcon() {
        return icon;
    }
}