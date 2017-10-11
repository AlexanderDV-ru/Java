package ru.alexandrdv.messenger;

import java.math.BigInteger;
import java.util.Random;

public class Encryptor
{
	static String[][] latin = new String[][] { "abcdefghijklmnopqrstuvwxyz".split(""), "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("") };
	static String[] digits = "0123456789".split("");
	static Random rand = new Random();

	public static String encrypt(String msg, int key, EncryptionType from, EncryptionType to)
	{
		String result = "";
		if (from == EncryptionType.None)
		{
			result = "1";
			for (char c : msg.toCharArray())
				result += BigInteger.valueOf(c).multiply(BigInteger.valueOf(key)) + latin[rand.nextInt(2)][rand.nextInt(26)] + digits[rand.nextInt(10)];
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
					result += new BigInteger(s.substring(1, s.length())).divide(BigInteger.valueOf(key)) + latin[rand.nextInt(2)][rand.nextInt(26)] + digits[rand.nextInt(10)];
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
					result += new BigInteger(s.substring(1, s.length())).multiply(BigInteger.valueOf(key)) + latin[rand.nextInt(2)][rand.nextInt(26)] + digits[rand.nextInt(10)];
			return result;
		}
		return msg;
	}

	public enum EncryptionType
	{
		None,
		Client,
		Double,
		Server
	}
}
