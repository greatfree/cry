package org.greatfree.cry.framework.multicast.admin;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.client.StandaloneClient;
import org.greatfree.cry.framework.multicast.MultiAppConfig;
import org.greatfree.cry.framework.multicast.message.AdminStopChildrenNotification;
import org.greatfree.cry.framework.multicast.message.AdminStopRootNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.cs.multinode.message.ShutdownServerNotification;
import org.greatfree.util.IPAddress;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
final class StartAdmin
{
	private static IPAddress multicastRootIP;

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		StandaloneClient.CS().init();

		int option = Options.NO_OPTION;
		String optionStr;
		
		multicastRootIP = StandaloneClient.CS().getIPAddress(MultiAppConfig.REGISTRY_SERVER_IP, MultiAppConfig.REGISTRY_SERVER_PORT, MultiAppConfig.ROOT_KEY);
		
		Scanner in = new Scanner(System.in);
		while (option != Options.QUIT)
		{
			printMenu();
			optionStr = in.nextLine();
			
			try
			{
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice is: " + option);
				send(option);
			}
			catch (NumberFormatException e)
			{
				option = Options.NO_OPTION;
				System.out.println(Menu.WRONG_OPTION);
			}
		}

		StandaloneClient.CS().dispose();
		in.close();
	}

	private static void printMenu()
	{
		System.out.println(Menu.MENU_HEAD);
		System.out.println(Menu.STOP_CHILDREN);
		System.out.println(Menu.STOP_ROOT);
		System.out.println(Menu.STOP_REGISTRY_SERVER);
		System.out.println(Menu.QUIT);
		System.out.println(Menu.MENU_TAIL);
		System.out.println(Menu.INPUT_PROMPT);
	}

	private static void send(int option) throws IOException, InterruptedException
	{
		switch (option)
		{
			case Options.STOP_CHILDREN:
				System.out.println(Menu.STOP_CHILDREN);
				StandaloneClient.CS().syncNotify(multicastRootIP.getIP(), multicastRootIP.getPort(), new AdminStopChildrenNotification());
				break;
				
			case Options.STOP_ROOT:
				System.out.println(Menu.STOP_ROOT);
				StandaloneClient.CS().syncNotify(multicastRootIP.getIP(), multicastRootIP.getPort(), new AdminStopRootNotification());
				break;
				
			case Options.STOP_REGISTRY_SERVER:
				System.out.println(Menu.STOP_REGISTRY_SERVER);
				StandaloneClient.CS().syncNotify(MultiAppConfig.REGISTRY_SERVER_IP, MultiAppConfig.REGISTRY_SERVER_PORT, new ShutdownServerNotification());
				break;
		}
		
	}
}
