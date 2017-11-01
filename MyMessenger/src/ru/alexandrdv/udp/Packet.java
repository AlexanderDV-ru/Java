package ru.alexandrdv.udp;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.HashMap;

import ru.alexandrdv.udp.Encryptor.EncryptionType;

/**
 * 
 * @author AlexandrDV
 *
 */
public class Packet implements Serializable
{
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
	public Packet(String type, String reciever, String sender, String msg, String id, InetSocketAddress address, EncryptionType crypt)
	{
		super();
		this.args.put("type", type);
		this.args.put("reciever", reciever);
		this.args.put("sender", sender);
		this.args.put("msg", msg);
		this.args.put("id", id);
		this.address = address;
		this.crypt = crypt;
	}

}
