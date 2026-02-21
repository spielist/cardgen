package com.meridae.cardgen;

import com.meridae.cardgen.defs.*;
import com.meridae.cardgen.exception.CanvasException;
import com.meridae.cardgen.exception.CardDefException;
import com.meridae.cardgen.exception.ProcessingException;
import com.meridae.cardgen.layers.TextLayer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.AttributedString;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static javax.print.attribute.ResolutionSyntax.DPI;

@Slf4j
public class Processor {

    private final CanvasDef canvasDef = new CanvasDef();
    private final CardDef cardDef = new CardDef();
    private final Map<String, Def> defs = new HashMap<>();

    public void processFile(String inputPath, String fileName, String outputPath) throws Exception {

        List<String> lines =
                Files.readAllLines(Paths.get(inputPath, fileName), StandardCharsets.UTF_8).stream()
                        .filter(line -> StringUtils.isNotBlank(line) &&
                                !line.startsWith("#")).collect(Collectors.toList());

        List<String> canvasDefLines = lines.stream().filter(line -> line.startsWith("CANVAS="))
                .map(line -> line.substring(7)).collect(Collectors.toList());
        loadCanvasDef(canvasDefLines);
        log.info("Canvas: {}", canvasDef);

        List<String> cardDefLines = lines.stream().filter(line -> line.startsWith("CARDDEF="))
                .map(line -> line.substring(8)).collect(Collectors.toList());
        loadCardDef(cardDefLines);
        log.info("CardDef: {}", cardDef);

        List<String> fontDefLines = lines.stream().filter(line -> line.startsWith("FONTDEF="))
                .map(line -> line.substring(8)).collect(Collectors.toList());
        loadFontDefs(inputPath, fontDefLines);

        List<String> fileDefLines = lines.stream().filter(line -> line.startsWith("FILEDEF="))
                .map(line -> line.substring(8)).collect(Collectors.toList());
        loadDefs(fileDefLines, FileDef.class);

        List<String> textDefLines = lines.stream().filter(line -> line.startsWith("TEXTDEF="))
                .map(line -> line.substring(8)).collect(Collectors.toList());
        loadDefs(textDefLines, TextDef.class);

        List<String> cards = lines.stream().filter(line -> line.startsWith("CARD="))
                .map(line -> line.substring(5)).collect(Collectors.toList());
        processCards(inputPath, cards, outputPath);

    }

    private void loadCanvasDef(List<String> lines) throws CanvasException {
        try {
            if (lines != null && !lines.isEmpty()) {
                this.canvasDef.init(lines.get(0));
                if (lines.size() > 1) {
                    log.warn("Multiple CANVAS records found, using: {}", this.canvasDef);
                }
            } else {
                log.warn("CANVAS not found, using {}", this.canvasDef);
            }
        } catch (Exception e) {
            log.error("Exception loading CANVAS", e);
            throw new CanvasException("Exception loading CANVAS", e);
        }
    }

    private void loadCardDef(List<String> lines) throws CardDefException {
        try {
            if (lines != null && !lines.isEmpty()) {
                this.cardDef.init(lines.get(0));
                if (lines.size() > 1) {
                    log.warn("Multiple CARDDEF records found, using: {}", this.cardDef);
                }
            } else {
                log.warn("CARDDEF not found, using {}", this.cardDef);
            }
        } catch (Exception e) {
            log.error("Exception loading CARDDEF", e);
            throw new CardDefException("Exception loading CARDDEF", e);
        }
    }

    private void loadFontDefs(String path, List<String> lines) throws Exception {
        for(String line : lines) {
            FontDef def = new FontDef(path);
            def.init(line);
            defs.put(def.getName(), def);
        }
    }

    private <T extends Def> void loadDefs(List<String> lines, Class<T> defClass) throws Exception {
        for(String line : lines) {
            T def = defClass.getDeclaredConstructor().newInstance();
            def.init(line);
            defs.put(def.getName(), def);
        }
    }

