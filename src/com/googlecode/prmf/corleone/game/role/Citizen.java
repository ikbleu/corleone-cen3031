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
import com.googlecode.prmf.corleone.game.team.Town;

public class Citizen extends Role {
	private final Town team;

	public Citizen(Town nteam) {
		setName("citizen");
		team = nteam;
		setNightAction(true);
	}

	@Override
	public void resetNightAction() {
		setNightAction(true);
	}

	@Override
	public boolean checkNightAction(String message) {
		return false;
	}

	public void nightAction(Player target) {
	}

	@Override
	public boolean hasNightAction() {
		return false;
	}

	@Override
	public Town getTeam() {
		return this.team;
	}

	@Override
	public String description() {
		return String.format("You are a %s! As a %s, you have no special powers. You win when all threats to the town are eliminated.", getName(), getName());
	}
}

