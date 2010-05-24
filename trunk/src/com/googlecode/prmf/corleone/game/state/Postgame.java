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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;

import com.googlecode.prmf.corleone.connection.IOThread;
import com.googlecode.prmf.corleone.game.Game;
import com.googlecode.prmf.corleone.game.Player;
import com.googlecode.prmf.corleone.game.team.Team;

//TODO: add some more cool stuff to this class! better results, game summaries, etc; lots of ways to go with this.
public class Postgame implements MafiaGameState {
	public IOThread inputOutputThread;
	public Player[] players;

	public Postgame(IOThread inputOutputThread, Player[] players) {
		this.inputOutputThread = inputOutputThread;
		this.players = players;
		wrapUp();
	}

	public boolean receiveMessage(Game game, String line)
	{
		return true;
	}

	public void swapState(Game game, MafiaGameState newState)
	{
		game.setState(newState);
	}

	public void status()
	{
		inputOutputThread.sendMessage(inputOutputThread.getChannel(), "The game is now over");
	}

	public void allTimeResults()
	{
		try
		{
			Scanner s = new Scanner(new File("results.txt"));
			while (s.hasNextLine())
			{
				String[] singleTeamResults = s.nextLine().split("\\s+");
				inputOutputThread.sendMessage(inputOutputThread.getChannel(), String.format("%s has won %s games", singleTeamResults[0].substring(0,singleTeamResults[0].length()-1), singleTeamResults[1]));
			}
		}
		catch (FileNotFoundException e)
		{

		}
	}
	public void wrapUp()
	{
		//this prints a list of all the winners!
		// TODO declare things as what they're used not what they are
		LinkedList<Team> teamsWon = new LinkedList<Team>();
		for(Player player : getPlayerList())
		{
			Team current = player.getRole().getTeam();
			if (current.hasWon(getPlayerList()))
			{
				if(!teamsWon.contains(current))
					teamsWon.add(current);
			}
		}

		StringBuilder ret = new StringBuilder();
		ret.append("Team ");
		for(Team team : teamsWon) {
			//TODO don't + when you append()
			ret.append(team.getName() + " consisting of: " + team.getPlayers() +" has won!");
			inputOutputThread.sendMessage(inputOutputThread.getChannel(),ret.toString());
			addWin(team.getName());
		}

		LinkedList<String> playersRoles = roleReveal();
		
		String playersRolesString = "";
		for(String pr: playersRoles) {
			if(playersRolesString.length() > 0) playersRolesString += ", ";
			playersRolesString += pr;
		}
		
		inputOutputThread.sendMessage(inputOutputThread.getChannel(), playersRolesString);
		allTimeResults();
	}

	public void addWin(String teamName)
	{
		try
		{
			Scanner s = new Scanner(new File("results.txt"));
			StringBuilder toPrint = new StringBuilder();
			boolean added = false;
			while (s.hasNext())
			{
				String[] singleTeamResults = s.nextLine().trim().split("\\s+");
				singleTeamResults[0] = singleTeamResults[0].substring(0,singleTeamResults[0].length()-1);
				if (teamName.equals(singleTeamResults[0]))
				{
					added = true;
					int curWins = Integer.parseInt(singleTeamResults[1]);
					++curWins;
					singleTeamResults[1] = Integer.toString(curWins);
				}
				toPrint.append(String.format("%s: %s\n", singleTeamResults[0], singleTeamResults[1]));
			}
			if (!added)
			{
				toPrint.append(String.format("%s: %s\n", teamName, "1"));
			}
			File f = new File("results.txt");
			f.delete();
			FileWriter outFile = new FileWriter("results.txt");
			PrintWriter out = new PrintWriter(outFile);
			out.print(toPrint.toString());
			out.close();
		}
		//Gotta catch 'em all!
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public Player[] getPlayerList()
	{
		return players;
	}

	public void endState(Game game)
	{

	}

	private LinkedList<String> roleReveal()
	{
		LinkedList<String> playersRoles = new LinkedList<String>();
		for(Player p : players)
			playersRoles.add(p.getName()+" was "+ p.getRole().getName());
		return playersRoles;
	}
}
