package com.meridae.cardgen.layers;

import com.meridae.cardgen.Utility;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Getter
public class TextLayer {

    private String fontDefName;
    private String x;
    private String y;
    private String text;

    public void init(String definition) {
        try {
            String[] textSpecs = definition.split("\\|");
            this.text = Utility.defaultIfMissing(textSpecs, 0, "");
            this.fontDefName = Utility.defaultIfMissing(textSpecs, 1, "");
            this.x = Utility.defaultIfMissing(textSpecs, 2, "0");
            this.y = Utility.defaultIfMissing(textSpecs, 3, "0");
            if (StringUtils.isEmpty(this.text) || StringUtils.isEmpty(this.fontDefName)
                    || StringUtils.isEmpty(this.x) || StringUtils.isEmpty(this.y)) {
                throw new IllegalArgumentException("Format should be text|fontdef|x|y");
            }
        } catch (Exception e) {
            log.error("Error loading TEXT layer: {}", definition, e);
        }
    }

}
