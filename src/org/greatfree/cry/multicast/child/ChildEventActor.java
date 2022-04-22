package org.greatfree.cry.multicast.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.concurrency.Async;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.MulticastNotification;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
final class ChildEventActor extends Async<MulticastNotification>
{
	private ChildSyncMulticastor multicastor;
	
	public ChildEventActor(ChildSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}

	@Override
	public void perform(MulticastNotification notification)
	{
		try
		{
			this.multicastor.notify(notification);
		}
		catch (IOException | DistributedNodeFailedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | InterruptedException | SymmetricKeyUnavailableException | CryptographyMismatchException | PublicKeyUnavailableException | ClassNotFoundException | RemoteReadException e)
		{
			e.printStackTrace();
		}
	}

}
