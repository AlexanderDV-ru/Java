package ru.alexandrdv.messenger.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.Iterator;

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

	public String getAddress()
	{
		return socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort();
	}

	boolean signedIn = false;

	public Client()
	{
		super();
		f.setTitle(getClass().getName() + " $" + Calendar.getInstance().getTimeInMillis() + " - Console");
		i = new Interface(this);
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					socket = new Socket("192.168.0.2", 25777);
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
									resendTo(writer, msgPacket.reciever, Encryptor.encrypt(msgPacket.msg, encryptionKey, EncryptionType.Server, EncryptionType.Double), EncryptionType.Double, msgPacket.sender);
								if (msgPacket.type == EncryptionType.Double)
									resendTo(writer, msgPacket.reciever, Encryptor.encrypt(msgPacket.msg, encryptionKey, EncryptionType.Double, EncryptionType.Server), EncryptionType.Server, msgPacket.sender);
								if (msgPacket.type == EncryptionType.Client)
								{
									println(Encryptor.encrypt(msgPacket.msg, encryptionKey, EncryptionType.Client, EncryptionType.None));
									i.chatsList.get(msgPacket.sender).addMsg(Encryptor.encrypt(msgPacket.msg, encryptionKey, EncryptionType.Client, EncryptionType.None).split("\n"), false);
								}
							}
							else if (p.packetType == PacketType.Query)
							{
								QueryPacket queryPacket = (QueryPacket) p;
								switch (queryPacket.query)
								{
									case "true":
									{
										reciever = lastToReciever;
										lastToReciever = "";
										i.addContactBtn(reciever);
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
										for (String s : queryPacket.argument.split(" "))
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
										if(i.tabbedPane.indexOfTab(" Contacts")==-1)
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
									String login = Encryptor.encrypt(signPacket.password, encryptionKey, EncryptionType.Double, EncryptionType.Server);
									String password = Encryptor.encrypt(signPacket.password, encryptionKey, EncryptionType.Double, EncryptionType.Server);
									writer.writeObject(new SignPacket(login, password, EncryptionType.Server, signPacket.sender, signPacket.signUp));
								}
							}
						}
					}
				}
				catch (Exception e)
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
							sendTo(writer, ((Chat) i.chats.getSelectedComponent()).user, Encryptor.encrypt(msg, encryptionKey, EncryptionType.None, EncryptionType.Client), EncryptionType.Client);
							((Chat) i.chats.getSelectedComponent()).addMsg(msg.replace("\\n", "\n").split("\n"), true);
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
					sendQuery(writer, args[1], null, EncryptionType.None, getAddress());
				}
				// TODO Create msg sys
				// else println(MsgSystem.get("notenoughargs").replace("%cmd%",
				// args[0]));
				break;
			case "exit":
				sendQuery(writer, "exit", null, EncryptionType.None, getAddress());
				try
				{
					socket.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
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
