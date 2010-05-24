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
package com.googlecode.prmf.corleone;

import com.googlecode.prmf.corleone.connection.DisconnectListener;
import com.googlecode.prmf.corleone.connection.IOThread;
import com.googlecode.prmf.corleone.connection.Listener;
import com.googlecode.prmf.corleone.connection.MafiaListener;
import com.googlecode.prmf.corleone.connection.PingListener;
import com.googlecode.prmf.corleone.connection.SettingsFileParser;

public class Main {
	public static void main(String[] arg) {
		SettingsFileParser settings;
		try {
			settings = new SettingsFileParser("settings.txt");
			settings.parseFile();
			IOThread inputOutputThread = new IOThread(settings);
			Listener mafiaListener = new MafiaListener(settings.getBotName());
			PingListener pingListener = new PingListener();
			DisconnectListener disconnectListener = new DisconnectListener();
			inputOutputThread.addListener(mafiaListener);
			inputOutputThread.addListener(pingListener);
			inputOutputThread.addListener(disconnectListener);
			inputOutputThread.start();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
