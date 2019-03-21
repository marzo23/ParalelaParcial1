/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package readerswriters;

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
public class ReadersWriters {
   public static void main(String[] args){
        new Menu();
    }
}


class Canvas extends JFrame{
    public Reader[] readers = null;
    public Writer[] writers = null;
    public Canvas(int readers, int writers){
        super();
        setSize(300, 70*(readers>writers?readers:writers));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        Database database = new Database();
        this.writers = new Writer[writers];
        this.readers = new Reader[readers];
        for (int i = 0; i < readers; i++)
        {
          this.readers[i] = new Reader(database);
          this.readers[i].start();
        }
        for (int i = 0; i < writers; i++)
        {
          this.writers[i] = new Writer(database);
          this.writers[i].start();
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
        if(writers!=null)
        for(int i=0; i< writers.length; i++){
            if(writers[i].bi!=null)
                g.drawImage(writers[i].bi, 20, i*writers[i].bi.getHeight()+20, rootPane);
        }
        if(readers!=null)
        for(int i=0; i< readers.length; i++){
            if(readers[i].bi!=null)
                g.drawImage(readers[i].bi, 170, i*readers[i].bi.getHeight()+20, rootPane);
        }
    }
}

class Menu extends JFrame implements ActionListener{
    public static JTextField writers;
    public static JTextField readers;
    public Menu(){
        super();
        setSize(300, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        JPanel options = new JPanel();
        options.setLayout(new GridLayout(19, 4));
        readers = new JTextField();
        readers.setText("Num readers");
        options.add(readers);
        writers = new JTextField();
        writers.setText("Num writers");
        options.add(writers);
        JButton btn = new JButton();
        btn.setText("EMPEZAR");
        btn.addActionListener(this);
        options.add(btn);
        this.add(options);
        this.show();
        
    }
    public void actionPerformed(ActionEvent e) {
        int i = 5, j = 5;
        try{
            i=Integer.parseInt(writers.getText());
            j=Integer.parseInt(readers.getText());
        }catch(Exception ex){}
        new Canvas(j,i);
    }
}

class Database
{
  private int readers;
  public Database()
  {
    this.readers = 0;
  }
 
  public void read(Reader r)
  {
    synchronized(this)
    {
      this.readers++;
      r.bi = r.active;
      System.out.println("Reader " + r.number + " leyendo.");
      
    }
 
    try
    {
      Thread.sleep((new Random().nextInt(7)+1)*1000);
    }
    catch (InterruptedException e) {}
 
    synchronized(this)
    {
      System.out.println("Reader " + r.number + " deja de leer.");
      r.bi = r.unactive;
      this.readers--;
      if (this.readers == 0)
      {
        this.notifyAll();
      }
    }
  }
 
  public synchronized void write(Writer w)
  {
    while (this.readers != 0)
    {
      try
      {
        this.wait();
      }
      catch (InterruptedException e) {}
    }
    w.bi = w.active;
    System.out.println("Writer " + w.number + " escribiendo.");
 
    try
    {
      Thread.sleep((new Random().nextInt(7)+1)*1000);
    }
    catch (InterruptedException e) {}
    
    System.out.println("Writer " + w.number + " deja de escribir.");
    w.bi = w.unactive;
    this.notifyAll();
 }
}

class Reader extends Thread
{
    private static int readers = 0;
    public BufferedImage bi = null;
    public BufferedImage active, unactive;
    public int number;
    private Database database;

    public Reader(Database database)
    {
        this.database = database;
        this.number = Reader.readers++;
        try {
            InputStream in = new FileInputStream("C:\\Users\\L440\\Documents\\Paralela\\Nueva Carpeta\\PhilosophersDiner\\rsc\\chopsticks.png");
            unactive = ImageIO.read(in);
            in = new FileInputStream("C:\\Users\\L440\\Documents\\Paralela\\Nueva Carpeta\\PhilosophersDiner\\rsc\\chopsticksUsed.png");
            active = ImageIO.read(in);
        } catch (IOException e) {}
        bi = unactive;
    }
    public void run()
    {
        while (true)
        {
            try
            {
                Thread.sleep((new Random().nextInt(7)+1)*1000);
            }
            catch (InterruptedException e) {}
            this.database.read(this);
        }
    }
}

class Writer extends Thread
{
    private static int writers = 0;
    public BufferedImage bi = null;
    public BufferedImage active, unactive;
    public int number;
    private Database database;
    public Writer(Database database)
    {
        this.database = database;
        this.number = Writer.writers++;
        try {
            InputStream in = new FileInputStream("C:\\Users\\L440\\Documents\\Paralela\\Nueva Carpeta\\PhilosophersDiner\\rsc\\chopsticks.png");
            unactive = ImageIO.read(in);
            in = new FileInputStream("C:\\Users\\L440\\Documents\\Paralela\\Nueva Carpeta\\PhilosophersDiner\\rsc\\chopsticksUsed.png");
            active = ImageIO.read(in);
        } catch (IOException e) {}
        bi = unactive;
    }
    public void run()
    {
        while (true)
        {
            try
            {
              Thread.sleep((new Random().nextInt(7)+1)*1000);
            }
            catch (InterruptedException e) {}
            this.database.write(this);
        }
    }
}