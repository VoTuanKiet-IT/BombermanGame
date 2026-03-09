package entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class Entity {
    
    // Tọa độ x, y trên màn hình
    public int x, y;
    
    // Tốc độ di chuyển
    public int speed;
    
    // Hướng di chuyển (up, down, left, right)
    public String direction;
    
    public Rectangle solidArea; // Vùng va chạm thực tế của nhân vật
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false; // Trạng thái: có đang chạm tường không?

    // Bắt buộc các lớp con phải ghi đè 2 hàm này
    public abstract void update();
    public abstract void draw(Graphics2D g2);
}