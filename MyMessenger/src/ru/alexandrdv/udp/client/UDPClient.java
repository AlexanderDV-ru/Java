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
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;

import ru.alexandrdv.udp.Packet;
import ru.alexandrdv.udp.Sounds;

/**
 * 
 * @author AlexandrDV
 *
 */
public class UDPClient
{
	private static final Random random = new Random();
	public InetAddress clientAddress = null;
	InetAddress serverAddress;
	int serverPort;
	public int port;
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
		new Thread(()->
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
		).start();
	}

	DatagramSocket s;

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
		Object packet = in.readObject();
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
	public Object recieve() throws IOException, ClassNotFoundException
	{
		byte[] data = new byte[Packet.packetSize];
		DatagramPacket pac = new DatagramPacket(data, data.length);
		s.receive(pac);
		return readByteArray(data);

	}

	/**
	 * 
	 * @param address
	 * @param port
	 * @param pack
	 * @return
	 */
	public int send(Object pack)
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

	public static class Sound implements Serializable
	{
		private static final long serialVersionUID = -8942733866610289458L;
		public byte[] data;
		public int port,id;

		public Sound(byte[] data, int port,int id)
		{
			super();
			this.data = data;
			this.port = port;
			this.id=id;
		}
	}

}
