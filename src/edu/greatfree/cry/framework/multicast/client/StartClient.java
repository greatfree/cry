package edu.greatfree.cry.framework.multicast.client;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.util.Tools;

import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.OwnerCheatingException;
import edu.greatfree.cry.exceptions.PeerNotRegisteredException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.framework.multicast.MultiAppConfig;

/**
 * 
 * @author libing
 * 
 * 04/11/2022
 *
 */
final class StartClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, DistributedNodeFailedException, CryptographyMismatchException, InstantiationException, IllegalAccessException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, OwnerCheatingException, CheatingException, PeerNotRegisteredException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		int option = MultiOptions.NO_OPTION;
		System.out.println("Starting client ...");

		try
		{
			ClientUI.FRONT().init("greatfree", MultiAppConfig.ROOT_NAME);
//			ClientUI.FRONT().inviteRoot();
			
			String optionStr;
			while (option != MultiOptions.QUIT)
			{
				ClientUI.FRONT().printMenu();
				optionStr = Tools.INPUT.nextLine();
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
		}
		catch (DuplicatePeerNameException e)
		{
			System.out.println(e);
		}
		finally
		{
			try
			{
				ClientUI.FRONT().dispose();
			}
			catch (PeerNameIsNullException e)
			{
				e.printStackTrace();
			}
		}
	}
}
