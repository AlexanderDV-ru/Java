package ru.alexandrdv.messenger.server;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.swing.Timer;

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
		f.setVisible(true);
		f.setDefaultCloseOperation(3);
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
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		new Timer(10, new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					createChat(server.accept());
				}
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}).start();
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

	private boolean sendQuery(ObjectOutputStream os, String query, String argument, EncryptionType crypt, String sender)
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

	int encryptionKey = 13;
	ArrayList<String> onlineIps=new ArrayList<String>();
	private void createChat(Socket socket)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
					sendQuery(writer, "yourip", socket.getInetAddress().getHostName() + ":" + socket.getPort(), EncryptionType.None, "Server");
					ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
					reader.readObject();
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
									println(msgPacket.args.get("msg"));
									if (msgPacket.type == EncryptionType.Client)
										sendTo(writer, msgPacket.args.get("reciever"), Encryptor.encrypt(msgPacket.args.get("msg"), encryptionKey, EncryptionType.Client, EncryptionType.Double), EncryptionType.Double, msgPacket.args.get("sender"));
									if (msgPacket.type == EncryptionType.Double)
										sendTo(writer, msgPacket.args.get("reciever"), Encryptor.encrypt(msgPacket.args.get("msg"), encryptionKey, EncryptionType.Double, EncryptionType.Client), EncryptionType.Client, msgPacket.args.get("sender"));
									if (msgPacket.type == EncryptionType.Server)
									{
										println(Encryptor.encrypt(msgPacket.args.get("msg"), encryptionKey, EncryptionType.Server, EncryptionType.None));
										if (clientByIpAndPort.containsKey(msgPacket.args.get("reciever")))
											sendTo(clientByIpAndPort.get(msgPacket.args.get("reciever")).os, msgPacket.args.get("reciever"), msgPacket.args.get("msg"), EncryptionType.Server, msgPacket.args.get("sender"));

									}
								}
								else if (p.packetType == PacketType.Query)
								{
									QueryPacket queryPacket = (QueryPacket) p;
									switch (queryPacket.args.get("query"))
									{
										case "isonline":
										{
											String ip=queryPacket.args.get("argument");
											
										sendQuery(writer, isOnline(ip) + "", null, EncryptionType.None, "Server");
										}
											break;
										case "getips":
										{
											String str = "";
											for (int i = 0; i < clientByIpAndPort.keySet().size(); i++)
											{
												str += " " + clientByIpAndPort.keySet().toArray()[i];
												isOnline(""+clientByIpAndPort.keySet().toArray()[i]);
											}
											sendQuery(writer, "onlineips", str.substring(1), EncryptionType.None, "Server");
										}
											break;
										case "imonline":
										{
											onlineIps.remove(onlineIps.indexOf(queryPacket.args.get("argument")));
										}
											break;
										case "exit":
										{
											String ip = socket.getInetAddress().getHostAddress();
											String port = socket.getPort() + "";
											writer.close();
											reader.close();
											socket.close();
											clientByIpAndPort.remove(ip + ":" + port);
										}
											break;
									}
								}
							}
							if (p.packetType == PacketType.Sign)
							{
								SignPacket signPacket = (SignPacket) p;
								if (signPacket.type == EncryptionType.Client)
								{
									String login = Encryptor.encrypt(signPacket.args.get("password"), encryptionKey, EncryptionType.Client, EncryptionType.Double);
									String password = Encryptor.encrypt(signPacket.args.get("password"), encryptionKey, EncryptionType.Client, EncryptionType.Double);
									writer.writeObject(new SignPacket(login, password, EncryptionType.Double, signPacket.args.get("sender"), signPacket.signUp));
								}
								if (signPacket.type == EncryptionType.Server)
								{
									String login = Encryptor.encrypt(signPacket.args.get("password"), encryptionKey, EncryptionType.Server, EncryptionType.None);
									String password = Encryptor.encrypt(signPacket.args.get("password"), encryptionKey, EncryptionType.Server, EncryptionType.None).hashCode() + "";
									if (signPacket.signUp)
									{
										if (!accounts.containsKey(login))
										{
											accounts.put(login, password);

											sendQuery(writer, "Account created", null, EncryptionType.None, "Server");
										}
										else sendQuery(writer, "Account already exists", null, EncryptionType.None, "Server");
									}
									else
									{
										if (accounts.containsKey(login))
											if (accounts.get(login).equals(password))
											{
												verified = true;
												sendQuery(writer, "Account verified", null, EncryptionType.None, "Server");
											}
											else sendQuery(writer, "Wrong Password", null, EncryptionType.None, "Server");
										else sendQuery(writer, "Account not exists", null, EncryptionType.None, "Server");

									}

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

	}
	public boolean isOnline(String ip)
	{
		boolean	isonline=clientByIpAndPort.containsKey(ip);
		onlineIps.add(ip);
		
		Timer t=new Timer(60*1000, new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(isonline&&onlineIps.contains(ip))
				{
					clientByIpAndPort.remove(ip);
					onlineIps.remove(ip);
				}
				
			}
		});
		t.setRepeats(false);
		t.start();
		return isonline;
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
				accounts = (HashMap<String, String>) ois.readObject();
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
