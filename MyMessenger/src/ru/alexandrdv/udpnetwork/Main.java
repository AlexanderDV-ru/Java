package ru.alexandrdv.udpnetwork;

import java.awt.Button;
import java.awt.SecondaryLoop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import javax.swing.Timer;

import ru.alexandrdv.udpnetwork.UDPClient.Packet;
import ru.alexandrdv.udpnetwork.Encryptor.EncryptionType;

public class Main
{

	public static void main(String[] args)
	{

		new Main();
	}

	UDPServer s;
	UDPClient c;

	public Main()
	{
		s = new UDPServer(20000, new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Packet p = ((Packet) e.getSource());
				if (p.crypt == EncryptionType.Double)
					s.send(p.ip, p.port, Encryptor.encryptPacket(p, serverkey, p.crypt, EncryptionType.Client));
				else if (p.crypt == EncryptionType.Client)
					s.send(p.ip, p.port, Encryptor.encryptPacket(p, serverkey, p.crypt, EncryptionType.Double));
				else if (p.crypt == EncryptionType.Server)
					Encryptor.encryptPacket(p, serverkey, p.crypt, EncryptionType.None);
				if (p.crypt == EncryptionType.None)
				{
					if (p.args.get("type").equals("joinPack"))
						s.send(p.ip, p.port, Encryptor.encryptPacket(new Packet("joinPack",p.ip.getHostAddress()+":"+p.port,p.ip.getHostAddress(),"", p.ip, p.port, EncryptionType.Server), serverkey,
								EncryptionType.None, EncryptionType.Server));
					else
					{
						System.out.println("Client says: " + p.args.get("msg"));
						s.send(p.ip, p.port, Encryptor.encryptPacket(new Packet("info",p.ip.getHostAddress()+":"+p.port,"Succesfully sended",p.args.get("id"), p.ip, p.port, EncryptionType.Server), serverkey,
								EncryptionType.None, EncryptionType.Server));
					}
				}

			}
		});
		c = new UDPClient("94.181.44.135", 20000, 10, new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Packet p = ((Packet) e.getSource());
				if (p.crypt == EncryptionType.Double)
					c.send(Encryptor.encryptPacket(p, clientkey, p.crypt, EncryptionType.Server));
				else if (p.crypt == EncryptionType.Server)
					c.send(Encryptor.encryptPacket(p, clientkey, p.crypt, EncryptionType.Double));
				else if (p.crypt == EncryptionType.Client)
					Encryptor.encryptPacket(p, clientkey, p.crypt, EncryptionType.None);
				if (p.crypt == EncryptionType.None)
				{
					if (clientIp == null)
					{
						try
						{
							clientIp = InetAddress.getByName(p.args.get("msg"));
						}
						catch (UnknownHostException e1)
						{
							e1.printStackTrace();
						}
					}
					else if(p.args.get("msg").startsWith("Succesfully sended"))
						System.out.println("You: "+msgs.remove(p.args.get("id")));
					else System.out.println("Server says: " + p.args.get("msg"));
					if(msgs.keySet().size()==0)
						clientkey = new Random().nextInt(999);
				}
			}
		});

		try
		{
			c.send(Encryptor.encryptPacket(new Packet("joinPack","server","none","none", InetAddress.getLocalHost(), c.port, null), clientkey, EncryptionType.None,
					EncryptionType.Client));
		}
		catch (UnknownHostException e1)
		{
			e1.printStackTrace();
		}
		int i = 0;
		for (; clientIp == null; i++)
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		System.out.println(i);
		for (int i2 = 0; i2 < 10; i2++)
			sendMsg(genWord(new Random().nextInt(5)+3),UUID.randomUUID().toString());

	}
	public void sendMsg(String message,String id)
	{
		c.send(Encryptor.encryptPacket(new Packet("message","none",message,id, clientIp, c.port, null), clientkey, EncryptionType.None,
				EncryptionType.Client));
		msgs.put(id, message);
	}
	HashMap<String,String> msgs=new HashMap<String,String>();

	int serverkey = new Random().nextInt(999);
	int clientkey = new Random().nextInt(999);

	public static String genWord(int length)
	{
		char[] letters = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		String aaaa = "aeiouy";
		String bbbb = "bcdfghjklmnpqrstvwxz";
		char last = letters[new Random().nextInt(letters.length)], llast = ' ', now = ' ';
		String word = "" + last;
		while (word.length() < length)
		{
			if (aaaa.contains(last + "") || aaaa.contains(llast + ""))
				now = bbbb.charAt(new Random().nextInt(bbbb.length()));
			if (bbbb.contains(last + "") && !bbbb.contains(llast + ""))
				now = aaaa.charAt(new Random().nextInt(aaaa.length()));
			llast = last;
			last = now;
			word += now;
		}
		return word;
	}

	InetAddress clientIp = null;
}
