package ru.alexandrdv.udp.client;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import ru.alexandrdv.udp.Packet;

/**
 * 
 * @author AlexandrDV
 *
 */
public class UDPClient
{
	private static final Random random = new Random();
	InetAddress clientAddress = null, serverAddress;
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
