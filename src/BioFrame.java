import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class BioPanel extends JPanel{


    BioSystem bioSys;

    public BioPanel(BioSystem bioSys){
        this.bioSys = bioSys;
        setBackground(Color.BLACK);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        int L = bioSys.getL();
        int K = bioSys.getK();
        int w = getWidth()/L;
        int h = getHeight()/K;

        for(int l = 0; l < L; l++) {
            if(bioSys.getMicrohabitat(l).getN() > 0){

                for(int k = 0; k < bioSys.getMicrohabitat(l).getN(); k++) {

                    if(bioSys.getBacteria(l, k).getM() == 1) {
                        g.setColor(Color.RED);
                        g.fillRect(w*l, h*k, w, l);
                    } else if(bioSys.getBacteria(l, k).getM() == 2) {
                        g.setColor(Color.ORANGE);
                        g.fillRect(w*l, h*k, w, l);
                    } else if(bioSys.getBacteria(l, k).getM() == 3) {
                        g.setColor(Color.YELLOW);
                        g.fillRect(w*l, h*k, w, l);
                    } else if(bioSys.getBacteria(l, k).getM() == 4) {
                        g.setColor(Color.GREEN);
                        g.fillRect(w*l, h*k, w, l);
                    } else if(bioSys.getBacteria(l, k).getM() == 5) {
                        g.setColor(Color.BLUE);
                        g.fillRect(w*l, h*k, w, l);
                    } else if(bioSys.getBacteria(l, k).getM() == 6) {
                        g.setColor(Color.MAGENTA);
                        g.fillRect(w*l, h*k, w, l);
                    }else {
                        g.setColor(Color.DARK_GRAY);
                        g.fillRect(w*l, h*k, w, l);
                    }
                }
            }
        }
    }


    public void monteAnimate(){
        for(int i = 0; i < 10000; i++){
            bioSys.performAction();
        }
        repaint();
    }

    public void updateAlpha(double newAlpha){
        bioSys = new BioSystem(bioSys.getL(), bioSys.getK(), bioSys.getS(), newAlpha);
        repaint();
    }
}




public class BioFrame extends JFrame {

    int L = 500, K = 100, S = 100;
    double alpha = 0.05;

    BioPanel bioPan;
    BioSystem bioSys;
    Timer monteTimer;

    JButton goButton = new JButton("Go");

    //stuff for allowing GUI variance of alpha
    JLabel alphaLabel = new JLabel("alpha: ");
    JTextField alphaField = new JTextField(String.valueOf(alpha), 10);

    public BioFrame(){

        bioSys = new BioSystem(L, K, S, alpha);

        bioPan = new BioPanel(bioSys);
        bioPan.setPreferredSize(new Dimension(1000, 200));

        JPanel controlPanel = new JPanel();
        controlPanel.add(goButton);
        controlPanel.add(alphaLabel);
        controlPanel.add(alphaField);

        getContentPane().add(bioPan, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.SOUTH);
        pack();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                System.exit(0);
            }
        });

        setTitle("Bacterial Monte Carlo");
        setLocation(100, 20);
        setVisible(true);
        setBackground(Color.LIGHT_GRAY);

        monteAnimate();
        updateAlpha();
    }



    public void monteAnimate(){
        monteTimer = new Timer(0, (e)->{bioPan.monteAnimate();});

        goButton.addActionListener((e)->{
            if(!monteTimer.isRunning()) {
                monteTimer.start();
                goButton.setText("Stop");
            }
            else {
                monteTimer.stop();
                goButton.setText("Go");
            }
        });
    }

    public void updateAlpha(){
        alphaField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                alphaField.setText("");
            }
        });

        alphaField.addActionListener((e)->{
            double alpha = Double.parseDouble(alphaField.getText());
            bioPan.updateAlpha(alpha);
        });
    }


}
