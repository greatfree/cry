package org.greatfree.cry.framework.ownership.owner;

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

import org.greatfree.cry.exceptions.CheatingException;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.OwnerCheatingException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.ownership.message.ReadResponse;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

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
	
	public void send(int option)
	{
		switch (option)
		{
			case OwnerOptions.REQUEST_OWNERSHIP:
				try
				{
					boolean isSucceeded = Owner.RSC().claimAsMachineOwner();
					if (isSucceeded)
					{
						System.out.println("You are succeeded to own the machine, " + Owner.RSC().getMachineName());
					}
					else
					{
						System.out.println("You are failed to own the machine, " + Owner.RSC().getMachineName());
					}
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| ShortBufferException | SignatureException | CryptographyMismatchException | RemoteReadException
						| IOException | DistributedNodeFailedException | OwnerCheatingException | CheatingException
						| PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				
			case OwnerOptions.WRITE:
				try
				{
					Owner.RSC().write("Hello, I am writing as a notification!");
				}
				catch (ClassNotFoundException | RemoteReadException | IOException | InterruptedException
						| DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case OwnerOptions.WRITE_SYMMETRICALLY:
				try
				{
					try
					{
						Owner.RSC().writeSymmetrically("Hello, I am writing as a notification symmetrically!");
					}
					catch (CryptographyMismatchException e)
					{
						log.info("Exception: Symmetrical cryptography method cannot be invoked because the client employs the asymmetrical algorithm!");
					}
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| RemoteReadException | IOException | InterruptedException
						| DistributedNodeFailedException | SymmetricKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				
			case OwnerOptions.WRITE_ASYMMETRICALLY:
				try
				{
					Owner.RSC().writeAsymmetrically("Hello, I am writing as a notification asymmetrically!");
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| RemoteReadException | IOException | InterruptedException | CryptographyMismatchException
						| DistributedNodeFailedException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				
			case OwnerOptions.WRITE_BY_SIGNATURE:
				try
				{
					Owner.RSC().writeBySignature("Hello, I am writing as a notification by signature!");
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| SignatureException | RemoteReadException | IOException | InterruptedException
						| CryptographyMismatchException | DistributedNodeFailedException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				
			case OwnerOptions.WRITE_PRIVATELY:
				try
				{
					Owner.RSC().writePrivately("Hello, I am writing as a notification privately!");
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| SignatureException | RemoteReadException | IOException | InterruptedException
						| CryptographyMismatchException | DistributedNodeFailedException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;

			case OwnerOptions.READ:
				try
				{
					ReadResponse rr = Owner.RSC().read("Hello, I am reading as a request/response!");
					if (rr != null)
					{
						System.out.println(rr.getResponse());
					}
					else
					{
						System.out.println("Response is null!");
					}
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| ShortBufferException | SignatureException | RemoteReadException | IOException
						| CryptographyMismatchException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				break;

			case OwnerOptions.READ_SYMMETRICALLY:
				try
				{
					try
					{
						ReadResponse rr = Owner.RSC().readSymmetrically("Hello, I am reading as a request/response symmetrically!");
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
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| ShortBufferException | RemoteReadException | IOException | DistributedNodeFailedException | SymmetricKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;

			case OwnerOptions.READ_ASYMMETRICALLY:
				try
				{
					ReadResponse rr = Owner.RSC().readAsymmetrically("Hello, I am reading as a request/response asymmetrically!");
					if (rr != null)
					{
						System.out.println(rr.getResponse());
					}
					else
					{
						System.out.println("Response is null!");
					}
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| ShortBufferException | RemoteReadException | IOException
						| CryptographyMismatchException | DistributedNodeFailedException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;

			case OwnerOptions.READ_BY_SIGNATURE:
				try
				{
					ReadResponse rr = Owner.RSC().readBySignature("Hello, I am reading as a request/response by signature!");
//					log.info("After readBySignature() ...");
					if (rr != null)
					{
						System.out.println(rr.getResponse());
					}
					else
					{
						System.out.println("Response is null!");
					}
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| ShortBufferException | RemoteReadException | IOException
						| CryptographyMismatchException | DistributedNodeFailedException | PublicKeyUnavailableException | SignatureException | OwnerCheatingException | CheatingException e)
				{
					e.printStackTrace();
				}
				break;

			case OwnerOptions.READ_PRIVATELY:
				try
				{
					ReadResponse rr = Owner.RSC().readPrivately("Hello, I am reading as a request/response privately!");
					if (rr != null)
					{
						System.out.println(rr.getResponse());
					}
					else
					{
						System.out.println("Response is null!");
					}
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| ShortBufferException | RemoteReadException | IOException
						| CryptographyMismatchException | DistributedNodeFailedException | SignatureException e)
				{
					e.printStackTrace();
				}
				break;
				
			case OwnerOptions.STOP_MACHINE_PRIVATELY:
				try
				{
					Owner.RSC().stopMachinePrivately();
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| SignatureException | RemoteReadException | IOException | InterruptedException
						| CryptographyMismatchException | DistributedNodeFailedException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				
			case OwnerOptions.STOP_MACHINE_PUBLICLY:
				try
				{
					Owner.RSC().stopMachinePublicly();
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| SignatureException | RemoteReadException | IOException | InterruptedException
						| CryptographyMismatchException | DistributedNodeFailedException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
		}
	}
	
}
