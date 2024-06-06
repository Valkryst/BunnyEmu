package com.valkryst.BunnyEmu.net.packets.server;

import com.valkryst.BunnyEmu.entities.character.Char;
import com.valkryst.BunnyEmu.entities.packet.ServerPacket;
import com.valkryst.BunnyEmu.utils.Opcodes;

/**
 * A chat message
 * 
 * @author Marijn
 *
 */
public class SMSG_MESSAGECHAT extends ServerPacket{

	private Char character;
	private String message;
	private int language;

	public SMSG_MESSAGECHAT(Char character, int language, String message) {
		super(Opcodes.SMSG_MESSAGECHAT, 100);
		this.character = character;
		this.message = message;
		this.language = language;
	}
	
	@Override
	public boolean writeMoP(){
		this.put((byte) 1); // fancy xp gain look : 0x21
		this.putInt(language);
        this.putLong(character.getGUID());
        this.putInt(0);
        this.putLong(character.getGUID());
        this.putInt(message.length());
        this.put(message.getBytes());
        this.putShort((short) 0);

        
		this.wrap();
		return true;
	}
				
}
