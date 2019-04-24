package com.ford.research.chemisense;

/**
 * Created by jyeung5 on 1/6/16.
 */
public class AirnowData {
    public final String type, category;
    public final int AQI, level;
    public AirnowData (String type, int AQI, String category, int level) {
        this.type=type;
        this.AQI = AQI;
        this.category = category;
        this.level=level;
    }
}