package ru.alexandrdv.messenger;

import java.io.Serializable;
import java.util.HashMap;

import ru.alexandrdv.messenger.Encryptor.EncryptionType;

public class Packet implements Serializable
{
	private static final long serialVersionUID = -5820873265290615784L;
	public final PacketType packetType;
	public HashMap<String, String> args=new HashMap<String, String>();
	public EncryptionType type;

	public Packet(PacketType packetType, EncryptionType type, String sender)
	{
		super();
		this.packetType = packetType;
		args.put("sender", sender);
		this.type = type;
	}

	static public class QueryPacket extends Packet
	{
		private static final long serialVersionUID = 9036846116178336866L;

		public QueryPacket(String query, String argument, EncryptionType type, String sender)
		{
			super(PacketType.Query, type, sender);
			args.put("query", query);
			args.put("argument", argument);
		}

	}

	static public class SignPacket extends Packet
	{
		private static final long serialVersionUID = -1224420620462717795L;
		public boolean signUp;

		public SignPacket(String login, String password, EncryptionType type, String sender, boolean signUp)
		{
			super(PacketType.Sign, type, sender);
			args.put("login", login);
			args.put("password", password);
			this.signUp = signUp;
		}

	}

	static public class MessagePacket extends Packet
	{
		private static final long serialVersionUID = -8520455807015143770L;

		public MessagePacket(String reciever, String msg, EncryptionType type, String sender)
		{
			super(PacketType.Message, type, sender);
			args.put("reciever", reciever);
			args.put("msg", msg);
		}
	}

	public static enum PacketType
	{
		Message,
		Query,
		Sign
	}
}
