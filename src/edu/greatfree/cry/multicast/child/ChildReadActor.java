package edu.greatfree.cry.multicast.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.logging.Logger;

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
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastRequest;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
// final class ChildReadActor<Dispatcher extends CryptoCSDispatcher> extends Async<MulticastRequest>
// final class ChildReadActor<Dispatcher extends ServerDispatcher<ServerMessage>> extends Async<PrimitiveMulticastRequest>
final class ChildReadActor<Dispatcher extends ServerDispatcher<ServerMessage>> implements Notifier<PrimitiveMulticastRequest>
{
	private final static Logger log = Logger.getLogger("edu.greatfree.cry.multicast.child");

	private ChildSyncMulticastor<Dispatcher> multicastor;
	
	public ChildReadActor(ChildSyncMulticastor<Dispatcher> multicastor)
	{
		this.multicastor = multicastor;
	}

	@Override
	public void notify(PrimitiveMulticastRequest request)
	{
		try
		{
			/*
			Map<String, IPAddress> childrenFromRoot = request.getChildrenIPs();
			for (Map.Entry<String, IPAddress> entry : childrenFromRoot.entrySet())
			{
				log.info("child entry = " + entry);
			}
			*/
			this.multicastor.read(request);
		}
		catch (IOException | DistributedNodeFailedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | SignatureException | InterruptedException | SymmetricKeyUnavailableException | CryptographyMismatchException | PublicKeyUnavailableException | ClassNotFoundException | RemoteReadException | RemoteIPNotExistedException e)
		{
			e.printStackTrace();
		}
		catch (PeerNameIsNullException e)
		{
			log.info(e.toString());
			e.printStackTrace();
		}
	}
}
