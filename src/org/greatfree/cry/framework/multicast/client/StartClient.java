package org.greatfree.cry.framework.multicast.client;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.cry.exceptions.CheatingException;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.OwnerCheatingException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.multicast.MultiAppConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * @author libing
 * 
 * 04/11/2022
 *
 */
final class StartClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, DistributedNodeFailedException, CryptographyMismatchException, InstantiationException, IllegalAccessException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, OwnerCheatingException, CheatingException
	{
		int option = MultiOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);
		
		System.out.println("Starting client ...");

		ClientUI.FRONT().init("greatfree", MultiAppConfig.ROOT_NAME);
//		ClientUI.FRONT().inviteRoot();
		
		String optionStr;
		while (option != MultiOptions.QUIT)
		{
			ClientUI.FRONT().printMenu();
			optionStr = in.nextLine();
			try
			{
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				if (option == MultiOptions.QUIT)
				{
					break;
				}
				ClientUI.FRONT().send(option);
			}
			catch (NumberFormatException e)
			{
				option = MultiOptions.NO_OPTION;
				System.out.println(MultiMenu.WRONG_OPTION);
			}
		}
		ClientUI.FRONT().dispose();
		in.close();
	}

}
