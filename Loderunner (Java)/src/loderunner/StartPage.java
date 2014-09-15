package loderunner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class StartPage {

    public StartPage(JFrame frame) {
    final JFrame parent = new JFrame();
    JButton button = new JButton();
    //JOptionPane.showInputDialog(parent,"Welcome to Lode Runner, by Philip Ross, Chace Beard and Samuel Lawrence",null);
    JOptionPane.showMessageDialog(parent, "Welcome to Lode Runner, by Philip Ross, Chace Beard and Samuel Lawrence");
     button.setText("Ok");
     parent.add(button);
     parent.pack();
     parent.setVisible(true);

     button.addActionListener(new java.awt.event.ActionListener() {

     @Override
     public void actionPerformed(java.awt.event.ActionEvent evt) {

     

     }

     });

  }
}