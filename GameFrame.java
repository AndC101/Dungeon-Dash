/* GameFrame class establishes the frame (window) for the game
It is a child of JFrame because JFrame manages frames
Runs the constructor in GamePanel class

*/ 
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

public class GameFrame extends JFrame implements ActionListener{

    private static GameFrame currentGameFrame; // Keep track of the current GameFrame instance
	GamePanel panel;


	public GameFrame(boolean levelSelect, boolean edit, boolean play, String levelTitle) throws IOException{

        if (currentGameFrame != null) {
            currentGameFrame.dispose();
        }

        currentGameFrame = this; // Set the current GameFrame to this instance


		panel = new GamePanel(levelSelect, edit, play, levelTitle); //run GamePanel constructor
		this.add(panel);
		this.setTitle("Dungeon Dash"); //set title for frame
		this.setResizable(false); //frame can't change size
		this.setBackground(Color.white);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //X button will stop program execution

		//if level select menu, the frame must be adjusted
		if(panel.levelSelect) {
			this.setTitle("Dungeon Dash - Level Select"); //set title for frame

			//back button
			JButton backButton = new JButton("Back to menu");
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(backButton);

			JScrollPane scroll = new JScrollPane();
			Container contentPane = this.getContentPane();

			SpringLayout layout = new SpringLayout();
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(layout);
			contentPane.setLayout(new BorderLayout());

			int j = 25;
			for(int i = 0; i < panel.names.size(); i++){
				String title = panel.names.get(i);
				JLabel label = new JLabel("Title: " + title);
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
				
				addPlayButtonListener(playButton, title); //when play/edit button is pressed, the title of the level is sent to gamepanel for processing
				addEditButtonListener(editButton, title);

				j+=60;
			}


			mainPanel.setPreferredSize(new Dimension(mainPanel.getWidth(), panel.totalHeight));
			scroll.setPreferredSize(new Dimension(GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT));
			scroll.setViewportView(mainPanel);
			contentPane.add(scroll);
			contentPane.add(buttonPanel,BorderLayout.SOUTH);

			//action listener for backButton
			backButton.addActionListener(this);

			// contentPane.add(buttonPanel,BorderLayout.SOUTH);
			this.setSize(900, 550);

		
		} 
			this.setSize(900, 550);
			this.pack();//makes components fit in window - don't need to set JFrame size, as it will adjust accordingly
			this.setVisible(true); //makes window visible to user
			this.setLocationRelativeTo(null);//set window in middle of screen


	}
    private void addPlayButtonListener(JButton playButton, String title) {
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform actions when any "Play" button is pressed
                System.out.println("Play button in row " + title + " pressed!");
				
				try {
					// System.out.println("WE GOOD" + title);
					new GameFrame(false, false, true, title);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
        });
    }

	private void addEditButtonListener(JButton editButton, String title) {
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Perform actions when any "Play" button is pressed
				System.out.println("Edit button in row " + title + " pressed!");
				
				try {
					// System.out.println("WE GOOD" + title);
					new GameFrame(false, true, false, title);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			});
    }


	@Override
	public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Back to menu")) {
            // Perform actions to return to the main menu
            dispose(); // Close the current frame

            // Example: Open a new frame for the main menu
            try {
                new GameFrame(false, false, false, "");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (e.getActionCommand().equals("Edit")) {
			dispose();

			try {
                new GameFrame(false, true, false, "");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

		}
	}
  
}