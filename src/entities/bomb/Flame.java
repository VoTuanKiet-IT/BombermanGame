package entities.bomb;

import entities.Entity;
import main.GamePanel;

import java.awt.Color;
import java.awt.Graphics2D;

public class Flame extends Entity {
    GamePanel gp;
    
    public int timeToLive = 15; // Thời gian hiển thị của tia lửa (15 frames ~ 0.25 giây)
    public boolean isAlive = true;

    public Flame(GamePanel gp, int worldX, int worldY) {
        this.gp = gp;
        this.x = worldX;
        this.y = worldY;
    }

    @Override
    public void update() {
        timeToLive--;
        if (timeToLive <= 0) {
            isAlive = false; // Hết thời gian thì dập tắt lửa
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Color.YELLOW); // Dùng màu vàng để đại diện cho lửa
        
        // Vẽ tia lửa lấp đầy ô vuông
        g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        
        // Vẽ thêm một lõi màu cam bên trong cho đẹp mắt hơn
        g2.setColor(Color.ORANGE);
        g2.fillRect(x + 4, y + 4, gp.tileSize - 8, gp.tileSize - 8);
    }
}
