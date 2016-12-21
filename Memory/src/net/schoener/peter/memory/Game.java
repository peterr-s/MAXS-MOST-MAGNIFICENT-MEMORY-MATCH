package net.schoener.peter.memory;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Game extends JFrame
{
	public static final Color GREEN = new Color(0, 255, 0),
			RED = new Color(255, 0, 0);
	
	public static final int CARD_CT = 6, // MORE THAN 52 AND BAD THINGS WILL HAPPEN
			ROWS = 2, // UPDATE THIS IF YOU ADD MORE CARDS
			COLUMNS = 3, // THIS TOO
			WIDTH = 500, // ALL CAPS
			HEIGHT = 500; // LOUD NOISES
	
	public static final String title = "MAX'S MOST MAGNIFICENT MEMORY MATCH";
	
	public static void main(String[] args)
	{
		@SuppressWarnings("unused")
		Game game = new Game();
	}

	private BetterButton turnedCard;
	private HashMap<BetterButton, String> buttonMap;
	private int clicks; // how much you suck
	
	public Game()
	{
		// DECLARE PRESENCE
		setTitle(title);
		
		turnedCard = null;
		clicks = 0;
		
		// do sizing things
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new GridLayout(ROWS, COLUMNS));

		buttonMap = new HashMap<>(); // initialize mapping from cards to their labels
		
		// make and shuffle deck
		Random rng = new Random(); // for picking cards
		char[] cardSymbols = new char[CARD_CT];
		for(int i = 0; i < CARD_CT; i++)
			cardSymbols[i] = ' ';
		for(int i = 0; i < CARD_CT / 2; i++)
		{
			int index1, index2;
			do
			{
				index1 = rng.nextInt(CARD_CT);
				index2 = rng.nextInt(CARD_CT);
			} while(index1 == index2 || cardSymbols[index1] != ' ' || cardSymbols[index2] != ' '); // pick two distinct unassigned cards at random
			
			char c = (char) (i + 65);
			cardSymbols[index1] = c; // make them the same
			cardSymbols[index2] = c;
		}
		
		for(int i = 0; i < CARD_CT; i++) // 6 iterations because we're doing the bare minimum
		{
			// make new button
			BetterButton tempCard = new BetterButton();
			tempCard.addActionListener((ActionEvent e) -> { // large as fuck lambda
				BetterButton thisCard = (BetterButton) e.getSource(); // get the button that was clicked so we can compare it to things
				
				thisCard.setText(buttonMap.get(thisCard)); // display the label
				repaint();
				
				if(turnedCard == null) // if nothing was turned
				{
					turnedCard = thisCard; // then this is turned
					turnedCard.setText(buttonMap.get(turnedCard)); // show the card because it's turned
					repaint(); // refresh
				}
				else if(turnedCard.equals(thisCard)) // why are you flipping and unflipping the same card
				{ // question is ambiguous; comment this if statement's body to disallow peeking
					turnedCard.setText(""); // no card is flipped
					turnedCard = null; // this card is also not flipped
					repaint();
				}
				else if(turnedCard.getText().equals(thisCard.getText())) // the cards match!
				{
					ActionListener[] tempListeners = turnedCard.getActionListeners();
					turnedCard.setBackground(GREEN);
					for(int j = 0; j < tempListeners.length; j++) // kill all action listeners including this one
						turnedCard.removeActionListener(tempListeners[j]); // shhh this is a great idea
					
					tempListeners = thisCard.getActionListeners();
					thisCard.setBackground(GREEN);
					for(int j = 0; j < tempListeners.length; j++)
						thisCard.removeActionListener(tempListeners[j]);
					
					repaint();
					turnedCard = null;
				}
				else // you dumbass there were only 6 cards how could you fuck this up
				{
					turnedCard.setBackground(RED); // make it red
					thisCard.setBackground(RED);
					repaint();

					SwingUtilities.invokeLater(() -> { // forces program to wait until repaint() is done
						try
						{
							Thread.sleep(1200);
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
						}
						
						turnedCard.setText(""); // put them back
						thisCard.setText("");
						turnedCard.setBackground(null);
						thisCard.setBackground(null);
						
						repaint();
						turnedCard = null;
					});
				}
				
				// increment the clicky thing
				setTitle(title + " - " + ++clicks);
			});
			
			buttonMap.put(tempCard, String.valueOf(cardSymbols[i])); // associate button with label
			
			// add the button to the window
			add(tempCard);
		}
		
		// I would like to see the buttons please
		setVisible(true);
		
		// close the program when you close the program
		addWindowListener(new GameWindowListener());
	}
	
	private class GameWindowListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
	    {
			// kill the program
			System.exit(0);
	    }
	}
	
	private class BetterButton extends JButton
	{
		private int code;
		
		public BetterButton()
		{
			super();
			code = System.identityHashCode(this);
		}
		
		public int hashCode()
		{
			return code;
		}
	}
}
