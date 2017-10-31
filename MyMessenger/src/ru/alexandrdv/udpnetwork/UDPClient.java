package ru.alexandrdv.udpnetwork;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import ru.alexandrdv.udpnetwork.Encryptor.EncryptionType;

/**
 * 
 * @author AlexandrDV
 *
 */
public class UDPClient
{
	/**
	 * 
	 * @author AlexandrDV
	 *
	 */
	static class Packet implements Serializable
	{
		private static final long serialVersionUID = -8942733866610289453L;
		public HashMap<String,String> args=new HashMap<String,String>();
		public InetAddress ip;
		public EncryptionType crypt;
		public int port;
		/**
		 * 
		 * @param msg
		 * @param ip
		 * @param port
		 */
		public Packet(String type,String reciever,String msg, String id, InetAddress ip, int port,EncryptionType crypt)
		{
			super();
			this.args.put("type",type);
			this.args.put("reciever",reciever);
			this.args.put("msg",msg);
			this.args.put("id",id);
			this.ip = ip;
			this.port = port;
			this.crypt = crypt;
		}

		

	}

	InetAddress clientAddress = null,serverAddress;
	int serverPort, port;
	ActionListener listener;

	/**
	 * 
	 * @param serverIp
	 * @param serverPort
	 * @param port
	 */
	public UDPClient(String serverIp, int serverPort, int port, ActionListener listener)
	{
		try
		{
			serverAddress = InetAddress.getByName(serverIp);
		}
		catch (HeadlessException e1)
		{
			e1.printStackTrace();
		}
		catch (UnknownHostException e1)
		{
			e1.printStackTrace();
		}
		this.serverPort = serverPort;
		this.port = port;
		this.listener = listener;
		try
		{
			s = new DatagramSocket(port);
		}
		catch (SocketException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}

		new Thread(new Runnable()
		{
			public void run()
			{
				startGetter();
			}
		}).start();
	}

	DatagramSocket s;
	int packetSize = 8 * 8 * 8 * 8 * 4;

	public void startGetter()
	{
		try
		{
			while (true)
				listener.actionPerformed(new ActionEvent(recieve(), 0, "recieved"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param packet
	 * @return
	 * @throws IOException
	 */
	private static byte[] writeToByteArray(Packet packet) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);
		out.writeObject(packet);
		byte[] bytes = baos.toByteArray();
		out.close();
		baos.close();
		return bytes;
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static Packet readByteArray(byte[] bytes) throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream in = new ObjectInputStream(bais);
		Packet packet = (Packet) in.readObject();
		in.close();
		bais.close();
		return packet;
	}

	/**
	 * 
	 * @param port
	 * @param pacSize
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Packet recieve() throws IOException, ClassNotFoundException
	{
		byte[] data = new byte[packetSize];
		DatagramPacket pac = new DatagramPacket(data, data.length);
		s.receive(pac);
		Packet packet = readByteArray(data);
		return packet;

	}

	/**
	 * 
	 * @param address
	 * @param port
	 * @param pack
	 * @return
	 */
	public int send(Packet pack)
	{
		try
		{
			byte[] data = writeToByteArray(pack);
			s.send(new DatagramPacket(data, data.length, serverAddress, serverPort));// отправление пакета
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();// неверный адрес получателя
			return -1;
		}
		catch (SocketException e)
		{
			e.printStackTrace();// возникли ошибки при передаче данных
			return -2;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace(); // не найден отправляемый файл
			return -3;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return -4;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1000;
		}
		return 1;
	}
}
