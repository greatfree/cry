package edu.greatfree.cry.framework.ownership.owner;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.OwnerCheatingException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.exceptions.PeerNotRegisteredException;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;

import edu.greatfree.cry.framework.ownership.message.ReadResponse;

/**
 * 
 * @author libing
 * 
 * 03/19/2022
 *
 */
class OwnerUI
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.ownership.owner");

	private OwnerUI()
	{
	}

	private static OwnerUI instance = new OwnerUI();
	
	public static OwnerUI RSC()
	{
		if (instance == null)
		{
			instance = new OwnerUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void printMenu()
	{
		System.out.println(OwnerMenu.MENU_HEAD);

		System.out.println(OwnerMenu.REQUEST_OWNERSHIP);
		System.out.println(OwnerMenu.ABANDON_OWNERSHIP);
		System.out.println(OwnerMenu.WRITE);
		System.out.println(OwnerMenu.WRITE_SYMMETRICALLY);
		System.out.println(OwnerMenu.WRITE_ASYMMETRICALLY);
		System.out.println(OwnerMenu.WRITE_BY_SIGNATURE);
		System.out.println(OwnerMenu.WRITE_PRIVATELY);

		System.out.println(OwnerMenu.READ);
		System.out.println(OwnerMenu.READ_SYMMETRICALLY);
		System.out.println(OwnerMenu.READ_ASYMMETRICALLY);
		System.out.println(OwnerMenu.READ_BY_SIGNATURE);
		System.out.println(OwnerMenu.READ_PRIVATELY);
		
		System.out.println(OwnerMenu.STOP_MACHINE_PRIVATELY);
		System.out.println(OwnerMenu.STOP_MACHINE_PUBLICLY);

		System.out.println(OwnerMenu.QUIT);
		System.out.println(OwnerMenu.MENU_TAIL);
		System.out.println(OwnerMenu.INPUT_PROMPT);
	}
	
	public void send(int option) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, CryptographyMismatchException, RemoteReadException, IOException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException, PeerNotRegisteredException, InterruptedException, SymmetricKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		boolean isSucceeded;
		ReadResponse rr;
		switch (option)
		{
			case OwnerOptions.REQUEST_OWNERSHIP:
				isSucceeded = Owner.RSC().claimAsMachineOwner();
				if (isSucceeded)
				{
					System.out.println("You are succeeded to own the machine, " + Owner.RSC().getMachineName());
				}
				else
				{
					System.out.println("You are failed to own the machine, " + Owner.RSC().getMachineName());
				}
				break;

			case OwnerOptions.ABANDON_OWNERSHIP:
				isSucceeded = Owner.RSC().abandonOwner();
				if (isSucceeded)
				{
					System.out.println("You are succeeded to abandon the machine, " + Owner.RSC().getMachineName());
				}
				else
				{
					System.out.println("You are failed to abandon the machine, " + Owner.RSC().getMachineName());
				}
				break;
				
			case OwnerOptions.WRITE:
				Owner.RSC().write("Hello, I am writing as a notification!");
				break;
				
			case OwnerOptions.WRITE_SYMMETRICALLY:
				try
				{
					Owner.RSC().writeSymmetrically("Hello, I am writing as a notification symmetrically!");
				}
				catch (CryptographyMismatchException e)
				{
					log.info("Exception: Symmetrical cryptography method cannot be invoked because the client employs the asymmetrical algorithm!");
				}
				break;
				
			case OwnerOptions.WRITE_ASYMMETRICALLY:
				Owner.RSC().writeAsymmetrically("Hello, I am writing as a notification asymmetrically!");
				break;
				
			case OwnerOptions.WRITE_BY_SIGNATURE:
				Owner.RSC().writeBySignature("Hello, I am writing as a notification by signature!");
				break;
				
			case OwnerOptions.WRITE_PRIVATELY:
				Owner.RSC().writePrivately("Hello, I am writing as a notification privately!");
				break;

			case OwnerOptions.READ:
				rr = Owner.RSC().read("Hello, I am reading as a request/response!");
				if (rr != null)
				{
					System.out.println(rr.getResponse());
				}
				else
				{
					System.out.println("Response is null!");
				}
				break;

			case OwnerOptions.READ_SYMMETRICALLY:
				try
				{
					rr = Owner.RSC().readSymmetrically("Hello, I am reading as a request/response symmetrically!");
					if (rr != null)
					{
						System.out.println(rr.getResponse());
					}
					else
					{
						System.out.println("Response is null!");
					}
				}
				catch (CryptographyMismatchException e)
				{
					log.info("Exception: Symmetrical cryptography method cannot be invoked because the client employs the asymmetrical algorithm!");
				}
				break;

			case OwnerOptions.READ_ASYMMETRICALLY:
				rr = Owner.RSC().readAsymmetrically("Hello, I am reading as a request/response asymmetrically!");
				if (rr != null)
				{
					System.out.println(rr.getResponse());
				}
				else
				{
					System.out.println("Response is null!");
				}
				break;

			case OwnerOptions.READ_BY_SIGNATURE:
				rr = Owner.RSC().readBySignature("Hello, I am reading as a request/response by signature!");
				log.info("After readBySignature() ...");
				if (rr != null)
				{
					System.out.println(rr.getResponse());
				}
				else
				{
					System.out.println("Response is null!");
				}
				break;

			case OwnerOptions.READ_PRIVATELY:
				rr = Owner.RSC().readPrivately("Hello, I am reading as a request/response privately!");
				if (rr != null)
				{
					System.out.println(rr.getResponse());
				}
				else
				{
					System.out.println("Response is null!");
				}
				break;
				
			case OwnerOptions.STOP_MACHINE_PRIVATELY:
				Owner.RSC().stopMachinePrivately();
				break;
				
			case OwnerOptions.STOP_MACHINE_PUBLICLY:
				Owner.RSC().stopMachinePublicly();
				break;
		}
	}
	
}
