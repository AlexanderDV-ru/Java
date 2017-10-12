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
import ru.alexandrdv.messenger.Packet.MessagePacket;
import ru.alexandrdv.messenger.Packet;
import ru.alexandrdv.messenger.Packet.QueryPacket;

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
			os.writeObject(new MessagePacket(reciever, msg, crypt));
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	private boolean sendQuery(ObjectOutputStream os,String query, EncryptionType crypt)
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
	private void createChat(Socket socket)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
					add(new ClientData("Client"+clientByIpAndPort.size(), socket.getInetAddress().getHostAddress(), socket.getPort(), socket, writer, reader));
					Packet p;
					for (;socket != null;)
					{
						if ((p=(Packet)reader.readObject()) != null)
						{
							if(!p.isQuery)
							{

								MessagePacket msgPacket=(MessagePacket)p;
								println(msgPacket.msg);
								if(msgPacket.type==EncryptionType.Client)
									sendTo(writer, msgPacket.reciever, Encryptor.encrypt(msgPacket.msg, 12, EncryptionType.Client, EncryptionType.Double), EncryptionType.Double);
								if(msgPacket.type==EncryptionType.Double)
									sendTo(writer,msgPacket.reciever, Encryptor.encrypt(msgPacket.msg, 12, EncryptionType.Double, EncryptionType.Client), EncryptionType.Client);
								if(msgPacket.type==EncryptionType.Server)
								{
									println(Encryptor.encrypt(msgPacket.msg, 12, EncryptionType.Server, EncryptionType.None));
									if(clientByIpAndPort.containsKey(msgPacket.reciever))
										sendTo(clientByIpAndPort.get(msgPacket.reciever).os, msgPacket.reciever, msgPacket.msg, EncryptionType.Server);
										
								}
							}
							else
							{
								QueryPacket q=(QueryPacket)p;
								sendQuery(writer,clientByIpAndPort.containsKey(q.query)+"", EncryptionType.None);
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
	HashMap<String, ClientData> clientByIpAndPort=new HashMap<String, ClientData>();
	HashMap<String, ClientData> clientByName=new HashMap<String, ClientData>();
	
	public void add(ClientData data)
	{
		clientByIpAndPort.put(data.ip+":"+data.port, data);
		clientByName.put(data.name, data);
	}
	public void remove(ClientData data)
	{
		clientByIpAndPort.remove(data.ip+":"+data.port, data);
		clientByName.remove(data.name, data);
	}

	public static void main(String[] args)
	{
		new Server();

	}

	@Override
	public void Command(String[] args)
	{

		
	}
class ClientData
{
	public String name;
	public String ip;
	public int port;
	public Socket socket;
	public ObjectOutputStream os;
	public ObjectInputStream is;
	public ClientData(String name, String ip, int port, Socket socket, ObjectOutputStream os, ObjectInputStream is)
	{
		super();
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.socket = socket;
		this.os = os;
		this.is = is;
	}
}
}
