package main;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        // Tạo cửa sổ window
        JFrame window = new JFrame("Bomberman - Đồ án Lập trình Java");
        
        // Cho phép tắt chương trình khi bấm dấu X
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Không cho người dùng kéo giãn cửa sổ làm hỏng khung hình game
        window.setResizable(false); 

        // Khởi tạo GamePanel và thêm vào cửa sổ
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        
        // Ép cửa sổ tự động nén lại cho vừa khít với kích thước của GamePanel
        window.pack(); 

        // Hiển thị cửa sổ ở chính giữa màn hình
        window.setLocationRelativeTo(null); 
        window.setVisible(true);

        // Bắt đầu chạy vòng lặp game
        gamePanel.startGameThread(); 
    }
}