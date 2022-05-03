package org.greatfree.cry.multicast.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.concurrency.ThreadPool;
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
// final class ChildReader<Dispatcher extends CryptoCSDispatcher>
final class ChildReader<Dispatcher extends ServerDispatcher<ServerMessage>>
{
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
	
	public void asyncRead(MulticastRequest request)
	{
		this.asynReader.read(request);
	}
	
	/*
	 * Disseminate the instance of Message. The message here is the one which is just received. It must be forwarded by the local client. 11/11/2014, Bing Li
	 */
	public void syncRead(MulticastRequest request) throws IOException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, InterruptedException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, ClassNotFoundException, RemoteReadException
	{
		this.multicastor.read(request);
	}
}
