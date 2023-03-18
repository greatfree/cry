package edu.greatfree.cry.multicast.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

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
// final class ChildEventer<Dispatcher extends CryptoCSDispatcher>
final class ChildEventer<Dispatcher extends ServerDispatcher<ServerMessage>>
{
	private ChildSyncMulticastor<Dispatcher> multicastor;
	private ChildAsyncMulticastEventer<Dispatcher> asyncEventer;
	
	public ChildEventer(ChildSyncMulticastor<Dispatcher> multicastor, ThreadPool pool)
	{
		this.multicastor = multicastor;
		this.asyncEventer = new ChildAsyncMulticastEventer<Dispatcher>(multicastor, pool);
	}
	
	public void dispose() throws IOException, InterruptedException
	{
		this.asyncEventer.dispose();
	}
	
	/*
	 * Disseminate the instance of Message asynchronously. 11/11/2014, Bing Li
	 */
	public void asyncNotify(PrimitiveMulticastNotification notification)
	{
		this.asyncEventer.notify(notification);
	}
	
	/*
	 * Disseminate the instance of Message synchronously. 11/11/2014, Bing Li
	 */
	public void syncNotify(PrimitiveMulticastNotification notification) throws IOException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, InterruptedException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.multicastor.notify(notification);
	}
}
