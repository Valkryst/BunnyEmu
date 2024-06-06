package com.valkryst.BunnyEmu.net.packets.server;

import com.valkryst.BunnyEmu.entities.character.Char;
import com.valkryst.BunnyEmu.entities.packet.ServerPacket;
import com.valkryst.BunnyEmu.utils.Opcodes;

/**
 * Required upon login, basic info
 * 
 * @author Marijn
 *
 */
public class SMSG_NAME_QUERY_RESPONSE extends ServerPacket{

	
	public SMSG_NAME_QUERY_RESPONSE(Char character){
		super(Opcodes.SMSG_NAME_QUERY_RESPONSE, character.getCharName().length()+1 + 8);
		writePackedGuid(character.getGUID());
		put((byte)0);
		putString(character.getCharName());
		put((byte)0);	   // this is a string showed besides players name (eg. in combat log), a custom title ?
		put(character.getCharRace()); // race
		put((byte) 0); // gender
		put(character.getCharClass()); // player info
		put((byte) 0); //VLack: tell the server this name is not declined... (3.1 fix?)
	}
	
}
