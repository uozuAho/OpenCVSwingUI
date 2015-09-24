package aho.uozu.opencv.swingui;

import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CameraSurface extends JComponent {

    Mat lastFrame;
    private CameraManager cameraManager;

    public CameraSurface(CameraManager cm) {
        cameraManager = cm;
        setPreferredSize(new Dimension(cm.getFrameWidth(), cm.getFrameHeight()));
        lastFrame = new Mat();
        setFrameProcessor();
    }

    @Override
    public void paint(Graphics g) {
        doDrawing(g);
    }

    void setFrameProcessor() {
        cameraManager.setFrameProcessor(new DefaultProcessor());
    }

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        BufferedImage frame = Utils.MatToBufferedImage(lastFrame);
        g2d.drawImage(frame, 0, 0, null);
    }

    /**
     * Default processor just copies camera frame to lastFrame
     */
    private class DefaultProcessor implements CameraManager.FrameProcessor  {
        @Override
        public void onFrame(Mat m) {
            m.copyTo(lastFrame);
            CameraSurface.this.repaint();
        }
    }
}
