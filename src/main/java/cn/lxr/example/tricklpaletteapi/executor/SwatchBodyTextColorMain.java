package cn.lxr.example.tricklpaletteapi.executor;

import cn.lxr.example.tricklpaletteapi.model.PaletteFile;
import com.trickl.palette.Palette;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 获取推荐文本颜色值
 */
@Slf4j
public class SwatchBodyTextColorMain {

    public static String RESOURCES_PATH = "src/main/resources/";

    public static void main(String[] args) {
        File fileDir = new File(RESOURCES_PATH + "upload");
        List<PaletteFile> paletteFileList = Arrays.stream(fileDir.listFiles(File::isFile)).parallel().map(file -> {
            BufferedImage bufferedImage = getBufferedImage(file);
            ColorModel colorModel = bufferedImage.getColorModel();
            System.out.println(file.getName() + "\t" + colorModel.getTransparency());
//            // 直接生成实例
//            Palette palette = Palette.from(bufferedImage).generate();
            // builder构筑生成实例
            Palette.Builder builder = new Palette.Builder(bufferedImage);
            builder.maximumColorCount(16);
            Palette palette = builder.generate();
            return new PaletteFile(palette, file, file.getName());
        }).collect(Collectors.toList());
        System.out.println("FileName\tDominant\tLightVibrant\tVibrant\tDarkVibrant\tLightMuted\tMuted\tDarkMuted");
        paletteFileList.parallelStream().forEach(paletteFile -> System.out.println(paletteFile.getName()
                + "\t" + getIfNotNull2RGBTextHex(paletteFile.getPalette().getDominantSwatch(), Palette.Swatch :: getBodyTextColor)
                + "\t" + getIfNotNull2RGBTextHex(paletteFile.getPalette().getLightVibrantSwatch(), Palette.Swatch :: getBodyTextColor)
                + "\t" + getIfNotNull2RGBTextHex(paletteFile.getPalette().getVibrantSwatch(), Palette.Swatch :: getBodyTextColor)
                + "\t" + getIfNotNull2RGBTextHex(paletteFile.getPalette().getDarkVibrantSwatch(), Palette.Swatch :: getBodyTextColor)
                + "\t" + getIfNotNull2RGBTextHex(paletteFile.getPalette().getLightMutedSwatch(), Palette.Swatch :: getBodyTextColor)
                + "\t" + getIfNotNull2RGBTextHex(paletteFile.getPalette().getMutedSwatch(), Palette.Swatch :: getBodyTextColor)
                + "\t" + getIfNotNull2RGBTextHex(paletteFile.getPalette().getDarkMutedSwatch(), Palette.Swatch :: getBodyTextColor)
        ));
    }

    /**
     * 生成图片流
     * @param file
     * @return
     */
    private static BufferedImage getBufferedImage(File file) {
        String path = "/" + file.getPath().replaceAll(RESOURCES_PATH, "");
        try {
            InputStream imageStream = SwatchBodyTextColorMain.class.getResourceAsStream(path);
            return ImageIO.read(imageStream);
        } catch (IOException e) {
            log.error("read image error", e);
        }
        return null;
    }

    private static String getIfNotNull2RGBTextHex(Palette.Swatch swatch, Function<Palette.Swatch, Color> func) {
        if (swatch == null) {
            return "null";
        }
        return Integer.toHexString(func.apply(swatch).getRGB());
    }
}