    public void processCards(String inputPath, List<String> lines, String outputPath) throws Exception {

        int i = 1;

        for (String line : lines) {
            if (StringUtils.isNotEmpty(line)) {

                String[] layers = line.split(",");
                String fileName = layers[0] + "." + this.canvasDef.getFileExt();
                BufferedImage combinedImage =
                        new BufferedImage(this.canvasDef.getCanvasWidth(), this.canvasDef.getCanvasHeight(), BufferedImage.TYPE_INT_ARGB);

                Graphics2D g2d = combinedImage.createGraphics();

                for (int j = 1; j < layers.length; j++) {
                    if (layers[j].startsWith("TEXT(")) {
                        String textSpec = StringUtils.substringBetween(layers[j], "TEXT(", ")");
                        TextLayer textLayer = new TextLayer();
                        textLayer.init(textSpec);
                        FontDef fontDef = (FontDef) defs.get(textLayer.getFontDefName());
                        writeText(g2d, fontDef.getFont(), fontDef.getForeColor(), fontDef.getBackColor(),
                                textLayer.getText(), textLayer.getX(), textLayer.getY(), cardDef);
                    } else if (layers[j].startsWith("FILE(")) {
                        String layerFile = StringUtils.substringBetween(layers[j], "FILE(", ")");
                        BufferedImage layer = ImageIO.read(new File(inputPath, layerFile));
                        g2d.drawImage(layer, this.cardDef.getLeft(), this.cardDef.getTop(), null);
                    } else {
                        Def def = defs.get(layers[j]);
                        if (def instanceof FileDef) {
                            FileDef fileDef = (FileDef) def;
                            if (fileDef.isVisible()) {
                                try {
                                    BufferedImage layer = ImageIO.read(new File(inputPath, fileDef.getFileName()));
                                    g2d.drawImage(layer, this.cardDef.getLeft(), this.cardDef.getTop(), null);
                                } catch (Exception e) {
                                    log.error("Cannot access file: " + fileDef.getFileName() + "!");
                                    throw new ProcessingException("Cannot access file: " + fileDef.getFileName(), e);
                                }
                            }
                        } else if (def instanceof TextDef) {
                            TextDef textDef = (TextDef) def;
                            FontDef fontDef = (FontDef) defs.get(textDef.getFontDefName());
                            String text = !textDef.getName().equals("#") ? textDef.getText() : String.valueOf(i);
                            writeText(g2d, fontDef.getFont(), fontDef.getForeColor(), fontDef.getBackColor(),
                                    text, textDef.getX(), textDef.getY(), cardDef);
                        }
                    }
                }

                g2d.dispose();

                for (Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix(this.canvasDef.getFileExt()); writers.hasNext(); ) {
                    ImageWriter writer = writers.next();
                    ImageWriteParam writeParam = writer.getDefaultWriteParam();
                    ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
                    IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
                    if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                        continue;
                    }
                    setDPI(metadata);
                    try (ImageOutputStream stream = ImageIO.createImageOutputStream(new File(outputPath, fileName))) {
                        writer.setOutput(stream);
                        writer.write(metadata, new IIOImage(combinedImage, null, metadata), writeParam);
                    }
                    break;
                }

                log.info("Card {} successfully generated!", i);

            }

            i++;

            if(i > 10) {
                break;
            }
        }

    }

    private void writeText(Graphics2D g2d, Font font, Color foreColor, Color backColor, String text, String x, String y, CardDef cardDef) {

        Rectangle rectImage2 = new Rectangle(cardDef.getCardWidth(), cardDef.getCardHeight());
        FontRenderContext frc = new FontRenderContext(null, true, true);

        AttributedString attributedText = new AttributedString(text);
        attributedText.addAttribute(TextAttribute.FONT, font);
        attributedText.addAttribute(TextAttribute.FOREGROUND, foreColor);
        if (backColor != null) {
            attributedText.addAttribute(TextAttribute.BACKGROUND, backColor);
        }

        TextLayout layout = new TextLayout(attributedText.getIterator(), frc);
        Rectangle rectText = layout.getPixelBounds(null, 0, 0);

        int realX = 0;
        if(cardDef.getLeft() < 0) {
            realX = Utility.calcX(x, rectImage2, rectText, -cardDef.getLeft() * 2);
        } else {
            realX = Utility.calcX(x, rectImage2, rectText);
        }

        int realY = 0;
        if(cardDef.getTop() < 0) {
            realY = Utility.calcY(y, rectImage2, rectText, -cardDef.getTop() * 2);
        } else {
            realY = Utility.calcY(y, rectImage2, rectText);
        }

        layout.draw(g2d, cardDef.getLeft() + realX, cardDef.getTop() + realY);

    }

    private void setDPI(IIOMetadata metadata) throws IIOInvalidTreeException {

        final double INCH_TO_CM = 2.54;
        double dotsPerMilli = 3.0 * DPI / 10 / INCH_TO_CM;

        IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
        horiz.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
        vert.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
        dim.appendChild(horiz);
        dim.appendChild(vert);

        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
        root.appendChild(dim);

        metadata.mergeTree("javax_imageio_1.0", root);
    }

}
