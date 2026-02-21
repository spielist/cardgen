package com.meridae.cardgen.defs;

import com.meridae.cardgen.Utility;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Getter
public class TextDef extends Def {

    private String text;
    private String fontDefName;
    private String x;
    private String y;

    public void init(String definition) {
        try {
            String[] textSpecs = definition.split("\\|");
            this.name = Utility.defaultIfMissing(textSpecs, 0, "");
            this.text = Utility.defaultIfMissing(textSpecs, 1, "");
            this.fontDefName = Utility.defaultIfMissing(textSpecs, 2, "");
            this.x = Utility.defaultIfMissing(textSpecs, 3, "0");
            this.y = Utility.defaultIfMissing(textSpecs, 4, "0");
            if( StringUtils.isEmpty(this.name) || StringUtils.isEmpty(this.text) || StringUtils.isEmpty(this.fontDefName)
                    || StringUtils.isEmpty(this.x) || StringUtils.isEmpty(this.y)) {
                throw new IllegalArgumentException("Format should be name|text|fontdef|x|y");
            }
        } catch (Exception e) {
            log.error("Error loading TEXTDEF: {}", definition, e);
        }
    }

}
