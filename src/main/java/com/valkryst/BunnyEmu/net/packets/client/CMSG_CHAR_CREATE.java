package com.valkryst.BunnyEmu.net.packets.client;

import java.io.UnsupportedEncodingException;

import com.valkryst.BunnyEmu.entities.packet.ClientPacket;
import com.valkryst.BunnyEmu.misc.Logger;

public class CMSG_CHAR_CREATE extends ClientPacket {
	public byte cHairStyle;
	public byte cFaceStyle;
	public byte cFacialHair;
	public byte cHairColor;
	public byte cRace;
	public byte cClass;
	public byte cSkinColor;
	public byte cGender;
	public String cName;
	
	public boolean readGeneric(){
		cName = getString();
		cRace      = get();
		cClass     = get();
		cGender     = get();
		cSkinColor  = get();
		cFaceStyle  = get();
		cHairStyle = get();
		cHairColor  = get();
		cFacialHair = get();
		formatName();
		return true;
	}
	
	public boolean readMoP() {
		Logger.writeLog(this.toString(), Logger.LOG_TYPE_VERBOSE);
		cHairStyle = get();
		cFaceStyle  = get();
		cFacialHair = get();
		cHairColor  = get();
		cRace      = get();
		cClass     = get();
		cSkinColor  = get();
		
		get();
		
		cGender     = get();
		
		byte nameLength = get();
	    StringBuilder builder = new StringBuilder();
		
		// length/4 is amount of characters in ASCII
		for (int x = 0; x < nameLength/4; x++) {
			try {
				builder.append(new String(new byte[] { get() }, "US-ASCII"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		cName = builder.toString();
		formatName();
		return true;
	}
	
	private void formatName(){
		cName = cName.toLowerCase();
		char[] chars = cName.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		cName = String.valueOf(chars);
	}
	
}
