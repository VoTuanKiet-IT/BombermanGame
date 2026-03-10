package tile;

import java.awt.image.BufferedImage;
//import java.awt.Color;

public class Tile {
//    public Color color;         // Màu sắc của ô (thay cho hình ảnh tạm thời)
    public BufferedImage image;
    public boolean collision = false; // Thuộc tính xác định ô này có chặn đường đi không
}