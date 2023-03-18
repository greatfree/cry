package edu.greatfree.cry.multicast.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;
import org.greatfree.util.IPAddress;

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
// final class ChildReader<Dispatcher extends CryptoCSDispatcher>
final class ChildReader<Dispatcher extends ServerDispatcher<ServerMessage>>
{
	private final static Logger log = Logger.getLogger("edu.greatfree.cry.multicast.child");

	private ChildSyncMulticastor<Dispatcher> multicastor;
	private ChildAsyncMulticastReader<Dispatcher> asynReader;

	public ChildReader(ChildSyncMulticastor<Dispatcher> eventer, ThreadPool pool)
	{
		this.multicastor = eventer;
		this.asynReader = new ChildAsyncMulticastReader<Dispatcher>(eventer, pool);
	}

	public void dispose() throws IOException, InterruptedException
	{
		this.asynReader.dispose();
	}
	
	public void asyncRead(PrimitiveMulticastRequest request)
	{
		this.asynReader.read(request);
	}
	
	/*
	 * Disseminate the instance of Message. The message here is the one which is just received. It must be forwarded by the local client. 11/11/2014, Bing Li
	 */
	public void syncRead(PrimitiveMulticastRequest request) throws IOException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, InterruptedException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		Map<String, IPAddress> childrenFromRoot = request.getChildrenIPs();
		for (Map.Entry<String, IPAddress> entry : childrenFromRoot.entrySet())
		{
			log.info("child entry = " + entry);
		}
		this.multicastor.read(request);
	}
}
