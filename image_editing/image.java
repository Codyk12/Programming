package com.example;

/**
 * Created by Admin on 1/16/17.
 */

public class image
{
    protected int H;
    protected int W;
    protected pixel[][] pixels;

    image(int H, int W)
    {
        this.H = H;
        this.W = W;
        pixels = new pixel[H][W];
    }

    public void add_pixel(pixel cur_pixel, int height, int width)
    {
        pixels[height][width] = cur_pixel;
    }

    public pixel[][] getPixels()
    {
        pixel[][] pixelsR = new pixel[H][W];

        for (int i = 0; i < H; i++)
        {
            for (int j = 0; j < W; j++)
            {
                pixelsR[i][j] = pixels[i][j];
            }
        }
        return pixelsR;
    }

    public pixel[][] getOGPixels()
    {
        return pixels;
    }

    public int getWidth()
    {
        return W;
    }

    public int getHeight()
    {
        return H;
    }

    public StringBuilder print()
    {
        StringBuilder output = new StringBuilder();

        output.append("P3 ");
        output.append(W);
        output.append(" ");
        output.append(H);
        output.append(" ");
        output.append(255);
        output.append(" ");


        for (int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++)
                output.append(pixels[i][j].print());
        }

        return output;
    }
}
