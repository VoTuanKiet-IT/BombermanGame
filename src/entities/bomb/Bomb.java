package entities.bomb;

import entities.Entity;
import main.GamePanel;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bomb extends Entity {
    GamePanel gp;
    
    // Đếm ngược thời gian nổ (ví dụ: 120 frames tương đương 2 giây ở 60FPS)
    public int timeToExplode = 120; 
    public boolean exploded = false;

    public Bomb(GamePanel gp, int worldX, int worldY) {
        this.gp = gp;
        
        // THUẬT TOÁN SNAP TO GRID: Ép bom nằm vừa khít vào ô lưới
        // Lấy tọa độ pixel chia cho kích thước ô để ra số thứ tự ô, rồi nhân ngược lại
        this.x = (worldX / gp.tileSize) * gp.tileSize;
        this.y = (worldY / gp.tileSize) * gp.tileSize;
    }

    @Override
    public void update() {
        // Đếm ngược mỗi khung hình
        timeToExplode--;
        
        if (timeToExplode <= 0) {
            exploded = true; // Hết giờ thì chuyển trạng thái thành nổ
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Color.RED); // Tạm thời dùng hình tròn màu đỏ đại diện cho bom
        
        // Vẽ hình tròn nhỏ hơn ô vuông một chút (lùi vào 8 pixel)
        g2.fillOval(x + 8, y + 8, gp.tileSize - 16, gp.tileSize - 16); 
    }
}