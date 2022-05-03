package org.greatfree.cry.framework.cluster.admin;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.client.StandaloneClient;
import org.greatfree.cry.framework.cluster.ClusterConfig;
import org.greatfree.cry.framework.cluster.message.StopChildrenNotification;
import org.greatfree.cry.framework.cluster.message.StopRootNotification;
import org.greatfree.cry.framework.multicast.admin.Menu;
import org.greatfree.cry.framework.multicast.admin.Options;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.cs.multinode.message.ShutdownServerNotification;
import org.greatfree.util.IPAddress;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
final class StartAdmin
{
	private static IPAddress rootAddress;

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		StandaloneClient.CS().init();

		int option = Options.NO_OPTION;
		String optionStr;

		rootAddress = StandaloneClient.CS().getIPAddress(ClusterConfig.REGISTRY_IP, ClusterConfig.REGISTRY_PORT, ClusterConfig.ROOT_KEY);

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
				StandaloneClient.CS().syncNotify(rootAddress.getIP(), rootAddress.getPort(), new StopRootNotification());
				break;
				
			case Options.STOP_ROOT:
				System.out.println(Menu.STOP_ROOT);
				StandaloneClient.CS().syncNotify(rootAddress.getIP(), rootAddress.getPort(), new StopChildrenNotification());
				break;
				
			case Options.STOP_REGISTRY_SERVER:
				System.out.println(Menu.STOP_REGISTRY_SERVER);
				StandaloneClient.CS().syncNotify(ClusterConfig.REGISTRY_IP, ClusterConfig.REGISTRY_PORT, new ShutdownServerNotification());
				break;
		}
		
	}
}
