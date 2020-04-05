package com.example;

/**
 * Created by Admin on 1/16/17.
 */
public class pixel {

    protected int red;
    protected int green;
    protected int blue;

    pixel(int r, int g, int b)
    {
        red = r;
        green = g;
        blue = b;
    }

    public int getRed()
    {
        return red;
    }

    public int getGreen()
    {
        return green;
    }

    public int getBlue()
    {
        return blue;
    }

    public StringBuilder print() {

        StringBuilder output = new StringBuilder();
        output.append(red);
        output.append(" ");
        output.append(green);
        output.append(" ");
        output.append(blue);
        output.append(" ");
        return output;
    }
}
