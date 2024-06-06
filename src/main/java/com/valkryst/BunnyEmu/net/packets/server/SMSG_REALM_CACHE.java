package com.valkryst.BunnyEmu.net.packets.server;

import com.valkryst.BunnyEmu.entities.Realm;
import com.valkryst.BunnyEmu.entities.packet.ServerPacket;
import com.valkryst.BunnyEmu.utils.BitPack;
import com.valkryst.BunnyEmu.utils.Opcodes;

/**
 * Realm data, MoP only.
 * 
 * @author Marijn
 * 
 */
public class SMSG_REALM_CACHE extends ServerPacket {

	private Realm realm;

	public SMSG_REALM_CACHE(Realm realm) {
		super(Opcodes.SMSG_REALM_CACHE, 100);
		this.realm = realm;
	}

	@Override
	public boolean writeMoP() {

		BitPack bitPack = new BitPack(this);

		this.putInt(realm.id);
		this.put((byte) 0); // unknown
		
		bitPack.write(realm.name.length(), 8);
		bitPack.write(realm.name.length(), 8);
		bitPack.write(1);
		
		bitPack.flush();
		
		this.putString(realm.name);
		this.putString(realm.name);
		
		this.wrap();
		return true;
	}

}
