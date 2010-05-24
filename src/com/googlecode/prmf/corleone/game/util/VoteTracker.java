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
package com.googlecode.prmf.corleone.game.util;

import com.googlecode.prmf.corleone.connection.IOThread;
import com.googlecode.prmf.corleone.game.Player;

///TODO: I'm thinking this class needs to be totally redone... the player object should probably track its own votes
//and it can report back to this when status updates are needed, or something like that

//I agree this class needs to be redone, but I disagree that the Player object should track its own votes.

public class VoteTracker {
	
	private int[] votes;
	private int noLynchVotes;
	private int numberOfPlayers;
	private Player[] players;
	
	public VoteTracker(Player[] players) {
		this.players = players;
		numberOfPlayers = players.length;
		noLynchVotes = 0;
		votes = new int[players.length];
		clearAllVotes();
	}

	private void clearAllVotes() {
		for (Player p : players)
		{
			p.setVote(-1);
		}
	}
	
	public void status(IOThread inputThread) {
		//reports all of the vote totals for any player with non-0 votes
		StringBuilder toPrint = new StringBuilder();
	
		for (int i=0;i<numberOfPlayers;++i)	{
			//each player gets his own StringBuilder, which eventually gets added to the bigun
			StringBuilder toAdd = new StringBuilder();
			
			//must check if player is alive because player can !quit is set to dead.
			//TODO: make sure comments make sense ^^^
			if (votes[i] == 0 || !players[i].isAlive()) continue;
			if (toPrint.length() > 0)
				toAdd.append(", ");
			toAdd.append(players[i].getName());
			toAdd.append(" has ");
			toAdd.append(votes[i]);
			toAdd.append(votes[i] == 1 ? " vote" : " votes");
			toPrint.append(toAdd);
		}
		//i feel so bad for noLynch, he isn't a player but gets voted for anyway :( so rude
		//at least he gets his own line on the vote status too!
		if (noLynchVotes > 0)
		{
			StringBuilder toAdd = new StringBuilder();
			if (toPrint.length() > 0)
				toAdd.append(", ");
			toAdd.append("No Lynch has ");
			toAdd.append(noLynchVotes);
			toAdd.append((noLynchVotes == 1) ? " vote" : " votes");
			toPrint.append(toAdd);
		}
		if(toPrint.length() == 0)
		{
			toPrint.append("There are currently no votes");
			
		}
		toPrint.append("."); //FULL STOP

		inputThread.sendMessage(inputThread.getChannel(), toPrint.toString());
	}
	
	// TODO probably better (i.e. OOP-ish) to pass in a Player object rather than an int voter 
	public int newVote(int voter, int voted , IOThread inputThread)
	{
		Action action = null;
		/*in Player, votes for -1 is no vote
		 * -2 is no lynch
		 *  0-# of players-1 represents the actual players
		 *  
		 *  the return types represent who was lynched: -1 is no majority, -2 is nolynch
		 *   >=0 is the player lynched
		 */

		//day.processVote(..) takes care of whether voted is valid player
		if (!players[voter].isAlive())
			return -1;
		if ( voted >= 0)
		{
			//oh gee whiz boss you wanna vote for someone?
			action = new VoteAction(voter, voted);
		}
		else if( voted == -1)
		{
			//aww boss you dont wanna vote?
			action = new UnvoteAction(voter);
		}
		else if( voted == -2)
		{
			//eh boss, nolynch isn't a very exciting vote
			action = new NoLynchAction(voter);
		}
		
		//voting is an action, so it gets an Action class associated with it.
		//then once we have decided what action class is created, the action gets performed
		//i used handle since it sounds better than perform
		//this makes the code look nicer (all the nasty specifics are buried at the bottom
		//plus everything has its own class, easier to fix and update if necessary
		//plus it makes added new things simpler, just make another action
		//also reusable, theoretically, if you need an action in multiple places
		if (action != null) //if action is null, the vote wasn't valid, so do nothing~
			action.handle();
		status(inputThread);
		return checkMajority(); //checkMajority() returns if a majority has been reached, which should end the day
	}
	
	public int checkMajority() 
	{
		int result = -1;
		int needed = 0;
		for (Player p : players)
			if (p.isAlive())
				++needed;
		needed = needed/2 + 1; //50% + 1
		if (noLynchVotes >= needed)
			result = -2; //TODO: why keep searching after something has been found?
		for (int i=0;i<numberOfPlayers;++i)
			if(votes[i] >= needed)
				result = i;	//TODO: why keep searching after something has been found?
		// i like putting 2 of the same TODOs right next to each other, makes it tough to miss ;)
		return result;
	}
	
	class UnvoteAction implements Action
	{
		private Player voter;
		public UnvoteAction(int voter)
		{
			this.voter = players[voter];
		}
		public void handle()
		{
			if(voter.getVote() >= 0)
			{
				--votes[voter.getVote()];
				//if he was voting for a player... make him not vote for a player
			}
			else if(voter.getVote() == -2)
			{
				--noLynchVotes;
				//if he was voting for noLynch... make him not vote for noLynch
			}
			voter.setVote(-1);
		}
	}
	
	class NoLynchAction implements Action
	{
		private Player voter;
		public NoLynchAction(int voter)
		{
			this.voter = players[voter];
		}
		public void handle()
		{
			if(voter.getVote() >= 0)
			{
				--votes[voter.getVote()];
				//if he was voting for a player... make him not vote for a player
			}
			//TODO: is this necessary? if his vote is already nolynch we are removing it and then adding it right back =\
			//should be removed IMO
			else if(voter.getVote() == -2)
			{
				--noLynchVotes;
				//if he was voting for noLynch... make him not vote for noLynch
			}
			++noLynchVotes;
			voter.setVote(-2);
		}
	}
	
	class VoteAction implements Action
	{
		private Player voter;
		private int target;
		public VoteAction(int voter, int voted)
		{
			this.voter = players[voter];
			this.target = voted;
		}
		public void handle()
		{
			if( voter.getVote() >= 0)
			{
				--votes[voter.getVote()];
				//if he was voting for a player... make him not vote for a player
			}
			else if(voter.getVote() == -2)
			{
				--noLynchVotes;
				//if he was voting for noLynch... make him not vote for noLynch
			}
			voter.setVote(target);
			++votes[target];
		}
	}
	
}
