package ru.alexandrdv.messenger.client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Random;

import javax.swing.JOptionPane;

import ru.alexandrdv.messenger.CmdGUI;
import ru.alexandrdv.messenger.Encryptor;
import ru.alexandrdv.messenger.Encryptor.EncryptionType;
import ru.alexandrdv.messenger.Packet;
import ru.alexandrdv.messenger.Packet.MessagePacket;
import ru.alexandrdv.messenger.Packet.PacketType;
import ru.alexandrdv.messenger.Packet.QueryPacket;
import ru.alexandrdv.messenger.Packet.SignPacket;

public class Client extends CmdGUI
{

	ObjectOutputStream writer;
	public Socket socket;
	public int encryptionKey = 14;
	Interface i;
	String ipport="";

	public String getAddress()
	{
		return ipport;
	}

	boolean signedIn = false;

	public Client()
	{
		super();
		f.setTitle(getClass().getName() + " $" + Calendar.getInstance().getTimeInMillis() + " - Console");
		WindowAdapter wl = new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				sendQuery(writer, "exit", null, EncryptionType.None, getAddress());
				try
				{
					Thread.sleep(10);
				}
				catch (InterruptedException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		f.addWindowListener(wl);
		i = new Interface(this);
		i.addWindowListener(wl);
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					socket = new Socket("94.181.44.135", 25777);
					writer = new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
					println("You're working on " + getAddress());
					Packet p;
					for (; socket != null;)
					{
						if ((p = (Packet) reader.readObject()) != null)
						{
							if (p.packetType == PacketType.Message)
							{
								MessagePacket msgPacket = (MessagePacket) p;
								if (msgPacket.type == EncryptionType.Server)
									resendTo(writer, msgPacket.args.get("reciever"), Encryptor.encrypt(msgPacket.args.get("msg"), encryptionKey, EncryptionType.Server, EncryptionType.Double), EncryptionType.Double, msgPacket.args.get("sender"));
								if (msgPacket.type == EncryptionType.Double)
									resendTo(writer, msgPacket.args.get("reciever"), Encryptor.encrypt(msgPacket.args.get("msg"), encryptionKey, EncryptionType.Double, EncryptionType.Server), EncryptionType.Server, msgPacket.args.get("sender"));
								if (msgPacket.type == EncryptionType.Client)
								{
									println(Encryptor.encrypt(msgPacket.args.get("msg"), encryptionKey, EncryptionType.Client, EncryptionType.None));
									i.chatsList.get(msgPacket.args.get("sender")).addMsg(Encryptor.encrypt(msgPacket.args.get("msg"), encryptionKey, EncryptionType.Client, EncryptionType.None).split("\n"), false);
								}
							}
							else if (p.packetType == PacketType.Query)
							{
								QueryPacket queryPacket = (QueryPacket) p;
								switch (queryPacket.args.get("query"))
								{
									case "true":
									{
										reciever = lastToReciever;
										lastToReciever = "";
										// i.addContactBtn(reciever);
									}
										break;
									case "yourip":
									{
										ipport=queryPacket.args.get("argument");
										sendQuery(writer, "ihaveip", "", Encryptor.EncryptionType.None, ipport);
									}
										break;
									case "false":
									{
										reciever = "";
										lastToReciever = "";
										println("This user isn't online or hasn't this programm!!");
									}
										break;
									case "onlineips":
									{
										for (String s : queryPacket.args.get("argument").split(" "))
											i.addContactBtn(s);
									}
										break;
									case "Account verified":
									{
										println("Account verified");
										signedIn = true;
										login = lastLogin;
										password = lastPassword;
										lastLogin = "";
										lastPassword = "";
										if (i.tabbedPane.indexOfTab(" Contacts") == -1)
											i.tabbedPane.addTab(" Contacts", null, i.contacts, null);
										i.messages.setVisible(true);
										i.mntmSignIn.setVisible(false);
										i.mntmAccount.setVisible(true);
									}
										break;
									case "Account not exists":
									{
										println("Account not exists!");
									}
										break;
									case "Wrong password":
									{
										println("Password is wrong!");
									}
										break;

									case "Account created":
									{
										println("Account successfully created!");
										i.mntmSignIn.setVisible(true);
									}
										break;
									case "Account already exists":
									{
										println("Account already exists!");
									}
										break;
								}
							}
							else if (p.packetType == PacketType.Sign)
							{
								SignPacket signPacket = (SignPacket) p;
								if (signPacket.type == EncryptionType.Double)
								{
									String login = Encryptor.encrypt(signPacket.args.get("password"), encryptionKey, EncryptionType.Double, EncryptionType.Server);
									String password = Encryptor.encrypt(signPacket.args.get("password"), encryptionKey, EncryptionType.Double, EncryptionType.Server);
									writer.writeObject(new SignPacket(login, password, EncryptionType.Server, signPacket.args.get("sender"), signPacket.signUp));
								}
							}
						}
					}
				}
				catch (UnknownHostException | ConnectException e)
				{
					JOptionPane.showMessageDialog(null, "Can't connect to server! Report about this into our site, please.", f.getTitle(), 0);
					System.exit(1);
				}
				catch (ClassNotFoundException | IOException e)
				{
					e.printStackTrace();
				}

			}
		}).start();
		try
		{

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	String login = "", password = "", lastLogin = "", lastPassword = "";

	public static void main(String[] args)
	{
		new Client();

	}

	String reciever = "", lastToReciever = "";

	@Override
	public void Command(String[] args)
	{
		switch (args[0])
		{
			case "addbtn":
				i.addContactBtn(""+new Random().nextInt(100000));
				break;
			case "send":

				if (args.length > 1)
				{
					if (signedIn)
					{
						if (reciever != null && !reciever.equals(""))
						{
							String msg = "";
							for (int i = 1; i < args.length; i++)
								msg += args[i] + " ";
							sendTo(writer, reciever, Encryptor.encrypt(msg, encryptionKey, EncryptionType.None, EncryptionType.Client), EncryptionType.Client);
							i.chatsList.get(reciever).addMsg(msg.replace("\\n", "\n").split("\n"), true);
						}
					}
					else println("You weren't sign in!");
				}
				// TODO Create msg sys
				// else println(MsgSystem.get("notenoughargs").replace("%cmd%",
				// args[0]));
				break;
			case "joinTo":

				if (args.length > 1)
				{
					args[1] = args[1].replace("lh", "192.168.0.2");
					lastToReciever = args[1];
					reciever = "";
					sendQuery(writer, "isonline", args[1], EncryptionType.None, getAddress());
				}
				// TODO Create msg sys
				// else println(MsgSystem.get("notenoughargs").replace("%cmd%",
				// args[0]));
				break;
		}
	}

	public boolean sendTo(ObjectOutputStream os, String reciever, String msg, EncryptionType crypt)
	{
		try
		{

			os.writeObject(new MessagePacket(reciever, msg, crypt, getAddress()));
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public boolean resendTo(ObjectOutputStream os, String reciever, String msg, EncryptionType crypt, String sender)
	{
		try
		{

			os.writeObject(new MessagePacket(reciever, msg, crypt, sender));
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendQuery(ObjectOutputStream os, String query, String argument, EncryptionType crypt, String sender)
	{
		try
		{

			os.writeObject(new QueryPacket(query, argument, crypt, sender));
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

}
