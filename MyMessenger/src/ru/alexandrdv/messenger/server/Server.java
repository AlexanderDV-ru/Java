package ru.alexandrdv.messenger.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;

import ru.alexandrdv.messenger.CmdGUI;
import ru.alexandrdv.messenger.Encryptor;
import ru.alexandrdv.messenger.Encryptor.EncryptionType;
import ru.alexandrdv.messenger.Packet;

public class Server extends CmdGUI
{

	
	private ServerSocket server;

	public Server()
	{
		super();
		f.setTitle(getClass().getName()+" $"+Calendar.getInstance().getTimeInMillis()+" - Console");
		try
		{
			server = new ServerSocket(25777, 1000);
			while (true)
				createChat(server.accept());
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
	HashMap<String, ObjectOutputStream> cl=new HashMap<String, ObjectOutputStream>();
	private void createChat(Socket socket)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
					cl.put(socket.getInetAddress().getHostAddress()+":"+socket.getPort(), writer);
					ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
					Packet p;
					for (;socket != null;)
					{
						if ((p=(Packet)reader.readObject()) != null)
						{
							if (p.msg.startsWith("exit"))
							{
								break ;
							}
							else
							{
								println(p.msg);
								if(p.type==EncryptionType.Client)
									sendTo(writer, p.reciever, Encryptor.encrypt(p.msg, 12, EncryptionType.Client, EncryptionType.Double), EncryptionType.Double);
								if(p.type==EncryptionType.Double)
									sendTo(writer, p.reciever, Encryptor.encrypt(p.msg, 12, EncryptionType.Double, EncryptionType.Client), EncryptionType.Client);
								if(p.type==EncryptionType.Server)
								{
									println(Encryptor.encrypt(p.msg, 12, EncryptionType.Server, EncryptionType.None));
									if(cl.containsKey(p.reciever))
										sendTo(cl.get(p.reciever), p.reciever, p.msg, EncryptionType.Server);
									else for(String k:cl.keySet())
										System.out.println(k);
									System.out.println(p.reciever);
										
								}
							}
						}
					}
								socket.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
		
	}

	public static void main(String[] args)
	{
		new Server();

	}

	@Override
	public void Command(String[] args)
	{

		
	}

}
