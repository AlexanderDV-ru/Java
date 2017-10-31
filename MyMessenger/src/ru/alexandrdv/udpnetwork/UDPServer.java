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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import ru.alexandrdv.udpnetwork.UDPClient.Packet;

public class UDPServer
{
	int port;
	ActionListener listener;

	/**
	 * 
	 * @param serverIp
	 * @param serverPort
	 * @param port
	 */
	public UDPServer(int port, ActionListener listener)
	{
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
		try
		{
			Packet packet = readByteArray(data);
			packet.ip = pac.getAddress();
			return packet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return recieve();
		}

	}

	/**
	 * 
	 * @param address
	 * @param port
	 * @param pack
	 * @return
	 */
	public int send(InetAddress clientAddress, int clientPort, Packet pack)
	{
		try
		{
			byte[] data = writeToByteArray(pack);
			s.send(new DatagramPacket(data, data.length, clientAddress, clientPort));// отправление пакета

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
