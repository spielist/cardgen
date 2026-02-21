package com.meridae.cardgen;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

import java.awt.*;

public class Utility {

    public static int defaultIfMissing(String[] values, int index, int defaultValue) {
        return index < values.length ? Integer.parseInt(values[index]) : defaultValue;
    }

    public static String defaultIfMissing(String[] values, int index, String defaultValue) {
        return index < values.length ? values[index] : defaultValue;
    }

    public static boolean defaultIfMissing(String[] values, int index, boolean defaultValue) {
        return index < values.length ? Boolean.parseBoolean(values[index]) : defaultValue;
    }

    public static int calcX(String value, Rectangle rectImage, Rectangle rectText) {
        Argument left = new Argument("left = 0");
        Argument center = new Argument("center = " + ((rectImage.width / 2) - (rectText.width / 2)));
        Argument right = new Argument("right = " + (rectImage.width - rectText.width));

        Expression expr = new Expression(value.trim(), left, center, right);
        return (int) expr.calculate();
    }

    public static int calcX(String value, Rectangle rectImage, Rectangle rectText, int xOffset) {
        Argument left = new Argument("left = 0");
        Argument center = new Argument("center = " + ((rectImage.width / 2) - (rectText.width / 2)));
        Argument right = new Argument("right = " + (rectImage.width - rectText.width + xOffset));

        Expression expr = new Expression(value.trim(), left, center, right);
        return (int) expr.calculate();
    }

    public static int calcY(String value, Rectangle rectImage, Rectangle rectText) {
        Argument top = new Argument("top = " + rectText.height);
        Argument middle = new Argument("middle = " + ((rectImage.height / 2) - (rectText.height / 2) + rectText.height));
        Argument bottom = new Argument("bottom = " + (rectImage.height - rectText.height));

        Expression expr = new Expression(value.trim(), top, middle, bottom);
        return (int) expr.calculate();
    }

    public static int calcY(String value, Rectangle rectImage, Rectangle rectText, int yOffset) {
        Argument top = new Argument("top = " + rectText.height);
        Argument middle = new Argument("middle = " + ((rectImage.height / 2) - (rectText.height / 2) + rectText.height));
        Argument bottom = new Argument("bottom = " + (rectImage.height - rectText.height + yOffset));

        Expression expr = new Expression(value.trim(), top, middle, bottom);
        return (int) expr.calculate();
    }

}
