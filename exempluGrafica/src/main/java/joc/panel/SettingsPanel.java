package joc.panel;

import joc.sql.Connection;
import joc.window.MenuWindow;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsPanel extends JPanel {

    private JButton backButton;
    private JButton saveButton;
    private JButton confirmButton;

    private String oldUsername;

    private JLabel usernameLabel;
    private JTextField usernameField;

    private MenuWindow parent;

    public String getUsername(){
        return this.oldUsername;
    }

    public SettingsPanel(MenuWindow parent){
        super();


        this.parent = parent;

        this.backButton = new JButton("back");
        this.saveButton = new JButton("save");
        this.confirmButton = new JButton("confirm");

        this.backButton.addActionListener(e->{
            goBack();
        });

        this.saveButton.addActionListener(e->{
            saveSettings();
        });

        this.confirmButton.addActionListener(e->{
            saveSettings();
            goBack();
        });

        this.usernameLabel = new JLabel("player name : ");
        this.usernameField = new JTextField();

        this.loadSettings();

        this.buildLayout();
    }

    private void goBack(){
        this.parent.remove(this);
        this.parent.add(this.parent.getButtonsContainer(), BorderLayout.CENTER);
        this.setVisible(false);
        this.parent.getButtonsContainer().setVisible(true);
    }

    private void loadSettings(){
        Connection.getInstance().connect();

        try{
            PreparedStatement statementSelect = Connection
                    .getInstance()
                    .getConnection()
                    .prepareStatement("SELECT * FROM player_settings LIMIT 1");

            ResultSet rs = statementSelect.executeQuery();

            rs.next();

            this.oldUsername = rs.getString(2);
            this.usernameField.setText(rs.getString(2));

        } catch ( SQLException e){
            e.printStackTrace();
        }

//        System.out.println(this.actualScoresList);

        Connection.getInstance().disconnect();
    }

    private void saveSettings(){
        if(this.usernameField.getText().equals(this.oldUsername))
            return;
        if(this.usernameField.getText().isEmpty())
            return;

        Connection.getInstance().connect();

        try{
            PreparedStatement statementSelect = Connection
                    .getInstance()
                    .getConnection()
                    .prepareStatement("UPDATE player_settings SET player_name = ? WHERE ID = 1");

            statementSelect.setString(1, this.usernameField.getText());
            this.oldUsername = this.usernameField.getText();

            statementSelect.executeUpdate();

        } catch ( SQLException e){
            e.printStackTrace();
        }

        Connection.getInstance().disconnect();
    }

    private void buildLayout(){
        GroupLayout groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addComponent(this.usernameLabel)
                    .addComponent(this.usernameField)
                )
                .addGroup(groupLayout.createSequentialGroup()
                    .addComponent(this.backButton)
                    .addComponent(this.saveButton)
                    .addComponent(this.confirmButton)
                )
        );

        groupLayout.setVerticalGroup(
            groupLayout.createSequentialGroup()
                .addGroup(
                    groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.usernameLabel)
                        .addComponent(this.usernameField)
                )
                .addGroup(
                    groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.backButton)
                        .addComponent(this.saveButton)
                        .addComponent(this.confirmButton)
                )
        );
    }
}
