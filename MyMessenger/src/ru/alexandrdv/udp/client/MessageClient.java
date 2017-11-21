package ru.alexandrdv.udp.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.print.attribute.standard.JobPrioritySupported;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import ru.alexandrdv.udp.Encryptor;

import ru.alexandrdv.udp.Encryptor.EncryptionType;
import ru.alexandrdv.udp.client.UDPClient.Sound;
import ru.alexandrdv.udpmessenger.Client;
import ru.alexandrdv.udpmessenger.ImagePack;
import ru.alexandrdv.udp.Packet;
import ru.alexandrdv.udp.Sounds;

public class MessageClient
{
	private static final Random random = new Random();
	public UDPClient udp;
	public int clientkey;

	public MessageClient(ActionListener listener)
	{
		clientkey = 1 + random.nextInt(999);
		udp = new UDPClient("94.181.44.135", 1, random.nextInt(50000) + 10000, (ev) ->
		{
			Object source = ev.getSource();
			if (source instanceof Packet)
			{
				Packet p = ((Packet) source);
				p.address = new InetSocketAddress(udp.clientAddress, udp.port);
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
					listener.actionPerformed(new ActionEvent(source, 1, "recieved"));
				}
			}
			else if (source instanceof Sound)
			{
				Sound sound = (Sound) source;
				try
				{
					byte[] mdata = sound.data;
					sounds.playClip(mdata);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else if(source instanceof ImagePack)
			{
				listener.actionPerformed(new ActionEvent(source, 2, "Image"));
			}
			

		});

		try
		{
			udp.send(Encryptor.encryptPacket(new Packet("joinPack", "server", null, null, new InetSocketAddress(InetAddress.getLocalHost(), udp.port), null),
					clientkey, EncryptionType.None, EncryptionType.Client));
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
				e1.printStackTrace();
			}
		System.out.println(i);
		System.out.println(udp.clientAddress);

	}

	public int[] lastImgBytes;
	public int lastImgIndex;

	public float soundVolumeLeft = 1.0f, soundVolumeRight = 0.8f;

	byte[][] soundes = new byte[4][Sounds.CHUNK_SIZE / 4];
	ArrayList<Integer> waiters = new ArrayList<Integer>();

	public void send(String type, String message, String sender, String reciever)
	{
		udp.send(Encryptor.encryptPacket(new Packet(type, reciever, sender, message, new InetSocketAddress(udp.clientAddress, udp.port), null), clientkey,
				EncryptionType.None, EncryptionType.Client));
	}

	public Sounds sounds = Client.sounds;
	public int otherSoundClient;

}
