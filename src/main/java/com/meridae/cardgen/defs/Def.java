package com.meridae.cardgen.defs;

import lombok.Getter;

@Getter
public abstract class Def {

    protected String name;

    public abstract void init(String definition);

}
