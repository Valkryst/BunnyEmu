/*
 * BunnyEmu - A Java WoW sandbox/emulator
 * https://github.com/marijnz/BunnyEmu
 */
package com.valkryst.BunnyEmu.logon;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import com.valkryst.BunnyEmu.entities.Realm;
import com.valkryst.BunnyEmu.entities.packet.ServerPacket;
import com.valkryst.BunnyEmu.enums.ClientVersion;
import com.valkryst.BunnyEmu.handlers.TempClientHandler;
import com.valkryst.BunnyEmu.net.WorldConnection;
import com.valkryst.BunnyEmu.net.packets.client.CMSG_AUTH_PROOF;
import com.valkryst.BunnyEmu.net.packets.server.SMSG_AUTH_RESPONSE;
import com.valkryst.BunnyEmu.utils.BigNumber;
import com.valkryst.BunnyEmu.utils.Opcodes;
import com.valkryst.BunnyEmu.misc.Logger;

/**
 *
 * Authenticates a client to the assigned realm
 *
 * @author Marijn
 */
public class RealmAuth extends Auth {

    private byte[] _seed;
    private WorldConnection connection;
    private Realm realm;

    public RealmAuth(WorldConnection c, Realm r) {
        connection = c;
        realm = r;
    }
    
    /**
     * MoP only, send on start
     */
    public void transferInitiate(){
    	 ServerPacket initiate = new ServerPacket(Opcodes.MSG_TRANSFER_INITIATE, 50);
    	 initiate.putString("RLD OF WARCRAFT CONNECTION - SERVER TO CLIENT");
    	 initiate.wrap();
    	 connection.send(initiate);
    }

    public void authChallenge() {
    	
        ServerPacket authChallenge = new ServerPacket(Opcodes.SMSG_AUTH_CHALLENGE, 50);
        _seed = new SecureRandom().generateSeed(4);
        
        if (realm.getVersion() != ClientVersion.VERSION_MOP) {
	        if (realm.getVersion() == ClientVersion.VERSION_MOP) {
	        	authChallenge.put(new BigNumber().setRand(16).asByteArray(16));
	        	authChallenge.put((byte) 1);
	        } else if(realm.getVersion() == ClientVersion.VERSION_CATA)
	        	authChallenge.putInt(1);
	        authChallenge.put(_seed);
	        authChallenge.put(new BigNumber().setRand(16).asByteArray(16));
        } else { // MoP sequence 5.3.0a
        	
        	for (int i = 0; i < 8; i++)
        		authChallenge.putInt(0);
        	
        	authChallenge.put(_seed);
        	authChallenge.put((byte) 0);
        }
	        
	    authChallenge.wrap();
        connection.send(authChallenge);
    }
    
    public void authSession(CMSG_AUTH_PROOF authProof) {
        Logger.writeLog("authSession", Logger.LOG_TYPE_VERBOSE);

        client = TempClientHandler.removeTempClient(authProof.getAccountName());

        if (client != null) {
        	client.connect(realm);
            client.attachWorld((WorldConnection) connection);
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA1");
            } catch (NoSuchAlgorithmException e) {
            }

            md.update(connection.getClient().getName().getBytes());
            md.update(new byte[] {0, 0, 0, 0}); // t
            md.update(authProof.getClientSeed());
            md.update(_seed);
            md.update(connection.getClient().getSessionKey());
            byte[] digest = md.digest();	
            
            Logger.writeLog("authSession " + client.getName(), Logger.LOG_TYPE_VERBOSE);
           // Log.log( new BigNumber(authProof.getDigest()).toHexString() + " and " +  new BigNumber(digest).toHexString());
            // The cataclysm and MoP digest calculation is unknown, simply allowing it..
            if (realm.getVersion() == ClientVersion.VERSION_MOP || new BigNumber(authProof.getDigest()).equals(new BigNumber(digest))) {
            	connection.getClient().initCrypt(connection.getClient().getSessionKey()); 
            	Logger.writeLog("Valid client connected: " + client.getName(), Logger.LOG_TYPE_VERBOSE);
                if (realm.getVersion() != ClientVersion.VERSION_CATA && realm.getVersion() != ClientVersion.VERSION_MOP){
                	ServerPacket authResponse = new ServerPacket(Opcodes.SMSG_AUTH_RESPONSE, 80);
	                authResponse.put((byte) 0x0C);
	                authResponse.put((byte) 0x30);
	                authResponse.put((byte) 0x78);
	                authResponse.putLong(0x02);
	                connection.send(authResponse);
                } else {
                	// MoP sends all classes and races + some other data, packet from Arctium
                    //connection.send(realm.loadPacket("authresponse_mop", 80)); // 0x0890 packet
                    connection.send(new SMSG_AUTH_RESPONSE());
                    connection.send(new ServerPacket(Opcodes.SMSG_UPDATE_CLIENT_CACHE_VERSION, 4));
                    connection.send(new ServerPacket(Opcodes.SMSG_TUTORIAL_FLAGS, 8*4));
                }
                return;
            } else {
                client.disconnect();
            }
        } else {
        	connection.close();
    	}
    
        Logger.writeLog("Client " + authProof.getAccountName() + " unknown, probably tried to connect to realm directly.", Logger.LOG_TYPE_WARNING);
    }
}
