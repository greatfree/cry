package org.greatfree.cry.framework.blockchain.peer;

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

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * @author libing
 * 
 * 01/26/2022, Bing Li
 *
 */
class StartChainNode
{

	public static void main(String[] args) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, SignatureException, IOException, RemoteReadException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, PublicKeyUnavailableException, SymmetricKeyUnavailableException
	{
		Scanner in = new Scanner(System.in);
		System.out.println("What is the name of the mining machine?");
//		String nodeName = Tools.generateUniqueKey();
		String nodeName = in.nextLine();
		System.out.println("Chain node, " + nodeName + ", is starting up ...");
		ChainNode.CHAIN().start(nodeName);
		System.out.println("Chain node, " + nodeName + ", is started ...");
		
		int option = MenuOptions.NO_OPTION;

		String optionStr;
		while (option != MenuOptions.QUIT)
		{
			ChainNodeUI.CHAIN().printMenu();
			optionStr = in.nextLine();
			option = Integer.parseInt(optionStr);
			System.out.println("Your choice: " + option);
			try
			{
				ChainNodeUI.CHAIN().send(option);
			}
			catch (NumberFormatException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ChainNodeMenu.WRONG_OPTION);
			}
		}
		
		ChainNode.CHAIN().stop();
		in.close();
		System.out.println("Chain node, " + nodeName + ", is stopped ...");
	}

}
