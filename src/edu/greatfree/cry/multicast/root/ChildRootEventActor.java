package edu.greatfree.cry.multicast.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.multicast.root.ChildMulticastNotification;

import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.server.CryptoCSDispatcher;

/**
 * 
 * @author libing
 * 
 * 04/08/2022
 *
 */
final class ChildRootEventActor<Dispatcher extends CryptoCSDispatcher> extends AsyncMulticastor<ChildMulticastNotification, Dispatcher>
{

//	public ChildRootEventActor(RootSyncMulticastor multicastor)
	public ChildRootEventActor(RootSyncMulticastor<Dispatcher> multicastor, int cryptoOption)
	{
		super(multicastor, cryptoOption);
//		super(multicastor);
	}

	@Override
	public void notify(ChildMulticastNotification notification)
	{
		try
		{
//			super.getMulticastor().notify(notification.getNotification(), notification.getChildKey(), super.getCryptoOption());
			super.getMulticastor().notify(notification.getNotification(), notification.getChildKey());
		}
		catch (IOException | DistributedNodeFailedException | InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | RemoteReadException | CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException | PublicKeyUnavailableException | RemoteIPNotExistedException e)
		{
			e.printStackTrace();
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
		}
		catch (IPNotExistedException e)
		{
//			log.info(e.toString());
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
