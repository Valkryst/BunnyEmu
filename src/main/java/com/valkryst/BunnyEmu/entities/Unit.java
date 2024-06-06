package com.valkryst.BunnyEmu.entities;

import com.valkryst.BunnyEmu.utils.types.MovementSpeed;

public abstract class Unit extends WorldObject{
	protected MovementSpeed movement;
	
	public MovementSpeed getMovement(){
		return movement;
	}
}
