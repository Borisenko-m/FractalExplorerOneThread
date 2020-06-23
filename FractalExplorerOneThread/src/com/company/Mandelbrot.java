package com.company;

import java.awt.geom.Rectangle2D;

public class Mandelbrot extends FractalGenerator
{
    @Override public void getInitialRange(Rectangle2D.Double range)
    {
    
    }
    @Override public int numIterations(double x, double y, int max)
    {
        double z_r = 0.0;
        double z_i = 0.0;
        double z_r_tmp;
        int iterCount = 0;
        while (z_r * z_r + z_i * z_i <= 4.0)
        {
            z_r_tmp = z_r;
            z_r = z_r * z_r - z_i * z_i + x;
            z_i = 2 * z_i * z_r_tmp + y;
            if (iterCount >= max) return max;
            iterCount ++;
        }
        return iterCount;
    }
}
