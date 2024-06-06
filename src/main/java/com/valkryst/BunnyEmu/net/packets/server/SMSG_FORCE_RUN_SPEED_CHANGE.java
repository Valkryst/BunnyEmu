package com.valkryst.BunnyEmu.net.packets.server;

import com.valkryst.BunnyEmu.entities.character.Char;
import com.valkryst.BunnyEmu.entities.packet.ServerPacket;
import com.valkryst.BunnyEmu.utils.Opcodes;

/**
 * Modifies the client's run speed.
 * 
 * @author Marijn
 *
 */
public class SMSG_FORCE_RUN_SPEED_CHANGE extends ServerPacket{
	
	public SMSG_FORCE_RUN_SPEED_CHANGE(Char character, float speed) {
		super(Opcodes.SMSG_FORCE_RUN_SPEED_CHANGE, 50);
		writePackedGuid(character.getGUID());
		putInt(0);
		put((byte) 0);
		putFloat(speed);
	}
		
}
