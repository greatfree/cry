package edu.greatfree.cry.framework.ownership.admin;

import java.io.IOException;

import org.greatfree.admin.Menu;
import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.cs.multinode.message.ShutdownServerNotification;
import org.greatfree.util.Tools;

import edu.greatfree.cry.framework.ownership.OwnerConfig;

/**
 * 
 * @author libing
 * 
 * 03/23/2022
 *
 */
class StartAdmin
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		StandaloneClient.CS().init();

		int option = Options.NO_OPTION;
		String optionStr;
		
		while (option != Options.QUIT)
		{
			printMenu();
			optionStr = Tools.INPUT.nextLine();
			try
			{
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice is: " + option);
				notifyServer(option);
			}
			catch (NumberFormatException e)
			{
				option = Options.NO_OPTION;
				System.out.println(Menu.WRONG_OPTION);
			}
		}

		StandaloneClient.CS().dispose();
	}

	private static void printMenu()
	{
		System.out.println(AdminMenu.MENU_HEAD);
		System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
		System.out.println(AdminMenu.QUIT);
		System.out.println(AdminMenu.MENU_TAIL);
		System.out.println(AdminMenu.INPUT_PROMPT);
	}

	private static void notifyServer(int option) throws IOException, InterruptedException
	{
		switch (option)
		{
			case Options.STOP_REGISTRY_SERVER:
				System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
				StandaloneClient.CS().syncNotify(OwnerConfig.REGISTRY_IP, OwnerConfig.REGISTRY_PORT, new ShutdownServerNotification());
				break;
		}
	}
}
