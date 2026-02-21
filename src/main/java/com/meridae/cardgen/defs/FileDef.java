package com.meridae.cardgen.defs;

import com.meridae.cardgen.Utility;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Getter
public class FileDef extends Def {

    private String fileName;

    private boolean visible;

    public void init(String definition) {
        try {
            if (StringUtils.isNotEmpty(definition)) {
                String[] fileSpecs = definition.split("\\|");
                this.name = Utility.defaultIfMissing(fileSpecs, 0, "");
                this.fileName = Utility.defaultIfMissing(fileSpecs, 1, "");
                this.visible = Utility.defaultIfMissing(fileSpecs, 2, true);
                if( StringUtils.isEmpty(this.name) || StringUtils.isEmpty(this.fileName) ) {
                    throw new IllegalArgumentException("Format should be name|fileName|visible");
                }
            }
        } catch (Exception e) {
            log.error("Error loading FILEDEF: {}", definition, e);
        }
    }

}
