package edu.greatfree.cry.framework.cluster.admin;

import java.io.IOException;

import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.cs.multinode.message.ShutdownServerNotification;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

import edu.greatfree.cry.framework.cluster.ClusterConfig;
import edu.greatfree.cry.framework.cluster.message.PeerCryptoOptionNotification;
import edu.greatfree.cry.framework.cluster.message.StopChildrenNotification;
import edu.greatfree.cry.framework.cluster.message.StopRootNotification;
import edu.greatfree.cry.messege.cluster.ClusterCryptoOptionNotification;
import edu.greatfree.cry.multicast.MulticastConfig;

/**
 * 
 * @author libing
 * 
 *         04/27/2022
 *
 */
final class StartAdmin
{
	private static IPAddress clientAddress;
	private static IPAddress rootAddress;
//	private static Scanner in = new Scanner(System.in);

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, InterruptedException, RemoteIPNotExistedException, IOException
	{
		StandaloneClient.CS().init();

		int option = MainOptions.NO_OPTION;
		String optionStr;

		clientAddress = StandaloneClient.CS().getIPAddress(ClusterConfig.REGISTRY_IP, ClusterConfig.REGISTRY_PORT, ClusterConfig.CLIENT_KEY);
		rootAddress = StandaloneClient.CS().getIPAddress(ClusterConfig.REGISTRY_IP, ClusterConfig.REGISTRY_PORT, ClusterConfig.ROOT_KEY);

		while (option != MainOptions.QUIT)
		{
			printMainMenu();
			optionStr = Tools.INPUT.nextLine();
			try
			{
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice is: " + option);
				executeMain(option);
			}
			catch (NumberFormatException e)
			{
				option = MainOptions.NO_OPTION;
				System.out.println(MainMenu.WRONG_OPTION);
			}
		}

		StandaloneClient.CS().dispose();
	}

	private static void printMainMenu()
	{
		System.out.println(MainMenu.MENU_HEAD);
		System.out.println(MainMenu.UPDATE_CLIENT_CRYPTO_OPTION);
		System.out.println(MainMenu.UPDATE_CLUSTER_CRYPTO_OPTION);
		System.out.println(MainMenu.STOP_CHILDREN);
		System.out.println(MainMenu.STOP_ROOT);
		System.out.println(MainMenu.STOP_REGISTRY_SERVER);
		System.out.println(MainMenu.QUIT);
		System.out.println(MainMenu.MENU_TAIL);
		System.out.println(MainMenu.INPUT_PROMPT);
	}

	private static void printSubMenu()
	{
		System.out.println(SubMenu.MENU_HEAD);
		System.out.println(SubMenu.PLAIN);
		System.out.println(SubMenu.SYMMETRIC);
		System.out.println(SubMenu.ASYMMETRIC);
		System.out.println(SubMenu.SIGNED);
		System.out.println(SubMenu.QUIT);
		System.out.println(SubMenu.MENU_TAIL);
		System.out.println(SubMenu.INPUT_PROMPT);
	}

	private static void executeMain(int option) throws IOException, InterruptedException
	{
		int subOption = MainOptions.NO_OPTION;
		String subOptionStr;
		int cryptoOption;
		switch (option)
		{
			case MainOptions.UPDATE_CLIENT_CRYPTO_OPTION:
				System.out.println(MainMenu.UPDATE_CLIENT_CRYPTO_OPTION);
				while (subOption != MainOptions.QUIT)
				{
					printSubMenu();
					subOptionStr = Tools.INPUT.nextLine();
					try
					{
						subOption = Integer.parseInt(subOptionStr);
						System.out.println("Your choice is: " + option);
						cryptoOption = subOption - 1;
						if (cryptoOption >= MulticastConfig.PLAIN && cryptoOption <= MulticastConfig.SIGNED)
						{
							StandaloneClient.CS().syncNotify(clientAddress.getIP(), clientAddress.getPort(), new PeerCryptoOptionNotification(cryptoOption));
							System.out.println("Crypto Option is sent!");
						}
						else
						{
							System.out.println("Wrong Crypto Option!");
						}
					}
					catch (NumberFormatException e)
					{
						option = MainOptions.NO_OPTION;
						System.out.println(MainMenu.WRONG_OPTION);
					}
				}
				break;

			case MainOptions.UPDATE_CLUSTER_CRYPTO_OPTION:
				System.out.println(MainMenu.UPDATE_CLUSTER_CRYPTO_OPTION);
				while (subOption != MainOptions.QUIT)
				{
					printSubMenu();
					subOptionStr = Tools.INPUT.nextLine();
					try
					{
						subOption = Integer.parseInt(subOptionStr);
						System.out.println("Your choice is: " + option);
						cryptoOption = subOption - 1;
						if (cryptoOption >= MulticastConfig.PLAIN && cryptoOption <= MulticastConfig.SIGNED)
						{
							StandaloneClient.CS().syncNotify(rootAddress.getIP(), rootAddress.getPort(), new ClusterCryptoOptionNotification(cryptoOption));
							System.out.println("Crypto Option is sent!");
						}
						else
						{
							System.out.println("Wrong Crypto Option!");
						}
					}
					catch (NumberFormatException e)
					{
						option = MainOptions.NO_OPTION;
						System.out.println(MainMenu.WRONG_OPTION);
					}
				}
				break;

			case MainOptions.STOP_CHILDREN:
				System.out.println(MainMenu.STOP_CHILDREN);
				/*
				 * Before notifying to shutdown the cluster, it is necessary to change the
				 * communication within the cluster as plain. Otherwise, it is possible that the
				 * relevant encryption configurations are removed such that the stop command
				 * cannot be decrypted. On the other hand, it is not necessary to encrypt the
				 * administration command. 05/16/2022, Bing Li
				 */
				StandaloneClient.CS().syncNotify(rootAddress.getIP(), rootAddress.getPort(), new ClusterCryptoOptionNotification(MulticastConfig.PLAIN));
				StandaloneClient.CS().syncNotify(rootAddress.getIP(), rootAddress.getPort(), new StopChildrenNotification());
				break;

			case MainOptions.STOP_ROOT:
				System.out.println(MainMenu.STOP_ROOT);
				StandaloneClient.CS().syncNotify(rootAddress.getIP(), rootAddress.getPort(), new StopRootNotification());
				break;

			case MainOptions.STOP_REGISTRY_SERVER:
				System.out.println(MainMenu.STOP_REGISTRY_SERVER);
				StandaloneClient.CS().syncNotify(ClusterConfig.REGISTRY_IP, ClusterConfig.REGISTRY_PORT, new ShutdownServerNotification());
				break;
		}
	}
}
