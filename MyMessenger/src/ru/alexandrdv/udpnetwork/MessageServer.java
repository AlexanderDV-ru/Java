package ru.alexandrdv.udpnetwork;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import ru.alexandrdv.udpnetwork.Encryptor.EncryptionType;
import ru.alexandrdv.udpnetwork.UDPClient.Packet;

/**
 * 
 * @author AlexandrDV
 *
 */
public class MessageServer
{
	UDPServer udp;
	int serverkey;
	public static final ArrayList<String> developers = new ArrayList<String>();
	static
	{
		developers.add("94.181.44.135");
	}
	HashMap<String, String> accounts = new HashMap<String, String>();

	public MessageServer(int port)
	{
		serverkey = 1 + new Random().nextInt(999);
		udp = new UDPServer(port, new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Packet p = ((Packet) e.getSource());
				if (p.crypt == EncryptionType.Double)
					udp.send(p.ip, p.port, Encryptor.encryptPacket(p, serverkey, p.crypt, EncryptionType.Client));
				else if (p.crypt == EncryptionType.Client)
					udp.send(p.ip, p.port, Encryptor.encryptPacket(p, serverkey, p.crypt, EncryptionType.Double));
				else if (p.crypt == EncryptionType.Server)
					Encryptor.encryptPacket(p, serverkey, p.crypt, EncryptionType.None);
				if (p.crypt == EncryptionType.None)
				{
					System.out.println(p.args.get("type"));
					switch (p.args.get("type"))
					{
						case "joinPack":
							udp.send(p.ip, p.port, Encryptor.encryptPacket(new Packet("joinPack", p.ip.getHostAddress() + ":" + p.port, p.ip.getHostAddress(),
									"", p.ip, p.port, EncryptionType.Server), serverkey, EncryptionType.None, EncryptionType.Server));
							if (developers.contains(p.ip.getHostAddress()))
							{
								Timer t = new Timer(0, new ActionListener()
								{

									@Override
									public void actionPerformed(ActionEvent e)
									{
										udp.send(p.ip, p.port, Encryptor.encryptPacket(new Packet("dev", p.ip.getHostAddress() + ":" + p.port, p.ip
												.getHostAddress(), "", p.ip, p.port, EncryptionType.Server), serverkey, EncryptionType.None,
												EncryptionType.Server));
									}
								});
								t.start();
								t.setRepeats(false);
							}
							break;
						case "msg":
							udp.send(p.ip, p.port, Encryptor.encryptPacket(new Packet("info", p.ip.getHostAddress() + ":" + p.port, "Succesfully sended", p.args
									.get("id"), p.ip, p.port, EncryptionType.Server), serverkey, EncryptionType.None, EncryptionType.Server));
							Timer t = new Timer(0, new ActionListener()
							{

								@Override
								public void actionPerformed(ActionEvent e)
								{
									try
									{
										udp.send(InetAddress.getByName(p.args.get("reciever").split(":")[0]), Integer.parseInt(p.args.get("reciever").split(
												":")[1]), Encryptor.encryptPacket(new Packet("msg", p.ip.getHostAddress() + ":" + p.port, p.args.get("msg"),
														p.args.get("id"), p.ip, p.port, EncryptionType.Server), serverkey, EncryptionType.None,
														EncryptionType.Server));
									}
									catch (NumberFormatException e1)
									{
										e1.printStackTrace();
									}
									catch (UnknownHostException e1)
									{
										e1.printStackTrace();
									}
								}
							});
							t.start();
							t.setRepeats(false);

							break;
						case "signin":
							if (accounts.containsKey(p.args.get("msg").split("\n")[0]))
								if (accounts.get(p.args.get("msg").split("\n")[0]).equals(p.args.get("msg").split("\n")[1].hashCode() + ""))
									udp.send(p.ip, p.port, Encryptor.encryptPacket(new Packet("signin", p.ip.getHostAddress() + ":" + p.port,
											"successfullySignedIn", p.args.get("id"), p.ip, p.port, EncryptionType.Server), serverkey, EncryptionType.None,
											EncryptionType.Server));
								else udp.send(p.ip, p.port, Encryptor.encryptPacket(new Packet("signin", p.ip.getHostAddress() + ":" + p.port, "wrongPassword",
										p.args.get("id"), p.ip, p.port, EncryptionType.Server), serverkey, EncryptionType.None, EncryptionType.Server));
							else udp.send(p.ip, p.port, Encryptor.encryptPacket(new Packet("signin", p.ip.getHostAddress() + ":" + p.port, "accountNotExists",
									p.args.get("id"), p.ip, p.port, EncryptionType.Server), serverkey, EncryptionType.None, EncryptionType.Server));
							break;
						case "signup":
							if (!accounts.containsKey(p.args.get("msg").split("\n")[0]))
							{
								accounts.put(p.args.get("msg").split("\n")[0], p.args.get("msg").split("\n")[1].hashCode() + "");
								udp.send(p.ip, p.port, Encryptor.encryptPacket(new Packet("signup", p.ip.getHostAddress() + ":" + p.port, "successfullySignedUp",
										p.args.get("id"), p.ip, p.port, EncryptionType.Server), serverkey, EncryptionType.None, EncryptionType.Server));
							}
							else udp.send(p.ip, p.port, Encryptor.encryptPacket(new Packet("signup", p.ip.getHostAddress() + ":" + p.port,
									"accountAlreadyExists", p.args.get("id"), p.ip, p.port, EncryptionType.Server), serverkey, EncryptionType.None,
									EncryptionType.Server));

							break;
					}
				}

			}
		});
	}

	public static void main(String[] args)
	{
		new MessageServer(/* Integer.parseInt(JOptionPane.showInputDialog("Enter port: ")) */1);
	}

}
