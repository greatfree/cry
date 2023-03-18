package edu.greatfree.cry.framework.cs.client;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Tools;

import edu.greatfree.cry.client.Client;
import edu.greatfree.cry.framework.cs.message.CSNotification;
import edu.greatfree.cry.framework.cs.message.CSRequest;
import edu.greatfree.cry.framework.cs.message.CSResponse;
import edu.greatfree.cry.framework.cs.message.ShutdownNotification;

/**
 * 
 * @author libing
 * 
 * 01/07/2022, Bing Li
 *
 */
class ClientUI
{
	private ClientUI()
	{
	}

	private static ClientUI instance = new ClientUI();
	
	public static ClientUI CS()
	{
		if (instance == null)
		{
			instance = new ClientUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose()
	{
	}

	public void printMenu()
	{
		System.out.println(ClientMenu.MENU_HEAD);
		System.out.println(ClientMenu.TYPE_NOTIFICATION);
		System.out.println(ClientMenu.TYPE_REQUEST);
		System.out.println(ClientMenu.SHUTDOWN_SERVER);
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}

	public void send(Client client, int option) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InterruptedException, ClassNotFoundException, ShortBufferException, RemoteReadException, RemoteIPNotExistedException, IOException
	{
		switch (option)
		{
			case MenuOptions.TYPE_NOTIFICATION:
				System.out.println("Type your notification below: ...");
				String notification = Tools.INPUT.nextLine();
				do
				{
					client.syncNotify(new CSNotification(notification));
					System.out.println("Type your notification below again: ...");
					notification = Tools.INPUT.nextLine();
				}
				while (!notification.equals("exit"));
				break;
				
			case MenuOptions.TYPE_REQUEST:
				System.out.println("Type your request below: ...");
				String request = Tools.INPUT.nextLine();
				CSResponse response;
				do
				{
					response = (CSResponse)client.read(new CSRequest(request));
					System.out.println("Response = " + response.getMessage());
					System.out.println("Type your request below again: ...");
					request = Tools.INPUT.nextLine();
				}
				while (!request.equals("exit"));
				break;
				
			case MenuOptions.SHUTDOWN_SERVER:
				client.syncNotify(new ShutdownNotification());
				System.out.println("Shutdown notification is sent ...");
				break;
		}
	}
}
