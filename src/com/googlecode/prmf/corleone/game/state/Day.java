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
import com.googlecode.prmf.corleone.game.util.Action;
import com.googlecode.prmf.corleone.game.util.VoteTracker;

public class Day implements MafiaGameState {
	//TODO why are there non-private fields here?
	VoteTracker tracker;
	Player[] players;
	boolean killed; // if anyone was killed the previous night
	String dead; // who was killed , if anyone;
	IOThread inputOutputThread;
	//TODO: Day, and other classes, have inputOutputThreads, inputThreads, and IOThreads. we need to pick a name and keep it
	//TODO better yet, they shouldn't all keep a reference to it if you ask me

	public Day(Player[] players, IOThread inputThread) {
		tracker = new VoteTracker(players);
		this.players = players;
		this.inputOutputThread = inputThread;
	}
	public boolean receiveMessage(Game game, String line) {
		boolean ret = false;
		String speaker = line.substring(1, line.indexOf("!"));
		String[] msg = line.split(" ");

		if(msg[1].startsWith("NICK") )
		{
			changeNick(speaker,msg[2]);
			return false;
		}

		int returnCode = parseMessage(line, speaker);
		//TODO why do we have this part in a separate method? =\
		if(returnCode >= 0) {
			inputOutputThread.sendMessage(inputOutputThread.getChannel(), players[returnCode] + " was lynched :(");
			players[returnCode].setAlive(false);
			players[returnCode].setCauseOfDeath(Player.causesOfDeath.LYNCH);
			ret = true;
		}
		else if(returnCode == -2) {
			inputOutputThread.sendMessage(inputOutputThread.getChannel(), "The majority has voted for no lynching today!");
			ret = true;
		}
		else if(returnCode == -1) {
			ret = false;
		}
		else if(returnCode == -3) {
			inputOutputThread.sendMessage(inputOutputThread.getChannel(), "What?");
			ret = false;
		}
		else if(returnCode == -4) {
			inputOutputThread.sendMessage(inputOutputThread.getChannel(), speaker + " has been removed from the game!");
		}
		// Must handle all cases of parseMessage return such as -3,-2,-1, >=0

		// TODO actually, it would be better to use enums
		// http://java.sun.com/docs/books/tutorial/java/javaOO/enum.html
		if (ret)
		{
			endState(game);
		}
		return ret;
	}

