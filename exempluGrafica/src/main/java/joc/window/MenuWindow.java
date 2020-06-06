package joc.window;

import joc.Main;
import joc.panel.ScoresPanel;
import joc.panel.SettingsPanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Set;

public class MenuWindow extends JFrame {

    private JButton playButton;
    private JButton settings;
    private JButton highScores;
    private JButton exit;

    private JPanel buttonsContainer;
    private SettingsPanel settingsPanel;
    private ScoresPanel scoresPanel;

    public MenuWindow(){
        this.setSize(1280, 720);
        this.setLocationRelativeTo(null);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public JPanel getButtonsContainer() {
        return buttonsContainer;
    }

    public MenuWindow buildComponents(){
        this.buttonsContainer = new JPanel();

        this.playButton = new JButton("play game");
        this.highScores = new JButton("high scores");
        this.settings = new JButton("settings");
        this.exit = new JButton("exit");

        this.scoresPanel = new ScoresPanel(this);
        this.scoresPanel.setVisible(false);
        this.playButton.setFocusable(false);

        this.settingsPanel = new SettingsPanel(this);
        this.settingsPanel.setVisible(false);
//        this.setFocusableWindowState(false);
//
//        this.playButton.setFocusable(false);
//        this.highScores.setFocusable(false);
//        this.settings.setFocusable(false);
//        this.exit.setFocusable(false);
//        this.setFocusable(false);
//        this.buttonsContainer.setFocusable(false);

        return this;
    }

    public MenuWindow buildListeners(){
        this.exit.addActionListener(e->{
            System.exit(0);
        });

        this.playButton.addActionListener(e->{
//            System.out.println(this.getFocusOwner());
            this.playButton.transferFocus();
            this.highScores.transferFocus();
            this.settings.transferFocus();
            this.exit.transferFocus();

            this.transferFocus();
            this.dispose();
            Main.username = this.settingsPanel.getUsername();
            System.out.println(this.getFocusOwner());
            this.startGame();
        });

        this.settings.addActionListener(e -> {
            this.buttonsContainer.setVisible(false);

            this.remove(this.buttonsContainer);
            this.add(this.settingsPanel, BorderLayout.CENTER);
            this.settingsPanel.setVisible(true);

        });

        this.highScores.addActionListener(e->{
            this.buttonsContainer.setVisible(false);
            this.scoresPanel.setVisible(true);
        });

        return this;
    }

    private void startGame(){
        System.out.println(this.getFocusOwner());
        GameWindow gameWindow=GameWindow.getInstance()
                .buildWindow()
                .buildComponents();

        gameWindow.setVisible(true);

        gameWindow
                .buildLayout()
                .initialize()
                .buildListeners()
                .setFps(60);

        System.out.println(this.getFocusOwner());
//        gameWindow.startMenu();


        gameWindow.toFront();
        gameWindow.setVisible(true);

        gameWindow.setFocusableWindowState(true);
        gameWindow.requestFocus();
        System.out.println(this.getFocusOwner());

        gameWindow.startGame();
        System.out.println(this.getFocusOwner());

    }

    public MenuWindow buildLayout(){

        this.add(this.buttonsContainer, BorderLayout.NORTH);
        this.add(this.scoresPanel, BorderLayout.CENTER);
//        this.add(this.settingsPanel, BorderLayout.SOUTH); //? ??

        GroupLayout groupLayout = new GroupLayout(this.buttonsContainer);
        buttonsContainer.setLayout(groupLayout);

        groupLayout.setAutoCreateContainerGaps(true);
        groupLayout.setAutoCreateGaps(true);

        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(this.playButton)
                    .addComponent(this.highScores)
                    .addComponent(this.settings)
                    .addComponent(this.exit)
        );

        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                    .addComponent(this.playButton)
                    .addComponent(this.highScores)
                    .addComponent(this.settings)
                    .addComponent(this.exit)
        );

        return this;
    }

}
