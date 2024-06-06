package com.valkryst.BunnyEmu.utils.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import com.valkryst.BunnyEmu.misc.Logger;

public class HashHelper {
	/* create and return hashPW here */
	public static String generatePasswordHash(String[] accountInfo) throws NoSuchAlgorithmException {
		MessageDigest md;

		md = MessageDigest.getInstance("SHA1");

		String user = accountInfo[1].toUpperCase() + ":" + accountInfo[2].toUpperCase();

		md.update(user.getBytes());

		String accountHash = DatatypeConverter.printHexBinary(md.digest());
		Logger.writeLog("AccountHash: " + accountHash, Logger.LOG_TYPE_VERBOSE);
		return accountHash;
	}
}
