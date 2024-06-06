package com.valkryst.BunnyEmu.net.packets.server;

import com.valkryst.BunnyEmu.entities.character.Char;
import com.valkryst.BunnyEmu.entities.packet.ServerPacket;
import com.valkryst.BunnyEmu.utils.BitPack;
import com.valkryst.BunnyEmu.utils.Opcodes;

/**
 * Character data, MoP only.
 * 
 * @author Marijn
 *
 */
public class SMSG_NAME_CACHE extends ServerPacket{

	private Char character;

	public SMSG_NAME_CACHE(Char character) {
		super(Opcodes.SMSG_NAME_CACHE, 50);
		this.character = character;
	}
	
	@Override
	public boolean writeMoP() {

		BitPack bitPack = new BitPack(this);
		bitPack.setGuid(character.getGUID());
		
		bitPack.write(0);
		bitPack.writeGuidMask(new byte[] {1, 3, 2});
		bitPack.write(character.getCharName().length(), 6);
		bitPack.writeGuidMask(new byte[] {6, 4, 0});
		bitPack.write(0);
		bitPack.writeGuidMask(new byte[] {5, 7});	

		bitPack.flush();
		
		bitPack.writeGuidBytes(new byte[] { 1 });
		
		this.putString(character.getCharName());
		
		bitPack.writeGuidBytes(new byte[] {0, 7});
		
		this.put((byte) character.getCharRace());
		this.put((byte) 0); // ?
		this.put((byte) character.getCharGender()); // gender
		this.put((byte) character.getCharClass());
		
		bitPack.writeGuidBytes(new byte[] {4, 6, 5});
		
		this.putInt(1);
		
		bitPack.writeGuidBytes(new byte[] {3, 2});
			
		this.wrap();
		return true;
	}
				
}
