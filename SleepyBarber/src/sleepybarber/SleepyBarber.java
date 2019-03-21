/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sleepybarber;

 
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
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
public class SleepyBarber {
    public static void main(String[] args){
        new Menu();
    }
}

class Canvas extends JFrame{
    public Baberia b;
    public Barber barber;
    public BufferedImage chair, customer;
    
    public Canvas(int chairs){
        super();
        setSize(300, 70*chairs);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        try {
            InputStream in = new FileInputStream("C:\\Users\\L440\\Documents\\Paralela\\Nueva Carpeta\\PhilosophersDiner\\rsc\\chopsticks.png");
            chair = ImageIO.read(in);
            in = new FileInputStream("C:\\Users\\L440\\Documents\\Paralela\\Nueva Carpeta\\PhilosophersDiner\\rsc\\chopsticksUsed.png");
            customer = ImageIO.read(in);
        } catch (IOException e) {}
        
        b = new Baberia(chairs);
 
        barber = new Barber(b);
        CustomerGenerator cg = new CustomerGenerator(b);
 
        Thread thbarber = new Thread(barber);
        Thread thcg = new Thread(cg);
        thcg.start();
        thbarber.start();
        
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
        if(b!=null){
            for(int i=0; i< b.listCustomer.size(); i++){
                g.drawImage(customer, 20, i*customer.getHeight()+20, rootPane);
            }
            for (int i = b.listCustomer.size()-1; i < b.chairs; i++) {
                g.drawImage(chair, 20, i*chair.getHeight()+20, rootPane);
            }
        }
        if(barber!=null)
            g.drawImage(barber.bi, 170, 20, rootPane);
    }
}

class Menu extends JFrame implements ActionListener{
    public static JTextField chairs;
    public Menu(){
        super();
        setSize(300, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        JPanel options = new JPanel();
        options.setLayout(new GridLayout(19, 4));
        chairs = new JTextField();
        chairs.setText("Num sillas");
        options.add(chairs);
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
            i=Integer.parseInt(chairs.getText());
        }catch(Exception ex){}
        new Canvas(i);
    }
}

 
class Barber implements Runnable{
    Baberia b;
    public BufferedImage bi = null;
    public BufferedImage active, unactive;
 
    public Barber(Baberia b){
        this.b = b;
        try {
            InputStream in = new FileInputStream("C:\\Users\\L440\\Documents\\Paralela\\Nueva Carpeta\\PhilosophersDiner\\rsc\\chopsticks.png");
            unactive = ImageIO.read(in);
            in = new FileInputStream("C:\\Users\\L440\\Documents\\Paralela\\Nueva Carpeta\\PhilosophersDiner\\rsc\\chopsticksUsed.png");
            active = ImageIO.read(in);
        } catch (IOException e) {}
        bi = unactive;
    }
    public void run(){
        try{
            Thread.sleep(10000);
        }
        catch(InterruptedException ex){}
        while(true){
            b.cutHair(this);
        }
    }
}
class Customer implements Runnable{
    public String name;
    Baberia b;
    public Customer(Baberia b){
        this.b = b;
    }
    public void run(){
        synchronized(this){
            b.add(this);
        }
    }
}
 
class CustomerGenerator implements Runnable{
    Baberia b;
    public CustomerGenerator(Baberia b){
        this.b = b;
    }
 
    public void run(){
        while(true){
            Customer customer = new Customer(b);
            Thread thcustomer = new Thread(customer);
            customer.name = thcustomer.getId()+"";
            thcustomer.start();
            try{
                Thread.sleep((new Random().nextInt(7)+3)*1000);
            }
            catch(InterruptedException ex){}
        }
    }
 
}
 
class Baberia{
    public int chairs;
    public List<Customer> listCustomer;
 
    public Baberia(int chairs){
        this.chairs = chairs;
        listCustomer = new LinkedList<Customer>();
    }
 
    public void cutHair(Barber b){
        Customer customer;
        b.bi = b.unactive;
        synchronized (listCustomer){
            while(listCustomer.size()==0){
                System.out.println("Barber is waiting for customer.");
                try{
                    listCustomer.wait();
                }catch(InterruptedException ex){}
            }
            b.bi = b.active;
            customer = (Customer)((LinkedList<?>)listCustomer).poll();
        }
        try{    
            System.out.println("Cuting hair of Customer : "+customer.name);
            Thread.sleep((new Random().nextInt(7)+3)*1000);
        }catch(InterruptedException ex){}
        System.out.println("Completed Cuting hair of Customer : "+customer.name);
    }
 
    public void add(Customer customer){
        System.out.println("Customer : "+customer.name);
 
        synchronized (listCustomer){
            if(listCustomer.size() == chairs){
                System.out.println("No chair available for customer "+customer.name);
                System.out.println("Customer "+customer.name+"Exists...");
                return ;
            }
 
            ((LinkedList<Customer>)listCustomer).offer(customer);
            System.out.println("Customer : "+customer.name+ " got the chair.");
             
            if(listCustomer.size()==1)
                listCustomer.notify();
        }
    }
}