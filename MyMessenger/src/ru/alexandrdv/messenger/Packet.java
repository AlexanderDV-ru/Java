package ru.alexandrdv.messenger;

import java.io.Serializable;

import ru.alexandrdv.messenger.Encryptor.EncryptionType;

public class Packet implements Serializable
{
	private static final long serialVersionUID = -5820873265290615784L;
	public String reciever, msg;
	public EncryptionType type;
	public Packet()
	{
		// TODO Auto-generated constructor stub
	}
	public Packet(String reciever, String msg, EncryptionType type)
	{
		super();
		this.reciever = reciever;
		this.msg = msg;
		this.type = type;
	}

}
