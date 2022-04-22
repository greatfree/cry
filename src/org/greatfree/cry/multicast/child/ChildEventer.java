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
import org.greatfree.message.multicast.MulticastNotification;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
final class ChildEventer
{
	private ChildSyncMulticastor multicastor;
	private ChildAsyncMulticastEventer asyncEventer;
	
	public ChildEventer(ChildSyncMulticastor multicastor, ThreadPool pool)
	{
		this.multicastor = multicastor;
		this.asyncEventer = new ChildAsyncMulticastEventer(multicastor, pool);
	}
	
	public void dispose() throws IOException, InterruptedException
	{
		this.asyncEventer.dispose();
	}
	
	/*
	 * Disseminate the instance of Message asynchronously. 11/11/2014, Bing Li
	 */
	public void asyncNotify(MulticastNotification notification)
	{
		this.asyncEventer.notify(notification);
	}
	
	/*
	 * Disseminate the instance of Message synchronously. 11/11/2014, Bing Li
	 */
	public void syncNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, InterruptedException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, ClassNotFoundException, RemoteReadException
	{
		this.multicastor.notify(notification);
	}
}
