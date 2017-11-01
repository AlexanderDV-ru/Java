package ru.alexandrdv.udp.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JOptionPane;

import ru.alexandrdv.udp.Encryptor;
import ru.alexandrdv.udp.Encryptor.EncryptionType;
import ru.alexandrdv.udp.Packet;

public class MessageClient
{
	private static final Random random = new Random();
	UDPClient udp;
	int clientkey;
	public HashMap<String, String> msgs = new HashMap<String, String>();

	public MessageClient(ActionListener listener)
	{
		clientkey = 1 + random.nextInt(999);
		udp = new UDPClient("94.181.44.135", 1, random.nextInt(50000) + 10000, new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Packet p = ((Packet) e.getSource());
				p.address=new InetSocketAddress(udp.clientAddress, udp.port);
				if (p.crypt == EncryptionType.Double)
					udp.send(Encryptor.encryptPacket(p, clientkey, p.crypt, EncryptionType.Server));
				else if (p.crypt == EncryptionType.Server)
					udp.send(Encryptor.encryptPacket(p, clientkey, p.crypt, EncryptionType.Double));
				else if (p.crypt == EncryptionType.Client)
					Encryptor.encryptPacket(p, clientkey, p.crypt, EncryptionType.None);
				if (p.crypt == EncryptionType.None)
				{

					if (udp.clientAddress == null)
					{
						try
						{
							udp.clientAddress = InetAddress.getByName(p.args.get("msg"));
						}
						catch (UnknownHostException e1)
						{
							e1.printStackTrace();
						}
					}
					listener.actionPerformed(new ActionEvent(p, 1, "recieved"));
				}
			}
		});

		try
		{
			udp.send(Encryptor.encryptPacket(new Packet("joinPack", "server",null, null, null, new InetSocketAddress(InetAddress.getLocalHost(), udp.port),
					null), clientkey, EncryptionType.None, EncryptionType.Client));
		}
		catch (UnknownHostException e1)
		{
			e1.printStackTrace();
		}
		int i = 0;
		for (; udp.clientAddress == null; i++)
			try
			{
				Thread.sleep(1);
				if (i > 3000)
				{
					JOptionPane.showMessageDialog(null,
							"Error: could not connect to the server!\nCheck your internet connection,\nprobably this server does not exist or isn't online.",
							"Title", 0);
					System.exit(1);
				}
			}
			catch (InterruptedException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		System.out.println(i);
		System.out.println(udp.clientAddress);

	}

	ArrayList<Integer> waiters = new ArrayList<Integer>();

	public void send(String type, String message, String sender, String reciever, String id)
	{
		udp.send(Encryptor.encryptPacket(new Packet(type, reciever,sender, message, id, new InetSocketAddress(udp.clientAddress, udp.port), null), clientkey,
				EncryptionType.None, EncryptionType.Client));
		if (type.equals("msg"))
			msgs.put(id, message);
	}

}
