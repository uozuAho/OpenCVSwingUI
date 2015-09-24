package aho.uozu.opencv.swingui.examples;

import aho.uozu.opencv.swingui.CameraManager;
import aho.uozu.opencv.swingui.CameraSurface;
import org.opencv.core.Core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;


/** Simple example of viewing camera images using
 *  CameraManager and CameraSurface objects.
 */
public class CameraViewer extends JFrame {

    private CameraSurface cameraSurface;
    private CameraManager cameraManager;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        try {
            CameraViewer cameraViewer = new CameraViewer(0);
        } catch (IOException e) {
            System.out.println("Error initialising video");
            e.printStackTrace();
        }
    }

    public CameraViewer(int videoSource) throws IOException {
        cameraManager = new CameraManager(videoSource);
        cameraSurface = new CameraSurface(cameraManager);

        // ui
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(cameraSurface, BorderLayout.CENTER);
        setTitle("CameraViewer");
        pack();

        // release resources when window closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                cameraManager.release();
            }
        });

        setVisible(true);
        cameraManager.run();
    }
}
