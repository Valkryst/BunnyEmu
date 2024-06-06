package com.valkryst.BunnyEmu.utils;

import com.valkryst.BunnyEmu.entities.packet.Packet;

import java.util.*;

public class PacketLog {
	
	public enum PacketType{
		SERVER, CLIENT_UNKNOWN, CLIENT_KNOWN_IMPLEMENTED, CLIENT_KNOWN_UNIMPLEMENTED
	}
	
	private static Map<Object,ArrayList<Packet>> multiPackets = new HashMap<Object,ArrayList<Packet>>();

	
	public static void logPacket(PacketType type, Packet p){
		if (!multiPackets.containsKey(type)){
			ArrayList<Packet> packetList = new ArrayList<Packet>();
			multiPackets.put(type, packetList);
		}
		ArrayList<Packet> packets = multiPackets.get(type);
		packets.add(p);
	}
	
	/**
	 * Get logged packets
	 * 
	 * @param types Decide which packets to return
	 * 
	 * @return A combination of types, of the logged packets
	 */
	public static Packet[] getPackets(ArrayList<PacketType> types) {
		ArrayList<Packet> packetList = new ArrayList<Packet>();
		for (PacketType type : types)
			if (multiPackets.containsKey(type))
				packetList.addAll(multiPackets.get(type));
		
		Object[] sortedPackets = (Object[]) packetList.toArray();
		Arrays.sort(sortedPackets, new Comparator<Object>() {
	        @Override
	        public int compare(Object p1, Object p2) {
	        	return (int) (((Packet) p1).timestamp - ((Packet) p2).timestamp);
	        }
		});
		return Arrays.copyOf(sortedPackets, sortedPackets.length, Packet[].class);
	}
	
	
}
