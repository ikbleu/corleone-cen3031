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
package com.googlecode.prmf.corleone.game.team;

import java.util.LinkedList;
import java.util.List;

import com.googlecode.prmf.corleone.game.Player;

public abstract class Team {
	private List<Player> list; // TODO make fields final where appropriate
	private String name;

	public Team() {
		list = new LinkedList<Player>();
	}

	// TODO why is an array of players passed as an argument?
	//
	//most win conditions don't just involve the team in isolation, but rather the team with respect to the rest of the game
	//without knowing some things about the other players it's impossible to determine if you've won or not
	//
	// if this information is needed, it should be passed elsewhere methinks
	public abstract boolean hasWon(Player[] players);

	public String getName() {
		return name;
	}

	// TODO why is changing the team's name allowed? seems like this belongs in the constructor and nowhere else
	public void setName(String name) {
		this.name = name;
	}

	public void addPlayer(Player p) {
		getList().add(p);
	}

	public boolean contains(Player player) {
		return getList().contains(player);
	}

	public List<Player> getList() {
		return list;
	}

	public String getPlayers() {
		String ret = "";
		for(Player p: list) {
			if(ret.length() > 0) ret += ", ";
			ret += p.getName();
		}
		return ret;
	}

	@Override
	public String toString() {
		return name;
	}
}
