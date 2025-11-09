import javax.swing.JFrame;
import java.awt.*;
public class App {
    public static void main(String[] args) throws Exception {
        int rowCount=21;
        int columnCount=19;
        int tileSize=32;
        int boardWidth=columnCount*tileSize;
        int boardHeight=rowCount*tileSize;

        JFrame frame =new JFrame("Pac Man");
        // frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Color babyPink = new Color(255, 209, 220);
        frame.getContentPane().setBackground(babyPink);

        PacMan pacmangame=new PacMan();
        frame.add(pacmangame);
        frame.pack();
        pacmangame.requestFocus();
        frame.setVisible(true);
    }
}
