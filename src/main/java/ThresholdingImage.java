import java.awt.image.BufferedImage;
import java.awt.Color;

public class ThresholdingImage {
    private BufferedImage grayImage;
    private int blockSize; // size of local region (must be odd)
    private int c;         // constant to subtract

    public ThresholdingImage(BufferedImage grayImage, int blockSize, int c) {
        this.grayImage = grayImage;
        this.blockSize = blockSize;
        this.c = c;
    }

    public BufferedImage applyAdaptiveMeanThresholding() {
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        // Compute integral image for faster mean calculation
        long[][] integral = new long[height + 1][width + 1];
        for (int y = 1; y <= height; y++) {
            long rowSum = 0;
            for (int x = 1; x <= width; x++) {
                int gray = new Color(grayImage.getRGB(x - 1, y - 1)).getRed();
                rowSum += gray;
                integral[y][x] = integral[y - 1][x] + rowSum;
            }
        }

        int radius = blockSize / 2;

        for (int y = 0; y < height; y++) {
            for (int x =  0; x < width; x++) {
                int x1 = Math.max(0, x - radius);
                int y1 = Math.max(0, y - radius);
                int x2 = Math.min(width - 1, x + radius);
                int y2 = Math.min(height - 1, y + radius);

                long sum = integral[y2 + 1][x2 + 1] - integral[y1][x2 + 1]
                        - integral[y2 + 1][x1] + integral[y1][x1];
                int area = (x2 - x1 + 1) * (y2 - y1 + 1);
                int localMean = (int)(sum / area);

                int pixelGray = new Color(grayImage.getRGB(x, y)).getRed();
                int threshold = localMean - c;

                binaryImage.setRGB(x, y, (pixelGray > threshold) ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
            }
        }

        return binaryImage;
    }
}