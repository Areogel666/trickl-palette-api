package cn.lxr.example.tricklpaletteapi.executor;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * 多层图片叠加
 */
@Slf4j
public class CompositeImageMain {

    public static String RESOURCES_PATH = "src/main/resources/";

    public static void main(String[] args) throws IOException {
//        BufferedImage sourceImage = ImageIO.read(new File(RESOURCES_PATH + "upload", "Firefox.png"));
        File fileDir = new File(RESOURCES_PATH + "uploadComposite");
        Arrays.stream(fileDir.listFiles(File::isFile)).parallel().forEach(file -> {
            BufferedImage sourceImage = getBufferedImage(file);

            BufferedImage foreground = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Color color = newColor("99313135");
            Graphics2D fgGraphics = foreground.createGraphics();
            fgGraphics.setColor(color);
            fgGraphics.fillRect(0, 0, sourceImage.getWidth(), sourceImage.getWidth());
            fgGraphics.dispose();

            BufferedImage outImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D outGraphics = outImage.createGraphics();
            outGraphics.drawImage(sourceImage, 0, 0, Color.WHITE, null);
            outGraphics.drawImage(foreground, 0, 0,null);
            outGraphics.dispose();
            try {
                createImage(outImage,RESOURCES_PATH + "composite/" + file.getName(),"png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * create Color
     * @param hexString
     * @return
     */
    public static Color newColor(String hexString) {
        long l = Long.parseLong(hexString, 16);
        int alpha = (int)(l >> 24) & 0xFF;
        int b = (int)(l >> 0) & 0xFF;
        int g = (int)(l >> 8) & 0xFF;
        int r = (int)(l >> 16) & 0xFF;
        return new Color(r, g, b, alpha);
    }

    /**
     * 输出图片流
     * @param image
     * @param output
     * @return
     */
    private static boolean createImage(BufferedImage image, String output, String format) throws IOException {
        return ImageIO.write(image, format, new File(output));
    }

    /**
     * 生成图片流
     * @param file
     * @return
     */
    private static BufferedImage getBufferedImage(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            log.error("read image error", e);
        }
        return null;
    }
}
