/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package philosophersdiner;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 *
 * @author L440
 */
public class PhilosophersDiner {
    public static void main(String[] args){

        new Menu();
    }
}


class Canvas extends JFrame{
    public Philosopher[] philosophers = null;
    public Canvas(int numPhilosophers){
        super();
        setSize(300, 70*numPhilosophers);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        //int rounds=numPhilosophers;

        Chopstick[] chopistics = new Chopstick[numPhilosophers];

        for(int i=0; i< chopistics.length; i++){
                chopistics[i] = new Chopstick(""+i);
        }
        philosophers = new Philosopher[numPhilosophers];
        for(int i=0; i< philosophers.length; i++){
            if(philosophers.length==i+1)
                philosophers[i] = new Philosopher(i+"", chopistics[i], chopistics[0]);//, rounds);
            else
                philosophers[i] = new Philosopher(i+"", chopistics[i], chopistics[i+1]); //, rounds);
            
            Thread t= new Thread( philosophers[i]);
            t.start();
        }
        this.show();
        Graphics g = this.getGraphics();
        
        while(true){
            paint(g);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {}
        }
    }
    public void upadte(Graphics g){
        paint(g);
    }
    
    public void paint(Graphics g){
        if(philosophers!=null)
        for(int i=0; i< philosophers.length; i++){
            if(philosophers[i].bi!=null)
                g.drawImage(philosophers[i].bi, 100, i*philosophers[i].bi.getHeight()+20, rootPane);
            if(philosophers[i].leftChopistick.bi!=null)
                g.drawImage(philosophers[i].leftChopistick.bi, 100-philosophers[i].leftChopistick.bi.getWidth(), i*philosophers[i].bi.getHeight()+20, rootPane);
            if(philosophers[i].rightChopistick.bi!=null)
                g.drawImage(philosophers[i].rightChopistick.bi, 100+philosophers[i].bi.getWidth(), i*philosophers[i].bi.getHeight()+20, rootPane);
        }
    }
}

class Menu extends JFrame implements ActionListener{
    public static JTextField numPhilosophers;
    public Menu(){
        super();
        setSize(300, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        JPanel options = new JPanel();
        options.setLayout(new GridLayout(19, 4));
        numPhilosophers = new JTextField();
        numPhilosophers.setText("Num filosofos");
        options.add(numPhilosophers);
        JButton btn = new JButton();
        btn.setText("EMPEZAR");
        btn.addActionListener(this);
        options.add(btn);
        this.add(options);
        this.show();
        
    }
    public void actionPerformed(ActionEvent e) {
        int i = 5;
        try{
            i=Integer.parseInt(numPhilosophers.getText());
        }catch(Exception ex){}
        new Canvas(i);
    }
}

class Philosopher extends Thread
{
    public Chopstick leftChopistick;
    public Chopstick rightChopistick;

    private String name;
    private int state;
    private Graphics g;
    public BufferedImage bi = null;
    private BufferedImage thinking, eating;
    private int rounds;

    public Philosopher ( String name, Chopstick left, Chopstick right){ //, int rounds){
        this.state = 1;
        this.name = name;
        leftChopistick = left;
        rightChopistick = right;
        //rounds = rounds;
        try {
            InputStream in = new FileInputStream("C:\\Users\\L440\\Documents\\Paralela\\Nueva Carpeta\\PhilosophersDiner\\rsc\\eating.png");
            eating = ImageIO.read(in);
            in = new FileInputStream("C:\\Users\\L440\\Documents\\Paralela\\Nueva Carpeta\\PhilosophersDiner\\rsc\\thinking.png");
            thinking = ImageIO.read(in);
        } catch (IOException e) {}
        bi = thinking;
    }

    public void eat()
    {
        System.out.println("filosofo "+name+" comiendo");
        while(true){
            if(name!="2" && name!="0")
            System.out.println("filosofo "+name+" comiendo");
        if(!leftChopistick.used){
            if(!rightChopistick.used){
                leftChopistick.take();
                rightChopistick.take();
                bi = eating;
                try {
                    Thread.sleep((new Random().nextInt(7)+1)*1000);
                } catch (InterruptedException ex) {}
                rightChopistick.release();
                leftChopistick.release();
                break;
            }
        }
        }
        think();
    }

    public void think(){
        System.out.println("filosofo "+name+" pensando");
        this.state = 1;
        bi = thinking;
        try {
            Thread.sleep((new Random().nextInt(7)+3)*1000);
        } catch (InterruptedException ex) {}
    }

    public void run(){
        while(true){//for(int i=0; i<=rounds; i++){
            eat();
        }
    }
}

class Chopstick{

    public boolean used;
    public String name;
    public BufferedImage bi = null;
    private BufferedImage active, unactive;

    public Chopstick(String name){
        this.name = name;
        try {
            InputStream in = new FileInputStream("C:\\Users\\L440\\Documents\\Paralela\\Nueva Carpeta\\PhilosophersDiner\\rsc\\chopsticks.png");
            unactive = ImageIO.read(in);
            in = new FileInputStream("C:\\Users\\L440\\Documents\\Paralela\\Nueva Carpeta\\PhilosophersDiner\\rsc\\chopsticksUsed.png");
            active = ImageIO.read(in);
        } catch (IOException e) {}
        bi = unactive;
    }

    public synchronized void take() {
        System.out.println("PALILLO "+name+" en uso");
        bi = active;
        this.used = true;
    }
    public synchronized void release() {
        System.out.println("PALILLO "+name+" libre");
        bi = unactive;
        this.used = false ;
    }
}