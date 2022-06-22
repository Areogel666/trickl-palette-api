package cn.lxr.example.tricklpaletteapi.executor;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * 判断透明，并将透明部分剪裁（得到一个正方形）
 */
@Slf4j
public class TransparentCutMain {

    public static String RESOURCES_PATH = "src/main/resources/";
    private final static int WHITE_THRESHOLD = 220;

    public static void main(String[] args) {
        File fileDir = new File(RESOURCES_PATH + "upload");
        Arrays.stream(fileDir.listFiles(File::isFile)).forEach(file -> {
            log.info("文件名：{}", file.getName());
            BufferedImage image = getBufferedImage(file);
            BufferedImage subImage = cut(image);
            try {
                createImage(subImage, RESOURCES_PATH + "cut/" + file.getName(), "png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 判断透明并剪裁
     * @param image
     */
    private static BufferedImage cut(BufferedImage image) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        boolean minXb = false, minYb = false, transparent = false;
        int minX = 0, minY = 0, maxX = 0, maxY = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // 如果是透明像素 跳过
                if (image.getRGB(x,y) == 0) {
                    transparent = true;
                    continue;
                }
//                // 获取该点像素，并以object类型表示
//                Object data = image.getRaster().getDataElements(x, y, null);
//                int r = image.getColorModel().getRed(data);
//                int g = image.getColorModel().getGreen(data);
//                int b = image.getColorModel().getBlue(data);
//                // 如果是近白像素，跳过
//                if (r > WHITE_THRESHOLD && g > WHITE_THRESHOLD && b > WHITE_THRESHOLD) {
//                    continue;
//                }
                if (!minXb) {
                    minX = x;
                    minXb = true;
                }
                if (!minYb) {
                    minY = y;
                    minYb = true;
                }
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
        }
        if (transparent) {
            log.info("透明");
        }
        log.info("获取到有内容区域左下角坐标为:({},{})", minX, minY);
        log.info("获取到有内容区域右上角坐标为:({},{})", maxX, maxY);
        return image.getSubimage(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * 生成图片流
     * @param file
     * @return
     */
    private static BufferedImage getBufferedImage(File file) {
        String path = "/" + file.getPath().replaceAll(RESOURCES_PATH, "");
        try {
            InputStream imageStream = ProminentHueMain.class.getResourceAsStream(path);
            return ImageIO.read(imageStream);
        } catch (IOException e) {
            log.error("read image error", e);
        }
        return null;
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

}
