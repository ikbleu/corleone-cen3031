//Issam Bouter
//Time panel for inputing time options for mafia game
package GUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
//class to test time panel
public class timePanel{
	public static void main(String[] args) {		
		
      JFrame frame = new JFrame();
      Container cp = frame.getContentPane();
      timeDurationPanel pane = new timeDurationPanel(frame, 8, 10);
      cp.add(pane.getPanel());

      frame.pack();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);

   }
}
class timeDurationPanel
{
	static int maxNightDuration = 30;
	static int maxDayDuration = 60;
	int dayTime, nightTime;	
	JFrame frame;
	
	//constructos
	public timeDurationPanel(JFrame Pframe, int daytime2, int nighttime2)
	{
		dayTime=daytime2;
		nightTime=nighttime2;
		frame=Pframe;
	}
	//initialize time panel and return it
	public JPanel getPanel()
	{		
		BorderLayout layout = new BorderLayout();
		//layout.setVgap(5);
		
		JPanel tpane = new JPanel(layout);	
		tpane.setSize(500, 600);
		tpane.setBackground(Color.white);
		tpane.setPreferredSize(new Dimension(300, 190));
		
		final JSlider daySlider = new JSlider();	
		daySlider.setMinimum(0);
		daySlider.setMaximum(maxDayDuration);
		daySlider.setValue(dayTime);		
		daySlider.setBackground(Color.WHITE);		
		final JLabel dayLabel = new JLabel(Integer.toString(daySlider.getValue())+" minutes");
		ChangeListener daySliderListener = new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(daySlider.getValue()>9)
					dayLabel.setText(Integer.toString(daySlider.getValue())+" minutes");
				else
					dayLabel.setText("0"+Integer.toString(daySlider.getValue())+" minutes");
			}			
		};		
		daySlider.addChangeListener(daySliderListener);				
		
		JPanel dayPane= new JPanel(new BorderLayout());
		dayPane.add(new JLabel("Duration of day time:   "), BorderLayout.WEST);
		dayPane.add(daySlider, BorderLayout.SOUTH);
		dayPane.add(Box.createVerticalStrut(20), BorderLayout.CENTER);
		dayPane.add(dayLabel, BorderLayout.EAST);
		dayPane.setBackground(Color.white);				

		final JSlider nightSlider = new JSlider();		
		nightSlider.setMinimum(0);
		nightSlider.setMaximum(maxNightDuration);
		nightSlider.setValue(nightTime);	
		nightSlider.setBackground(Color.WHITE);		
		final JLabel nightLabel = new JLabel(Integer.toString(nightSlider.getValue())+" minutes");
		ChangeListener nightSliderListener = new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(nightSlider.getValue()>9)
					nightLabel.setText(Integer.toString(nightSlider.getValue())+" minutes");
				else
					nightLabel.setText("0"+Integer.toString(nightSlider.getValue())+" minutes");
			}			
		};		
		nightSlider.addChangeListener(nightSliderListener);
		
		JPanel nightPane= new JPanel(new BorderLayout());
		nightPane.add((new JLabel("Duration of night time: ")), BorderLayout.WEST);
		nightPane.add(nightSlider, BorderLayout.SOUTH);
		nightPane.add(Box.createVerticalStrut(20), BorderLayout.CENTER);
		nightPane.add(nightLabel, BorderLayout.EAST);
		nightPane.setBackground(Color.white);
		
		JPanel tempPane = new JPanel();
		tempPane.setBackground(Color.white);
		tempPane.add(dayPane);
		tempPane.add(nightPane);
		
		JLabel headerLabel = new JLabel("      Time Options");
		JPanel headerPane = new JPanel(new BorderLayout());
		headerPane.add(Box.createVerticalStrut(40));		
		headerPane.add(headerLabel, BorderLayout.WEST);
		headerPane.setBackground(Color.white);
		tpane.add(headerPane, BorderLayout.PAGE_START);
		tpane.add(tempPane, BorderLayout.CENTER);
		
		JButton okButton = new JButton("Ok");
		ActionListener ButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{ 	
				dayTime=daySlider.getValue();
				nightTime=nightSlider.getValue();
				frame.setVisible(false);				
			}			
		};
		
		okButton.addActionListener(ButtonListener);
		
		
		JPanel okPane = new JPanel();
		okPane.setBackground(Color.white);
		okButton.setPreferredSize(new Dimension(70, 23));
		okPane.add(okButton);
		okPane.add(Box.createVerticalStrut(40));
		tpane.add(okPane, BorderLayout.PAGE_END);		
		
		return tpane;
	}
	//get dayTime
	public int getDayDuration()
	{
		return dayTime;
	}
	//get nightTime
	public int getNightDuration()
	{
		return nightTime;
	}	
	//get dayTime
	public void setDayDuration(int time)
	{
		dayTime=time;
	}
	//get nightTime
	public void setNightDuration(int time)
	{
		nightTime=time;
	}
}