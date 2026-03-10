package tile;

import main.GamePanel;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.io.IOException;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][]; // Mảng 2 chiều lưu trữ bản đồ

    public TileManager(GamePanel gp) {
        this.gp = gp;
        
        // Khởi tạo mảng chứa 10 loại tile khác nhau (hiện tại mình dùng 3 loại)
        tile = new Tile[10]; 
        mapTileNum = new int[gp.maxScreenRow][gp.maxScreenCol]; // Lưu ý: Row (hàng) đi trước, Col (cột) đi sau
        
        getTileTypes();
        loadMap("/maps/level1.txt");
    }

    public void getTileTypes() {
        try {
        // Đọc file ảnh từ thư mục res/tiles/
        tile[0] = new Tile();
        tile[0].image = ImageIO.read(getClass().getResourceAsStream("/res/grass.png"));

        tile[1] = new Tile();
        tile[1].image = ImageIO.read(getClass().getResourceAsStream("/res/wall.png"));;
        tile[1].collision = true;

        tile[2] = new Tile();
        tile[2].image = ImageIO.read(getClass().getResourceAsStream("/res/brick.png"));
        tile[2].collision = true;

        } catch (IOException e) {
            e.printStackTrace(); // Báo lỗi nếu không tìm thấy file ảnh
        }
//        // Mã số 0: Cỏ (Màu xanh lá) - Không cản đường
//        tile[0] = new Tile();
//        tile[0].color = new Color(34, 139, 34); // Xanh lá cây đậm
//        
//        // Mã số 1: Tường cứng (Màu xám) - Cản đường, bom không phá được
//        tile[1] = new Tile();
//        tile[1].color = Color.GRAY;
//        tile[1].collision = true;
//
//        // Mã số 2: Gạch mềm (Màu cam) - Cản đường, bom phá được
//        tile[2] = new Tile();
//        tile[2].color = Color.ORANGE;
//        tile[2].collision = true;
    }

    // Thêm tham số filePath (đường dẫn file) vào hàm
    public void loadMap(String filePath) {
        try {
            // 1. Dùng InputStream để tìm và đọc file trong package
            java.io.InputStream is = getClass().getResourceAsStream(filePath);
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is));

            int col = 0;
            int row = 0;

            // 2. Vòng lặp đọc từng dòng của file text
            while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
                String line = br.readLine(); // Đọc 1 dòng

                // 3. Tách các số trên 1 dòng dựa vào dấu cách (" ")
                String numbers[] = line.split(" "); 

                while (col < gp.maxScreenCol) {
                    int num = Integer.parseInt(numbers[col]); // Ép kiểu từ chữ sang số
                    mapTileNum[row][col] = num; // Gắn vào ma trận
                    col++;
                }
                
                // Xuống dòng tiếp theo
                if (col == gp.maxScreenCol) {
                    col = 0;
                    row++;
                }
            }
            br.close(); // Nhớ đóng luồng đọc file
            
        } catch (Exception e) {
            e.printStackTrace(); // Báo lỗi nếu gõ sai tên file
        }
    }

    public void draw(Graphics2D g2) {
        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
            int tileNum = mapTileNum[row][col]; // Lấy mã số ô (0, 1 hoặc 2)

            // Vẽ khối màu
//            g2.setColor(tile[tileNum].color);
//            g2.fillRect(x, y, gp.tileSize, gp.tileSize);
            g2.drawImage(tile[tileNum].image, x, y, gp.tileSize, gp.tileSize, null);

            // Vẽ viền đen cho dễ nhìn ranh giới các khối
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, gp.tileSize, gp.tileSize);

            col++;
            x += gp.tileSize;

            if (col == gp.maxScreenCol) {
                col = 0;
                x = 0;
                row++;
                y += gp.tileSize;
            }
        }
    }
}