package ru.alexandrdv.messenger.server;

import ru.alexandrdv.messenger.client.Client;

public class Server
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

}
