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
	{System.out.println("ENTERED allTimeResults()!!!!!!\n\n\n\n");
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
	
	// -------------------------------------------------------------------------------------------------------------
	// YAWDIE INVASION: the method below is the beginning of the updated scoring system. Eventually,
	// the goal is to merge pieces of the allTimeResults() method here to facilitate more modular
	// scoring methods, depending on whether the scoring applies to a team, a person, a role, etc.
	// -------------------------------------------------------------------------------------------------------------
	
	// players = all players from that game
	// in wrapUp():
	//		each person is checked for the team they're on
	//		based on that team, they check to see if that team has won
	//		if that team has won, they're added to the LinkedList teamsWon if they're not already in it
	// storing winning and losing players in case losing players may factor in later for statistics or
	// other such reasons.
	
	public void generatePlayerScores()
	{System.out.println("ENTERED GENERATEPLAYERSCORES!!!!\n\n\n\n\n");
		LinkedList< Player > winningPlayers = new LinkedList< Player >();
		LinkedList< Player > losingPlayers = new LinkedList< Player >();
		LinkedList< Player > playerList = buildFinalPlayerCount();
		int numPlayers = playerList.size();
		Player selectedPlayer;
		
		// NOTE: use an iterator here
		for( int i = 0; i < numPlayers; ++i )
		{
			selectedPlayer = playerList.get( i );
			
			if( hasPlayerWon( selectedPlayer ) )
				winningPlayers.add( selectedPlayer );
			else
				losingPlayers.add( selectedPlayer );
		}
		
		recordWinningPlayerScores( winningPlayers );
		// need to add sorter that will sort players in accordance to who has highest number of wins
		// and also how to handle sorting of ties for one position (like 3 people tying for first place,
		// four for second place, three for third place, etc.)
	}
	
	// this class increments the win total of the current players based off of a player's name and
	// also adds the player to the list if that player was not already present in the list
	// currently only storing scores in the form of PLAYER_NAME SCORE on separate lines.
	// I have not yet implemented a sorted ranking of scores (1st place, 2nd place, etc.)
	// ... and make this method more efficient. seriously.
	private void recordWinningPlayerScores( LinkedList< Player > winningPlayers )
	{System.out.println("ENTERED recordWinningPlayerScores()!!!!!!\n\n\n\n");
		try
		{
			Scanner scn = new Scanner( new File( "playerScores.txt" ) );
			
			LinkedList< String > playerNames = new LinkedList< String >();
			LinkedList< Integer > playerScores = new LinkedList< Integer >();
			int numPlayersWonThisGame = winningPlayers.size();
			int numStoredPlayers = 0;
			
			String currentLine = "";
			String[] currentLineSplit = new String[ 2 ];
			for( int i = 0; scn.hasNextLine(); ++i )
			{
				currentLine = scn.nextLine();
				if( currentLine.equals( "" ) )
					break;
				currentLineSplit = currentLine.split( " " );
				playerScores.add( Integer.parseInt( currentLineSplit[ 0 ] ) );
				playerNames.add( currentLineSplit[ 1 ] );
			}
			
			scn.close();
			
			numStoredPlayers = playerNames.size();
			boolean playerFound = false;
			
			for( int i = 0; i < numPlayersWonThisGame; ++i )
			{
				playerFound = false;
				
				for( int j = 0; j < numStoredPlayers; ++j )
				{
					if( playerNames.get( j ).equals( winningPlayers.get( i ).getName() ) )
					{
						playerScores.set( i, ( playerScores.get( j ) ) + 1 );
						playerFound = true;
					}
				}
				
				if( !playerFound ) // adding new player with their score of 1
				{
					playerNames.add( winningPlayers.get( i ).getName() );
					playerScores.add( 1 );
				}
			}
			
			// by this point, playerNames and playerScores contain the up-to-date names and scores
			// for the scoreboard, ready to be written to the playerScores.txt file on the server.
			// write the scores to the appropriate file on the server. separate this into a separate method.
			// you're going to rewrite all the names with their updated corresponding scores.
			// make it more efficient later.
			
			writeWinningPlayerScores( playerNames, playerScores );
		}
		catch( Exception e )
		{ e.printStackTrace(); }
	}
	
	// reason for existance: to write the generated player scores, with corresponding usernames,
	// to playerScores.txt
	private void writeWinningPlayerScores( LinkedList< String > playerNames, LinkedList< Integer > playerScores )
	{System.out.println("ENTERED writeWinningPlayerScores()!!!!!!\n\n\n\n");
		try
		{
			File file = new File( "playerScores.txt" );
			file.delete();
			PrintWriter out = new PrintWriter( new FileWriter( "playerScores.txt" ) );
			
			// sort scores here
			
			for( int i = 0; i < playerNames.size(); ++i )
				out.print( playerScores.get( i ) + " " + playerNames.get( i ) + "\n" );
			
			out.close();
			System.out.println("PLAYER SCORES HAVE BEEN WRITTEN \n\n\n\n\n");
		}
		catch( Exception e )
		{ System.out.println("ERROR WRITING PLAYER SCORES \n\n\n\n\n");}
	}
	
	// reason for existance: to get final, accurate count of the number of players due to uncertainties
	// about player list by the end of the match (like disconnects and the like)
	private LinkedList< Player > buildFinalPlayerCount()
	{System.out.println("ENTERED buildFinalPlayerCount()!!!!!!\n\n\n\n");
		LinkedList< Player > finalListOfPlayers = new LinkedList< Player >();
		
		for( Player player : getPlayerList() )
		{
			if( player != null )
				finalListOfPlayers.add( player );
		}
		
		return finalListOfPlayers;
	}
	
	// NOTE: develop a more efficient way of determining which team has won and which team has lost...
	// namely have it calculate the winning and losing teams just once, then be able to check very
	// fast just "which team does this player belong to?" and know yes or no without having to
	// recalculate whether that team won or lost. This appleis to hasPlayerWon( Player player ).
	// Until then, this method will do for now.
	private boolean hasPlayerWon( Player player )
	{System.out.println("ENTERED hasPlayerWon()!!!!!!\n\n\n\n");
		Team teamPlayerIsOn = player.getTeam();
		
		if( teamPlayerIsOn.hasWon( getPlayerList() ) )
			return true;
		else
			return false;
	}
	
	// -------------------------------------------------------------------------------------------------------------
	// END YAWDIE INVASION
	// -------------------------------------------------------------------------------------------------------------
	
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
		
		// ------------------------------------------------------------------------------------------
		// START YAWDIE INVASION
		// ------------------------------------------------------------------------------------------
		
		generatePlayerScores();
		
		// ------------------------------------------------------------------------------------------
		// END YAWDIE INVASION
		// ------------------------------------------------------------------------------------------
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
