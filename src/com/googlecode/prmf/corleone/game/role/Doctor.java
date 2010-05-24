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

import com.googlecode.prmf.corleone.game.team.Town;

public class Doctor extends Role {
	private final Town team;

	public Doctor(Town nteam) {
		setName("doctor");
		team = nteam;
	}

	@Override
	public Town getTeam() {
		return this.team;
	}
	
	@Override
	public boolean checkNightAction(String message)
	{
		boolean result = false;
		message = message.toLowerCase();
		if (message.substring(2).startsWith("heal"))
			result = true;
		return result;
	}
	
	@Override
	public String resolveNightAction()
	{
		if (getTarget() == null)
			return "You didn't give a target!";
		StringBuilder sb = new StringBuilder();
		getTarget().setNightLives(getTarget().getNightLives() + 1);
		return sb.toString();
	}

	@Override
	public String description() {
		return String.format("You are a %s! As a %s, you have the ability to heal a player every night. Note that you cannot heal yourself. You win when all threats to the town are eliminated.", getName(), getName());
	}

}

