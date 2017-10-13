package ru.alexandrdv.messenger.server;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;

import ru.alexandrdv.messenger.CmdGUI;
import ru.alexandrdv.messenger.Encryptor;
import ru.alexandrdv.messenger.Encryptor.EncryptionType;
import ru.alexandrdv.messenger.Packet;
import ru.alexandrdv.messenger.Packet.MessagePacket;
import ru.alexandrdv.messenger.Packet.PacketType;
import ru.alexandrdv.messenger.Packet.QueryPacket;
import ru.alexandrdv.messenger.Packet.SignPacket;

public class Server extends CmdGUI
{

	private ServerSocket server;

	public Server()
	{
		super();
		start();
		f.addWindowListener(new WindowAdapter()
		{
			
			
			@Override
			public void windowClosing(WindowEvent e)
			{
				end();
				
			}
		
		});
		f.setTitle(getClass().getName() + " $" + Calendar.getInstance().getTimeInMillis() + " - Console");
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

	private boolean sendTo(ObjectOutputStream os, String reciever, String msg, EncryptionType crypt, String sender)
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

	private boolean sendQuery(ObjectOutputStream os, String query, EncryptionType crypt, String sender)
	{
		try
		{
			os.writeObject(new QueryPacket(query, crypt, sender));
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	int encryptionKey = 13;

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
					add(new ClientData("Client" + clientByIpAndPort.size(), socket.getInetAddress().getHostAddress(), socket.getPort(), socket, writer, reader));
					boolean verified = false;
					Packet p;
					for (; socket != null;)
					{
						if ((p = (Packet) reader.readObject()) != null)
						{
							if (verified)
							{
								if (p.packetType == PacketType.Message)
								{

									MessagePacket msgPacket = (MessagePacket) p;
									println(msgPacket.msg);
									if (msgPacket.type == EncryptionType.Client)
										sendTo(writer, msgPacket.reciever, Encryptor.encrypt(msgPacket.msg, encryptionKey, EncryptionType.Client, EncryptionType.Double), EncryptionType.Double, msgPacket.sender);
									if (msgPacket.type == EncryptionType.Double)
										sendTo(writer, msgPacket.reciever, Encryptor.encrypt(msgPacket.msg, encryptionKey, EncryptionType.Double, EncryptionType.Client), EncryptionType.Client, msgPacket.sender);
									if (msgPacket.type == EncryptionType.Server)
									{
										println(Encryptor.encrypt(msgPacket.msg, encryptionKey, EncryptionType.Server, EncryptionType.None));
										if (clientByIpAndPort.containsKey(msgPacket.reciever))
											sendTo(clientByIpAndPort.get(msgPacket.reciever).os, msgPacket.reciever, msgPacket.msg, EncryptionType.Server, msgPacket.sender);

									}
								}
								else if (p.packetType == PacketType.Query)
								{
									QueryPacket q = (QueryPacket) p;
									sendQuery(writer, clientByIpAndPort.containsKey(q.query) + "", EncryptionType.None, "Server");
								}
							}
							if (p.packetType == PacketType.Sign)
							{
								SignPacket signPacket = (SignPacket) p;
								if (signPacket.type == EncryptionType.Client)
								{
									String login = Encryptor.encrypt(signPacket.password, encryptionKey, EncryptionType.Client, EncryptionType.Double);
									String password = Encryptor.encrypt(signPacket.password, encryptionKey, EncryptionType.Client, EncryptionType.Double);
									writer.writeObject(new SignPacket(login, password, EncryptionType.Double, signPacket.sender, signPacket.signUp));
								}
								if (signPacket.type == EncryptionType.Server)
								{
									String login = Encryptor.encrypt(signPacket.password, encryptionKey, EncryptionType.Server, EncryptionType.None);
									String password = Encryptor.encrypt(signPacket.password, encryptionKey, EncryptionType.Server, EncryptionType.None).hashCode() + "";
									if (signPacket.signUp)
									{
										if (!accounts.containsKey(login))
										{
											accounts.put(login, password);

											sendQuery(writer, "Account created", EncryptionType.None, "Server");
										}
										else sendQuery(writer, "Account already exists", EncryptionType.None, "Server");
									}
									else
									{
										if (accounts.containsKey(login))
											if (accounts.get(login).equals(password))
											{
												verified = true;
												sendQuery(writer, "Account verified", EncryptionType.None, "Server");
											}
											else sendQuery(writer, "Wrong Password", EncryptionType.None, "Server");
										else sendQuery(writer, "Account not exists", EncryptionType.None, "Server");

									}

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

	public void saveAccounts()
	{
		try
		{
			File f = new File("data.dat");
			if (!f.exists())
				f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(accounts);
			oos.close();
			fos.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public void start()
	{
		loadAccounts();
	}
	public void end()
	{
		saveAccounts();
	}

	public void loadAccounts()
	{
		try
		{
			File f = new File("data.dat");
			if (!f.exists())
				f.createNewFile();
			else
			{
				FileInputStream fis = new FileInputStream(f);
				ObjectInputStream ois = new ObjectInputStream(fis);
				accounts=(HashMap<String, String>) ois.readObject();
				ois.close();
				fis.close();
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	HashMap<String, String> accounts = new HashMap<String, String>();
	HashMap<String, ClientData> clientByIpAndPort = new HashMap<String, ClientData>();
	HashMap<String, ClientData> clientByName = new HashMap<String, ClientData>();

	public void add(ClientData data)
	{
		clientByIpAndPort.put(data.ip + ":" + data.port, data);
		clientByName.put(data.name, data);
	}

	public void remove(ClientData data)
	{
		clientByIpAndPort.remove(data.ip + ":" + data.port, data);
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
