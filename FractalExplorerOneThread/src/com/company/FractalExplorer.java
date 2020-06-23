package com.company;

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

public class FractalExplorer
extends JFrame implements Runnable
{
    static final int WIDTH  = 600;
    static final int HEIGHT = 600;
    
    Canvas        canvas;
    static BufferedImage image;
    Mandelbrot mandelbrot = new Mandelbrot();
    Deque<Thread> threads = new LinkedList<>();
    int           threadsStarted;
    
    static final int    MAX_ITERATIONS      = 2000;
    static final double DEFAULT_ZOOM        = 100.0;
    static final double DEFAULT_TOP_LEFT_X  = -3.0;
    static final double DEFAULT_TOP_LEFT_Y  = +3.0;
    
    double zoom     = DEFAULT_ZOOM;
    double topLeftX = DEFAULT_TOP_LEFT_X;
    double topLeftY = DEFAULT_TOP_LEFT_Y;
    
    public FractalExplorer(int width, int height) {
        setInitialGUIProperties();
        addCanvas();
        updateFractal();
        this.setVisible(true);
        
    }
    void addCanvas() {
        canvas = new Canvas();
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        this.add(canvas, BorderLayout.CENTER);
    }
    void setInitialGUIProperties() {
        this.setTitle("Fractal Explorer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WIDTH, HEIGHT);
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    @Override public void run() {}
    double getXPos(double x){
        return x / zoom + topLeftX;
    }
    double getYPos(double y){
        return y / zoom - topLeftY;
    }
    void updateFractal(){
        double c_r;
        double c_i;
        int iterCount;
        int pixelColor;
        int width = WIDTH;
        int height = HEIGHT;
        int x = 0;
        int y = 0;
        switch (threadsStarted){
            case 0: break;
            case 1:
                width  = WIDTH / 2;
                height = HEIGHT / 2;
                x      = 0;
                y      = 0;
                break;
            case 2:
                width  = WIDTH;
                height = HEIGHT / 2;
                x      = WIDTH / 2;
                y      = 0;
                break;
            case 3:
                width  = WIDTH / 2;
                height = HEIGHT;
                x      = 0;
                y      = HEIGHT / 2;
                break;
            case 4:
                width  = WIDTH;
                height = HEIGHT;
                x      = WIDTH / 2;
                y      = HEIGHT / 2;
                break;
        }
        threadsStarted--;
        for (int i = x ; i < width; i++){
            for (int j = y ; j < height; j++){
                c_r = getXPos(i);
                c_i = getYPos(j);
                
                iterCount = mandelbrot.numIterations(c_r, c_i, MAX_ITERATIONS);
                
                pixelColor = makeColor(iterCount);
                image.setRGB(i, j, pixelColor);
            }
        }
        canvas.repaint();
    }
    void multithreadingUpdate(boolean on) {
        final boolean[] isAlive = {true};
        threads.push(new Thread(this::updateFractal));
        threads.push(new Thread(this::updateFractal));
        threads.push(new Thread(this::updateFractal));
        threads.push(new Thread(this::updateFractal));
        if(on) threadsStarted = 4;
        threads.forEach(Thread::start);
        threads.clear();
    }
    int makeColor(int iterCount) {
        return
        iterCount == MAX_ITERATIONS ? Color.BLACK.getRGB() :
        iterCount << 3;
    }
    void adjustZoom(double newX, double newY, double newZoom){
        topLeftX += newX / zoom;
        topLeftY -= newY / zoom;
        zoom = newZoom;
        topLeftX -= (WIDTH / 2.0) / zoom;
        topLeftY += (HEIGHT / 2.0) / zoom;
    
        multithreadingUpdate(false);
    }
    
    private class Canvas extends JPanel implements MouseListener
    {
        public Canvas() {
            addMouseListener(this);
        }
    
        @Override public Dimension getPreferredSize() {
            return new Dimension(WIDTH, HEIGHT);
        }
    
        @Override public void paintComponent(Graphics drawingObj) {
            drawingObj.drawImage(image, 0, 0, null );
        }
    
        @Override public void mousePressed(MouseEvent mouse) {
            double x =  mouse.getX();
            double y =  mouse.getY();
        
            switch( mouse.getButton() ) {
                case MouseEvent.BUTTON1:
                    adjustZoom( x, y,zoom * 2);
                    break;
                case MouseEvent.BUTTON3:
                    adjustZoom( x, y,zoom / 2);
                    break;
            }
        }
        @Override public void mouseClicked(MouseEvent e){}
        @Override public void mouseReleased(MouseEvent e){}
        @Override public void mouseEntered(MouseEvent e){}
        @Override public void mouseExited(MouseEvent e){}
    }
}
