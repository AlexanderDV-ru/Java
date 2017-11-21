package ru.alexandrdv.udp.server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

import ru.alexandrdv.udp.Encryptor;
import ru.alexandrdv.udp.Encryptor.EncryptionType;
import ru.alexandrdv.udp.Packet;
import ru.alexandrdv.udp.Packet.LoginPacket;
import ru.alexandrdv.udpmessenger.Account;
import ru.alexandrdv.udpmessenger.ImagePack;

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
	HashMap<String, Account> accounts = new HashMap<String, Account>();
	HashMap<String, ArrayList<InetSocketAddress>> users = new HashMap<String, ArrayList<InetSocketAddress>>();
	HashMap<String, ArrayList<InetSocketAddress>> toDisconnect = new HashMap<String, ArrayList<InetSocketAddress>>();

	public MessageServer(int port)
	{
		loadAccounts();
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(3);
		f.setVisible(true);
		f.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				saveAccounts();
			}
		});
		serverkey = 1 + random.nextInt(999);
		udp = new UDPServer(port, (e) ->
		{
			Object source = e.getSource();
			if (source instanceof Packet)
			{
				Packet p = ((Packet) source);
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
									.getPort(), p.args.get("sender"), p.address.getAddress().getHostAddress(), p.address, null));
							if (developers.contains(p.address.getAddress().getHostAddress()))
								send(p.address.getAddress(), p.address.getPort(), new Packet("dev", p.address.getAddress().getHostAddress() + ":" + p.address
										.getPort(), p.args.get("sender"), p.address.getAddress().getHostAddress(), p.address, null));
							break;
						case "msg":
							if (users.containsKey(p.args.get("sender")))
								for (InetSocketAddress user : users.get(p.args.get("sender")))
									send(user.getAddress(), user.getPort(), new Packet("msg", user.getAddress() + ":" + user.getPort(), p.args.get("sender"),
											p.args.get("msg"), p.address, null));

							if (users.containsKey(p.args.get("reciever")))
								for (InetSocketAddress user : users.get(p.args.get("reciever")))
									send(user.getAddress(), user.getPort(), new Packet("msg", user.getAddress() + ":" + user.getPort(), p.args.get("sender"),
											p.args.get("msg"), p.address, null));

							break;
						case "update":
							if (toDisconnect.containsKey(p.args.get("sender")))
							{
								toDisconnect.get(p.args.get("sender")).remove(p.address);
								if (toDisconnect.get(p.args.get("sender")).size() == 0)
									toDisconnect.remove(p.args.get("sender"));
							}
							else send(p.address.getAddress(), p.address.getPort(), new Packet("signin", null, p.args.get("sender"), "accountNotExists",
									p.address, null));
							break;
						case "onlines":
							String onlines = "";
							for (String user : users.keySet())
								onlines += user + "\n";
							send(p.address.getAddress(), p.address.getPort(), new Packet("onlines", null, null, onlines, null, null));
							break;
						case "signin":
						{
							String login = p.args.get("msg").split("\n")[0];
							String password = p.args.get("msg").split("\n")[1];
							String sender = p.address.getAddress().getHostAddress() + ":" + p.address.getPort();
							if (accounts.containsKey(login))
								if (accounts.get(login).password.equals(password.hashCode() + ""))
								{
									if (users.containsKey(login) && users.get(login).contains(sender))
										send(p.address.getAddress(), p.address.getPort(), new Packet("signin", null, p.args.get("sender"), "youAlreadySignedIn",
												p.address, null));
									else
									{
										send(p.address.getAddress(), p.address.getPort(), new Packet("signin", null, p.args.get("sender"),
												"successfullySignedIn", p.address, null));
										if (!users.containsKey(login))
											users.put(login, new ArrayList<InetSocketAddress>());
										ArrayList<InetSocketAddress> userIps = users.get(login);
										userIps.add(p.address);
										Account acc = new Account(accounts.get(login));
										acc.password = password;
										send(p.address.getAddress(), p.address.getPort(), new LoginPacket("changeaccountinfo", null, "accountInfo", p.address,
												null, acc));
									}
								}
								else send(p.address.getAddress(), p.address.getPort(), new Packet("signin", null, p.args.get("sender"), "wrongPassword",
										p.address, null));
							else send(p.address.getAddress(), p.address.getPort(), new Packet("signin", null, p.args.get("sender"), "accountNotExists",
									p.address, null));
						}
							break;
						case "changeaccountinfo":
						{
							String login = p.args.get("msg").split("\n")[0];
							String password = p.args.get("msg").split("\n")[1];
							if (accounts.containsKey(login))
								if (accounts.get(login).password.equals(password.hashCode() + ""))
								{
									accounts.remove(login);
									accounts.put(login, new Account(login, p.args.get("password").hashCode() + "", p.args.get("name"), p.args.get("surname"),
											p.args.get("secondname"), p.args.get("gender"), p.args.get("age"), p.args.get("state"), p.args.get("phone"), p.args
													.get("email")));
									p.args.remove("msg");
									p.args.put("msg", "accountInfoChanged");
									if (!password.equals(p.args.get("password")))
									{
										for (InetSocketAddress user : users.get(login))
										{
											if (user != p.address)
												send(user.getAddress(), user.getPort(), new Packet("changeaccountinfo", null, null, "passwordChanged", user,
														null));
											else send(user.getAddress(), user.getPort(), p);
										}
										users.get(login).clear();
										users.get(login).add(p.address);
									}
									else for (InetSocketAddress user : users.get(login))
										send(user.getAddress(), user.getPort(), p);

								}
								else send(p.address.getAddress(), p.address.getPort(), new Packet("changeaccountinfo", null, p.args.get("sender"),
										"wrongPassword", p.address, null));
							else send(p.address.getAddress(), p.address.getPort(), new Packet("changeaccountinfo", null, p.args.get("sender"),
									"accountNotExists", p.address, null));
						}
							break;
						case "signup":
						{
							String login = p.args.get("msg").split("\n")[0];
							String password = p.args.get("msg").split("\n")[1];
							if (!accounts.containsKey(login))
							{
								accounts.put(login, new Account(login, password.hashCode() + "", "", "", "", "", "", "", "", ""));
								send(p.address.getAddress(), p.address.getPort(), new Packet("signup", null, p.args.get("sender"), "successfullySignedUp",
										p.address, null));
							}
							else send(p.address.getAddress(), p.address.getPort(), new Packet("signup", null, p.args.get("sender"), "accountAlreadyExists",
									p.address, null));
						}
							break;
					}
				}
			}
			else if (source instanceof ImagePack)
			{
				ImagePack imgPack = (ImagePack) source;
				if (users.containsKey(imgPack.login))
					for (InetSocketAddress user : users.get(imgPack.login))
						send(user.getAddress(), user.getPort(), imgPack);
				// s.send(new DatagramPacket(data, data.length, pac.getAddress(), pac.getPort()));// отправление пакета
			}

		});
		Timer timer = new Timer(10000, (e) ->
		{
			for (String tDcnt : toDisconnect.keySet())
			{
				for (InetSocketAddress address : toDisconnect.get(tDcnt))
					users.get(tDcnt).remove(address);
				if (users.get(tDcnt).size() == 0)
					users.remove(tDcnt);
			}
			toDisconnect.clear();
			for (String user : users.keySet())
			{
				toDisconnect.put(user, new ArrayList<InetSocketAddress>());
				for (InetSocketAddress address : users.get(user))
				{
					toDisconnect.get(user).add(address);
					send(address.getAddress(), address.getPort(), new Packet("update", null, null, null, null, null));
				}
			}
		});
		timer.start();
	}

	String accountsFilePath = "accounts.cfg";

	public void saveAccounts()
	{
		try
		{
			File file = new File(accountsFilePath);
			if (!file.exists())
				file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(accounts);
			oos.close();
			fos.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void loadAccounts()
	{
		try
		{
			File file = new File(accountsFilePath);
			if (!file.exists())
				file.createNewFile();
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object readed = ois.readObject();
			if (readed instanceof HashMap<?, ?>)
				accounts = (HashMap<String, Account>) readed;
			ois.close();
			fis.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (ClassCastException e)
		{
			e.printStackTrace();
		}
	}

	int timeToSend = 0;

	public void send(InetAddress ip, int port, Packet packet)
	{
		Encryptor.encryptPacket(packet, serverkey, EncryptionType.None, EncryptionType.Server);
		udp.send(ip, port, packet);
	}public void send(InetAddress ip, int port, Object packet)
	{
		udp.send(ip, port, packet);
	}

	public static void main(String[] args)
	{
		new MessageServer(1);
	}

}
