package com.meridae.cardgen.defs;

import com.meridae.cardgen.Utility;
import lombok.Getter;

@Getter
public class CanvasDef extends Def {

    private int canvasWidth = 820;
    private int canvasHeight = 1120;
    private String fileExt = "png";

    @Override
    public void init(String definition) {
        String[] canvasSpecs = definition.split("\\|");
        this.name = "CanvasDef";
        this.canvasWidth = Utility.defaultIfMissing(canvasSpecs, 0, this.canvasWidth);
        this.canvasHeight = Utility.defaultIfMissing(canvasSpecs, 1, this.canvasHeight);
        this.fileExt = Utility.defaultIfMissing(canvasSpecs, 2, this.fileExt);
    }

    @Override
    public String toString() {
        return String.format("width=%s, height=%s, fileExt=%s",
                canvasWidth, canvasHeight, fileExt);
    }
}
