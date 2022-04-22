package org.greatfree.cry.framework.tncs.client;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.cry.client.Client;
import org.greatfree.cry.framework.tncs.Config;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.IPAddress;

/**
 * 
 * @author libing
 * 
 * 01/07/2022, Bing Li
 *
 */
class StartClient
{

	public static void main(String[] args) throws IOException, InterruptedException
	{
		System.out.println("Client starting up ...");

		Client client = new Client.ClientBuilder()
				.serverAddress(new IPAddress("192.168.1.18", Config.CRYPTO_SERVER_PORT))
				.cipherAlgorithm(Config.AES)
//				.cipherSpec("AES/CBC/PKCS5Padding")
				.cipherSpec(Config.AES_SPEC)
				.cipherKeyLength(256)
				.ivKeyLength(128)
				.build();

		System.out.println("Client started ...");

		int option = MenuOptions.NO_OPTION;
		String optionStr;
		Scanner in = new Scanner(System.in);

		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			ClientUI.CS().printMenu();
			optionStr = in.nextLine();
			try
			{
				// Convert the input string to integer. 09/21/2014, Bing Li
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				
				// Send the option to the polling server. 09/21/2014, Bing Li
				ClientUI.CS().send(client, option);
			}
			catch (NumberFormatException | ClassNotFoundException | RemoteReadException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | ShortBufferException | InterruptedException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}

		client.dispose();
		in.close();
	}

}
