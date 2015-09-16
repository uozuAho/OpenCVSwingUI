/**
 * OpenCV camera viewer with Swing UI libraries.
 */


package aho.uozu.opencv.swingui;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class CameraViewer {

    private JFrame uiFrame;
    private VideoCapture cap;
    private BufferedImage frame;
    private CameraPanel cameraPanel;
    private Thread frameGrabber;
    private FrameProcessor frameProcessor;

    private int frameWidth;
    private int frameHeight;

    public CameraViewer() {
        init("Camera Feed");
    }

    public CameraViewer(String title) {
        init(title);
    }

    private void init(String title) {
        try {
            initVideo();
            initUI(title);
            frameGrabber = new Thread(new FrameGrabber());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        uiFrame.setVisible(true);
        frameGrabber.start();
    }

    public void setFrameProcessor(FrameProcessor fp) {
        frameProcessor = fp;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    /**
     * Initialise camera and get first frame.
     * Ensures frame != null.
     */
    private void initVideo() throws Exception {
        // initialise video
        cap = new VideoCapture(0);
        try {
            Thread.sleep(500);
        }
        catch (InterruptedException e) {}

        // get a frame to initialise GUI
        Mat vidFrame = new Mat();
        while (!cap.read(vidFrame)) {}

        if (vidFrame != null) {
            frame = Utils.MatToBufferedImage(vidFrame);
            frameWidth = frame.getWidth();
            frameHeight = frame.getHeight();
            vidFrame.release();
        }
        else {
            throw new Exception("vidFrame shouldn't be null!");
        }
    }

    /**
     * Initialise UI. Expects video to be initialised
     * and frame != null.
     */
    private void initUI(String title) {
        // initialise window
        uiFrame = new JFrame();
        uiFrame.setTitle(title);
        cameraPanel = new CameraPanel(frame.getWidth(), frame.getHeight());
        uiFrame.add(cameraPanel);
        uiFrame.pack();
        uiFrame.setLocationRelativeTo(null);
        uiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // stop video capture & release resources when window closes
        uiFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                frameGrabber.interrupt();
                cap.release();
                System.exit(0);
            }
        });
    }

    /**
     * Runnable to grab new camera frames.
     */
    private class FrameGrabber implements Runnable {
        @Override
        public void run() {
            for(;;) {
                try {
                    Thread.sleep(10);
                    Mat vidFrame = new Mat();
                    if (cap.read(vidFrame)) {
                        Mat outFrame;
                        if (frameProcessor != null) {
                            outFrame = frameProcessor.process(vidFrame);
                        }
                        else {
                            outFrame = vidFrame;
                        }
                        if (outFrame != null && !outFrame.empty()
                                && outFrame.rows() > 0 && outFrame.cols() > 0) {
                            frame = Utils.MatToBufferedImage(outFrame);
                            cameraPanel.repaint();
                        }
                        vidFrame.release();
                    }
                }
                catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    /**
     * UI panel for drawing camera frames on.
     */
    private class CameraPanel extends JPanel {

        public CameraPanel(int width, int height) {
            Dimension dm = new Dimension(width, height);
            setPreferredSize(dm);
        }

        private void doDrawing(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(frame, 0, 0, null);
            if (frameProcessor != null) {
                frameProcessor.draw(g2d);
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            doDrawing(g);
        }
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        CameraViewer camview = new CameraViewer();
        camview.run();
    }
}
