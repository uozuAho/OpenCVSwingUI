package aho.uozu.opencv.swingui;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.io.IOException;


/**
 * Wraps OpenCV's VideoCapture object, provides an onFrame
 * method.
 *
 * Remember to call release() when done with this object.
 */
public class CameraManager {

    private VideoCapture cap;
    private FrameProcessor frameProcessor;
    private Thread frameGrabber;
    private int frameWidth;
    private int frameHeight;

    /** Small usage example */
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        try {
            CameraManager cam = new CameraManager(0);
            cam.setFrameProcessor(new FrameProcessor() {
                @Override
                public void onFrame(Mat frame) {
                    System.out.println("Got frame: " + frame.width() + " " + frame.height());
                }
            });
            cam.run();

            // sleep until user stops
            for (;;) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("releasing resources");
                    cam.release();
                }
            }
        }
        catch (IOException e) {
            System.out.println("Couldn't initialise camera");
            e.printStackTrace();
        }
    }

    /**
     * Initialise the camera.
     *
     * @param videoSource 0 = first available camera
     */
    public CameraManager(int videoSource) throws IOException {
        initVideo(videoSource);
    }

    /**
     * Initialise the camera & frame processor.
     *
     * @param videoSource 0 = first available camera
     * @param fp deal with frames as they are available
     */
    public CameraManager(int videoSource, FrameProcessor fp) throws IOException {
        initVideo(videoSource);
        setFrameProcessor(fp);
    }

    /**
     * Release internal resources.
     */
    public void release() {
        if (frameGrabber != null) {
            frameGrabber.interrupt();
        }
        if (cap != null) {
            cap.release();
        }
    }

    /**
     * Set the thing that does stuff with camera frames
     */
    public void setFrameProcessor(FrameProcessor fp) {
        frameProcessor = fp;
    }

    /**
     * Start capturing frames
     */
    public void run() {
        frameGrabber = new Thread(new FrameGrabber());
        frameGrabber.run();
    }

    public int getFrameWidth()  { return frameWidth; }
    public int getFrameHeight() { return frameHeight; }

    /**
     * Interface for doing stuff with camera frames as they become available.
     */
    public interface FrameProcessor {
        /**
         * Called when a new frame is available from the camera.
         * frame is released internally, so if you want to keep
         * it, you must manually copy it to your own data.
         */
        void onFrame(Mat frame);
    }

    private void initVideo(int videoSource) throws IOException {
        // initialise video
        cap = new VideoCapture(videoSource);
        try {
            // give it a bit of time to initialise (often done in code samples)
            Thread.sleep(500);
        }
        catch (InterruptedException e) {
            release();
            return;
        }

        // get a frame to initialise GUI
        Mat frame = new Mat();
        while (frameWidth == 0 || frameHeight == 0) {
            if (cap.read(frame)) {
                frameWidth = frame.width();
                frameHeight = frame.height();
            }
        }
        frame.release();
    }

    private class FrameGrabber implements Runnable {
        @Override
        public void run() {
            for(;;) {
                try {
                    Thread.sleep(10);
                    Mat vidFrame = new Mat();
                    if (cap.read(vidFrame)) {
                        if (frameProcessor != null) {
                            frameProcessor.onFrame(vidFrame);
                            vidFrame.release();
                        }
                    }
                }
                catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
