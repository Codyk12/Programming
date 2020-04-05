package com.example;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class MyClass
{
    protected int H;
    protected int W;
    protected int red;
    protected int green;
    protected int blue;
    protected int newRed;
    protected int newGreen;
    protected int newBlue;
    protected int gray;
    protected int max;
    protected int q;

    public static void main(String args[])
    {
        int width;
        int height;

        image newImage;

        try {
            Scanner input = new Scanner(new BufferedInputStream(new FileInputStream(args[0]))).useDelimiter("((#[^\\n]*\\n)|(\\s+))+");

            input.next();
            width = Integer.parseInt(input.next());
            height = Integer.parseInt(input.next());
            input.next();

            MyClass editor = new MyClass();

            image cur_image = new image(height, width);

            for (int i = 0; i < height; i++)
            {
                for (int j = 0; j < width; j++)
                {
                    pixel pixel1 = new pixel(Integer.parseInt(input.next()),Integer.parseInt(input.next()),Integer.parseInt(input.next()));
                    cur_image.add_pixel(pixel1, i, j);
                }
            }


            if(args[2].equals("invert"))
            {
                newImage = editor.invert(cur_image);
            }
            else if(args[2].equals("emboss"))
            {
                newImage = editor.emboss(cur_image);
            }
            else if(args[2].equals("grayscale"))
            {
                newImage = editor.grayScale(cur_image);
            }
            else if(args[2].equals("motionblur"))
            {
                newImage = editor.blur(cur_image, args[3]);
            }
            else{
                System.out.println("Error");
                return;
            }

            try{
                PrintWriter writer = new PrintWriter(args[1]);
                writer.println(newImage.print());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public image invert(image cur_image)
    {
        pixel[][] pixelsOG;
        pixelsOG = cur_image.getOGPixels();

        H = cur_image.getHeight();
        W = cur_image.getWidth();


        pixel[][] new_pixels = new pixel[H][W];

        image newImage = new image(H, W);

        for (int i = 0; i < H; i++)
        {
            for (int j = 0; j < W; j++)
            {
                red = pixelsOG[i][j].getRed();
                green = pixelsOG[i][j].getGreen();
                blue = pixelsOG[i][j].getBlue();

                newRed = 255-red;
                newGreen = 255-green;
                newBlue = 255-blue;

                pixel new_pixel = new pixel(newRed, newGreen, newBlue);

                newImage.add_pixel(new_pixel, i, j);

            }
        }
        return newImage;


    }

    public image grayScale(image cur_image)
    {
        pixel[][] pixelsOG;
        pixelsOG = cur_image.getOGPixels();

        H = cur_image.getHeight();
        W = cur_image.getWidth();

        pixel[][] new_pixels = new pixel[H][W];

        image newImage = new image(H, W);

        for (int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++) {

                red = pixelsOG[i][j].getRed();
                green = pixelsOG[i][j].getGreen();
                blue = pixelsOG[i][j].getBlue();

                gray = (red + green + blue) / 3;

                pixel new_pixel = new pixel(gray, gray, gray);

                newImage.add_pixel(new_pixel, i, j);
            }
        }

        return newImage;
    }

    public image emboss(image cur_image)
    {
        pixel[][] pixelsOG;
        pixelsOG = cur_image.getOGPixels();

        int H = cur_image.getHeight();
        int W = cur_image.getWidth();

        pixel[][] new_pixels = new pixel[H][W];

        image newImage = new image(H, W);

        for (int i = H-1; i >= 0; i--) {
            for (int j = W-1; j >= 0; j--) {

                if(((i - 1) >= 0) && ((j - 1) >= 0)) {
                    newRed = pixelsOG[i][j].getRed() - pixelsOG[i - 1][j - 1].getRed();
                    newGreen = pixelsOG[i][j].getGreen() - pixelsOG[i - 1][j - 1].getGreen();
                    newBlue = pixelsOG[i][j].getBlue() - pixelsOG[i - 1][j - 1].getBlue();

                    max = newRed;

                    if(Math.abs(newGreen) > Math.abs(max))
                        max = newGreen;
                    if(Math.abs(newBlue) > Math.abs(max))
                        max = newBlue;

                    max += 128;

                    if(max > 255)
                        max = 255;
                    if(max < 0)
                        max = 0;
                }
                else{
                    max = 128;
                }

                pixel new_pixel = new pixel(max, max, max);

                newImage.add_pixel(new_pixel, i, j);

            }
        }


        return newImage;
    }

    public image blur(image cur_image, String blurLength)
    {
        int blurlen = Integer.parseInt(blurLength);

        if(blurlen > 1)
        {
            pixel[][] pixelsOG;
            pixelsOG = cur_image.getOGPixels();

            H = cur_image.getHeight();
            W = cur_image.getWidth();

            pixel[][] new_pixels = new pixel[H][W];

            image newImage = new image(H, W);

            for (int i = 0; i < H; i++)
            {
                for (int j = 0; j < W; j++)
                {
                    red = 0;
                    green = 0;
                    blue = 0;
                    if ((j + blurlen) >= W)
                    {
                        q = 0;
                        for (int k = j; k < W; k++)
                        {
                            red += pixelsOG[i][k].getRed();
                            green += pixelsOG[i][k].getGreen();
                            blue += pixelsOG[i][k].getBlue();
                            q++;
                        }
                        red /= q;
                        green /= q;
                        blue /= q;
                    }
                    else
                    {
                        for (int k = j; k < j+blurlen; k++) {
                            red += pixelsOG[i][k].getRed();
                            green += pixelsOG[i][k].getGreen();
                            blue += pixelsOG[i][k].getBlue();
                        }
                        red /= blurlen;
                        green /= blurlen;
                        blue /= blurlen;
                    }

                    pixel new_pixel = new pixel(red, green, blue);

                    newImage.add_pixel(new_pixel, i, j);
                }
            }
            return newImage;
        }
        return cur_image;
    }
}
