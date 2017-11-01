package ru.alexandrdv.udp;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

import ru.alexandrdv.udp.Packet;

public class Encryptor
{
	private static final String[][] latin = new String[][] { "abcdefghijklmnopqrstuvwxyz".split(""), "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("") };
	private static final String[] digits = "0123456789".split("");
	private static final Random random = new Random();

	public static String encrypt(String msg, int key, EncryptionType from, EncryptionType to)
	{
		if(msg==null)
			return msg;
		String result = "";
		if (from == EncryptionType.None)
		{
			result = "1";
			for (char c : msg.toCharArray())
				result += BigInteger.valueOf(c).multiply(BigInteger.valueOf(key)) + latin[random.nextInt(2)][random.nextInt(26)] + digits[random.nextInt(10)];
			return result;
		}
		if (from == EncryptionType.Double)
		{
			result = "1";
			msg = msg.toLowerCase();
			for (int i = 0; i < 26; i++)
				msg = msg.replace(latin[0][i], "&");
			for (String s : msg.split("&"))
				if (s.length() > 1)
					result += new BigInteger(s.substring(1, s.length())).divide(BigInteger.valueOf(key)) + latin[random.nextInt(2)][random.nextInt(26)]
							+ digits[random.nextInt(10)];
			return result;
		}
		if (to == EncryptionType.None)
		{
			msg = msg.toLowerCase();
			for (int i = 0; i < 26; i++)
				msg = msg.replace(latin[0][i], "&");
			for (String s : msg.split("&"))
				if (s.length() > 1)
					result += (char) new BigInteger(s.substring(1, s.length())).divide(BigInteger.valueOf(key)).intValue();
			return result;
		}
		if (to == EncryptionType.Double)
		{
			result = "1";
			msg = msg.toLowerCase();
			for (int i = 0; i < 26; i++)
				msg = msg.replace(latin[0][i], "&");
			for (String s : msg.split("&"))
				if (s.length() > 1)
					result += new BigInteger(s.substring(1, s.length())).multiply(BigInteger.valueOf(key)) + latin[random.nextInt(2)][random.nextInt(26)]
							+ digits[random.nextInt(10)];
			return result;
		}
		return msg;
	}

	public static Packet encryptPacket(Packet p, int enkey, EncryptionType from, EncryptionType to)
	{
		HashMap<String, String> args = new HashMap<String, String>();
		for (String key : p.args.keySet())
			args.put(key, Encryptor.encrypt(p.args.get(key), enkey, from, to));
		p.args = args;
		p.crypt = to;
		return p;
	}

	public enum EncryptionType
	{
		None,
		Client,
		Double,
		Server
	}
}
