package ru.alexandrdv.messenger.client;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;

import javax.swing.JOptionPane;

import ru.alexandrdv.messenger.CmdGUI;
import ru.alexandrdv.messenger.Encryptor;
import ru.alexandrdv.messenger.Encryptor.EncryptionType;
import ru.alexandrdv.messenger.Packet;
import ru.alexandrdv.messenger.Packet.MessagePacket;
import ru.alexandrdv.messenger.Packet.QueryPacket;
import ru.alexandrdv.messenger.client.Interface.LineType;

public class Client extends CmdGUI
{

	ObjectOutputStream writer;
	private Socket socket;
	private int encryptionKey = 14;
	Interface i;

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
					println("You're working on " + socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort());
					Packet p;
					for (; socket != null;)
					{
						System.out.println(writer);
						if ((p = (Packet) reader.readObject()) != null)
						{
							if (!p.isQuery)
							{
								MessagePacket msgPacket = (MessagePacket) p;
								if (msgPacket.type == EncryptionType.Server)
									sendTo(writer, msgPacket.reciever, Encryptor.encrypt(msgPacket.msg, encryptionKey, EncryptionType.Server, EncryptionType.Double), EncryptionType.Double);
								if (msgPacket.type == EncryptionType.Double)
									sendTo(writer, msgPacket.reciever, Encryptor.encrypt(msgPacket.msg, encryptionKey, EncryptionType.Double, EncryptionType.Server), EncryptionType.Server);
								if (msgPacket.type == EncryptionType.Client)
								{
									println(Encryptor.encrypt(msgPacket.msg, encryptionKey, EncryptionType.Client, EncryptionType.None));
									i.addMsg(Encryptor.encrypt(msgPacket.msg, encryptionKey, EncryptionType.Client, EncryptionType.None).split("\n"), false);
								}
							}
							else
							{
								QueryPacket queryPacket = (QueryPacket) p;
								if (queryPacket.query.equals("true"))
								{
									reciever = lastToReciever;
									lastToReciever = "";
								}
								else if (queryPacket.query.equals("false"))
								{
									reciever = "";
									lastToReciever = "";
									JOptionPane.showMessageDialog(null, "This user isn't online or hasn't this programm!!");
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
			case "sendTo":

				if (args.length > 2)
				{
					String reciever = args[1];
					String msg = "";
					for (int i = 2; i < args.length; i++)
						msg += args[i] + " ";
					sendTo(writer, reciever, Encryptor.encrypt(msg, encryptionKey, EncryptionType.None, EncryptionType.Client), EncryptionType.Client);
					i.addMsg(msg.replace("\\n", "\n").split("\n"), true);
				}
				// TODO Create msg sys
				// else println(MsgSystem.get("notenoughargs").replace("%cmd%",
				// args[0]));
				break;
			case "send":

				if (args.length > 1)
				{
					String msg = "";
					for (int i = 1; i < args.length; i++)
						msg += args[i] + " ";
					sendTo(writer, reciever, Encryptor.encrypt(msg, encryptionKey, EncryptionType.None, EncryptionType.Client), EncryptionType.Client);
					i.addMsg(msg.replace("\\n", "\n").split("\n"), true);
				}
				// TODO Create msg sys
				// else println(MsgSystem.get("notenoughargs").replace("%cmd%",
				// args[0]));
				break;
			case "joinTo":

				if (args.length > 1)
				{
					lastToReciever = args[1];
					reciever="";
					sendQuery(writer, args[1], EncryptionType.None);
					i.addContactBtn(args[1]);
				}
				// TODO Create msg sys
				// else println(MsgSystem.get("notenoughargs").replace("%cmd%",
				// args[0]));
				break;
			case "exit":
				sendQuery(writer, "exit", EncryptionType.None);
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

	private boolean sendTo(ObjectOutputStream os, String reciever, String msg, EncryptionType crypt)
	{
		try
		{

			os.writeObject(new MessagePacket(reciever, msg, crypt));
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	private boolean sendQuery(ObjectOutputStream os, String query, EncryptionType crypt)
	{
		try
		{

			os.writeObject(new QueryPacket(query, crypt));
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

}
