package ru.alexandrdv.udp.server;

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
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

import ru.alexandrdv.udp.Packet;
import ru.alexandrdv.udp.client.UDPClient.Sound;
import ru.alexandrdv.udpmessenger.ImagePack;
import ru.alexandrdv.udp.Sounds;

public class UDPServer
{
	private static final Random random = new Random();
	int port;
	InetAddress addr;
	ActionListener listener;

	/**
	 * 
	 * @param serverIp
	 * @param serverPort
	 * @param port
	 */
	public UDPServer(int port, ActionListener listener)
	{
		try
		{
			addr = InetAddress.getLocalHost();
		}
		catch (UnknownHostException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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

		new Thread(() ->
		{
			startGetter();
		}).start();
	}

	DatagramSocket s;

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
	 * @param object
	 * @return
	 * @throws IOException
	 */
	private static byte[] writeToByteArray(Object object) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);
		out.writeObject(object);
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
	private static Object readByteArray(byte[] bytes) throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream in = new ObjectInputStream(bais);
		Object pack = (Object) in.readObject();
		in.close();
		bais.close();
		return pack;
	}

	/**
	 * 
	 * @param port
	 * @param pacSize
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Object recieve() throws IOException, ClassNotFoundException
	{
		Object pack;

		byte[] data = new byte[Packet.packetSize];
		DatagramPacket pac = new DatagramPacket(data, data.length);
		s.receive(pac);
		pack = readByteArray(data);
		if (pack instanceof Packet)
		{
			Packet packet = (Packet) pack;
			packet.address = new InetSocketAddress(pac.getAddress(), packet.address.getPort());
		}
		if (pack instanceof Sound)
		{
			s.send(new DatagramPacket(data, data.length, pac.getAddress(), ((Sound) pack).port));// отправление пакета
			try
			{
				// Clip clip=AudioSystem.getClip();
				// clip.open(Sounds.FORMAT, ((Sound)pack).data, 0, ((Sound)pack).data.length);
				// clip.start();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pack;

	}

	/**
	 * 
	 * @param address
	 * @param port
	 * @param pack
	 * @return
	 */
	public int send(InetAddress clientAddress, int clientPort, Object pack)
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
