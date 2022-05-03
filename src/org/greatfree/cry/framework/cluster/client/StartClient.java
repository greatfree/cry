package org.greatfree.cry.framework.cluster.client;

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
import org.greatfree.cry.framework.cluster.ClusterConfig;
import org.greatfree.cry.framework.multicast.client.MultiMenu;
import org.greatfree.cry.framework.multicast.client.MultiOptions;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
class StartClient
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, OwnerCheatingException, CheatingException
	{
		int option = MultiOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);
		
		System.out.println("Starting client ...");
		ClientUI.CRY().init("greatfree", ClusterConfig.ROOT_NAME);
		
		String optionStr;
		while (option != MultiOptions.QUIT)
		{
			ClientUI.CRY().printMenu();
			optionStr = in.nextLine();
			try
			{
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				if (option == MultiOptions.QUIT)
				{
					break;
				}
				ClientUI.CRY().send(option);
			}
			catch (NumberFormatException e)
			{
				option = MultiOptions.NO_OPTION;
				System.out.println(MultiMenu.WRONG_OPTION);
			}
		}
		ClientUI.CRY().dispose();
		in.close();
	}

}
