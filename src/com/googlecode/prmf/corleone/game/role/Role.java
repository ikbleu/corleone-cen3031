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
package com.googlecode.prmf.corleone.game.role;

import com.googlecode.prmf.corleone.game.Player;
import com.googlecode.prmf.corleone.game.team.Team;

public abstract class Role {
	private Team team;
	private String name;
	private Player target;
	private boolean didNightAction;

	// TODO implement some sort of priority system, so the bot knows which roles' night actions come first
	// aren't they technically simultaneous?
	// eh i dont think so... if agent alters someone, that needs to happen before cop checks right?
	public boolean nightAction(String message, Player[] players) {
		//this method makes sure the target for the night action is valid, then targets him
		boolean toReturn = false;
		if (!checkNightAction(message))
			return toReturn;
		String[] splitMessage = message.split(" ");
		String targetName = splitMessage[1];
		//TODO: you shouldn't be able to target dead people... but you can :O! I think
		//causesOfDeath isnt fully used around the code yet, it needs to be properly set when someone dies during night
		//but for now we can maybe start using it instead of isAlive , it seems pointless to have both..even though we need causesOfDeath for certain roles.
		for (Player p : players)
			if (p.getName().equals(targetName) && p.getCauseOfDeath().equals(Player.causesOfDeath.NOTDEAD))
			{
				this.target = p;
				toReturn = true;
				setNightAction(true);
				break;
			}
		return toReturn;

	}

	public void resetNightAction() {
		setNightAction(false);
	}

	public boolean hasNightAction() {
		return true;
	}

	public String getName() {
		return this.name;
	}

	public Team getTeam() {
		return this.team;
	}

	public Player getTarget() {
		return this.target;
	}

	public void setTarget(Player p) {
		this.target = p;
	}

	public abstract boolean checkNightAction(String message);

	public void setName(String name) {
		this.name = name;
	}

	public abstract String description();

	public String resolveNightAction() {
		return "";
	}

	public void setNightAction(boolean done) {
		didNightAction = done;
	}

	public boolean didNightAction() {
		return didNightAction;
	}
}

