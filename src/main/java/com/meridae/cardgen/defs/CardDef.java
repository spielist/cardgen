package com.meridae.cardgen.defs;

import com.meridae.cardgen.Utility;
import lombok.Getter;

@Getter
public class CardDef extends Def {

    private int cardWidth = 820;
    private int cardHeight = 1120;
    private int top = 1;
    private int left = 1;

    @Override
    public void init(String definition) {
        String[] cardSpecs = definition.split("\\|");
        this.name = "CardDef";
        this.cardWidth = Utility.defaultIfMissing(cardSpecs, 0, this.cardWidth);
        this.cardHeight = Utility.defaultIfMissing(cardSpecs, 1, this.cardHeight);
        this.top = Utility.defaultIfMissing(cardSpecs, 2, this.top);
        this.left = Utility.defaultIfMissing(cardSpecs, 3, this.left);
    }

    @Override
    public String toString() {
        return String.format("width=%s, height=%s, top=%s, left=%s",
                cardWidth, cardHeight, top, left);
    }

}
