//Karanei Kimutai Michael,183523,ICS,04/11/2024
package com.karanei.sufeeds;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        //make the ui more appealing
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }



        // Start application with login page
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
//Where did all this time go