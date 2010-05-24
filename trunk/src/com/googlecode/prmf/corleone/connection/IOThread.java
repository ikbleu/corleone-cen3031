/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package com.googlecode.prmf.corleone.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IOThread extends Thread {
	private Socket soc;
	private InputStream inputStream; 
	private List<Listener> list;
	private PrintStream printStream; 
	private String channel;
	private String botName;
	private SettingsFileParser settings;
	
	public IOThread(SettingsFileParser settings) throws Exception {
		this.settings = settings;
		this.setName("I/O"); //TODO use super() constructor to do this
		makeSocket();
		inputStream = soc.getInputStream(); //TODO be consistent, qualify everything with "this"
		printStream = new PrintStream(soc.getOutputStream()); //TODO be consistent, qualify everything with "this"
		list = new ArrayList<Listener>(); //TODO be consistent, qualify everything with "this"
		this.channel = settings.getChannel();
		this.botName = settings.getBotName();
		
	}
	
	@Override
	public void run() {
		printStream.println("NICK " + this.botName); 
		printStream.println("USER " + this.botName + " 12 * " + this.botName);
		printStream.println("JOIN " + channel);
		Scanner in = new Scanner(inputStream);
		while(in.hasNextLine()) {
			String line = in.nextLine();
			System.out.println("<<<< " + line);
			for(Listener l : list)
				l.receiveLine(line, this);
		}
	}
	
	public void ceaseTimer() {
		for(Listener l: list)
			l.timerMessage();
	}
	
	public void addListener(Listener listener) {
		list.add(listener);
	}
	
	// TODO reimplement this method using the other sendMessage()
	public void sendMessage(String destination, String message) {
		printStream.println("PRIVMSG "+destination+" :"+message);
		System.out.println(">>>> " + "PRIVMSG "+destination+" :"+message);
	}
	
	// TODO rename this method; current name is inaccurate
	public void sendMessage(String command, String destination, String message) {
		printStream.println(command+" "+destination+" "+message);
		System.out.println(">>>> " + command+" "+destination+" "+message);
	}
	
	// TODO reimplement this method using sendMessage()
	public void sendPONG(String[] msg) {
		String concat = "PONG";
		for(int i = 1; i < msg.length; ++i)
			concat += " " + msg[i];
		
		printStream.println(concat);
		System.out.println(">>>> " + concat);
	}
	
	public String getChannel() {
		return channel;
	}

	public SettingsFileParser getSettings() {
		return settings;
	}

	private void makeSocket() throws IOException {
		//TODO this could even go directly in the constructor...
		soc = new Socket(getSettings().getServer(), getSettings().getPort());
	}
}
