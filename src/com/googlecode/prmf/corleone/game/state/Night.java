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
package com.googlecode.prmf.corleone.game.state;

import com.googlecode.prmf.corleone.connection.IOThread;
import com.googlecode.prmf.corleone.game.Game;
import com.googlecode.prmf.corleone.game.Player;


public class Night implements MafiaGameState
{
	private Player[] players;
	private IOThread inputOutputThread;

	public Night(Player[] players, IOThread inputOutputThread)
	{
		this.players = players;
		this.inputOutputThread = inputOutputThread;
	}

	//	TODO add a timer to night
	public boolean receiveMessage(Game game, String line)
	{
		String[] splitLine = line.split(" ");
		String speaker = splitLine[0].substring(1,line.indexOf("!"));
		if(splitLine[1].startsWith("NICK") )
		{
			changeNick(speaker,splitLine[2]);
			return false;
		}
		//what why is this here ?_?
		if (isNightOver())
			return true;

		//temporary bad solution until we get around to overhauling the command system

		if (splitLine.length <= 4)
			return false;

		String action = splitLine[3];
		String target = splitLine[4];



		Player speaking = null;
		for (Player p : players)
		{
			if (p.equals(new Player(speaker)))
			{
				speaking = p;
				break;
			}
		}
		if (speaking == null)
			return false;

		//TODO: just send the night action to the role, don't ask if it has one and then send~
		if (speaking.getRole().hasNightAction())
		{
			boolean result = speaking.getRole().nightAction(action + " " + target, players);
			inputOutputThread.sendMessage(speaking.toString(),
					(result?"You successfully targetted " + target:"Your night action did not resolve"));

		}
		boolean isOver = isNightOver();
		if (isOver)
		{
			endState(game);
		}
		return isOver;
	}

	public void introduction() {
		for(Player player : players) {
			if(player.isAlive()) {
				inputOutputThread.sendMessage(player.getName(), player.getRole().description());
			}
		}
	}

	public boolean isNightOver() {
		boolean result = true;
		for(Player p: players)
			if(p.isAlive() && !p.getRole().didNightAction())
			{
				result = false;
				break;
			}
		return result;
	}

	public void resolveNightActions()
	{
		for (Player p : players)
		{
			String result = p.getRole().resolveNightAction();
			inputOutputThread.sendMessage(p.toString(), result);
		}
	}

	//TODO: turn this into something Player does in endNight perhaps
	public void resetActions()
	{
		for (Player p : players)
			p.getRole().resetNightAction();
	}

	//TODO: combine with resetActions{} somehow
	public void resetLives()
	{
		for (Player p : players)
		{
			p.endNight();
		}
	}

	public void cleanUp()
	{
		resetActions();
		results();
		resetLives();
	}

	public void status()
	{
		inputOutputThread.sendMessage(inputOutputThread.getChannel(), "It is now night!");
		//TODO: report time left
	}

	public void results()
	{
		for (Player p : players)
		{
			if (p.getNightLives() == 0 && p.getTargetted())
			{
				inputOutputThread.sendMessage(p.toString(), "You were saved!");
			}
			if (p.getNightLives() < 0)
			{
				inputOutputThread.sendMessage(inputOutputThread.getChannel(), p + " was killed during the night!");
			}
		}
	}

	private void changeNick(String oldNick , String newNick)
	{
		System.err.println(oldNick + " to " + newNick);
		for(int i=0;i<players.length;++i)
		{
			if(players[i].getName().equals(oldNick))
			{
				players[i].setName(newNick.substring(1));
				return;
			}
		}
	}

	public void endState(Game game)
	{
		resolveNightActions();
		cleanUp();
		for(Player p : game.getPlayerList())
		{
			if(p.isAlive())
				inputOutputThread.sendMessage("MODE",inputOutputThread.getChannel(), "+v "+p.getName());
		}
		game.setState(new Day(players, inputOutputThread));

		if(!game.isOver())
			game.startTimer();
	}
}
