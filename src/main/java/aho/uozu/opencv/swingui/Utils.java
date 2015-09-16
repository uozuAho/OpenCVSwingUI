package aho.uozu.opencv.swingui;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Utils {

    public static BufferedImage MatToBufferedImage(Mat m) {
        Mat m2 = new Mat();
        int bufferedImgType = BufferedImage.TYPE_BYTE_GRAY;
        if (m.type() == CvType.CV_8UC3) {
            bufferedImgType = BufferedImage.TYPE_3BYTE_BGR;
            Imgproc.cvtColor(m, m2, Imgproc.COLOR_BGR2RGB);
        } else if (m.type() == CvType.CV_8UC4) {
            bufferedImgType = BufferedImage.TYPE_4BYTE_ABGR;
            Imgproc.cvtColor(m, m2, Imgproc.COLOR_BGRA2RGBA);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + m.type());
        }
        int blen = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[blen];
        m2.get(0, 0, b);
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), bufferedImgType);
        image.getRaster().setDataElements(0, 0, m.cols(), m.rows(), b);
        m2.release();
        return image;
    }

    public static Mat BufferedImageToMat(BufferedImage img) {
        byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        // TODO: will probably need to convert image type / colour here
        Mat mat = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);
        return mat;
    }
}
