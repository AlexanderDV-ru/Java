package ru.alexandrdv.udp.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ru.alexandrdv.udp.Packet;
import ru.alexandrdv.udp.Encryptor;
import ru.alexandrdv.udp.Encryptor.EncryptionType;

/**
 * 
 * @author AlexandrDV
 *
 */
public class MessageServer
{
	private static final Random random = new Random();
	UDPServer udp;
	int serverkey;
	public static final ArrayList<String> developers = new ArrayList<String>();
	static
	{
		developers.add("94.181.44.135");
	}
	HashMap<String, String> accounts = new HashMap<String, String>();
	HashMap<String, ArrayList<InetSocketAddress>> users = new HashMap<String, ArrayList<InetSocketAddress>>();

	public MessageServer(int port)
	{
		serverkey = 1 + random.nextInt(999);
		udp = new UDPServer(port, new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Packet p = ((Packet) e.getSource());
				if (p.crypt == EncryptionType.Double)
					udp.send(p.address.getAddress(), p.address.getPort(), Encryptor.encryptPacket(p, serverkey, p.crypt, EncryptionType.Client));
				else if (p.crypt == EncryptionType.Client)
					udp.send(p.address.getAddress(), p.address.getPort(), Encryptor.encryptPacket(p, serverkey, p.crypt, EncryptionType.Double));
				else if (p.crypt == EncryptionType.Server)
					Encryptor.encryptPacket(p, serverkey, p.crypt, EncryptionType.None);
				if (p.crypt == EncryptionType.None)
				{
					switch (p.args.get("type"))
					{
						case "joinPack":
							send(p.address.getAddress(), p.address.getPort(), new Packet("joinPack", p.address.getAddress().getHostAddress() + ":" + p.address
									.getPort(), p.args.get("sender"), p.address.getAddress().getHostAddress(), "", p.address, null));
							if (developers.contains(p.address.getAddress().getHostAddress()))
								send(p.address.getAddress(), p.address.getPort(), new Packet("dev", p.address.getAddress().getHostAddress() + ":" + p.address
										.getPort(), p.args.get("sender"), p.address.getAddress().getHostAddress(), "", p.address, null));
							break;
						case "msg":
							send(p.address.getAddress(), p.address.getPort(), new Packet("info", p.address.getAddress().getHostAddress() + ":" + p.address
									.getPort(), p.args.get("sender"), "Succesfully sended", p.args.get("id"), p.address, null));
							if (users.containsKey(p.args.get("reciever")))
								for (InetSocketAddress user : users.get(p.args.get("reciever")))
									send(user.getAddress(), user.getPort(), new Packet("msg", user.getAddress() + ":" + user.getPort(), p.args.get("sender"),
											p.args.get("msg"), p.args.get("id"), p.address, null));

							break;
						case "signin":
						{
							String login = p.args.get("msg").split("\n")[0];
							String password = p.args.get("msg").split("\n")[1];
							String sender = p.address.getAddress().getHostAddress() + ":" + p.address.getPort();
							if (accounts.containsKey(login))
								if (accounts.get(login).equals(password.hashCode() + ""))
								{
									if (users.containsKey(login) && users.get(login).contains(sender))
										send(p.address.getAddress(), p.address.getPort(), new Packet("signin", sender, p.args.get("sender"),
												"youAlreadySignedIn", p.args.get("id"), p.address, null));
									else
									{
										send(p.address.getAddress(), p.address.getPort(), new Packet("signin", sender, p.args.get("sender"),
												"successfullySignedIn", p.args.get("id"), p.address, null));
										if (!users.containsKey(login))
											users.put(login, new ArrayList<InetSocketAddress>());
										ArrayList<InetSocketAddress> userIps = users.get(login);
										userIps.add(p.address);
									}
								}
								else send(p.address.getAddress(), p.address.getPort(), new Packet("signin", sender, p.args.get("sender"), "wrongPassword",
										p.args.get("id"), p.address, null));
							else send(p.address.getAddress(), p.address.getPort(), new Packet("signin", sender, p.args.get("sender"), "accountNotExists", p.args
									.get("id"), p.address, null));
						}
							break;
						case "signup":
						{
							String login = p.args.get("msg").split("\n")[0];
							String password = p.args.get("msg").split("\n")[1];
							String sender = p.address.getAddress().getHostAddress() + ":" + p.address.getPort();
							if (!accounts.containsKey(login))
							{
								accounts.put(login, password.hashCode() + "");
								send(p.address.getAddress(), p.address.getPort(), new Packet("signup", sender, p.args.get("sender"), "successfullySignedUp",
										p.args.get("id"), p.address, null));
							}
							else send(p.address.getAddress(), p.address.getPort(), new Packet("signup", sender, p.args.get("sender"), "accountAlreadyExists",
									p.args.get("id"), p.address, null));
						}
							break;
					}
				}

			}
		});
	}

	int timeToSend = 0;

	public void send(InetAddress ip, int port, Packet packet)
	{
		Encryptor.encryptPacket(packet, serverkey, EncryptionType.None, EncryptionType.Server);
		udp.send(ip, port, packet);
	}

	public static void main(String[] args)
	{
		new MessageServer(1);
	}

}
