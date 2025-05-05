import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            String inputPath = "C:\\java\\Number_plate_detect\\assets\\number_plate.jpg";
            String grayPath = "c:\\java\\Number_plate_detect\\assets\\Gray_Scale_M.png";
            String blurPath = "c:\\java\\Number_plate_detect\\assets\\Gaussian_Blur.png";


            BufferedImage inputImage = ImageIO.read(new File(inputPath));
            if (inputImage == null) {
                System.out.println("failed to load input image");
            }
            GrayScale grayScale = new GrayScale();
            BufferedImage grayImage = grayScale.convertToGrayScaleManual(inputImage);
            ImageIO.write(grayImage, "png", new File(grayPath));


            GaussianBlur gaussianBlur = new GaussianBlur(3, 1.0, grayImage);
            BufferedImage blurredImage = gaussianBlur.applyGaussianBlur();

            if (blurredImage != null) {
                ImageIO.write(blurredImage, "png", new File(blurPath));
                System.out.println("Processes complete, Images saved ");
                System.out.println("Gray Image--->" + grayPath);
                System.out.println("Blur Image--->" + blurPath);
            } else {
                System.out.println("Problem");
            }

        } catch (Exception e) {
            System.out.println("Error in processing image : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
