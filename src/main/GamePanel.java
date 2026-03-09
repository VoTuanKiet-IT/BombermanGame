package main;

import entities.moving.Bomber;
import input.KeyHandler;
import tile.TileManager;
import entities.bomb.Bomb;
import entities.bomb.Flame;
import entities.moving.Enemy;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {
    public int gameState;
    public final int playState = 1;
    public final int gameOverState = 2;
    public final int gameWinState = 3;
    
    // 1. CÀI ĐẶT THÔNG SỐ MÀN HÌNH
    // Kích thước chuẩn của game cổ điển thường là 16x16 pixel cho mỗi ô (tile)
    final int originalTileSize = 16; 
    final int scale = 3; // Phóng to lên 3 lần cho dễ nhìn trên màn hình hiện đại
    
    public final int tileSize = originalTileSize * scale; // Kích thước thực tế của 1 ô: 48x48 pixel

    // Kích thước lưới bản đồ (16 cột, 12 hàng)
    public final int maxScreenCol = 16; 
    public final int maxScreenRow = 12;    
    
    final int screenWidth = tileSize * maxScreenCol; // 768 pixel
    final int screenHeight = tileSize * maxScreenRow; // 576 pixel

    // 2. LUỒNG GAME (THREAD)
    Thread gameThread;
    
    // FPS (Số khung hình trên giây)
    int FPS = 60;
    
    KeyHandler keyH = new KeyHandler();
    public Bomber bomber = new Bomber(this, keyH);
    public TileManager tileM = new TileManager(this);
    public CollisionChecker cChecker = new CollisionChecker(this);
    public ArrayList<Bomb> bombs = new ArrayList<>();
    public ArrayList<Flame> flames = new ArrayList<>();
    public ArrayList<Enemy> enemies = new ArrayList<>();

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        
        // Thêm 2 dòng này
        this.addKeyListener(keyH);
        this.setFocusable(true); // Để GamePanel có thể "nghe" được bàn phím
        
        // Gọi hàm setup để sinh ra kẻ địch khi vừa bật game
        setupGame();
        
        gameState = playState; // Vừa mở game lên là ở trạng thái đang chơi
    }
    
    public void setupGame() {
        // Tọa độ truyền vào là: (Cột * kích thước ô, Hàng * kích thước ô)
        enemies.add(new Enemy(this, 14 * tileSize, 1 * tileSize)); // Góc trên bên phải
        enemies.add(new Enemy(this, 1 * tileSize, 10 * tileSize)); // Góc dưới bên trái
        enemies.add(new Enemy(this, 14 * tileSize, 10 * tileSize)); // Góc dưới bên phải
    }
    public void retry() {
        // 1. Reset lại vị trí của người chơi
        bomber.setDefaultValues();
        bomber.isAlive = true; 

        // 2. Load lại bản đồ từ đầu (Khôi phục lại các viên gạch cam đã bị phá)
        tileM.loadMap("/maps/level1.txt");

        // 3. Xóa sạch bom và tia lửa cũ đang bay trên màn hình
        bombs.clear();
        flames.clear();

        // 4. Xóa quái vật cũ và sinh ra quái vật mới
        enemies.clear();
        setupGame();

        // 5. Đưa trạng thái game về lại Play
        gameState = playState;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        // VÒNG LẶP GAME (GAME LOOP) - Trái tim của mọi tựa game
        double drawInterval = 1000000000 / FPS; // Thời gian cho mỗi khung hình (tính bằng nanosecond)
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            // Bước 1: CẬP NHẬT thông tin (tọa độ nhân vật, bom...)
            update();

            // Bước 2: VẼ LẠI màn hình với thông tin mới
            repaint();

            // Tính toán thời gian nghỉ để ép game chạy đúng 60 FPS
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000; // Đổi về millisecond cho Thread.sleep

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        // Chỉ cho phép nhân vật, bom di chuyển khi game đang ở trạng thái PLAY
        if (gameState == playState) {
            bomber.update();
            
            for (int i = 0; i < bombs.size(); i++) {
                Bomb b = bombs.get(i);
                b.update();

                // Thay thế đoạn check b.exploded cũ bằng đoạn này:
                if (b.exploded) {
                    bombs.remove(i);
                    spawnFlames(b.x, b.y); // Gọi hàm sinh tia lửa tại vị trí bom nổ
                    i--; // Lùi index lại 1 bước vì ArrayList vừa bị rút ngắn
                }
            }
            // Cập nhật di chuyển cho Kẻ địch
            for (int i = 0; i < enemies.size(); i++) {
                Enemy e = enemies.get(i);
                e.update();
            }

            // Thêm đoạn này vào dưới vòng lặp cập nhật Bom
            for (int i = 0; i < flames.size(); i++) {
                Flame f = flames.get(i);
                f.update();
                if (!f.isAlive) {
                    flames.remove(i);
                    i--;
                }
            }

            // GỌI HÀM KIỂM TRA VA CHẠM Ở CUỐI CÙNG
            checkEntityCollisions();
        }
        // THÊM ĐOẠN ELSE IF NÀY VÀO:
        else if (gameState == gameOverState || gameState == gameWinState) {
            // Nếu đang ở màn hình kết thúc mà bấm Enter thì chơi lại
            if (keyH.enterPressed) {
                retry();
            }
        }
        // Nếu là trạng thái gameOverState hoặc gameWinState -> Không update gì cả (đứng hình)
        
    }
    
    public void checkEntityCollisions() {
        // Tạo hộp giới hạn (Hitbox) thực tế của Người chơi trên bản đồ
        java.awt.Rectangle playerRect = new java.awt.Rectangle(
            bomber.x + bomber.solidArea.x, 
            bomber.y + bomber.solidArea.y, 
            bomber.solidArea.width, 
            bomber.solidArea.height
        );

        // 1. KIỂM TRA: QUÁI VẬT CHẠM NGƯỜI CHƠI
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            java.awt.Rectangle enemyRect = new java.awt.Rectangle(
                e.x + e.solidArea.x, e.y + e.solidArea.y, e.solidArea.width, e.solidArea.height
            );
            
            if (playerRect.intersects(enemyRect)) {
                gameState = gameOverState; // Chạm quái -> Thua
            }
        }

        // 2. KIỂM TRA: TIA LỬA THIÊU ĐỐT
        for (int i = 0; i < flames.size(); i++) {
            Flame f = flames.get(i);
            java.awt.Rectangle flameRect = new java.awt.Rectangle(f.x, f.y, tileSize, tileSize);

            // Lửa thiêu Người chơi
            if (flameRect.intersects(playerRect)) {
                gameState = gameOverState; // Bị bom nổ trúng -> Thua
            }

            // Lửa thiêu Quái vật
            for (int j = 0; j < enemies.size(); j++) {
                Enemy e = enemies.get(j);
                java.awt.Rectangle enemyRect = new java.awt.Rectangle(
                    e.x + e.solidArea.x, e.y + e.solidArea.y, e.solidArea.width, e.solidArea.height
                );
                
                if (flameRect.intersects(enemyRect)) {
                    enemies.remove(j); // Tiêu diệt quái vật
                    j--; // Lùi index vì danh sách vừa bị xóa đi 1 phần tử
                }
            }
        }
        
        // 3. KIỂM TRA ĐIỀU KIỆN THẮNG
        if (enemies.isEmpty()) {
            gameState = gameWinState; // Giết hết quái vật -> Thắng
        }
    }
    
    public void spawnFlames(int bombX, int bombY) {
        // Quy đổi tọa độ bom ra số thứ tự Cột và Hàng
        int col = bombX / tileSize;
        int row = bombY / tileSize;

        // 1. Sinh lửa ở Tâm (ngay tại quả bom)
        flames.add(new Flame(this, bombX, bombY));

        // 2. Mảng tọa độ 4 hướng: Phải, Trái, Xuống, Lên (Độ dài tia lửa tạm để là 1 ô)
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int i = 0; i < directions.length; i++) {
            int checkCol = col + directions[i][0];
            int checkRow = row + directions[i][1];

            // Đảm bảo không xét vượt quá viền bản đồ gây lỗi ArrayOutOfBounds
            if (checkCol >= 0 && checkCol < maxScreenCol && checkRow >= 0 && checkRow < maxScreenRow) {

                int tileNum = tileM.mapTileNum[checkRow][checkCol];

                if (tileNum == 0) { 
                    // Nếu là Cỏ (0): Tia lửa lan ra bình thường
                    flames.add(new Flame(this, checkCol * tileSize, checkRow * tileSize));
                } 
                else if (tileNum == 2) { 
                    // Nếu là Gạch cam (2): Phá hủy gạch (biến thành cỏ) và sinh tia lửa tại đó
                    tileM.mapTileNum[checkRow][checkCol] = 0; 
                    flames.add(new Flame(this, checkCol * tileSize, checkRow * tileSize));
                }
                // Nếu là Tường xám (1): Không làm gì cả, tia lửa bị chặn
            }
        }
    }

    // Ghi đè phương thức có sẵn của JPanel để vẽ đồ họa
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Chuyển Graphics thành Graphics2D để vẽ mượt hơn và có nhiều tính năng hơn
        Graphics2D g2 = (Graphics2D) g;

        // Vẽ bản đồ trước
        tileM.draw(g2);
        
        for (int i = 0; i < bombs.size(); i++) bombs.get(i).draw(g2);
        for (int i = 0; i < flames.size(); i++) flames.get(i).draw(g2);
        for (int i = 0; i < enemies.size(); i++) enemies.get(i).draw(g2);
        bomber.draw(g2);
        
        // HIỂN THỊ CHỮ KHI THẮNG / THUA
        if (gameState == gameOverState) {
            g2.setColor(new java.awt.Color(0, 0, 0, 150));
            g2.fillRect(0, 0, screenWidth, screenHeight);
            
            g2.setColor(java.awt.Color.RED);
            g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 50));
            g2.drawString("GAME OVER!", tileSize * 4, tileSize * 6);
            
            // THÊM 2 DÒNG NÀY (Hướng dẫn chơi lại)
            g2.setColor(java.awt.Color.WHITE);
            g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20));
            g2.drawString("Nhấn ENTER để chơi lại", tileSize * 5, tileSize * 7 + 20);
        } 
        else if (gameState == gameWinState) {
            g2.setColor(new java.awt.Color(0, 0, 0, 150));
            g2.fillRect(0, 0, screenWidth, screenHeight);
            
            g2.setColor(java.awt.Color.GREEN);
            g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 50));
            g2.drawString("YOU WIN!", tileSize * 5, tileSize * 6);
            
            // THÊM 2 DÒNG NÀY (Hướng dẫn chơi lại)
            g2.setColor(java.awt.Color.WHITE);
            g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20));
            g2.drawString("Nhấn ENTER để chơi lại", tileSize * 5, tileSize * 7 + 20);
        }

        g2.dispose();
    }
}