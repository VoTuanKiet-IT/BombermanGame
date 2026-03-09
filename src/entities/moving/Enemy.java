package entities.moving;

import entities.Entity;
import main.GamePanel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

public class Enemy extends Entity {
    GamePanel gp;
    public boolean isAlive = true;

    public Enemy(GamePanel gp, int startWorldX, int startWorldY) {
        this.gp = gp;
        this.x = startWorldX;
        this.y = startWorldY;
        
        this.speed = 2; // Kẻ địch đi chậm hơn người chơi một chút (người chơi tốc độ 4)
        this.direction = "down";

        // Khởi tạo Hitbox y hệt người chơi
        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 8;
        solidArea.width = 32;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    public void update() {
        if (!isAlive) return;

        // Bật kiểm tra va chạm
        collisionOn = false;
        gp.cChecker.checkTile(this);

        // THUẬT TOÁN AI CƠ BẢN:
        if (collisionOn) {
            // Nếu đụng tường/gạch -> Chọn ngẫu nhiên hướng đi mới
            setRandomDirection();
        } else {
            // Nếu không đụng tường -> Tiếp tục đi theo hướng hiện tại
            switch (direction) {
                case "up": y -= speed; break;
                case "down": y += speed; break;
                case "left": x -= speed; break;
                case "right": x += speed; break;
            }
        }
    }

    public void setRandomDirection() {
        Random random = new Random();
        int i = random.nextInt(4); // Trả về ngẫu nhiên số 0, 1, 2 hoặc 3

        if (i == 0) direction = "up";
        if (i == 1) direction = "down";
        if (i == 2) direction = "left";
        if (i == 3) direction = "right";
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!isAlive) return;

        // Dùng khối màu Tím (Magenta) để phân biệt kẻ địch với người chơi
        g2.setColor(Color.MAGENTA);
        int offset = 8;
        int width = gp.tileSize - (offset * 2);
        int height = gp.tileSize - (offset * 2);
        g2.fillRect(x + offset, y + offset, width, height);
    }
}