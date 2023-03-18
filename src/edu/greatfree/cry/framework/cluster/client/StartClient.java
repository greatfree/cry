package edu.greatfree.cry.framework.cluster.client;

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
import edu.greatfree.cry.framework.cluster.ClusterConfig;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
class StartClient
{
	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, OwnerCheatingException, CheatingException, PeerNotRegisteredException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		int option = ClusterOptions.NO_OPTION;
//		Scanner in = new Scanner(System.in);
		
		System.out.println("Starting client ...");
		try
		{
			ClientUI.CRY().init(ClusterConfig.CLIENT_NAME, ClusterConfig.ROOT_NAME);
			
			String optionStr;
			while (option != ClusterOptions.QUIT)
			{
				ClientUI.CRY().printMenu();
				optionStr = Tools.INPUT.nextLine();
				try
				{
					option = Integer.parseInt(optionStr);
					System.out.println("Your choice: " + option);
					if (option == ClusterOptions.QUIT)
					{
						break;
					}
					ClientUI.CRY().send(option);
				}
				catch (NumberFormatException e)
				{
					option = ClusterOptions.NO_OPTION;
					System.out.println(ClusterMenu.WRONG_OPTION);
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
				ClientUI.CRY().dispose();
			}
			catch (PeerNameIsNullException e)
			{
				e.printStackTrace();
			}
//			in.close();
		}
	}
}
