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
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.server.ServerDispatcher;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
// final class ChildReadActor<Dispatcher extends CryptoCSDispatcher> extends Async<MulticastRequest>
final class ChildReadActor<Dispatcher extends ServerDispatcher<ServerMessage>> extends Async<MulticastRequest>
{
	private ChildSyncMulticastor<Dispatcher> multicastor;
	
	public ChildReadActor(ChildSyncMulticastor<Dispatcher> multicastor)
	{
		this.multicastor = multicastor;
	}

	@Override
	public void perform(MulticastRequest request)
	{
		try
		{
			this.multicastor.read(request);
		}
		catch (IOException | DistributedNodeFailedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | InterruptedException | SymmetricKeyUnavailableException | CryptographyMismatchException | PublicKeyUnavailableException | ClassNotFoundException | RemoteReadException e)
		{
			e.printStackTrace();
		}
	}

}
