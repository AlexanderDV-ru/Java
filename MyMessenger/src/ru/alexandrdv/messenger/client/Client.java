package ru.alexandrdv.messenger.client;

import javax.swing.JFrame;

import ru.alexandrdv.messenger.CmdGUI;

public class Client extends CmdGUI
{

	public Client()
	{
		System.out.println("Check client...");
		System.out.println("Client is working");
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
