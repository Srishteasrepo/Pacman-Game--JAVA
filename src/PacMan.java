import java.awt.*;
 import java.awt.event.*;
 import java.util.HashSet;
 import java.util.Random;
 import javax.swing.*;
 import java.util.ArrayList; 

public class PacMan extends JPanel implements ActionListener,KeyListener{
    class Block{
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction='U';
        int velocityX=0;
        int velocityY=0;

        Block(Image image, int x, int y, int width, int height){
            this.image=image;
            this.x=x;
            this.y=y;
            this.width=width;
            this.height=height;
            this.startX=x;
            this.startY=y;
        }

        // Renamed to be specific to Pac-Man
        void setPacmanDirection(char direction) {
            this.direction = direction;
            updateVelocity();

            // change the image according to direction
            if (direction == 'U') this.image = pacmanUpImage;
            else if (direction == 'D') this.image = pacmanDownImage;
            else if (direction == 'L') this.image = pacmanLeftImage;
            else if (direction == 'R') this.image = pacmanRightImage;
        }

        // New method for ghosts that doesn't change the image
        void setGhostDirection(char direction) {
            this.direction = direction;
            updateVelocity();
        }

        void updateVelocity(){
            if(this.direction=='U'){
                this.velocityX=0;
                this.velocityY= -tileSize/4;
            }
            else if(this.direction=='D'){
                this.velocityX= 0;
                this.velocityY=tileSize/4;
            }
            else if(this.direction=='L'){
                this.velocityX= -tileSize/4;
                this.velocityY=0;
            }
             else if(this.direction=='R'){
                this.velocityX= tileSize/4;
                this.velocityY=0;
            }
        }
        void reset(){
            this.x=this.startX;
            this.y=this.startY;
        }
    }
    private int rowCount=21;
    private int columnCount=19;
    private int tileSize=32;
    private int boardWidth=columnCount*tileSize;
    private int boardHeight=rowCount*tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;
    private Image scaredGhostImage;
    private Image orangeGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X X   X X XXXX", // MODIFIED: This is the "door"
        "O      rbpo       O", // MODIFIED: All ghosts lined up
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };
    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;
    Timer gameloop;
    char[] directions={'U','D','L','R'};
    Random random=new Random();
    int Score=0;
    private int startingLives = 3;
    int lives=startingLives;
    boolean gameOver=false;
    

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();


        //load images
        wallImage = new ImageIcon(getClass().getResource("/images/wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("/images/blueGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("/images/pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("/images/redGhost.png")).getImage();
        scaredGhostImage = new ImageIcon(getClass().getResource("/images/scaredGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("/images/orangeGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("/images/pacmanUp.png")).getImage();  
        pacmanDownImage = new ImageIcon(getClass().getResource("/images/pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("/images/pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("/images/pacmanRight.png")).getImage();

        loadMap();
        // Give ghosts an initial random direction at game start
        for(Block ghost:ghosts){
            char newDirection=directions[random.nextInt(4)];
            ghost.setGhostDirection(newDirection);
        }
        gameloop=new Timer(50,this);
        gameloop.start();
        
    } 
    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();
        for(int r=0;r<rowCount;r++){
            for(int c=0;c<columnCount;c++){
                String row=tileMap[r];
                char tileMapChar=row.charAt(c);
                int x=c*tileSize;
                int y=r*tileSize;
                if(tileMapChar=='X'){
                    Block wall=new Block(wallImage,x,y,tileSize,tileSize);
                    walls.add(wall);
                }
                else if(tileMapChar=='b'){//blue ghost
                    Block ghost=new Block(blueGhostImage,x,y,tileSize,tileSize);
                    ghosts.add(ghost);
                }
                else if(tileMapChar=='o'){//orange ghost
                    Block ghost=new Block(orangeGhostImage,x,y,tileSize,tileSize);
                    ghosts.add(ghost);
                }
                else if(tileMapChar=='p'){//pink ghost
                    Block ghost=new Block(pinkGhostImage,x,y,tileSize,tileSize);
                    ghosts.add(ghost);
                }
                else if(tileMapChar=='r'){//red ghost
                    Block ghost=new Block(redGhostImage,x,y,tileSize,tileSize);
                    ghosts.add(ghost);
                }
                else if(tileMapChar=='P'){//pacman
                    pacman=new Block(pacmanRightImage,x,y,tileSize,tileSize);
                }
                else if(tileMapChar==' '){//food
                    Block food=new Block(null,x+14,y+14,4,4);
                    foods.add(food);
                }
            }
        }
        
    }
    public char randomDirection() {
        char[] dirs = {'U', 'D', 'L', 'R'};
        Random random = new Random();
        return dirs[random.nextInt(4)];
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        g.drawImage(pacman.image,pacman.x, pacman.y,pacman.width, pacman.height,null);
        for(Block ghost:ghosts){
            g.drawImage(ghost.image,ghost.x,ghost.y,ghost.width,ghost.height,null);
        }
        for(Block wall:walls){
            g.drawImage(wall.image,wall.x,wall.y,wall.width,wall.height,null);
        }
        g.setColor(Color.WHITE);
        for(Block food:foods){
            g.fillRect(food.x,food.y,food.width,food.height); 
        }

        // Updated score/lives display
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + Score, tileSize / 2, tileSize - 10);
        g.drawString("Lives: " + lives, boardWidth - (tileSize * 3), tileSize - 10);
    }
    
    public void move(){
        pacman.x +=pacman.velocityX;
        pacman.y += pacman.velocityY;
        // check wall collisions
        for(Block wall:walls){
            if(collision(pacman,wall)){
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }
        // ghost collisions
        for(Block ghost:ghosts){
            if(collision(ghost,pacman)){
                lives -=1;
                if(lives==0){
                    gameOver=true;
                } else {
                    resetPositions();
                }
                // Stop the move function immediately after a death/reset
                return; 
            }

            ghost.x +=ghost.velocityX;
            ghost.y +=ghost.velocityY;
            
            for(Block wall:walls){
                if(collision(ghost, wall)|| ghost.x <=0||ghost.x+ghost.width>=boardWidth){
                    ghost.x -=ghost.velocityX; // reverse
                    ghost.y -=ghost.velocityY; // reverse

                    // This is your simple random logic, which is perfect
                    // for when they are out on the map.
                    char newDirection = directions[random.nextInt(4)];
                    ghost.setGhostDirection(newDirection);
                    
                    break; 
                }
            }
        }
        Block foodEaten=null;
        for(Block food:foods){
            if(collision(pacman, food)){
                foodEaten=food;
                Score +=10;
            }
        }
        foods.remove(foodEaten);
    }

    public boolean collision(Block a,Block b){
        return  a.x < b.x + b.width &&
           a.x + a.width > b.x &&
           a.y < b.y + b.height &&
           a.y + a.height > b.y;
    }
    public void resetPositions(){
        pacman.reset();
        pacman.velocityX=0;
        pacman.velocityY=0;
        for(Block ghost:ghosts){
            ghost.reset();
            ghost.setGhostDirection('U'); 
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       if (gameOver) return; // Don't move if game is over

       move();
       repaint();

       if(gameOver){
            gameloop.stop();
            // Show Game Over popup
            int livesUsed = startingLives - lives;
            String message = "<html>"
                           + "<body style='padding: 10px;'>"
                           + "<h1 style='color:red; text-align:center;'>Game Over!</h1>"
                           + "<hr>"
                           + "<p style='font-size:14px;'>Your Final Score: <b>" + Score + "</b></p>"
                           + "<p style='font-size:14px;'>Lives Used: <b>" + livesUsed + "</b></p>"
                           + "<br>"
                           + "<b>Would you like to play again?</b>"
                           + "</body>"
                           + "</html>";

            String[] options = {"Play Again", "Quit"};
            int choice = JOptionPane.showOptionDialog(this,
                message,
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, // Use a question icon
                null, 
                options,
                options[0]); // Default button is "Play Again"

            if (choice == 0) { 
               
                loadMap();
                resetPositions(); 
                // Give ghosts a new random direction for the new game
                for(Block ghost:ghosts){
                    char newDirection=directions[random.nextInt(4)];
                    ghost.setGhostDirection(newDirection);
                }
                lives = startingLives; 
                Score=0;
                gameOver=false;
                gameloop.start();
            } else { // User chose "Quit" or closed the dialog
                System.exit(0);
            }
       }
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {
        if(gameOver){
            return;
        }
        
        // Use setPacmanDirection
        if(e.getKeyCode()==KeyEvent.VK_UP){
            pacman.setPacmanDirection('U');
        }
        else if(e.getKeyCode()==KeyEvent.VK_DOWN){
            pacman.setPacmanDirection('D');
        }
        else if(e.getKeyCode()==KeyEvent.VK_LEFT){
            pacman.setPacmanDirection('L');
        }
        else if(e.getKeyCode()==KeyEvent.VK_RIGHT){
            pacman.setPacmanDirection('R');
        }

    }
}