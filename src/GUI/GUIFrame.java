//Issam Bouter
//Front end MAFIA container
package GUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

public class GUIFrame 
{
	static ActionListener CloseMenuListener, TimeOptionsListener;
	static Container cp;
	static JPanel frontPane;
	static timeDurationPanel timepane=null;
	static int day=0;
	static boolean MenuOpen=false;
	//initializes frame and all subcomponents of frame, puts the front end on the frame and displays it
	public static void main(String[] args) 
	{		
	      JFrame frame = new JFrame();
	      frame.setPreferredSize(new Dimension(800, 715));
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
	static int delay = 1000; //milliseconds
	static ActionListener taskPerformer;
	static JProgressBar progBar;
	static int minutes, seconds;
	static JLabel timerLabel = new JLabel();
	static int daytime=2;
	static int nighttime=30;
    //Creates and adds progresspane to bottom of frame
	private static void addProgressPane() 
	{		
		JPanel ProgressPane = new JPanel(new BorderLayout());
		ProgressPane.setPreferredSize(new Dimension(780, 40));
		
		timerLabel = new JLabel(" Time: "+minutes+":0"+seconds);
		minutes=0;
		seconds=0;
		
		JPanel barPane = new JPanel();
		progBar = new JProgressBar();
		progBar.setMinimum(0);
		progBar.setMaximum(100);
		progBar.setValue(00);
		progBar.setString("           Welcome To The Corleone Project                  ");
		progBar.setStringPainted(true);
		
		taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				updateTime();
				updateProgressBar();
		    }
		};
		new Timer(delay, taskPerformer).start();
	//	barPane.setPreferredSize(new Dimension(800, 400));
		barPane.add(progBar);
		barPane.setBackground(Color.LIGHT_GRAY);
		ProgressPane.setBackground(Color.LIGHT_GRAY);
		ProgressPane.add(Box.createVerticalStrut(5), BorderLayout.NORTH);
		ProgressPane.add(timerLabel, BorderLayout.WEST);
		ProgressPane.add(barPane, BorderLayout.CENTER);		
		frontPane.add(ProgressPane);
		
	}
	private static void updateTime() 
	{		
		if(seconds<10)
		{
			timerLabel.setText(" Time: "+minutes+":0"+seconds);
		}
		else
		{
			timerLabel.setText(" Time: "+minutes+":"+seconds);
		}
		seconds+=1;
		if(seconds>=60)
		{
			minutes+=1;
			seconds=0;
		}
	}
	static int currentMinutesDisplayed = -1;
	static int currentSecondsLeft = -1;
	static int percentagePlayed;
	static int intermission = 5;
	private static void updateProgressBar() 
	{		
		if(MenuOpen)
		{
			if(timepane.getDayDuration()==0)
				return;
			else
			{
				daytime=timepane.getDayDuration();
				nighttime=timepane.getNightDuration();
			}
		}		
		if(progBar.getValue()==100)
		    {
			day++;
			if(day>3)
				day=0;
			seconds=0;
			minutes=0;
		    }
		if(currentSecondsLeft!=seconds)
		{			
			currentMinutesDisplayed=minutes;
			
			if(day==0 || day==2)
			{
				percentagePlayed=(int)((double)seconds/(intermission)*100);
				progBar.setString("                         "+(intermission-seconds)+" seconds remaining.....                         ");
				progBar.setValue(percentagePlayed);
			}
			if(day==1)
			{	
				percentagePlayed=(int)((double)(seconds+minutes*60)/(daytime*60)*100);
				if(currentMinutesDisplayed<10)
				{														
					progBar.setString("             "+(daytime-currentMinutesDisplayed)+" minutes of day time left                  ");
					progBar.setValue(percentagePlayed);
				}
				else
					progBar.setString("            "+(daytime-currentMinutesDisplayed)+" minutes of day time left                  ");
					progBar.setValue(percentagePlayed);
			}
			if(day==3)
			{			
				percentagePlayed=(int)((double)(seconds+minutes*60)/(nighttime*60)*100);
				if(currentMinutesDisplayed<10)
				{														
					progBar.setString("             "+(nighttime-currentMinutesDisplayed)+" minutes of night time left                  ");
					progBar.setValue(percentagePlayed);
				}
				else
					progBar.setString("            "+(nighttime-currentMinutesDisplayed)+" minutes of night time left                  ");
					progBar.setValue(percentagePlayed);
			}
	
		}				
	}
    //Add game play to middle of frame
	private static void addGUI() 
	{
		JPanel guiPane = new JPanel();
		guiPane.setPreferredSize(new Dimension(800, 600));
		guiPane.setBackground(Color.white);				
		
		//Panel guiPane= new imagePanel();
		guiPane.add(new JLabel(new ImageIcon("images//test.png")));			
		//guiPane.add(imageLabel);
		
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
				 MenuOpen=true;
				 JFrame frame = new JFrame();
			     Container cp = frame.getContentPane();
			     timepane = new timeDurationPanel(frame, daytime, nighttime);			     
			     cp.add(timepane.getPanel());

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

class imagePanel extends Panel 
{
	BufferedImage  image;
	public imagePanel() 
	{
		try 
		{
			System.out.println("Enter image name\n");
			BufferedReader bf=new BufferedReader(new 
			InputStreamReader(System.in));
			String imageName=bf.readLine();
			File input = new File(imageName);
			image = ImageIO.read(input);
		} 
		catch (IOException ie) 
		{
			System.out.println("Error:"+ie.getMessage());
		}
	}

  public void paint(Graphics g) 
  {
	  g.drawImage( image, 0, 0, null);
  }
}