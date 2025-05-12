import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            String inputPath = "C:\\java\\Number_plate_detect\\assets\\number_plate.jpg";
            String grayPath = "c:\\java\\Number_plate_detect\\assets\\Gray_Scale_M.png";
            String blurPath = "c:\\java\\Number_plate_detect\\assets\\Gaussian_Blur.png";
            String thresholdingPath = "c:\\java\\Number_plate_detect\\assets\\Thresholding_img.png";
            String edgesPath = "c:\\java\\Number_plate_detect\\assets\\Edges.png";

            // 1. Load and convert to grayscale
            BufferedImage inputImage = ImageIO.read(new File(inputPath));
            if (inputImage == null) {
                System.out.println("Failed to load input image");
                return;
            }

            GrayScale grayScale = new GrayScale();
            BufferedImage grayImage = grayScale.convertToGrayScaleManual(inputImage);
            ImageIO.write(grayImage, "png", new File(grayPath));

            // 2. Apply Gaussian blur
            GaussianBlur gaussianBlur = new GaussianBlur(3, 1.0, grayImage);
            BufferedImage blurredImage = gaussianBlur.applyGaussianBlur();

            // 3. Apply adaptive thresholding
            ThresholdingImage thresholder = new ThresholdingImage(blurredImage, 15, 5);
            BufferedImage binary = thresholder.applyAdaptiveMeanThresholding();

            // 4. Edge detection
            CannyEdgeDetector edgeDetector = new CannyEdgeDetector(binary, 30, 100);
            BufferedImage edges = edgeDetector.detectEdges();

            // Save all results
            ImageIO.write(blurredImage, "png", new File(blurPath));
            ImageIO.write(binary, "png", new File(thresholdingPath));
            ImageIO.write(edges, "png", new File(edgesPath));

            System.out.println("Processing complete. Images saved to:");
            System.out.println("Gray Image: " + grayPath);
            System.out.println("Blurred Image: " + blurPath);
            System.out.println("Thresholded Image: " + thresholdingPath);
            System.out.println("Edge Detection: " + edgesPath);

        } catch (Exception e) {
            System.out.println("Error in processing image: " + e.getMessage());
            e.printStackTrace();
        }
    }
}