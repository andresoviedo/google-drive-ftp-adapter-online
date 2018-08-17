package org.andresoviedo.util.security;

import javax.swing.*;

public class AskPasswordDialog {

    public AskPasswordDialog(){
    }

    public String show(){
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Password:");
        JPasswordField pass = new JPasswordField(50);
        panel.add(label);
        panel.add(pass);
        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panel, "Application Security",
                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[1]);
        if(option == 0) // pressing OK button
        {
            char[] password = pass.getPassword();
            return new String(password);
        }
        return null;
    }

    public static void main(String[] args){
        new AskPasswordDialog();
    }
}
