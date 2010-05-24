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

public class MafiaTeam extends Team {
	public MafiaTeam() {
		setName("MafiaTeam");
	}

	@Override
	public String getName() {
		return "MafiaTeam";
	}

	@Override
	public boolean hasWon(Player[] players) {
		// itt we check for victory
		// if mafia make up at least 50% of the living players return true
		int nonMafia = 0, mafia = 0;
		for (Player p : players) {
			if (!p.isAlive())
				continue;
			if (p.getRole().getTeam().equals(this))
				// TODO the above line looks like a bug to me
				// why is that a bug?
				// because I don't think the equals() method works properly
				// well then, someone should fix equals() :D
			{
				++mafia;
				System.err.println(p + " is a mafia");
			}

			else {
				++nonMafia;
				System.err.println(p + " is not a mafia");
			}
		}
		return nonMafia <= mafia;
	}

	@Override
	public String toString() {
		//gives a list of living mafiosos~
		StringBuilder toReturn = new StringBuilder();
		toReturn.append("The current living mafia team members are: ");
		for (Player p : getList())
			if (p.isAlive())
				toReturn.append(p + " "); //TODO: replace with p.toString()
		return toReturn.toString();
	}

	public boolean agreeOnTarget() {
		// all of the mafia must agree on the target before they are done with their night action

		Player target = null;
		boolean agree = true;
		for (Player p : getList()) {
			if (!p.isAlive())
				continue;
			//first time through, target should be null and that's OK, just get the target
			if (target == null) {
				target = p.getRole().getTarget();
				//if it's still null, ruh-roh!
				if (target == null) {
					agree = false;
					break;
				}
			}
			try {
				agree = target.equals(p.getRole().getTarget());
			} catch (Exception e) {
				System.out.println("still sucks");
			}

			if (!agree)
				break;
		}
		if (agree)
			for (Player p : getList())
				p.getRole().setNightAction(true);
		return agree;

	}

}
