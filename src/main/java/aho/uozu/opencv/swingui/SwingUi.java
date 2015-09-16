package aho.uozu.opencv.swingui;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;


public class SwingUi {

    // disallow instantiation
    private SwingUi() {}

    public static Mat imread(String path) throws IOException {
        BufferedImage bimg = ImageIO.read(new File(path));
        return Utils.BufferedImageToMat(bimg);
    }

    public static Mat imread(URL path) throws IOException {
        BufferedImage bimg = ImageIO.read(path);
        return Utils.BufferedImageToMat(bimg);
    }

    public static void imshow(String windowName, final Mat m) {
        final JFrame f = new JFrame();
        BufferedImage img = Utils.MatToBufferedImage(m);
        f.add(new DrawPanel(img));
        f.setTitle(windowName);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // close window on any keypress
        f.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                f.dispose();
            }
        });
        f.setVisible(true);
    }

    private static class DrawPanel extends JPanel {
        private BufferedImage img;

        public DrawPanel(BufferedImage im) {
            img = im;
            Dimension dm = new Dimension(img.getWidth(null), img.getHeight(null));
            setPreferredSize(dm);
        }

        private void doDrawing(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(img, 0, 0, null);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            doDrawing(g);
        }
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        URL img_path = SwingUi.class.getClassLoader().getResource("testimg.jpg");
        try {
            Mat img = imread(img_path);
            imshow("image", img);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
