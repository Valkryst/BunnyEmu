package com.valkryst.BunnyEmu.utils.update;

import java.util.BitSet;
import java.util.Hashtable;

import com.valkryst.BunnyEmu.entities.packet.Packet;
import com.valkryst.BunnyEmu.misc.Logger;

/**
 * Update data for WorldObject UpdatePackets
 * 
 * @author Marijn
 *
 */
public class UpdateData {

	// 1973 in MoP? Seems 1326 in wotlk
	private static final int dataLength = 1973;
	
	private Hashtable<Integer, Integer> updateData = new Hashtable<Integer, Integer>();
	private BitSet mask;
	private int maskSize;
	
	private FieldParser fields; // UpdateFields, version dependable and parsed from xml
	
	private int realmVersion;
	
	public void initFields(int realmVersion){
		this.realmVersion = realmVersion;
		maskSize = (dataLength + 32) / 32;
		fields = new FieldParser(realmVersion);

		updateData = new Hashtable<Integer, Integer>();
		mask = new BitSet(dataLength);
	}
	
	/**
	 * Set a new update field with offset 0.
	 */
	public <T extends Number> void setUpdateField(String field, String name, T value, Class<T> type) {
		this.setUpdateField(field, name, value, type, (byte) 0);
	}
	
	/**
	 * Set a new update field with added value to index and offset 0.
	 * 
	 * @added value added to the generated index
	 */
	public <T extends Number> void setUpdateField(String field, String name, int added, T value, Class<T> type) {
		int index = getIndex(field, name);
		this.setUpdateField(index + added, value, type, 0);
	}

	/**
	 * Set a new update field
	 * 
	 * @param field For example: UnitFields, used to generate index
	 * @param name For example: MaxHealth, used to generate index
	 * @param value The value of the updatefield, For example: 235.32f
	 * @param type The type of the given value, For example: Float
	 * @param offset The offset of the offset, happens in case of i.e.: Skin, Face, Hairstyle, Haircolor, passed as 4 bytes on the same index
	 */
	public <T extends Number> void setUpdateField(String field, String name, T value, Class<T> type, int offset) {
		int index = getIndex(field, name);
		setUpdateField(index, value, type, offset);
	}
	
	private <T extends Number> void setUpdateField(int index, T value, Class<T> type, int offset) {
		if(index == -1)
			return;
		if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(Short.class)) {
			int tmpValue = type.isAssignableFrom(Byte.class) ? value.byteValue() : value.shortValue();
			
			int multipliedOffset = offset * (type.isAssignableFrom(Byte.class) ? 8 : 16);
			if (mask.get(index)){
				updateData.put(index, updateData.get(index) | (tmpValue << multipliedOffset));
			}
			else
            	updateData.put(index, (int)(tmpValue << multipliedOffset));
			mask.set(index);
		} else if (type.isAssignableFrom(Long.class)) {
             long tmpValue = value.longValue();
             updateData.put(index, (int) (tmpValue & Integer.MAX_VALUE));
             updateData.put(index+1, (int) ((tmpValue >> 32) & Integer.MAX_VALUE));   
             mask.set(index);
			 mask.set(index + 1);
		} else if (type.isAssignableFrom(Float.class)) {
			updateData.put(index, Float.floatToIntBits(value.floatValue()));
			mask.set(index);
		} else if (type.isAssignableFrom(Integer.class)) {
			updateData.put(index, value.intValue());
			mask.set(index);
		} else {
			Logger.writeLog("Datatype for updatefield not supported", Logger.LOG_TYPE_WARNING);
		}
	}
	
	private int getIndex(String field, String name) {
		Integer index = fields.get(field, name);
		if(index == null) {
			Logger.writeLog("Can't retrieve index for UpdateField: " + field + " - " + name + "  (" + realmVersion + ")", Logger.LOG_TYPE_WARNING);
			return -1;
		}
		return index;
	}

	/**
	 * Write the update hashtable to the update packet.
	 * @param p The packet the update data has to be written on
	 */
	public void writeUpdateFields(Packet p) {
		p.put((byte) maskSize);
		byte[] b = new byte[((mask.size() + 8) / 8) + 1];
		byte[] maskB = mask.toByteArray();
		for (int i = 0; i < maskB.length; i++)
			b[i] = maskB[i];

		p.put(b, 0, maskSize * 4);
		for (int i = 0; i < mask.size(); i++)
			if (mask.get(i))
					p.putInt((int) updateData.get(i));
	}
}
