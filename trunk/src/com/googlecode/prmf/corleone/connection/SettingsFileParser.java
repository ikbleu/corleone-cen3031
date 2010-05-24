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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Scanner;

public class SettingsFileParser {
	private Scanner in;
	private String channel;
	private String botName;
	private String server;
	private int port;

	public SettingsFileParser(String filename) throws FileNotFoundException {
		this.in = new Scanner(new FileInputStream(new File(filename)));
	}
	
	public SettingsFileParser() throws FileNotFoundException {
		this("settings.txt");
	}

	public void parseFile() throws ParseException {
		int numSettings = 0;
		while(in.hasNextLine()) {
			String line = in.nextLine();

			// TODO do the following using a regular expression!
			int start = 0;
			for(start = 0; start < line.length(); ++start) {
				if(line.charAt(start) != ' ')
					break;
			}
			if(line.charAt(start) == '#')
				continue;
			
			String setting[] = line.split("=");
			for(int i = 0; i < setting.length; ++i)
				setting[i] = setting[i].trim();

			if(setting[0].equalsIgnoreCase("server")) {
				server = setting[1];
			}
			else if(setting[0].equalsIgnoreCase("nick")) {
				botName = setting[1];
			}
			else if(setting[0].equalsIgnoreCase("port")) {
				port = Integer.parseInt(setting[1]);
			}
			else if(setting[0].equalsIgnoreCase("channel")) {
				channel = setting[1];
			}
			else {
				throw new ParseException("Settings file corrupt", numSettings);
			}
			++numSettings;
		}
		if(numSettings != 4) {
			throw new ParseException("Settings file corrupt", numSettings);
		}
		//System.err.println(botName + " " + channel + " " + port + " " + server);
	}

	public String getBotName() {
		return botName;
	}

	public String getChannel() {
		return channel;
	}

	public int getPort() {
		return port;
	}

	public String getServer() {
		return server;
	}
}
