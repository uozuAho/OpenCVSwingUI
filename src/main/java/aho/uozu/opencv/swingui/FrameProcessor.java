package aho.uozu.opencv.swingui;

import org.opencv.core.Mat;

import java.awt.*;

public interface FrameProcessor {
    /**
     * Process the given matrix & return the result
     */
    Mat process(Mat m);

    /**
     * Draw on top of the output frame
     */
    void draw(Graphics2D g);
}
