package cn.lxr.example.tricklpaletteapi.model;

import com.trickl.palette.Palette;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;

@Data
@AllArgsConstructor
public class PaletteFile {
    private Palette palette;
    private File file;
    private String name;
}
