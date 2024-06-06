package com.valkryst.BunnyEmu.utils.crypto;

import com.valkryst.BunnyEmu.misc.Logger;


public class BCCrypt extends VanillaCrypt {

	private final static byte[] seed = { 0x38, (byte) 0xA7, (byte) 0x83, 0x15, (byte) 0xF8,(byte)  0x92, 0x25, 0x30, 0x71, (byte) 0x98, 0x67, (byte) 0xB1, (byte) 0x8C, 0x4, (byte) 0xE2, (byte) 0xAA };

	/**
	 * Instantiates a new Burning Crusade crypt.
	 */
	
	public BCCrypt(){
		Logger.writeLog("Created new Burning Crusade crypt", Logger.LOG_TYPE_VERBOSE);
	}
	
	@Override
	public void init(byte[] key) {
		_key = CryptTools.getKey(seed, key);
		_send_i = _send_j = _recv_i = _recv_j = 0;
	    _initialized = true;
	}
	
}
