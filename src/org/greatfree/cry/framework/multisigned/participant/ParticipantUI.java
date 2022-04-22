package org.greatfree.cry.framework.multisigned.participant;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.cry.exceptions.CheatingException;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.OwnerCheatingException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.framework.multisigned.MSConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
class ParticipantUI
{
	private ParticipantUI()
	{
	}

	private static ParticipantUI instance = new ParticipantUI();
	
	public static ParticipantUI CONS()
	{
		if (instance == null)
		{
			instance = new ParticipantUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void printMenu()
	{
		System.out.println(ParticipantMenu.MENU_HEAD);

		System.out.println(ParticipantMenu.REQUEST_OWNERSHIP);

		System.out.println(ParticipantMenu.UNI_CONSENSUS_NOTIFICATION);
		System.out.println(ParticipantMenu.ANY_CONSENSUS_NOTIFICATION);
		System.out.println(ParticipantMenu.BROAD_CONSENSUS_NOTIFICATION);

		System.out.println(ParticipantMenu.UNI_CONSENSUS_REQUEST);
		System.out.println(ParticipantMenu.ANY_CONSENSUS_REQUEST);
		System.out.println(ParticipantMenu.BROAD_CONSENSUS_REQUEST);

		System.out.println(ParticipantMenu.STOP_SERVER);
		
		System.out.println(ParticipantMenu.QUIT);
		System.out.println(ParticipantMenu.MENU_TAIL);
		System.out.println(ParticipantMenu.INPUT_PROMPT);
	}

	public void send(int option) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, CryptographyMismatchException, RemoteReadException, IOException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException, InterruptedException
	{
		switch (option)
		{
			case ParticipantOptions.REQUEST_OWNERSHIP:
				boolean isSucceeded = Participant.CONS().claimAsMachineOwner();
				if (isSucceeded)
				{
					System.out.println("You are succeeded to own the server, " + Participant.CONS().getServerName());
				}
				else
				{
					System.out.println("You are failed to own the server, " + Participant.CONS().getServerName());
				}
				break;
				
			case ParticipantOptions.UNI_CONSENSUS_NOTIFICATION:
				Participant.CONS().uniConsensusNotify(MSConfig.UNI_NOTIFY_OPERATION, MSConfig.UNI_NOTIFY_DESCRIPTION);
				break;
				
			case ParticipantOptions.ANY_CONSENSUS_NOTIFICATION:
				Participant.CONS().anyConsensusNotify(MSConfig.ANY_NOTIFY_OPERATION, MSConfig.ANY_NOTIFY_DESCRIPTION);
				break;
				
			case ParticipantOptions.BROAD_CONSENSUS_NOTIFICATION:
				Participant.CONS().broadConsensusNotify(MSConfig.BROAD_NOTIFY_OPERATION, MSConfig.BROAD_NOTIFY_DESCRIPTION);
				break;
				
			case ParticipantOptions.UNI_CONSENSUS_REQUEST:
				Participant.CONS().uniConsensusRequest(MSConfig.UNI_REQUEST_OPERATION, MSConfig.UNI_REQUEST_DESCRIPTION);
				break;
				
			case ParticipantOptions.ANY_CONSENSUS_REQUEST:
				Participant.CONS().anyConsensusRequest(MSConfig.ANY_REQUEST_OPERATION, MSConfig.ANY_REQUEST_DESCRIPTION);
				break;
				
			case ParticipantOptions.BROAD_CONSENSUS_REQUEST:
				Participant.CONS().broadConsensusRequest(MSConfig.BROAD_REQUEST_OPERATION, MSConfig.BROAD_REQUEST_DESCRIPTION);
				break;
				
			case ParticipantOptions.STOP_SERVER:
				Participant.CONS().stopServer();
				break;
		}
	}
	
}
