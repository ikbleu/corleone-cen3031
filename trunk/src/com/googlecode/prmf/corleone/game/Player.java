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
package com.googlecode.prmf.corleone.game;

import com.googlecode.prmf.corleone.game.role.Role;
import com.googlecode.prmf.corleone.game.team.Team;

public class Player {
	public static enum causesOfDeath {
		LYNCH, NIGHTKILL, QUIT, NOTDEAD
	}
	
	private String name;
	private Role role;
	private boolean isAlive;
	private int votedFor;
	private int nightLives;
	private int level;
	private int items;
	private int actionpoint;
	private boolean targetted;
	private causesOfDeath deathCause;

	public Player(String name) {
		setName(name);
		isAlive = true;
		role = null;
		votedFor = -1;
		level = 0;
		items = 3;
		actionpoint = 240;
		targetted = false;
		deathCause = causesOfDeath.NOTDEAD;
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if(obj instanceof Player) {
			Player temp = (Player) obj;
			ret = this.getName().equals(temp.getName());
		}
		return ret;
	}

	public void endNight() {
		if (getNightLives() < 0) //this means they were targeted to be killed more than targeted to be saved
			setAlive(false);
		resetNightLives();
		targetted = false;
	}

	public String getName() {
		return name;
	}
	
	public int getlevel() 
	{
		return level;
	}
	
	public int getitems()
	{
		return items;
	}

	public Role getRole() {
		return role;
	}
	
	public void setlevel(int lv)
	{
		this.level = lv;
	}
	
	public void setitems(int i)
	{
		this.items = i;
	}
	
	public int getap()
	{
		return actionpoint;
	}
	
	public void setap(int ap)
	{
		this.actionpoint = ap;
	}
	
	// -----------------------------------------------------------------------------------------------------
	// START YAWDIE INVASION
	// -----------------------------------------------------------------------------------------------------
	
	public Team getTeam() {
		return role.getTeam();
	}
	
	// -----------------------------------------------------------------------------------------------------
	// END YAWDIE INVASION
	// -----------------------------------------------------------------------------------------------------
	
	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public int getVote() {
		return votedFor;
	}

	public void setAlive(boolean status) {
		this.isAlive = status;
	}

	public void setVote(int vote) {
		this.votedFor = vote;
	}

	public void resetNightLives() {
		setNightLives(0);
	}

	public void setNightLives(int setTo) {
		targetted = true;
		nightLives = setTo;
	}

	public int getNightLives() {
		return nightLives;
	}

	public boolean getTargetted() {
		return targetted;
	}
	
	public void setCauseOfDeath(causesOfDeath death)
	{
		deathCause = death;
	}
	
	public causesOfDeath getCauseOfDeath()
	{
		return deathCause;
	}
}
