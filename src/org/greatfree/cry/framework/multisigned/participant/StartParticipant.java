package org.greatfree.cry.framework.multisigned.participant;

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
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
class StartParticipant
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, DistributedNodeFailedException, CryptographyMismatchException, InterruptedException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, IOException, ShortBufferException, OwnerCheatingException, CheatingException
	{
		int option = ParticipantOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);

		System.out.println("Tell me your name: ");
		String nodeName = in.nextLine();
		System.out.println("Tell me your machine's name: ");
		String machineName = in.nextLine();
		System.out.println("Tell me the owner's name for the machine: ");
		String yourName = in.nextLine();

		try
		{
			Participant.CONS().start(nodeName, yourName, nodeName, machineName);
			Participant.CONS().inviteServer();
		}
		catch (ClassNotFoundException | IOException | RemoteReadException e)
		{
			e.printStackTrace();
		}

		String optionStr;
		while (option != ParticipantOptions.QUIT)
		{
			ParticipantUI.CONS().printMenu();
			optionStr = in.nextLine();
			option = Integer.parseInt(optionStr);
			System.out.println("Your choice: " + option);

			try
			{
				ParticipantUI.CONS().send(option);
			}
			catch (NumberFormatException e)
			{
				option = ParticipantOptions.NO_OPTION;
				System.out.println(ParticipantMenu.WRONG_OPTION);
			}
		}
		
		Participant.CONS().stop();
		in.close();
		System.out.println("Participant quit!");
	}

}
