package edu.greatfree.cry.multicast.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.concurrency.Notifier;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
// final class ChildEventActor<Dispatcher extends CryptoCSDispatcher> extends Async<MulticastNotification>
// final class ChildEventActor<Dispatcher extends ServerDispatcher<ServerMessage>> extends Async<PrimitiveMulticastNotification>
final class ChildEventActor<Dispatcher extends ServerDispatcher<ServerMessage>> implements Notifier<PrimitiveMulticastNotification>
{
	private ChildSyncMulticastor<Dispatcher> multicastor;
	
	public ChildEventActor(ChildSyncMulticastor<Dispatcher> multicastor)
	{
		this.multicastor = multicastor;
	}

	@Override
//	public void perform(PrimitiveMulticastNotification notification)
	public void notify(PrimitiveMulticastNotification notification)
	{
		try
		{
			this.multicastor.notify(notification);
		}
		catch (IOException | DistributedNodeFailedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | InterruptedException | SymmetricKeyUnavailableException | CryptographyMismatchException | PublicKeyUnavailableException | ClassNotFoundException | RemoteReadException | RemoteIPNotExistedException e)
		{
			e.printStackTrace();
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
		}
	}

}
