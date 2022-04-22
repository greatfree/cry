package org.greatfree.cry.framework.bitcoin.wallet;

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
 * @author Bing Li
 * 
 * 02/07/2022
 * 
 * The cheater pretends the mining machine with the signature of the owner of the machine. 02/07/2022, Bing Li
 *
 *
 */
class StartCheaterByFakeSignature
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, DistributedNodeFailedException, CryptographyMismatchException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, InterruptedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException
	{
		int option = WalletOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);

		System.out.println("Tell me your name: ");
		String nodeName = in.nextLine();
		System.out.println("Tell me your mining machine's name: ");
		String machineName = in.nextLine();
		System.out.println("Tell me the owner's name for the machine: ");
		String yourName = in.nextLine();

		WalletNode.CHAIN().start(nodeName, yourName, yourName, machineName);
//		WalletNode.CHAIN().inviteMachine(machineName);
		WalletNode.CHAIN().inviteMachine();
		
		String optionStr;
		while (option != WalletOptions.QUIT)
		{
			WalletUI.CHAIN().printMenu();
			optionStr = in.nextLine();
			option = Integer.parseInt(optionStr);
			System.out.println("Your choice: " + option);
			try
			{
//				WalletUI.CHAIN().send(yourName, machineName, option);
//				WalletUI.CHAIN().send(machineName, option);
				WalletUI.CHAIN().send(option);
			}
			catch (NumberFormatException e)
			{
				option = WalletOptions.NO_OPTION;
				System.out.println(WalletMenu.WRONG_OPTION);
			}
		}
		
		WalletNode.CHAIN().stop();
		in.close();
		System.out.println("Wallet closed!");
	}

}
