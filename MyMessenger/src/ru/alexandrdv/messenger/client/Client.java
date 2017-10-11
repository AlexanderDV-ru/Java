package ru.alexandrdv.messenger.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import ru.alexandrdv.messenger.CmdGUI;
import ru.alexandrdv.messenger.Encryptor;
import ru.alexandrdv.messenger.Encryptor.EncryptionType;

public class Client extends CmdGUI
{

	private PrintWriter pw;
	private Socket socket;
	private int encryptionKey;

	public Client()
	{
		super();
		f.setTitle(getClass().getName() + " ¹" + "1" + " - Console");
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					socket = new Socket("192.168.0.2", 25777);
					println("You're working on " + socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort());
					pw = new PrintWriter(socket.getOutputStream(), true);
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String input;
					for (; socket != null;)
						if ((input = br.readLine()) != null)
						{
							System.out.println(input);
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
					String reciever = "Server";
					String msg = "";
					for (int i = 2; i < args.length; i++)
						msg += args[i] + " ";
					sendTo(reciever, Encryptor.encrypt(msg, encryptionKey, EncryptionType.None, EncryptionType.Client), EncryptionType.Client);
				}
				// TODO Create msg sys
				// else println(MsgSystem.get("notenoughargs").replace("%cmd%",
				// args[0]));
				break;
			case "exit":
				sendTo("Server", "exit:0", EncryptionType.None);
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

	private boolean sendTo(String reciever, String msg, EncryptionType crypt)
	{
		try
		{
			pw.println(crypt.name() + " > " + msg + " > " + reciever);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

}
