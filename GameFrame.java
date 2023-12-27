/* GameFrame class establishes the frame (window) for the game
It is a child of JFrame because JFrame manages frames
Runs the constructor in GamePanel class

*/ 
import java.awt.*;
import java.io.IOException;

import javax.swing.*;

public class GameFrame extends JFrame{

  GamePanel panel;

  public GameFrame(boolean levelSelect) throws IOException{
    panel = new GamePanel(levelSelect); //run GamePanel constructor
	this.add(panel);
	this.setTitle("Dungeon Dash"); //set title for frame
	this.setResizable(false); //frame can't change size
	this.setBackground(Color.white);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //X button will stop program execution

	//if level select menu, the frame must be adjusted
	if(panel.levelSelect) {
		this.setTitle("Dungeon Dash - Level Select"); //set title for frame

		JScrollPane scroll = new JScrollPane();
		Container contentPane = this.getContentPane();

		SpringLayout layout = new SpringLayout();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(layout);
		contentPane.setLayout(new BorderLayout());

		int j = 25;
		for(int i =1;i<=18;i++){
			JLabel label = new JLabel("Title: title | User: username");
			JButton playButton = new JButton("Play");
			JButton editButton = new JButton("Edit");
			label.setFont(new Font("Impact", Font.PLAIN, 18));

			mainPanel.add(label);
			mainPanel.add(playButton);
			mainPanel.add(editButton);
			layout.putConstraint(SpringLayout.WEST, label, 20, SpringLayout.WEST,
							contentPane);
			layout.putConstraint(SpringLayout.NORTH, label, j, SpringLayout.NORTH,
							contentPane);
			layout.putConstraint(SpringLayout.NORTH, playButton, j, SpringLayout.NORTH,
							contentPane);
			layout.putConstraint(SpringLayout.WEST, playButton, 20, SpringLayout.EAST,
							label);
			layout.putConstraint(SpringLayout.NORTH, editButton, j, SpringLayout.NORTH,
							contentPane);
			layout.putConstraint(SpringLayout.WEST, editButton, 20, SpringLayout.EAST,
							playButton);

			j+=60;
		}


		mainPanel.setPreferredSize(new Dimension(mainPanel.getWidth(), panel.totalHeight));
		scroll.setPreferredSize(new Dimension(GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT));
		scroll.setViewportView(mainPanel);
		contentPane.add(scroll);
		// contentPane.add(buttonPanel,BorderLayout.SOUTH);
		this.setSize(900, 550);

	
	} 
		this.setSize(900, 550);
	  	this.pack();//makes components fit in window - don't need to set JFrame size, as it will adjust accordingly
	  	this.setVisible(true); //makes window visible to user
	  	this.setLocationRelativeTo(null);//set window in middle of screen


  }
  
}