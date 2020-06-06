package joc.panel;

import joc.sql.Connection;
import joc.window.MenuWindow;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScoresPanel extends JPanel {
    private MenuWindow parent;
    private JButton backButton;
    private JList<String> scoresList;
    private DefaultListModel<String> listModel;
    private List<String> actualScoresList;

    public ScoresPanel(MenuWindow parent){
        super();
        this.parent = parent;

        this.setSize(new Dimension(parent.getWidth(), parent.getHeight()));

        this.backButton = new JButton("back");
        this.listModel = new DefaultListModel<>();

        this.scoresList = new JList<>(this.listModel);

        this.actualScoresList = new ArrayList<>();

        this.setLayout(new BorderLayout());

        this.add(this.backButton, BorderLayout.SOUTH);
        this.add(this.scoresList, BorderLayout.CENTER);

        this.populateScores();

        this.backButton.addActionListener(e->{
            this.setVisible(false);
            this.parent.getButtonsContainer().setVisible(true);
        });

        this.setVisible(true);
    }

    private void populateScores(){
        Connection.getInstance().connect();

        try{
            PreparedStatement statementSelect = Connection
                    .getInstance()
                    .getConnection()
                    .prepareStatement("SELECT * FROM high_scores ORDER BY score desc");

            ResultSet rs = statementSelect.executeQuery();

            this.actualScoresList.clear();

            while(rs.next()){
//                System.out.println(rs.getInt(1) + ", " + rs.getString(2) + ", " + rs.getInt(3));
                this.actualScoresList.add(rs.getString(2) + "'s score : " + rs.getInt(3));
            }

            this.listModel.clear();

            int index = 0;

            for( String score : this.actualScoresList ){
                this.listModel.add(index++, score);
            }

        } catch ( SQLException e){
            e.printStackTrace();
        }

//        System.out.println(this.actualScoresList);

        Connection.getInstance().disconnect();
    }


}
