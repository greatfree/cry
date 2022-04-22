package org.greatfree.cry.multicast.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.multicast.root.ChildMulticastNotification;

/**
 * 
 * @author libing
 * 
 * 04/08/2022
 *
 */
final class ChildRootEventActor extends AsyncMulticastor<ChildMulticastNotification>
{

//	public ChildRootEventActor(RootSyncMulticastor multicastor)
	public ChildRootEventActor(RootSyncMulticastor multicastor, int cryptoOption)
	{
		super(multicastor, cryptoOption);
//		super(multicastor);
	}

	@Override
	public void perform(ChildMulticastNotification notification)
	{
		try
		{
//			super.getMulticastor().notify(notification.getNotification(), notification.getChildKey(), super.getCryptoOption());
			super.getMulticastor().notify(notification.getNotification(), notification.getChildKey());
		}
		catch (IOException | DistributedNodeFailedException | InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | RemoteReadException | CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException | PublicKeyUnavailableException e)
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(ChildMulticastNotification notification, int cryptoOption)
	{
	}
	*/

}
