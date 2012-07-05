package redesiii;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class servidor_web_starter extends JFrame {

    JPanel jPanel1 = new JPanel();
    JScrollPane jScrollPane1 = new JScrollPane();
    JTextArea jTextArea2 = new JTextArea();
    static Integer listen_port = 8080;
    Servidor mi_servidor;

    //basic class constructor
    public servidor_web_starter(Servidor mi_servidorn) {
        try {
            mi_servidor = mi_servidorn;
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void jbInit() throws Exception {
        jTextArea2.setBackground(new Color(16, 12, 66));
        jTextArea2.setForeground(new Color(151, 138, 255));
        jTextArea2.setBorder(BorderFactory.createLoweredBevelBorder());
        jTextArea2.setToolTipText("");
        jTextArea2.setEditable(false);
        jTextArea2.setColumns(30);
        jTextArea2.setRows(15);
        //change this to impress your friends
        this.setTitle("Log de la interfaz web");
        //ugly inline listener, it's for handling closing of the window
        this.addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                this_windowClosed(e);
            }
        });
        //add the various to the proper containers
        jScrollPane1.getViewport().add(jTextArea2);
        jPanel1.add(jScrollPane1);
        this.getContentPane().add(jPanel1, BorderLayout.EAST);

        //tveak the apearance
        this.setVisible(true);
        this.setSize(640, 400);
        this.setResizable(false);
        //make sure it is drawn
        this.validate();
        //create the actual serverstuff,
        //all that is implemented in another class
        servidor_web servidor_web = new servidor_web(listen_port.intValue(), this, mi_servidor);
    }

    //exit program when "X" is pressed.
    void this_windowClosed(WindowEvent e) {
        System.exit(1);
    }

    //this is a method to get messages from the actual
    //server to the window
    public void send_message_to_window(String s) {
        jTextArea2.append(s);
    }
} //class end
