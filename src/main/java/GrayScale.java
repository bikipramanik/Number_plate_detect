import java.awt.*;
import java.awt.image.BufferedImage;

public class GrayScale {

    public BufferedImage convertToGrayScaleManual(BufferedImage image) {

        if (image == null) {
            System.out.println("Image path null");
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);

                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;

                //using standard luminosity formula
                int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);

                //setting grayscale value
                output.getRaster().setSample(x, y, 0, gray);
            }

        }

        return output;
    }

    public BufferedImage convertToGrayScale(BufferedImage image) {
        if (image == null) {
            System.out.println("Image null");
            return null;
        }

        // Create new grayscale image
        BufferedImage grayImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);

        // Draw the original image onto the grayscale image
        Graphics g = grayImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return grayImage;
    }
}
