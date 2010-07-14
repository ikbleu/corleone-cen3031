//Issam Bouter
//Front end MAFIA container
package GUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class GUIFrame 
{
	static ActionListener CloseMenuListener, TimeOptionsListener;
	static Container cp;
	static JPanel frontPane;
	//initializes frame and all subcomponents of frame, puts the front end on the frame and displays it
	public static void main(String[] args) 
	{		
	      JFrame frame = new JFrame();
	      frame.setPreferredSize(new Dimension(800, 600));
	      cp = frame.getContentPane();
	      
	      initMenuListeners(frame);
	      addMenu(frame);	      
	      frontPane = new JPanel();
	      addGUI();
	      addProgressPane();
	     
	      cp.add(frontPane);
	      frame.pack();
	      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      frame.setVisible(true);
	}
    //Creates and adds progresspane to bottom of frame
	private static void addProgressPane() 
	{		
		JPanel ProgressPane = new JPanel(new BorderLayout());
		ProgressPane.setPreferredSize(new Dimension(800, 130));
		ProgressPane.add(new JLabel("Progress Panel"));
		ProgressPane.setBackground(Color.LIGHT_GRAY);
		
		JPanel barPane = new JPanel();
		JProgressBar progBar = new JProgressBar();
		progBar.setMinimum(0);
		progBar.setMaximum(100);
		progBar.setValue(50);
		progBar.setString("                23 minutes of daytime left                  ");
		progBar.setStringPainted(true);
	//	barPane.setPreferredSize(new Dimension(800, 400));
		barPane.add(progBar);
		barPane.setBackground(Color.LIGHT_GRAY);
		ProgressPane.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
		ProgressPane.add(barPane, BorderLayout.CENTER);
		frontPane.add(ProgressPane);
		
	}
    //Add game play to middle of frame
	private static void addGUI() {
		JPanel guiPane = new JPanel();
		guiPane.setPreferredSize(new Dimension(800, 470));
		guiPane.setBackground(Color.white);
		guiPane.add(new JLabel("Game...."));
		frontPane.add(guiPane);
		
	}
    //Initialize menu listeners
	private static void initMenuListeners(final JFrame frame) 
	{
		  CloseMenuListener = new ActionListener() 
		  {
			public void actionPerformed(ActionEvent e) 
			{ 					
				frame.setVisible(false);				
			}			
		  };
		  TimeOptionsListener = new ActionListener() 
		  {
			public void actionPerformed(ActionEvent e) 
			{ 				
				 int x=frame.getX();
				 int y=frame.getY();
				 
				 JFrame frame = new JFrame();
			     Container cp = frame.getContentPane();
			     timeDurationPanel pane = new timeDurationPanel(frame);
			     cp.add(pane.getPanel());

			     frame.pack();
			     frame.setTitle("Time Options");
			     frame.setLocation(x+170, y+170);
			     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			     frame.setVisible(true);				
			}			
		  };
	}
    //initialize and set menu bar 
	private static void addMenu(JFrame frame) 
	{
		  //Init MenuBar 
	      JMenuBar menuBar =  new JMenuBar();
	      //Build the menu.
	      JMenu menu = new JMenu("File");
	      menu.setMnemonic(KeyEvent.VK_F);
	      //menu.getAccessibleContext().setAccessibleDescription("The only m");
	      JMenuItem menuItem = new JMenuItem("Close"/*, new ImageIcon("images/middle.gif")*/);
	      menuItem.setMnemonic(KeyEvent.VK_C);
	      menuItem.addActionListener(CloseMenuListener);
	      menu.add(menuItem);
	      menuBar.add(menu);
	      menu = new JMenu("Options");
	      menu.setMnemonic(KeyEvent.VK_O);
	      //menu.getAccessibleContext().setAccessibleDescription("The only m");
	      menuItem = new JMenuItem("Time Options"/*, new ImageIcon("images/middle.gif")*/);
	      menuItem.setMnemonic(KeyEvent.VK_T);
	      menuItem.addActionListener(TimeOptionsListener);
	      menu.add(menuItem);
	      menuBar.add(menu);
	      menu = new JMenu("Help");
	      menu.setMnemonic(KeyEvent.VK_H);
	      //menu.getAccessibleContext().setAccessibleDescription("The only m");
	      menuItem = new JMenuItem("About"/*, new ImageIcon("images/middle.gif")*/);
	      menuItem.setMnemonic(KeyEvent.VK_A);
	      menu.add(menuItem);
	      menuBar.add(menu);
	      
	      frame.setJMenuBar(menuBar);
		
	}
}
