package ru.alexandrdv.messenger;

import java.io.Serializable;

import ru.alexandrdv.messenger.Encryptor.EncryptionType;

public class Packet implements Serializable
{
	private static final long serialVersionUID = -5820873265290615784L;
	public final boolean isQuery;
	public final String sender;
	public EncryptionType type;

	public Packet(boolean isQuery, EncryptionType type,String sender)
	{
		super();
		this.isQuery = isQuery;
		this.sender=sender;
		this.type = type;
	}

	static public class QueryPacket extends Packet
	{
		private static final long serialVersionUID = 9036846116178336866L;
		public String query;

		public QueryPacket(String query, EncryptionType type,String sender)
		{
			super(true, type,sender);
			this.query = query;
		}

	}

	static public class MessagePacket extends Packet
	{
		private static final long serialVersionUID = -8520455807015143770L;
		public String reciever, msg;

		public MessagePacket(String reciever, String msg, EncryptionType type,String sender)
		{
			super(false, type,sender);
			this.reciever = reciever;
			this.msg = msg;
		}
	}
}
