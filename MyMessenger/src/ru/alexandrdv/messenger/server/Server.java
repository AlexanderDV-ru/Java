package ru.alexandrdv.messenger.server;

import javax.swing.JFrame;

import ru.alexandrdv.messenger.CmdGUI;
import ru.alexandrdv.messenger.client.Client;

public class Server extends CmdGUI
{

	public Server()
	{
		System.out.println("Check server...");
		
		System.out.println("Server is working");
	}

	public static void main(String[] args)
	{
		new Client();

	}

	@Override
	public void Command(String[] args)
	{
		// TODO Auto-generated method stub
		
	}

}
