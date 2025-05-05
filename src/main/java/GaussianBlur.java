import java.awt.image.BufferedImage;

public class GaussianBlur {  // Changed class name to follow conventions
    private final int size;
    private final double sigma;
    private final BufferedImage image;
    private final double[][] kernel;

    public GaussianBlur(int size, double sigma, BufferedImage image) {
        if (size % 2 == 0) {
            throw new IllegalArgumentException("Size must be odd");
        }
        this.size = size;
        this.sigma = sigma;
        this.image = image;
        this.kernel = createKernel();
    }

    private double[][] createKernel() {
        double[][] kernel = new double[size][size];
        int center = size / 2;
        double sum = 0;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int dx = x - center;
                int dy = y - center;
                kernel[x][y] = Math.exp(-(dx * dx + dy * dy) / (2 * sigma * sigma));
                sum += kernel[x][y];
            }
        }

        // Normalize the kernel
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                kernel[x][y] /= sum;
            }
        }
        return kernel;
    }

    public BufferedImage applyGaussianBlur() {
        int width = image.getWidth();
        int height = image.getHeight();
        int pad = size / 2;

        BufferedImage output = new BufferedImage(width, height, image.getType());

        // Process each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double sum = 0;
                double weightSum = 0;

                // Apply kernel
                for (int ky = 0; ky < size; ky++) {
                    for (int kx = 0; kx < size; kx++) {
                        int px = x + kx - pad;
                        int py = y + ky - pad;

                        // Clamp to image boundaries
                        px = Math.max(0, Math.min(width - 1, px));
                        py = Math.max(0, Math.min(height - 1, py));

                        int pixel = image.getRGB(px, py);
                        int gray = (pixel >> 16) & 0xFF;  // For grayscale, all components are equal
                        double weight = kernel[kx][ky];

                        sum += gray * weight;
                        weightSum += weight;
                    }
                }

                int value = (int) Math.round(sum / weightSum);
                value = Math.max(0, Math.min(255, value));
                int newPixel = (value << 16) | (value << 8) | value;
                output.setRGB(x, y, newPixel);
            }
        }

        return output;
    }
}