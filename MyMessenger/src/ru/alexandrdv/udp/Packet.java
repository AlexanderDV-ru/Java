package ru.alexandrdv.udp;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.HashMap;

import ru.alexandrdv.udp.Encryptor.EncryptionType;
import ru.alexandrdv.udpmessenger.Account;

/**
 * 
 * @author AlexandrDV
 */
public class Packet implements Serializable
{
	public static final int packetSize = 8 * 8 * 8 * 8 * 8;
	private static final long serialVersionUID = -8942733866610289453L;
	public HashMap<String, String> args = new HashMap<String, String>();
	public InetSocketAddress address;
	public EncryptionType crypt;

	/**
	 * 
	 * @param msg
	 * @param ip
	 * @param port
	 */
	public Packet(String type, String reciever, String sender, String msg, InetSocketAddress address, EncryptionType crypt)
	{
		super();
		this.args.put("type", type);
		this.args.put("reciever", reciever);
		this.args.put("sender", sender);
		this.args.put("msg", msg);
		this.address = address;
		this.crypt = crypt;
	}
	public static class LoginPacket extends Packet implements Serializable
	{
		private static final long serialVersionUID = -8942733867510289453L;

		public LoginPacket(String type, String sender, String msg, InetSocketAddress address, EncryptionType crypt,Account account)
		{
			super(type, null, sender, msg, address, crypt);
			args.put("login", account.login);
			args.put("password", account.password);
			args.put("name", account.name);
			args.put("surname", account.surname);
			args.put("secondname", account.secondname);
			args.put("gender", account.gender);
			args.put("age", account.age);
			args.put("state", account.state);
			args.put("phone", account.phone);
			args.put("email", account.email);
			
		}
		
	}

}
