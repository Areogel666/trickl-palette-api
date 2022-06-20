package cn.lxr.example.tricklpaletteapi.executor;

import cn.lxr.example.tricklpaletteapi.model.PaletteFile;
import com.trickl.palette.Palette;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 获取主色调
 */
@Slf4j
public class ProminentHueMain {

    public static String RESOURCES_PATH = "src/main/resources/";

    public static void main(String[] args) {
        File fileDir = new File(RESOURCES_PATH + "upload");
        System.out.println("FileName\tDominant\tLightVibrant\tVibrant\tDarkVibrant\tLightMuted\tMuted\tDarkMuted");
        List<PaletteFile> paletteFileList = Arrays.stream(fileDir.listFiles(File::isFile)).parallel().map(file -> {
            BufferedImage bufferedImage = getBufferedImage(file);
//            // 直接生成实例
//            Palette palette = Palette.from(bufferedImage).generate();
            // builder构筑生成实例
            Palette.Builder builder = new Palette.Builder(bufferedImage);
            builder.maximumColorCount(16);
            Palette palette = builder.generate();
            return new PaletteFile(palette, file, file.getName());
        }).collect(Collectors.toList());
        paletteFileList.parallelStream().forEach(paletteFile -> System.out.println(paletteFile.getName()
                + "\t" + getIfNotNull2RGBHex(paletteFile.getPalette().getDominantSwatch(), Palette.Swatch :: getRgb)
                + "\t" + getIfNotNull2RGBHex(paletteFile.getPalette().getLightVibrantSwatch(), Palette.Swatch :: getRgb)
                + "\t" + getIfNotNull2RGBHex(paletteFile.getPalette().getVibrantSwatch(), Palette.Swatch :: getRgb)
                + "\t" + getIfNotNull2RGBHex(paletteFile.getPalette().getDarkVibrantSwatch(), Palette.Swatch :: getRgb)
                + "\t" + getIfNotNull2RGBHex(paletteFile.getPalette().getLightMutedSwatch(), Palette.Swatch :: getRgb)
                + "\t" + getIfNotNull2RGBHex(paletteFile.getPalette().getMutedSwatch(), Palette.Swatch :: getRgb)
                + "\t" + getIfNotNull2RGBHex(paletteFile.getPalette().getDarkMutedSwatch(), Palette.Swatch :: getRgb)
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
            InputStream imageStream = ProminentHueMain.class.getResourceAsStream(path);
            return ImageIO.read(imageStream);
        } catch (IOException e) {
            log.error("read image error", e);
        }
        return null;
    }

    private static String getIfNotNull(Palette.Swatch swatch, Function<Palette.Swatch, Object> func) {
        if (swatch == null) {
            return "null";
        }
        return String.valueOf(func.apply(swatch));
    }

    private static String getIfNotNull2RGBHex(Palette.Swatch swatch, Function<Palette.Swatch, Integer> func) {
        if (swatch == null) {
            return "null";
        }
        return Integer.toHexString(func.apply(swatch)).substring(2);
    }
}
