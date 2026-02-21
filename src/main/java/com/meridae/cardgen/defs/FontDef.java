package com.meridae.cardgen.defs;

import java.awt.*;
import java.io.File;

import com.meridae.cardgen.Utility;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Getter
public class FontDef extends Def {

    private final String fontPath;
    private Font font;
    private Color foreColor = null;
    private Color backColor = null;

    private static final GraphicsEnvironment ge =
            GraphicsEnvironment.getLocalGraphicsEnvironment();

    public FontDef(String fontPath) {
        this.fontPath = fontPath;
    }

    public void init(String definition) {
        try {

            String[] fontSpecs = definition.split("\\|");
            this.name = Utility.defaultIfMissing(fontSpecs, 0, "");
            String fontFile = Utility.defaultIfMissing(fontSpecs, 1, "");
            int fontSize = Utility.defaultIfMissing(fontSpecs, 2, 12);
            int fontStyle = parseFontStyle(Utility.defaultIfMissing(fontSpecs, 3, "plain"));
            this.foreColor = Color.black;
            this.backColor = null;

            String colorValue = Utility.defaultIfMissing(fontSpecs, 4, "#000000");
            if (colorValue.startsWith("#")) {
                this.foreColor = new Color(
                        Integer.valueOf(colorValue.substring(1, 3), 16),
                        Integer.valueOf(colorValue.substring(3, 5), 16),
                        Integer.valueOf(colorValue.substring(5, 7), 16));
            } else {
                this.foreColor = Color.getColor(colorValue, Color.black);
            }

            // make back color transparent by default
            colorValue = Utility.defaultIfMissing(fontSpecs, 5, "none");
            if (!colorValue.equalsIgnoreCase("none")) {
                if (colorValue.startsWith("#")) {
                    this.backColor = new Color(
                            Integer.valueOf(colorValue.substring(1, 3), 16),
                            Integer.valueOf(colorValue.substring(3, 5), 16),
                            Integer.valueOf(colorValue.substring(5, 7), 16));
                } else {
                    this.backColor = Color.getColor(colorValue, Color.white);
                }
            }

            if (StringUtils.isNotEmpty(this.name) && StringUtils.isNotEmpty(fontFile) && fontSize > 0) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath, fontFile));
                ge.registerFont(font);
                this.font = new Font(font.getName(), fontStyle, fontSize);
                log.info("Loaded font: {}", this.font.getName());
            } else {
                throw new IllegalArgumentException("Format should be nickname|fontfile|size|style|foreColor|backColor");
            }

        } catch (Exception e) {
            log.error("Error loading FONTDEF: {}", definition, e);
        }
    }

    private int parseFontStyle(String fontStyle) {
        int style = Font.PLAIN;
        switch (fontStyle.toLowerCase()) {
            case "bold":
                style = Font.BOLD;
                break;
            case "italic":
                style = Font.ITALIC;
                break;
            case "bold/italic":
                style = Font.BOLD + Font.ITALIC;
                break;
        }
        return style;
    }

}
