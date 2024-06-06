package com.valkryst.BunnyEmu.handlers;

import java.util.ArrayList;

import com.valkryst.BunnyEmu.Server;
import com.valkryst.BunnyEmu.entities.Client;
import com.valkryst.BunnyEmu.entities.Realm;
import com.valkryst.BunnyEmu.entities.packet.AuthPacket;
import com.valkryst.BunnyEmu.enums.ClientVersion;

public class RealmHandler {
	
    private static ArrayList<Realm> realms = new ArrayList<Realm>(10);
    
    /**
     * @return All launched realms
     */
    public static ArrayList<Realm> getRealms(){
    	return realms;
    }
    
    /**
     * @return The realmlist packet, version dependable
     */
	public static AuthPacket getRealmList(){
		short size = 8;
        for (int i = 0; i < realms.size(); i++) 
            size += realms.get(i).getSize();
        
        AuthPacket realmPacket = new AuthPacket((short) (size + 3));
        
        realmPacket.put((byte) 0x10);          // Header
        realmPacket.putShort(size);       		// Size Placeholder
        realmPacket.putInt(0);                 // unknown?
        realmPacket.putShort((short) realms.size());       // Realm count
       
        // all realms
        for (Realm realm : realms) {
        	realmPacket.put((byte) 0x2A); 
        	realmPacket.put((byte) 0); 
        	realmPacket.put((byte) realm.flags);   
        	realmPacket.putString(realm.name);      // Name
        	realmPacket.putString(realm.address);   // Address
        	realmPacket.putFloat(realm.population);      // Population
        	realmPacket.put((byte) 0); // char count

        	realmPacket.put((byte) 1);        // ??
        	realmPacket.put((byte) 0x2C);        // ??
        }
        
        realmPacket.putShort((short) 0x10);
        realmPacket.wrap();
        return realmPacket;
	}
	
	/**
	 * Adding a realm to the realmlist
	 * @param realm The realm
	 */
	public static void addRealm(Realm realm){
		realms.add(realm);
	}
	
	/**
	 * Creates a new realm for the given version if it doesn't exist already
	 * 
	 * @param version The WoW client version, such as 335
	 */
	public static void addVersionRealm(ClientVersion version){
		for(Realm realm : realms)
			if(realm.getVersion() == version)
				return;
		realms.add(new Realm(realms.size()+1, "Version realm", Server.realmlist, 8100 + realms.size(), version));
	}

	/**
	 * @return The realm that belongs to the given ID
	 */
	public static Realm getRealm(int id){
		return realms.get(id);
	}
	
	/**
	 * @return All clients from all realms
	 */
	public static ArrayList<Client> getAllClientsAllRealms(){
		ArrayList<Client> allClients = new ArrayList<Client>();
		for(Realm realm : realms)
			allClients.addAll(realm.getAllClients());
		return allClients;
	}
	
}
