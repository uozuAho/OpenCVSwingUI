package aho.uozu.opencv.swingui.examples;

import aho.uozu.opencv.swingui.CameraManager;
import aho.uozu.opencv.swingui.CameraSurface;
import org.opencv.core.Core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;


/**
 * An example of how to extend CameraSurface to add user
 * interaction.
 */
public class CameraGUI extends JFrame {

    CameraManager cameraManager;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        try {
            CameraGUI cameraGUI = new CameraGUI();
        }
        catch (IOException e) {
            System.out.println("Error initialising video");
            e.printStackTrace();
        }
    }

    public CameraGUI() throws IOException {
        cameraManager = new CameraManager(0);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new MyCameraSurface(cameraManager), BorderLayout.CENTER);

        // release resources on window close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                cameraManager.release();
            }
        });

        setTitle("CameraGUI");
        pack();
        setVisible(true);
        cameraManager.run();
    }

    private class MyCameraSurface extends CameraSurface {
        Point startDrag, endDrag;

        public MyCameraSurface(CameraManager cm) {
            super(cm);

            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Point p = new Point(e.getX(), e.getY());
                    System.out.println("Click @ " + p);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    startDrag = new Point(e.getX(), e.getY());
                    endDrag = startDrag;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    startDrag = null;
                    endDrag = null;
                    repaint();
                }
            });

            this.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    endDrag = new Point(e.getX(), e.getY());
                    repaint();
                }
            });
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));

            if (startDrag != null && endDrag != null) {
                g2.setPaint(Color.RED);
                Shape r = makeRectangle(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
                g2.draw(r);
            }
        }

        private Rectangle2D.Float makeRectangle(int x1, int y1, int x2, int y2) {
            return new Rectangle2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
        }
    }
}
