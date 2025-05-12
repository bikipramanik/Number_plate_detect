import java.awt.image.BufferedImage;
import java.awt.Color;

public class CannyEdgeDetector {
    private final BufferedImage image;
    private final int lowThreshold;
    private final int highThreshold;
    private final int width;
    private final int height;

    public CannyEdgeDetector(BufferedImage image, int lowThreshold, int highThreshold) {
        if (image == null) throw new IllegalArgumentException("Image cannot be null");
        if (lowThreshold >= highThreshold) throw new IllegalArgumentException("Low threshold must be less than high threshold");

        this.image = image;
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public BufferedImage detectEdges() {
        // Step 1: Compute gradients
        float[][] gradientMagnitude = new float[width][height];
        float[][] gradientDirection = new float[width][height];
        computeGradients(gradientMagnitude, gradientDirection);

        // Step 2: Non-maximum suppression
        BufferedImage suppressed = nonMaxSuppression(gradientMagnitude, gradientDirection);

        // Step 3: Hysteresis thresholding
        return hysteresisThreshold(suppressed);
    }

    private void computeGradients(float[][] magnitude, float[][] direction) {
        int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] sobelY = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                float gx = 0, gy = 0;

                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int pixel = new Color(image.getRGB(x + kx, y + ky)).getRed();
                        gx += pixel * sobelX[ky + 1][kx + 1];
                        gy += pixel * sobelY[ky + 1][kx + 1];
                    }
                }

                magnitude[x][y] = (float) Math.sqrt(gx * gx + gy * gy);
                direction[x][y] = (float) Math.toDegrees(Math.atan2(gy, gx));
            }
        }
    }

    private BufferedImage nonMaxSuppression(float[][] magnitude, float[][] direction) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                float angle = direction[x][y];
                float mag = magnitude[x][y];

                // Normalize angle to nearest 45 degree sector
                float normAngle = ((angle + 22.5f) % 180) / 45;
                int sector = (int) normAngle;
                if (sector > 3) sector = 0;

                boolean isMax = switch (sector) {
                    case 0 -> mag >= magnitude[x+1][y] && mag >= magnitude[x-1][y];
                    case 1 -> mag >= magnitude[x+1][y-1] && mag >= magnitude[x-1][y+1];
                    case 2 -> mag >= magnitude[x][y-1] && mag >= magnitude[x][y+1];
                    case 3 -> mag >= magnitude[x-1][y-1] && mag >= magnitude[x+1][y+1];
                    default -> true;
                };

                int value = isMax ? Math.min(255, (int) mag) : 0;
                result.setRGB(x, y, new Color(value, value, value).getRGB());

            }
        }
        return result;
    }

    private BufferedImage hysteresisThreshold(BufferedImage image) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        boolean[][] visited = new boolean[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gray = new Color(image.getRGB(x, y)).getRed();
                if (!visited[x][y] && gray >= highThreshold) {
                    followEdge(x, y, image, result, visited);
                }
            }
        }
        return result;
    }

    private void followEdge(int x, int y, BufferedImage image, BufferedImage result, boolean[][] visited) {
        if (x <= 0 || x >= width - 1 || y <= 0 || y >= height - 1 || visited[x][y]) return;

        visited[x][y] = true;
        int gray = new Color(image.getRGB(x, y)).getRed();

        if (gray >= lowThreshold) {
            result.setRGB(x, y, Color.WHITE.getRGB());
            for (int ky = -1; ky <= 1; ky++) {
                for (int kx = -1; kx <= 1; kx++) {
                    if (kx != 0 || ky != 0) {
                        followEdge(x + kx, y + ky, image, result, visited);
                    }
                }
            }
        }
    }
}