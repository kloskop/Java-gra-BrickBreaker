package brickBreaker;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class Gameplay extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;
    private int totalBricks = 21;

    private Timer timer;
    private int delay = 8;

    private int playerX = 310; //pozycja startowa gracza

    private int ballposX = 120; //pozycja startowa piłki
    private int ballposY = 350; //pozycja startowa piłki
    private int ballXdir = -1;
    private int ballYdir = -2;

    private MapGenerator map;

    public Gameplay() { //Konstruktor
        map = new MapGenerator(3,7);
        addKeyListener(this);
        setFocusable(true); //pozwala na ustalenie focusa
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();

    }

    public void paint(Graphics g) {
        // tło
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        // rysowanie mapy
        map.draw((Graphics2D)g);

        // granice
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        // deska
        g.setColor(Color.blue);
        g.fillRect(playerX, 550, 100, 8);

        // piłka
        g.setColor(Color.white);
        g.fillOval(ballposX, ballposY, 20, 20);


        if(ballposY>570) {

            play = false;
            ballXdir = 0;
            ballYdir =0;
            g.setColor(Color.red);
            g.setFont(new Font("system",Font.BOLD,25));
            g.drawString("KONIEC GRY, Wynik: "+score,190,300);

            g.setFont(new Font("system",Font.BOLD,20));
            g.drawString("Naciśnij ENTER żeby zacząć od nowa",170,350);



        }

        if(totalBricks <= 0){
            play = false;
            ballXdir = 0;
            ballYdir =0;
            g.setColor(Color.red);
            g.setFont(new Font("system",Font.BOLD,25));
            g.drawString("Wygrałeś!: "+score,190,300);

            g.setFont(new Font("system",Font.BOLD,20));
            g.drawString("Naciśnij ENTER żeby zacząć od nowa",170,350);


        }


        //wyniki
        g.setColor(Color.white);
        g.setFont(new Font("system",Font.BOLD,25));
        g.drawString(""+score,590,30);

        g.dispose();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        timer.start();

        if(play){
            if(new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))){
                ballYdir = -ballYdir;

                try {
                    String soundName = "HitPad.wav";
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();
                }
                catch (Exception exc) {
                    System.err.println(exc.getMessage());
                }
            }

            A: for(int i=0;i<map.map.length;i++){
                for(int j=0;j<map.map[0].length;j++){
                    if(map.map[i][j]>0){
                        int brickX = j*map.brickWidth+80;
                        int brickY=i*map.brickHeight+50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX,brickY,brickWidth,brickHeight);
                        Rectangle ballRect = new Rectangle(ballposX,ballposY,20,20);
                        Rectangle brickRect = rect;

                        if(ballRect.intersects(brickRect)){
                            map.setBrickValue(0,i,j);
                            totalBricks--;
                            score+=5;

                            if(ballposX+19<=brickRect.x||ballposX+1>=brickRect.x+brickRect.width){
                                ballXdir= -ballXdir;
                            }
                            else{
                                ballYdir = -ballYdir;
                            }
                            try {
                                String soundName = "HitBrick.wav";
                                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
                                Clip clip = AudioSystem.getClip();
                                clip.open(audioInputStream);
                                clip.start();
                            }
                            catch (Exception exc) {
                                System.err.println(exc.getMessage());
                            }

                        break A;
                        }
                    }
                }
            }

            ballposX += ballXdir;
            ballposY += ballYdir;
            if(ballposX<0){
                ballXdir = -ballXdir;
            }
            if(ballposY<0){
                ballYdir = -ballYdir;
            }
            if(ballposX>670){
                ballXdir = -ballXdir;
            }
        }
        repaint(); // przerysowywanie elementów
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) { }
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600) {
                playerX = 600;
            } else {
                moveRight();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) {
                playerX = 10;
            } else {
                moveLeft();
            }
        }
        if(e.getKeyCode()==KeyEvent.VK_ENTER){
            if(!play){
               play = true;
               ballposX =120;
               ballposY =350;
               ballXdir = -1;
               ballYdir = -2;
               playerX = 310;
               score = 0;
               totalBricks = 21;
               map = new MapGenerator(3,7);

               repaint();

                try {
                    String soundName = "NewGame.wav";
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();
                }
                catch (Exception exc) {
                    System.err.println(exc.getMessage());
                }
            }
        }
    }
    public void moveRight(){
        play = true;
        playerX+=20;
        }
    public void moveLeft(){
        play = true;
        playerX-=20;
    }




}
