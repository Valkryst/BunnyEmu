package com.valkryst.BunnyEmu.net.packets.client;

import com.valkryst.BunnyEmu.entities.packet.ClientPacket;
import com.valkryst.BunnyEmu.utils.BitUnpack;

public class CMSG_MESSAGECHAT extends ClientPacket {

	private String message;
	private int language;
	
	public boolean readMoP() {
		BitUnpack bitUnpack = new BitUnpack(this);
        language = getInt();
        int messageLength = bitUnpack.getBits((byte) 8);
        message = getString(messageLength);
        
		return true;
	}
	
	public String getMessage(){
		return message;
	}
	
	public int getLanguage(){
		return language;
	}
	
}
