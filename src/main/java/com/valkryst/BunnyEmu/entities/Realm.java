/*
 * BunnyEmu - A Java WoW sandbox/emulator
 * https://github.com/marijnz/BunnyEmu
 */
package com.valkryst.BunnyEmu.entities;

import com.valkryst.BunnyEmu.Server;
import com.valkryst.BunnyEmu.entities.packet.ServerPacket;
import com.valkryst.BunnyEmu.enums.ClientVersion;
import com.valkryst.BunnyEmu.misc.Logger;
import com.valkryst.BunnyEmu.net.WorldConnection;
import com.valkryst.BunnyEmu.utils.Opcodes;
import com.valkryst.BunnyEmu.utils.PacketMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * A realm that has to be added to the RealmHandler.
 * 
 * @author Marijn
 */
public class Realm extends Thread {
	public int id;
	public String name;
	public String address;
	public int port;
	public int icon;
	public int flags = 0;
	public int timezone;
	public float population = 0;
	private ClientVersion version;
	private PacketMap packets;

	private ArrayList<Client> clients = new ArrayList<Client>(10);

	ServerSocket socket = null;

	public Realm() {
		this(1, "Marijnz ultimate server", Server.realmlist, 3456, ClientVersion.VERSION_WOTLK);
	}

	/**
	 * Instantiates a new realm, usually only created through the RealmHandler.
	 * 
	 * @param id An unique id.
	 * @param name The name of the ream how it should be listed in the realmlist.
	 * @param address The address of the worldsocket, usually same host as the logonsocket.
	 * @param port The port of the worldsocket.
	 * @param version The version of the realm, see Readme for up-to-date version support.
	 */
	public Realm(int id, String name, String address, int port, ClientVersion version) {
		this.id = id;
		this.name = "[" + version + "]" + name ;
		this.address = address + ":" + String.valueOf(port);
		this.port = port;
		this.version = version;
		
		if(version == ClientVersion.VERSION_WOTLK || version == ClientVersion.VERSION_BC || version == ClientVersion.VERSION_VANILLA)
			packets = Opcodes.formWotLK();
		else if(version == ClientVersion.VERSION_CATA)
			packets = Opcodes.formCata();
		else if(version == ClientVersion.VERSION_MOP)
			packets = Opcodes.formMoP();
		start();
		
		Logger.writeLog("Created new realm: " + this.name, Logger.LOG_TYPE_VERBOSE);
	}

	/**
	 * @return The size of the realm, only used for the realmlist packet.
	 */
	public int getSize() {
		return 8 + 4 + address.length() + name.length();
	}

	@Override
	public void run() {
		try {
			listenSocket();
		} catch (IOException ex) {
			Logger.writeLog("Couldn't create a listening socket for realm " + id, Logger.LOG_TYPE_WARNING);
		}
	}

	private void listenSocket() throws IOException {
		socket = new ServerSocket(port);

		while (true) {
			// TODO: Keep track on worldconnections in case we want to support multiple clients to interact. 
			new WorldConnection(socket.accept(), this);
			Logger.writeLog("Connection made to realm " + id, Logger.LOG_TYPE_VERBOSE);
		}
	}

	/**
	 * Add a new client to the realm, usually done after the client has been connected to it.
	 * @param client The connected client
	 */
	public void addClient(Client client) {
		clients.add(client);
	}

	/**
	 * Removes a client from the realm, usually done after the client has been disconnected.
	 * @param client The disconnected client
	 */
	public void removeClient(Client client) {
		clients.remove(client);
	}

	/**
	 * Get a client connected to this realm
	 * 
	 * @param name The name of the client
	 * @return
	 */
	public Client getClient(String name) {
		name = name.toUpperCase();
		for(Client client : clients)
			if (client.getName().equals(name))
				return client;
		return null;
	}
	
	public ArrayList<Client> getAllClients(){
		return clients;
	}

	/**
	 * Send a packet to all connected clients except for passed client
	 */
	public void sendAllClients(ServerPacket p, Client ignoreClient){
		Logger.writeLog("Ignore client: " + ignoreClient.getName(), Logger.LOG_TYPE_VERBOSE);
		for(Client client : clients)
			if(!client.equals(ignoreClient)){
				Logger.writeLog("Sending packet " + p.sOpcode + " to client: " + client.getName(), Logger.LOG_TYPE_VERBOSE);
				client.getWorldConnection().send(p);
			}
	}
	/**
	 * @return The version of this realm, can be used to build packets for specific versions.
	 */
	public ClientVersion getVersion() {
		return version;
	}
	
	public String getVersionName(){
		if(this.version == ClientVersion.VERSION_VANILLA)
			return "Vanilla";
		if(this.version == ClientVersion.VERSION_BC)
			return "BC";
		if(this.version == ClientVersion.VERSION_WOTLK)
			return "WotLK";
		if(this.version == ClientVersion.VERSION_CATA)
			return "Cata";
		if(this.version == ClientVersion.VERSION_MOP)
			return "MoP";
		else
			return null;
	}


	/**
	 * @return The packets that belong to the version this realm has been assigned.
	 */
	public PacketMap getPackets() {
		return packets;
	}
	
	 /**
     * Loading a packet text file assuming an arcemu-like packet dump
     * @param packetDir The packet to be loaded
     * @param capacity	How much size should be buffered for the returned data
     * 
	  * TODO: Make it logging style independent
     */
    public ServerPacket loadPacket(String packetDir, int capacity){
    	Logger.writeLog("loading packet", Logger.LOG_TYPE_VERBOSE);
    	String opcode = null;
    	ByteBuffer data = ByteBuffer.allocate(capacity);
    	try {
    		BufferedReader in = new BufferedReader(new FileReader("assets" + "/" + packetDir));
            String line = "";
            line = in.readLine(); // opcode and info line
            int firstHook = line.indexOf("(")+3;

            opcode = line.substring(firstHook, line.indexOf(")", firstHook));
            for (int i = 0; i < 3; i++)
            	in.readLine(); // unused text
            
            // "|" = start or end of line
            while((line = in.readLine()).charAt(0) == '|'){
            	String curHexChar = "";
            	int i = 1; // Skip the first "|"
            	while(true){
            		curHexChar = line.substring(i, i + 2);
            		i += 3;
            		if(curHexChar.contains(" ") || curHexChar.contains("|"))
            			break;
            		data.put((byte) Integer.parseInt(curHexChar, 16)); // Read two bytes, hex
            	}
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    	Logger.writeLog(data.toString(), Logger.LOG_TYPE_VERBOSE);
    	ServerPacket p;
    	try{
    		p = new ServerPacket(packets.getOpcodeName(Short.parseShort(opcode, 16)), data);
    	} catch (NumberFormatException e){
    		p = new ServerPacket(Opcodes.SMSG_COMPRESSED_UPDATE_OBJECT, data);
    	}
    	return p;
    	
    }

}
