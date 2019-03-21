/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package producerconsumer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 *
 * @author L440
 */
public class ProducerConsumer{
    
    public static void main(String[] args){
        new Menu();
    }
}


class Canvas extends JFrame{
    public static List<status> sList = new ArrayList<status>();
    public static Warehouse c;
    public Canvas(int prod, int cons, int maxWh){
        super();
        setSize(300, 70*(prod+cons));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        c = new Warehouse();
        
        for (int i = 0; i < cons; i++) {
            Consumer consumer = new Consumer(c, i, 5);
            sList.add(consumer);
            new Thread(consumer).start();
        }
        for (int i = 0; i < prod; i++) {
            Producer producer = new Producer(c, i, 5);
            sList.add(producer);
            new Thread(producer).start();
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
        //if(c!=null? c.dato!=null:false)
        //lbl.setText(c.dato.size()+"");
        int start = 0;
        g.setColor(Color.white);
        g.fillRect(0, start, 300, 500);
        for (int i = 0; i < sList.size(); i++) {
            if(sList.get(i).isConsumer){ //SQUARE IS CONSUMER
                if(sList.get(i).isWaiting==1)
                    g.setColor(Color.red);
                else if(sList.get(i).isWaiting==2)
                    g.setColor(Color.blue);
                else
                    g.setColor(Color.pink);
                g.fillRect(100, start+i*70, 50, 50);
            }else{ //CIRCLE PRODUCER
                if(sList.get(i).isWaiting==1)
                    g.setColor(Color.red);
                else if(sList.get(i).isWaiting==2)
                    g.setColor(Color.blue);
                else
                    g.setColor(Color.pink);
                g.fillOval(100, start+i*70, 50, 50);
            }
        }
    }
}

class Menu extends JFrame implements ActionListener{
    public static JTextField maxWh;
    public static JTextField prod;
    public static JTextField cons;
    public static JLabel lbl;
    
    public Menu(){
        super();
        setSize(300, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        JPanel panel = (JPanel) this.getContentPane();
        panel.setLayout(new BorderLayout());
        JPanel options = new JPanel();
        options.setLayout(new GridLayout(19, 4));
        
        prod = new JTextField();
        prod.setText("Num productores");
        options.add(prod);
        cons = new JTextField();
        cons.setText("Num. consumidores");
        options.add(cons);
        maxWh = new JTextField();
        maxWh.setText("Almacenado máximo");
        options.add(maxWh);
        
        lbl = new JLabel();
        lbl.setBounds(0, 450, 100, 50);
        lbl.setText("holi");
        options.add(lbl);
        lbl.show();
        
        JButton btn = new JButton();
        btn.setText("EMPEZAR");
        btn.addActionListener(this);
        options.add(btn);
        panel.add("Center", options);
        this.show();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        int p = 1, c = 1, wh = 10;
        try{
            p = Integer.parseInt(prod.getText());
        }catch(NumberFormatException ex){}
        try{
            c = Integer.parseInt(cons.getText());
        }catch(NumberFormatException ex){}
        try{
            wh = Integer.parseInt(maxWh.getText());
        }catch(NumberFormatException ex){}
        new Canvas(p,c,wh);
    }
}


class Producer extends status implements Runnable{
    private Warehouse wh;
    private int id;
    private int num;
    public Producer(Warehouse wh, int id, int num){
        this.wh= wh;
        this.id = id;
        this.num = num;
        this.isConsumer = false;
    }
    public void run(){
        for(int i = 0; i < 1000; i++){
            int rand = i; //(int)(Math.random()*100);
            synchronized(wh){
                wh.isUsed = this;
            }
            wh.put(this, rand);
            synchronized(wh){
                wh.isUsed = null;
            }
            
            System.out.println("Productor "+id+". put: " + rand);
            /*
            int before = isWaiting;
            isWaiting = 3;
            try{
                sleep((int)(Math.random() * 100)%6*1000);
            } catch(InterruptedException e) { }
            isWaiting = before;*/
        }
    }
}

class status{
    int isWaiting = 1;
    boolean isConsumer = true;
}

class Consumer extends status implements Runnable{
    private Warehouse wh;
    private int id;
    private int num;
    public Consumer(Warehouse wh, int id, int num){
        this.wh= wh;
        this.id = id;
        this.num = num;
    }
    public void run(){
        int value = 0;
        while(true){
            synchronized(wh){
                wh.isUsed = this;
            }
            value = wh.get(this);
            synchronized(wh){
                wh.isUsed = null;
            }
            System.out.println("Consumidor "+id+". get: " + value);
        }
        /*
        for(int i = 0; i < num; i++){
            value = wh.get(this);
            System.out.println("Consumidor "+id+". get: " + value);
            /*
            int before = isWaiting;
            isWaiting = 3;
            try{
                sleep((int)(Math.random() * 100)%6*1000);
            } catch(InterruptedException e) { }
            isWaiting = before;
            
        }*/
    }
}

class Warehouse {
    public int maxCapacity = 10;
    public volatile status isUsed = null;
    public volatile List<Integer> dato = new ArrayList<Integer>();
    public int get(status s){
        System.out.println("get: "+dato.size());
        while(dato.size()<=0 || (isUsed!= null && isUsed!=s)){
            try{
                s.isWaiting = 2;
                synchronized(this){
                wait();
                }
            } catch(InterruptedException e){ }
        }
        s.isWaiting = 1;
        System.out.println("get after wait ");
        int tmp = -1;
        synchronized(this){
        tmp = dato.get(0);
        dato.remove(dato.get(0));
        }
        try{
            sleep(1000);
        } catch(InterruptedException e) { }
        synchronized(this){
        notifyAll();
        }
        return tmp;
    }
    public synchronized void put(status s, int valor){
        System.out.println("put: "+dato.size());
        while(dato.size()>=maxCapacity || (isUsed!= null && isUsed!=s)){
            try{
                s.isWaiting = 2;
                synchronized(this){
                wait();
                }
            }catch(InterruptedException e){ }
        }
        s.isWaiting = 1;
        System.out.println("put after wait ");
        synchronized(this){
        dato.add(valor);
        }
        try{
            sleep(3000);
        } catch(InterruptedException e) { }
        synchronized(this){
        notifyAll();
        }
    }
}
