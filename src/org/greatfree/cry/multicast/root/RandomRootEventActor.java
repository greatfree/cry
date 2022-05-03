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
import org.greatfree.cry.server.CryptoCSDispatcher;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.MulticastNotification;

/**
 * 
 * @author libing
 * 
 * 04/08/2022
 *
 */
final class RandomRootEventActor<Dispatcher extends CryptoCSDispatcher> extends AsyncMulticastor<MulticastNotification, Dispatcher>
{

//	public RandomRootEventActor(RootSyncMulticastor multicastor)
	public RandomRootEventActor(RootSyncMulticastor<Dispatcher> multicastor, int cryptoOption)
	{
		super(multicastor, cryptoOption);
//		super(multicastor);
	}

	@Override
	public void perform(MulticastNotification notification)
	{
		try
		{
//			super.getMulticastor().randomNotify(notification, super.getCryptoOption());
			super.getMulticastor().randomNotify(notification);
		}
		catch (IOException | DistributedNodeFailedException | InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | RemoteReadException | CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException | PublicKeyUnavailableException e)
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(MulticastNotification notification, int cryptoOption)
	{
	}
	*/

}
