package ru.alexandrdv.messenger;

import java.io.Serializable;

import ru.alexandrdv.messenger.Encryptor.EncryptionType;

public class Packet implements Serializable
{
	private static final long serialVersionUID = -5820873265290615784L;
	public final PacketType packetType;
	public final String sender;
	public EncryptionType type;

	public Packet(PacketType packetType, EncryptionType type, String sender)
	{
		super();
		this.packetType = packetType;
		this.sender = sender;
		this.type = type;
	}

	static public class QueryPacket extends Packet
	{
		private static final long serialVersionUID = 9036846116178336866L;
		public String query, argument;

		public QueryPacket(String query, String argument, EncryptionType type, String sender)
		{
			super(PacketType.Query, type, sender);
			this.query = query;
			this.argument = argument;
		}

	}

	static public class SignPacket extends Packet
	{
		private static final long serialVersionUID = -1224420620462717795L;
		public String login;
		public String password;
		public boolean signUp;

		public SignPacket(String login, String password, EncryptionType type, String sender, boolean signUp)
		{
			super(PacketType.Sign, type, sender);
			this.login = login;
			this.password = password;
			this.signUp = signUp;
		}

	}

	static public class MessagePacket extends Packet
	{
		private static final long serialVersionUID = -8520455807015143770L;
		public String reciever, msg;

		public MessagePacket(String reciever, String msg, EncryptionType type, String sender)
		{
			super(PacketType.Message, type, sender);
			this.reciever = reciever;
			this.msg = msg;
		}
	}

	public static enum PacketType
	{
		Message,
		Query,
		Sign
	}
}
