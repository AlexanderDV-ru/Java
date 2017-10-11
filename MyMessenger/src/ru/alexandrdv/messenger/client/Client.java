package ru.alexandrdv.messenger.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;

import ru.alexandrdv.messenger.CmdGUI;
import ru.alexandrdv.messenger.Encryptor;
import ru.alexandrdv.messenger.Encryptor.EncryptionType;
import ru.alexandrdv.messenger.Packet;

public class Client extends CmdGUI
{

	private ObjectOutputStream writer;
	private Socket socket;
	private int encryptionKey=14;

	public Client()
	{
		super();
		f.setTitle(getClass().getName() + " $" + Calendar.getInstance().getTimeInMillis() + " - Console");
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					socket = new Socket("192.168.0.2", 25777);
						writer = new ObjectOutputStream(socket.getOutputStream());
						ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
					 println("You're working on " +
					 socket.getLocalAddress().getHostAddress() + ":" +
					 socket.getLocalPort());
					Packet p;
					for (; socket != null;)
					{
						System.out.println(writer);
						if ((p = (Packet) reader.readObject()) != null)
						{
							if (p.msg.equals("exit"))
							{
								reader.close();
								socket.close();
								return;
							}
							else
							{
								if (p.type == EncryptionType.Server)
									sendTo(writer,p.reciever, Encryptor.encrypt(p.msg, encryptionKey, EncryptionType.Server, EncryptionType.Double), EncryptionType.Double);
								if (p.type == EncryptionType.Double)
									sendTo(writer,p.reciever, Encryptor.encrypt(p.msg, encryptionKey, EncryptionType.Double, EncryptionType.Server), EncryptionType.Server);
								if (p.type == EncryptionType.Client)
									println(Encryptor.encrypt(p.msg, encryptionKey, EncryptionType.Client, EncryptionType.None));
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

	@Override
	public void Command(String[] args)
	{
		switch (args[0])
		{
			case "send":

				if (args.length > 2)
				{
					String reciever = args[1];
					String msg = "";
					for (int i = 2; i < args.length; i++)
						msg += args[i] + " ";
					sendTo(writer,reciever, Encryptor.encrypt(msg, encryptionKey, EncryptionType.None, EncryptionType.Client), EncryptionType.Client);
				}
				// TODO Create msg sys
				// else println(MsgSystem.get("notenoughargs").replace("%cmd%",
				// args[0]));
				break;
			case "exit":
				sendTo(writer,"Server", "exit:0", EncryptionType.None);
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

	private boolean sendTo(ObjectOutputStream os,String reciever, String msg, EncryptionType crypt)
	{
		try
		{
			os.writeObject(new Packet(reciever, msg, crypt));
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

}
