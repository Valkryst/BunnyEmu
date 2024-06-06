package com.valkryst.BunnyEmu.net.packets.server;

import com.valkryst.BunnyEmu.entities.character.Char;
import com.valkryst.BunnyEmu.entities.packet.ServerPacket;
import com.valkryst.BunnyEmu.utils.Opcodes;

/**
 * Instantly teleports the character to a new map.
 * 
 * @author Marijn
 * 
 */
public class SMSG_NEW_WORLD extends ServerPacket {

	private Char character;

	public SMSG_NEW_WORLD(Char character) {
		super(Opcodes.SMSG_NEW_WORLD, 20);
		this.character = character;
	}

	public boolean writeGeneric() {
		putInt(character.getMapID());
		putFloat(character.getPosition().getX());
		putFloat(character.getPosition().getO()); 
		putFloat(character.getPosition().getY());
		putFloat(character.getPosition().getZ());
		return true;
	}
}
