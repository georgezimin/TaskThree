package org.zimin;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.opencv.opencv_core.CvPoint;
import org.bytedeco.opencv.opencv_core.CvRect;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_TM_CCORR_NORMED;
import static org.bytedeco.opencv.global.opencv_imgproc.cvMatchTemplate;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvLoadImage;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws TemplateNotFoundException {
        try{
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        String username = System.getProperty("user.name");
        String path = "C:\\Users\\" + username + "\\Desktop\\";
        File imageFile = new File(path, "screen.png");
        try {
            BufferedImage screen = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ImageIO.write(screen, "png", new File(path, "screen.png"));
        } catch (AWTException exc) {
            exc.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        IplImage src = cvLoadImage(path + "screen.png", 0);
        IplImage tmp = cvLoadImage(path + "furfing\\template.png", 0);
        IplImage result = cvCreateImage(cvSize(src.width() - tmp.width() + 1, src.height() - tmp.height() + 1),
                IPL_DEPTH_32F,1);
        cvZero(result);

        cvMatchTemplate(src, tmp, result, CV_TM_CCORR_NORMED);
        DoublePointer min_val = new DoublePointer();
        DoublePointer max_val = new DoublePointer();

        CvPoint minLoc = new CvPoint();
        CvPoint maxLoc = new CvPoint();

        cvMinMaxLoc(result, min_val, max_val, minLoc, maxLoc, null);
        CvPoint point = new CvPoint();
        point.x(maxLoc.x() + tmp.width());
        point.y(maxLoc.y() + tmp.height());
        if (point.x() == 0 || maxLoc.x() == 0) {
            throw new TemplateNotFoundException();
        }
        String resToFile = "Темплейт находится по координатам\n"
                            +"Начало: X - " + maxLoc.x() + " Y - " + maxLoc.y() + "\n"
                            +"Конец: X - " + point.x() + " Y - " + point.y();
        try {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path+"\\coordinates.txt"));
        writer.write(resToFile);
        writer.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }

    }
}
