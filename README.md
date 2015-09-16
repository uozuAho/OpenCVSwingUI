# OpenCVSwingUI
Swing-based UI library for OpenCV

# Getting started
- Requires OpenCV (tested using 3.0.0)
- Intellij IDEA should be able to import this project.
- CameraViewer and SwingUi classes have main functions that
  demonstrate how to use the classes.
- Note: You need to specify the OpenCV native library path in
  order to run the examples. Add the following argument when
  running the jvm:
  `-Djava.library.path=/path/to/your/opencv-3.0.0/release/lib`
  In IDEA, this is under Run -> Edit Configurations ->
  <select application to run> -> VM options

# Notes
- This project includes the OpenCV 3.0.0 java library, but not the
  native library. This needs to be
