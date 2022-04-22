package org.greatfree.cry.framework.ownership.owner;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * @author libing
 * 
 * 03/19/2022
 *
 */
class StartOwner
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, DistributedNodeFailedException, CryptographyMismatchException, InterruptedException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, IOException
	{
		int option = OwnerOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);

		System.out.println("Tell me your name: ");
		String nodeName = in.nextLine();
		System.out.println("Tell me your machine's name: ");
		String machineName = in.nextLine();
		System.out.println("Tell me the owner's name for the machine: ");
		String yourName = in.nextLine();

		try
		{
			Owner.RSC().start(nodeName, yourName, nodeName, machineName);
			Owner.RSC().inviteMachine();
		}
		catch (ClassNotFoundException | IOException | RemoteReadException e)
		{
			e.printStackTrace();
		}
		
		String optionStr;
		while (option != OwnerOptions.QUIT)
		{
			OwnerUI.RSC().printMenu();
			optionStr = in.nextLine();
			option = Integer.parseInt(optionStr);
			System.out.println("Your choice: " + option);

			try
			{
				OwnerUI.RSC().send(option);
			}
			catch (NumberFormatException e)
			{
				option = OwnerOptions.NO_OPTION;
				System.out.println(OwnerMenu.WRONG_OPTION);
			}
		}
		
		Owner.RSC().stop();
		in.close();
		System.out.println("Owner quit!");
	}

}