	//get ID number of the player for syncing with votes
	private int searchPlayers(String name) {
		int ret = -3;

		// TODO replace with the sexier for-each syntax
		for(int i = 0; i < players.length; ++i) {
			if(players[i].getName().equals(name)) {
				ret = i;
				break;
			}
		}
		return ret;
	}
	private int parseMessage(String instruc, String speaker)
	{
		// TODO this method looks like a perfect application of Java enums
		//TODO listen to ^^^
		int ret = -3; //this is the default "nothing happens" return.
		String[] instrucTokens = instruc.split(" ",5); //split into parts, parts being speaker, type of action, place it was sent to, etc
		String command = instrucTokens[3]; //command is "lynch" "unvote" etc
		String target=""; //target of lynch etc
		if(command.equals(":~lynch"))
		{
			if(instrucTokens.length >= 5)
				target = instrucTokens[4];
			else
				return -3; //no lynch target = bad vote
		}
		//extract the player and the target from the message
		int speakerId = searchPlayers(speaker);
		int targetId = searchPlayers(target);

		//should be impossible though
		if(speakerId == -3)
			return -3;

		// TODO I hope you realize there are better ways to do this than a bunch of if/else statements.
		//TODO: change this to use the Class.forName() method. I think that's a much nicer solution than the one we have
		//change this to be similar to Pregame's receiveMessage()
		if( command.equals(":~lynch") )
		{ 
		  if(players[speakerId].gettup() == true || players[targetId].getwind() == false)
		  {	  
			  ret = processVote(speakerId, targetId);
			  int e = players[speakerId].getexp() + 25;
			  inputOutputThread.sendMessage(players[speakerId].getName(), "You gained 25 exps from this action!");
			  players[speakerId].setexp(e);
			  if(players[speakerId].getexp() >= 100)
			  {	
			     int tmp = players[speakerId].getlevel() + 1;
			     players[speakerId].setlevel(tmp);
			     players[speakerId].setexp(0);
			     inputOutputThread.sendMessage(players[speakerId].getName(), "Congratulations! You have leveled up! Note: Any superabundance experience points will be dropped after a game finished");
			  }  
		  }
		  else
		  {
			     inputOutputThread.sendMessage(players[speakerId].getName(), "Your target: " + players[targetId].getName()+ "is protecting by a unknown extreme holy power........lynch failure");
		  }
		}
		else if( command.equals(":~nolynch") )
		{
			ret = processVote(speakerId, -2);

		}
		else if( command.equals(":~unvote") )
		{
			ret = processVote(speakerId, -1);
		}
		else if (command.equals(":~states"))
		{
			inputOutputThread.sendMessage(players[speakerId].getName(), "You are: " + players[speakerId].getName() + "and your level is "+ players[speakerId].getlevel() + " You current have: " + players[speakerId].getitems() + "items and " + players[speakerId].getap() + " action points!");
		}
		else if (command.equals(":~items"))
		{
			inputOutputThread.sendMessage(players[speakerId].getName(), "You have three items which can be used right now, some of them will become your powerful weapon, the other will be the solid shield. So use them wisely in order to win the game! For details about what is and how to use those items, please type: Command :~infor01 for first item; Command :~infor02 for first item; Command :~infor03 for first item;");
		}
		else if (command.equals(":~infor01"))
		{
			inputOutputThread.sendMessage(players[speakerId].getName(), "[Wind of Guardian Angel] >>> an item cost 60 action points to use; it can be used before the voting process, which means it can protect you from any voting kill in game. But the player has to determine to use it or not, you will have only one change during a game. Type command ~wga to use this item");
		}
		else if (command.equals(":~infor02"))
		{
			inputOutputThread.sendMessage(players[speakerId].getName(),"[Gate of two worlds] >>> an item cost 60 action points to use; When the player died because of a voting process, and before the win/loss determination, that player can use this item to reenter the game. Note: if that player died because of quit command, he or she would not be able to use this item. Type command ~up to use this item");
		}
		else if (command.equals(":~infor03"))
		{
			inputOutputThread.sendMessage(players[speakerId].getName(), "[Unbeatable power] >>> an item cost 60 action points to use. It can be used before the voting process, and it can ignore the effect of “Wind of Guardian Angel”, and still can let that target player be killed. Note: the game still need majority voted here. Type command ~gow to use this item");
		}
		else if (command.equals(":~wga"))
		{
			if(players[speakerId].getap() >= 60)
			{	
			   players[speakerId].setwind(true);
			   inputOutputThread.sendMessage(players[speakerId].getName(), "You are protecting by a extreme holy power!....");
			}
			else
			{
				inputOutputThread.sendMessage(players[speakerId].getName(), "No enough AP to use this item!");
			}
		}
		else if (command.equals(":~up"))
		{
			if(players[speakerId].getap() >= 60)
			{
			   players[speakerId].settup(true);
			   inputOutputThread.sendMessage(players[speakerId].getName(), "You gained a unknown Unbeatable Power!....");
			} 
			else
			{
				inputOutputThread.sendMessage(players[speakerId].getName(), "No enough AP to use this item!");
			}
		}
		else if (command.equals(":~gow"))
		{
			if(players[speakerId].getap() >= 60)
			{
			    players[speakerId].setAlive(true);
				tracker.status(inputOutputThread);
			    inputOutputThread.sendMessage(inputOutputThread.getChannel(),"Look at that! @ @ ......"+ players[speakerId].getName()+ "comes back to the battel!");
			}  
			else
			{
				inputOutputThread.sendMessage(players[speakerId].getName(), "No enough AP to use this item!");
			}
		}
		else if( command.equals(":~quit") )
		{
			//TODO: check game status after this, make sure game isn't over
			//kill speaker
			players[speakerId].setAlive(false);
			tracker.status(inputOutputThread);
			ret = -4;
		}

		return ret;
	}

	private int processVote(int voter, int voted)
	{
		/** int voted values:
		 *  -4 , player quit command ..
		 *  -3 , voted player does not exist
		 *  -2 , vote to nolynch
		 *  -1 , command to retract vote
		 *  i>=0 , player ID
		 */

		//check to make sure voted name exists
		if(voted == -3)
			return -3;

		return tracker.newVote( voter, voted , inputOutputThread);
		/**return values:   -3 , bad vote, not processed
		 * 					-2, +1 nolynch
		 * 					-1 , no majority
		 * 					 i>=0  , i lynched.
		 */
	}

	public void status() {
		inputOutputThread.sendMessage(inputOutputThread.getChannel(), "It is now day! Notice: you can only use your item during day time");
		
		for(int i = 0; i < players.length; i++)
		{
			players[i].setap(180);
			players[i].settup(false);
			players[i].setwind(false);
		}

		//gives a list of players
		if(players.length >= 1)
			inputOutputThread.sendMessage(inputOutputThread.getChannel(), "The following people are still alive:");
		else
		{
			//TODO: wtf, how is this even possible? someone copy/pasted this from pregame i think. fix it.
			inputOutputThread.sendMessage(inputOutputThread.getChannel(), "There is no one registered yet!");
			return;
		}

		String livingPeople = "";
		for(Player p: players) {
			if(p.isAlive()) {
				if(livingPeople.length() > 0) livingPeople += ", ";
				livingPeople += p.getName();
			}
		}

		inputOutputThread.sendMessage(inputOutputThread.getChannel(), livingPeople);
	}

	class LynchAction implements Action {

		private int voter;
		private int voted;
		public LynchAction(int voter, int voted)
		{
			this.voter=voter;
			this.voted=voted;
		}
		public void handle() {
			//just pass the vote to the tracker
			tracker.newVote(voter, voted, inputOutputThread);
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
		game.setState(new Night(players, inputOutputThread));
		for(Player p : game.getPlayerList())
		{
			//unvoice the players since NO TALKING DURING THE NIGHT
			inputOutputThread.sendMessage("MODE",inputOutputThread.getChannel(), "-v "+p.getName());
		}

		if(!game.isOver())
			game.startTimer();
	}
}


