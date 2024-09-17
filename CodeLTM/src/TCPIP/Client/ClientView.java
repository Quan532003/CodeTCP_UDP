package TCPIP.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import model.*;

public class ClientView {

    private JFrame mainFrame;
    private JPanel cards; // Container for different views
    private CardLayout cardLayout;

    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JTextField registerUsernameField;
    private JPasswordField registerPasswordField;
    private JTextField statsUsernameField;

    public void createAndShowGUI() {
        mainFrame = new JFrame("Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 300);
        mainFrame.setLayout(new BorderLayout());

        // Initialize CardLayout and main panel
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // Create and add different panels
        cards.add(createMainMenuPanel(), "MainMenu");
        cards.add(createLoginPanel(), "Login");
        cards.add(createRegisterPanel(), "Register");
        cards.add(createStatsPanel(), "Stats");

        mainFrame.add(cards, BorderLayout.CENTER);

        // Show the main menu panel initially
        cardLayout.show(cards, "MainMenu");

        mainFrame.setVisible(true);
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton loginButton = new JButton("Đăng Nhập");
        JButton registerButton = new JButton("Đăng Ký");
        JButton statsButton = new JButton("Thống Kê");

        loginButton.addActionListener(e -> {
            clearLoginPanel();
            cardLayout.show(cards, "Login");
        });
        registerButton.addActionListener(e -> {
            clearRegisterPanel();
            cardLayout.show(cards, "Register");
        });
        statsButton.addActionListener(e -> {
            clearStatsPanel();
            cardLayout.show(cards, "Stats");
        });

        panel.add(loginButton);
        panel.add(registerButton);
        panel.add(statsButton);

        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel usernameLabel = new JLabel("Username:");
        loginUsernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        loginPasswordField = new JPasswordField(15);
        JButton loginButton = new JButton("Đăng Nhập");
        JButton backButton = new JButton("Quay Lại");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(loginUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(loginPasswordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(loginButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(backButton, gbc);

        loginButton.addActionListener(e -> {
            ClientControl clientCtr = new ClientControl();
            clientCtr.openConnection();

            User user = new User(loginUsernameField.getText(),
                    new String(loginPasswordField.getPassword()));
            DataSend data = new DataSend(user, 0);
            clientCtr.sendData(data);
            String result = clientCtr.receiveData();
            if (result.equals("ok")) {
                JOptionPane.showMessageDialog(mainFrame, "Đăng nhập thành công!");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Đăng nhập thất bại!");
            }
            clientCtr.closeConnection();
        });

        backButton.addActionListener(e -> cardLayout.show(cards, "MainMenu"));

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel usernameLabel = new JLabel("Username:");
        registerUsernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        registerPasswordField = new JPasswordField(15);
        JButton registerButton = new JButton("Đăng Ký");
        JButton backButton = new JButton("Quay Lại");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(registerUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(registerPasswordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(registerButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(backButton, gbc);

        registerButton.addActionListener(e -> {
            ClientControl clientCtr = new ClientControl();
            clientCtr.openConnection();
            User user = new User(registerUsernameField.getText(),
                    new String(registerPasswordField.getPassword()));
            DataSend data = new DataSend(user, 1);
            clientCtr.sendData(data);
            String result = clientCtr.receiveData();
            if (result.equals("ok")) {
                JOptionPane.showMessageDialog(mainFrame, "Đăng ký thành công!");
            } else if (result.equals("fail")) {
                JOptionPane.showMessageDialog(mainFrame, "Đăng ký thất bại!");
            } else if (result.equals("exist")) {
                JOptionPane.showMessageDialog(mainFrame, "Teen dang nhap da ton tai");
            }
            clientCtr.closeConnection();
        });

        backButton.addActionListener(e -> cardLayout.show(cards, "MainMenu"));

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        JLabel usernameLabel = new JLabel("Username:");
        statsUsernameField = new JTextField(15);
        JButton searchButton = new JButton("Tìm");
        JButton backButton = new JButton("Quay Lại");

        inputPanel.add(usernameLabel);
        inputPanel.add(statsUsernameField);
        inputPanel.add(searchButton);
        inputPanel.add(backButton);

        // Create table model and table
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"User", "Details"}, 0);
        JTable statsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(statsTable);

        searchButton.addActionListener(e -> {
            ClientControl clientCtr = new ClientControl();
            clientCtr.openConnection();

            User user = new User(statsUsernameField.getText(), "");
            DataSend data = new DataSend(user, 2);
            clientCtr.sendData(data);
            ArrayList<User> result = clientCtr.receiveDataUser();
            System.out.println(result.size());
            clientCtr.closeConnection();

            // Update table model with received data
            tableModel.setRowCount(0); // Clear existing rows
            for (User u : result) {
                tableModel.addRow(new Object[]{u.getUserName(), "Details for " + u.getUserName()});
            }
        });

        backButton.addActionListener(e -> cardLayout.show(cards, "MainMenu"));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void clearLoginPanel() {
        loginUsernameField.setText("");
        loginPasswordField.setText("");
    }

    private void clearRegisterPanel() {
        registerUsernameField.setText("");
        registerPasswordField.setText("");
    }

    private void clearStatsPanel() {
        statsUsernameField.setText("");
    }

    void setVisible(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
