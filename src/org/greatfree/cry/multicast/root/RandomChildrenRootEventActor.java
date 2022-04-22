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
import org.greatfree.multicast.root.RandomChildrenMulticastNotification;

/**
 * 
 * @author libing
 * 
 * 04/08/2022
 *
 */
final class RandomChildrenRootEventActor extends AsyncMulticastor<RandomChildrenMulticastNotification>
{

//	public RandomChildrenRootEventActor(RootSyncMulticastor multicastor)
	public RandomChildrenRootEventActor(RootSyncMulticastor multicastor, int cryptoOption)
	{
		super(multicastor, cryptoOption);
//		super(multicastor);
	}

	@Override
	public void perform(RandomChildrenMulticastNotification notification)
	{
		try
		{
//			super.getMulticastor().notifyWithinNChildren(notification.getNotification(), notification.getChildrenSize(), super.getCryptoOption());
			super.getMulticastor().notifyWithinNChildren(notification.getNotification(), notification.getChildrenSize());
		}
		catch (IOException | DistributedNodeFailedException | InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | RemoteReadException | CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException | PublicKeyUnavailableException e)
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(RandomChildrenMulticastNotification notification, int cryptoOption)
	{
	}
	*/

}
