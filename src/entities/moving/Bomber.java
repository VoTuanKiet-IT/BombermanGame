package entities.moving;

import entities.Entity;
import input.KeyHandler;
import main.GamePanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Bomber extends Entity {
    
    GamePanel gp;
    KeyHandler keyH;
    public int bombCooldown = 0;
    public boolean isAlive = true;

    public Bomber(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        
        
        
        // KHỞI TẠO HỘP GIỚI HẠN (Hitbox)
        // Mình cho hitbox nhỏ hơn nhân vật một chút để chui qua các góc tường dễ hơn
        solidArea = new Rectangle();
        solidArea.x = 8; // Lùi vào 8 pixel
        solidArea.y = 8; // Lùi xuống 8 pixel 
        solidArea.width = 32; 
        solidArea.height = 32;
        
        // Lưu lại giá trị mặc định để sau này dùng
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        
        setDefaultValues();
    }

    // Đặt vị trí và tốc độ khởi điểm cho Bomber
    public void setDefaultValues() {
        x = gp.tileSize; // Khởi đầu ở Cột 1
        y = gp.tileSize; // Khởi đầu ở Hàng 1
        speed = 4;
        direction = "down";
    }

    @Override
    public void update() {
        if (bombCooldown > 0) {
                    bombCooldown--;
        }
        // Kiểm tra người chơi bấm phím Space
        if (keyH.spacePressed && bombCooldown == 0) {
            // Tính toán tâm của nhân vật để đặt bom
            int centerWorldX = x + solidArea.x + (solidArea.width / 2);
            int centerWorldY = y + solidArea.y + (solidArea.height / 2);

            gp.bombs.add(new entities.bomb.Bomb(gp, centerWorldX, centerWorldY));
            
            bombCooldown = 30; // Phải đợi nửa giây (30 khung hình) mới được đặt quả tiếp theo
        }
        // Chỉ xử lý khi người chơi có bấm phím
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            
            // Bước 1: Xác định hướng đang muốn đi
            if (keyH.upPressed) direction = "up";
            else if (keyH.downPressed) direction = "down";
            else if (keyH.leftPressed) direction = "left";
            else if (keyH.rightPressed) direction = "right";

            // Bước 2: Bật kiểm tra va chạm
            collisionOn = false;
            gp.cChecker.checkTile(this);

            // Bước 3: Nếu KHÔNG bị va chạm thì mới cho phép di chuyển
            if (!collisionOn) {
                switch (direction) {
                    case "up": y -= speed; break;
                    case "down": y += speed; break;
                    case "left": x -= speed; break;
                    case "right": x += speed; break;
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Color.BLUE);
        
        // Khai báo khoảng lùi (offset) để thu nhỏ nhân vật
        // Ví dụ: lùi vào 8 pixel từ mỗi cạnh
        int offset = 8; 
        
        // Tính toán kích thước mới của khối vuông
        int width = gp.tileSize - (offset * 2);
        int height = gp.tileSize - (offset * 2);
        
        // Vẽ khối vuông với tọa độ x, y được cộng thêm khoảng lùi để nó nằm giữa ô
        g2.fillRect(x + offset, y + offset, width, height);
    }
}