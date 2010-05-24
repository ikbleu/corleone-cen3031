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

import com.googlecode.prmf.corleone.game.Player;

public class Town extends Team {
	public Town() {
		setName("Town");
	}

	@Override
	public boolean hasWon(Player[] players) {
		//TODO: this method works in the current game, but technically is incorrect.
		//if possible, change it to check for threats to the town as opposed to non-town (survivors etc)
		boolean result = true;
		for(Player p: players) {
			if(!p.getRole().getTeam().equals(this) && p.isAlive()) {
				result = false;
				break;
			}
		}
		return result;
	}
}
