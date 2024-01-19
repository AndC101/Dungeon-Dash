/*
 * Ethan Lin & Andrew Chen
 * January 11, 2023
 * GameFrame creates the window or "frame" for the game, switches based on game mode
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GameFrame extends JFrame implements ActionListener{

    public static GameFrame currentGameFrame; // keeps track of current GameFrame instance
	GamePanel panel;
	JPanel mainPanel;
	BufferedImage myPicture = ImageIO.read(new File("Images/brick.png"));
	JLabel picLabel = new JLabel(new ImageIcon(myPicture));

	public GameFrame(boolean levelSelect, boolean edit, boolean play, String levelTitle) throws IOException{
		
		//dispose old gameframes 
        if (currentGameFrame != null) {
            currentGameFrame.dispose();
        }

        currentGameFrame = this; // Set the current GameFrame to this instance


		panel = new GamePanel(levelSelect, edit, play, levelTitle); //run GamePanel constructor
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
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(backButton);

			//enable scrolling feature
			JScrollPane scroll = new JScrollPane();
			Container contentPane = this.getContentPane();

			//layout
			SpringLayout layout = new SpringLayout();
			mainPanel = new JPanel();
			// mainPanel.add(picLabel);

			mainPanel.setLayout(layout);
			contentPane.setLayout(new BorderLayout());
			
			int j = 25; // initial y pos
			for(int i = 0; i < panel.names.size(); i++){ //loops through each level saved ( names.size() )
				
				//get the level title and make label and buttons for it
				String title = panel.names.get(i);
				JLabel label = new JLabel("Title: " + title);
				JButton playButton = new JButton("Play");
				JButton editButton = new JButton("Edit");
				JButton deleteButton = new JButton("Delete");

				label.setFont(new Font("Impact", Font.PLAIN, 18));

				//add and format the information
				mainPanel.add(label);
				mainPanel.add(playButton);
				mainPanel.add(editButton);
				mainPanel.add(deleteButton);
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



							
				addPlayButtonListener(playButton, title); //when play/edit/delete button is pressed, the title of the level is sent to gamepanel for processing
				addEditButtonListener(editButton, title);
				addDeleteButtonListener(deleteButton, title);

				j+=60;
			}


			//makes scrollpane work
			mainPanel.setPreferredSize(new Dimension(mainPanel.getWidth(), panel.names.size()*62));
			scroll.setPreferredSize(new Dimension(GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT));
			scroll.setViewportView(mainPanel);
			contentPane.add(scroll);
			contentPane.add(buttonPanel,BorderLayout.SOUTH);

			//action listener for backButton
			backButton.addActionListener(this); //return to menu

			this.setSize(900, 550);

		
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
                // debug for play button pressed
                System.out.println("Play button in row " + title + " pressed!");
				
				try {
					panel.running = false;
					new GameFrame(false, false, true, title);
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
				// Perform actions when any "Play" button is pressed
				System.out.println("Edit button in row " + title + " pressed!");
				
				try {
					panel.running = false;
					new GameFrame(false, true, false, title);
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
                // debug for play button pressed
                System.out.println("Delete button in row " + title + " pressed!");
				deleteLevel(title);
				deleteLevelName(title);

				JOptionPane.showMessageDialog(mainPanel,  "Level deleted!", "Delete Confirmation",
				JOptionPane.INFORMATION_MESSAGE);
				try {
					panel.running = false;
					new GameFrame(true,false, false, "");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
	

            }
        });
    }



	//checks if a button is pressed (back button only)
	public void actionPerformed(ActionEvent e) {
		//if back button pressed
        if (e.getActionCommand().equals("Back to menu")) {
            
            dispose(); //close the current frame

            //opens new frame back to the main menu
            try {
                new GameFrame(false,false, false, "");
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
			StringBuffer inputBuffer = new StringBuffer();
			String line;

			//read and append each line except title
			while ((line = file.readLine()) != null) {

				if (!line.startsWith(title)) {
					inputBuffer.append(line);
					inputBuffer.append('\n');
				} 
			}

			file.close();

			// write the new string with the replaced line OVER the same file
			FileOutputStream fileOut = new FileOutputStream("LevelSave.txt");
			fileOut.write(inputBuffer.toString().getBytes());
			fileOut.close();

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

  
