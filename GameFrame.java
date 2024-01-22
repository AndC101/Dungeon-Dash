/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * GameFrame creates the window or "frame" for the game, switches based on game mode
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class GameFrame extends JFrame implements ActionListener{

    public static GameFrame currentGameFrame; // keeps track of current GameFrame instance
	GamePanel panel;
	JPanel mainPanel;


	Image background = new ImageIcon("Images/levelSelectBackground.png").getImage();
	public GameFrame(boolean levelSelect, boolean edit, boolean play, String levelTitle, long ms) throws IOException{
		
		//dispose old gameframes 
        if (currentGameFrame != null) {
            currentGameFrame.dispose();
        }

        currentGameFrame = this; // Set the current GameFrame to this instance


		try {
			panel = new GamePanel(levelSelect, edit, play, levelTitle, ms); //ms refers to the current time stamp of the menu music.
			//this causes the menu music to play uniformly throughout levelselect, mainmenu and instruction states
		} catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
			e.printStackTrace();
		} //run GamePanel constructor
		this.add(panel);
		this.setTitle("Dungeon Dash"); //set title for frame
		this.setResizable(false); //frame can't change size
		this.setBackground(Color.white);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //X button will stop program execution

		//if level select menu, the frame must be adjusted to levelselect mode
		if(panel.levelSelect) {
			this.setTitle("Dungeon Dash - Level Select"); //set title for frame

			//back button
			JButton backButton = new JButton("Back to menu");
			backButton.setBackground(Color.DARK_GRAY);
			backButton.setForeground(Color.white);
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(backButton);

			//enable scrolling feature
			JScrollPane scroll = new JScrollPane();
			Container contentPane = this.getContentPane();

			//layout for buttons and label
			SpringLayout layout = new SpringLayout();
			mainPanel = new JPanel();
			mainPanel.setOpaque( false ); //enables the background image to be seen


			mainPanel.setLayout(layout);
			contentPane.setLayout(new BorderLayout());
			
			int j = 25; // initial y pos
			for(int i = 0; i < panel.names.size(); i++){ //loops through each level saved ( names.size() )
				
				//get the level title and make label and buttons for it
				
				String title = panel.names.get(i);
				JLabel label = new JLabel("Title: " + title);
				label.setForeground(Color.white);
				JButton playButton = new JButton("Play");
				JButton editButton = new JButton("Edit");
				JButton deleteButton = new JButton("Delete");
				JButton hsButton = new JButton("High Scores");
				playButton.setBackground(Color.white);
				editButton.setBackground(Color.white);
				deleteButton.setBackground(Color.white);
				hsButton.setBackground(Color.white);


				label.setFont(new Font("Impact", Font.PLAIN, 18));

				//add and format the information
				mainPanel.add(label);
				mainPanel.add(playButton);
				mainPanel.add(editButton);
				mainPanel.add(deleteButton);
				mainPanel.add(hsButton);
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
				layout.putConstraint(SpringLayout.NORTH, deleteButton, j, SpringLayout.NORTH,
				contentPane);
				layout.putConstraint(SpringLayout.WEST, deleteButton, 20, SpringLayout.EAST,
				editButton);
				layout.putConstraint(SpringLayout.NORTH, hsButton, j, SpringLayout.NORTH,
				contentPane);
				layout.putConstraint(SpringLayout.WEST, hsButton, 20, SpringLayout.EAST,
				deleteButton);
				//spring layout allows you to have greater control over the placement of components relative to one another.



							
				addPlayButtonListener(playButton, title); //when play/edit/delete button is pressed, the title of the level is sent to gamepanel for processing
				addEditButtonListener(editButton, title);
				addDeleteButtonListener(deleteButton, title);
				addHSButtonListener(hsButton, title);
				j+=60; //increases the vertical distance between each level
			}

			//sets viewport to make background cave image "static"
			JViewport viewport = new JViewport()
			{
				@Override
				protected void paintComponent(Graphics g)
				{
					super.paintComponent(g);
					g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
				}
			};
			

			//makes scrollpane work
			mainPanel.setPreferredSize(new Dimension(mainPanel.getWidth(), panel.names.size()*62));
			scroll.setPreferredSize(new Dimension(GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT));

			scroll.setViewport(viewport); //displays background "static" image

			scroll.setViewportView(mainPanel); 

			contentPane.add(scroll); //adds the scrollpane

			contentPane.add(buttonPanel,BorderLayout.SOUTH); //return to menu
			buttonPanel.setBackground(Color.DARK_GRAY);

			//action listener for backButton
			backButton.addActionListener(this); //return to menu


		
		} 
		this.setSize(900, 550);
		this.pack();//makes components fit in window - don't need to set JFrame size, as it will adjust accordingly
		this.setVisible(true); //makes window visible to user
		this.setLocationRelativeTo(null);//set window in middle of screen


	}

	//checks for which play button is pressed and loads the game file in a new gameframe
    private void addPlayButtonListener(JButton playButton, String title) {
        playButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				//sets states and makes new gameframe				
				try {
					panel.play = true;
					panel.levelSelect = false;
					panel.running = false;
					new GameFrame(false, false, true, title, panel.menuMusicStart);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
        });
    }

	//same logic as play button, but sends user into edit mode for the level
	private void addEditButtonListener(JButton editButton, String title) {
		editButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				//sets states and makes new gameframe for edit mode
				try {
					panel.edit=true;
					panel.levelSelect = false;
					panel.running = false;
					new GameFrame(false, true, false, title, panel.menuMusicStart);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			});
    }

	//same logic as play button, but sends deletes level from file io and displays message
	private void addDeleteButtonListener(JButton deleteButton, String title) {
        deleteButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				deleteLevel(title);
				deleteLevelName(title);

				JOptionPane.showMessageDialog(mainPanel,  "Level deleted!", "Delete Confirmation",
				JOptionPane.INFORMATION_MESSAGE);
				try {
					panel.running = false;
					new GameFrame(true,false, false, "", panel.menuMusicStart);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
	

            }
        });
    }

	//adds button listener to the high score button
	private void addHSButtonListener(JButton hsButton, String title) {
        hsButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JDialog popup = new JDialog(currentGameFrame, title + " High Scores", true); //popup for the high score frame
				popup.setSize(300, 200);
				popup.setLayout(new BorderLayout());
		
				String highScoresText = getHighScores(title); //retrieves all high scores and formats it nicely

				// Create a JTextArea to be placed inside the JScrollPane
				JTextArea textArea = new JTextArea(highScoresText);
				textArea.setEditable(false);
				
				// Create a JScrollPane and add the JTextArea to it
				JScrollPane scrollPane = new JScrollPane(textArea);
		
				// Add the JScrollPane to the popup
				popup.add(scrollPane, BorderLayout.CENTER);
		
		
				// Set the location of the popup relative to the parent frame
				popup.setLocationRelativeTo(currentGameFrame);
		
				// Make the popup visible
				popup.setVisible(true);
		
            }
        });
    }


	//returns the high scores for the level from the HighScores.txt file 
	public String getHighScores(String title) {
		String scores = "";
        PriorityQueue<Pair> scorePairs = new PriorityQueue<>(); //priority queue for automatic sorting 
		int idx = 1;

		if (title.equals("")) {
			return "Invalid.";
		} else {
			String filePath = "HighScores.txt"; 
			//reads information from txt file
			try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
				String line;

				while ((line = reader.readLine()) != null) {
					// Split the line into words using : 
					if (line.startsWith(title)) {
						String[] words = line.split(": ");
						for (int i = 1; i < words.length; i++) {
							// i = 1 skip over the title of the thing ASSUMES THAT the user doesn't enter :
							// in the title itself
							String[] inputs = words[i].split(" "); // splits based on space
							scorePairs.add(new Pair(inputs[0], Integer.parseInt(inputs[1]))); //sorts the pairs by lowest time when added
							
						}
					}

				}

				//loops through the priority queue and removes the top node each time for the lowest time scorer
				while(!scorePairs.isEmpty()) {
					Pair x = scorePairs.poll();
					scores += "#" + idx + ".  "  + "USER: " + x.name + "     TIME: " + x.time + "\n\n";
					idx++; //index of the winner
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			return scores;
		}
   }


	//checks if a button is pressed (back button only)
	public void actionPerformed(ActionEvent e) {
		//if back button pressed
        if (e.getActionCommand().equals("Back to menu")) {
            
            dispose(); //close the current frame

            //opens new frame back to the main menu
            try {
				panel.running= false;
                new GameFrame(false,false, false, "", panel.menuMusicStart);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } 
		
	}

	//deletes the level information from file
	public void deleteLevel(String title) {
	// if the title is not found or there is no title

		try {
			// input the (modified) file content to the StringBuffer "input"
			BufferedReader file = new BufferedReader(new FileReader("LevelSave.txt"));
			BufferedReader hsFile = new BufferedReader(new FileReader("HighScores.txt"));

			StringBuffer inputBuffer = new StringBuffer();
			StringBuffer inputBufferHS = new StringBuffer();

			String line;

			//read and append each line except title
			while ((line = file.readLine()) != null) {

				if (!line.startsWith(title)) {
					inputBuffer.append(line);
					inputBuffer.append('\n');
				} 
			}

			//deletes the title from the high score file as well
			while ((line = hsFile.readLine()) != null) {

				if (!line.startsWith(title)) {
					inputBufferHS.append(line);
					inputBufferHS.append('\n');
				} 
			}

			file.close();
			hsFile.close();

			// write the new string with the replaced line OVER the same file
			FileOutputStream fileOut = new FileOutputStream("LevelSave.txt");
			fileOut.write(inputBuffer.toString().getBytes());
			fileOut.close();

			FileOutputStream fileOutHS = new FileOutputStream("HighScores.txt");
			fileOutHS.write(inputBufferHS.toString().getBytes());
			fileOutHS.close();


		} catch (Exception e) {
			System.out.println("Problem reading file.");
		}
	}	

	//deletes the name of the level from the file
	public void deleteLevelName(String title) {
		String[] names;
		String newTitles = "";
		String line ="";
		BufferedReader file;
		StringBuffer inputBuffer = new StringBuffer();
		FileOutputStream fileOut;
		// if the title is not found or there is no title
	
			try {
				// input the (modified) file content to the StringBuffer "input"
				file = new BufferedReader(new FileReader("names.txt"));
				line = file.readLine();
				names = line.split(", ");
				//splits based on regex , and adds all names except title;
				for(String name: names){
					if(!name.equals(title)){
						newTitles += name + ", ";
					}
				}
				//append all titles excluding the "title"
				inputBuffer.append(newTitles);
				file.close();
	
				// write the new string with the replaced line OVER the same file
				fileOut = new FileOutputStream("names.txt");
				fileOut.write(inputBuffer.toString().getBytes());
				fileOut.close();
	
			} catch (Exception e) {
				System.out.println("Problem reading file.");
			}
		}	
	


}

  