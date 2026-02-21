package com.meridae.cardgen;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static javax.print.attribute.ResolutionSyntax.DPI;

@Slf4j
public class CardGridMaker {

    public void makeCardGrid(String path, int startCard, String fileName) throws IOException {

        BufferedImage grid = new BufferedImage(820 * 9, 1120 * 6, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = grid.createGraphics();

        int cardNum = 1;
        for(int row = 1; row <= 6; row++) {
            for(int col = 1; col <= 9; col++) {
                String cardFileName = StringUtils.leftPad(String.valueOf(startCard + cardNum), 3, "0") + "f.png";
                log.info("Adding file: {}", cardFileName);
                BufferedImage card = ImageIO.read(new File(path, cardFileName));
                g2d.drawImage(card, (col-1)*820+1, (row-1)*1120+1, null);
                cardNum++;
            }
        }

        g2d.dispose();

        for (Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("png"); writers.hasNext(); ) {
            ImageWriter writer = writers.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                continue;
            }
            setDPI(metadata);
            try (ImageOutputStream stream = ImageIO.createImageOutputStream(new File(path, fileName))) {
                writer.setOutput(stream);
                writer.write(metadata, new IIOImage(grid, null, metadata), writeParam);
            }
            break;
        }

    }

    private void setDPI(IIOMetadata metadata) throws IIOInvalidTreeException {

        final double INCH_2_CM = 2.54;
        double dotsPerMilli = 3.0 * DPI / 10 / INCH_2_CM;

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
