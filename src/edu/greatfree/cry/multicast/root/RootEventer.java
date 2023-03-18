package edu.greatfree.cry.multicast.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;

import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;
import edu.greatfree.cry.server.CryptoCSDispatcher;

/**
 * 
 * @author libing
 * 
 * 04/06/2022
 *
 */
final class RootEventer<Dispatcher extends CryptoCSDispatcher>
{
	private RootSyncMulticastor<Dispatcher> multicastor;
	// The instance of the asynchronous based on ActorPool. 09/14/2018, Bing Li
	private RootAsyncMulticastEventer<Dispatcher> asyncEventer;
//	private AtomicInteger cryptoOption;

//	public RootEventer(RootSyncMulticastor multicastor, ThreadPool pool, int cryptoOption)
	public RootEventer(RootSyncMulticastor<Dispatcher> multicastor, ThreadPool pool)
	{
		this.multicastor = multicastor;
//		this.asyncEventer = new RootAsyncMulticastEventer(this.multicastor, cryptoOption, pool);
		this.asyncEventer = new RootAsyncMulticastEventer<Dispatcher>(this.multicastor, pool);
//		this.cryptoOption = new AtomicInteger(cryptoOption);
	}
	
	public void dispose() throws IOException, InterruptedException
	{
		this.asyncEventer.dispose();
	}
	
	public String getRandomClientKey()
	{
		return this.multicastor.getRandomClientKey();
	}
	
	/*
	 * The method is invoked when intercasting is performed. Since the remote application-level client has the application-partner ID only. The ID must have a corresponding cluster-child/client. The method return the child/client ID in the cluster. 03/12/2019, Bing Li 
	 */
	public String getNearestChildKey(String key)
	{
		return this.multicastor.getNearestClientKey(key);
	}
	
	/*
	 * Multicast data in an asynchronous way. It could be a broadcast or an anycast. 09/15/2018, Bing Li
	 */
	public void asyncNotify(PrimitiveMulticastNotification notification)
	{
		this.asyncEventer.asyncNotify(notification);
	}
	
	/*
	 * Multicast data in a synchronous way. It could be a broadcast or an anycast. 09/15/2018, Bing Li
	 */
	public void syncNotify(PrimitiveMulticastNotification notification) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.multicastor.notify(notification);
	}
	
	/*
	 * Broadcast data in an synchronous way within a group of specified children. 09/15/2018, Bing Li
	 */
	public void asyncNotify(PrimitiveMulticastNotification notification, Set<String> childrenKeys)
	{
		this.asyncEventer.asyncNotify(notification, childrenKeys);
	}

	/*
	 * Broadcast data in a synchronous way within a group of specified children. 09/15/2018, Bing Li
	 */
	public void syncNotify(PrimitiveMulticastNotification notification, Set<String> childrenKeys) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.multicastor.notify(notification, childrenKeys);
	}
	
	/*
	 * Broadcast data in a synchronous way within a group of randomly selected n children. 09/11/2020, Bing Li
	 */
	public void syncNotifyWithinNChildren(PrimitiveMulticastNotification notification, int childrenSize) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.multicastor.notifyWithinNChildren(notification, childrenSize);
	}
	
	/*
	 * Broadcast data in an asynchronous way within a group of randomly selected n children. 09/11/2020, Bing Li
	 */
	public void asyncNotifyWithinNChildren(PrimitiveMulticastNotification notification, int childrenSize) throws IOException, DistributedNodeFailedException
	{
		this.asyncEventer.asyncNotifyWithinNChildren(notification, childrenSize);
	}
	
	/*
	 * Unicast data in an asynchronous way to a specified child. 09/15/2018, Bing Li
	 */
	public void asyncNotify(PrimitiveMulticastNotification notification, String childKey)
	{
		this.asyncEventer.asyncNotify(notification, childKey);
	}

	/*
	 * Unicast data in a synchronous way to a specified child. 09/15/2018, Bing Li
	 */
	public void syncNotify(PrimitiveMulticastNotification notification, String childKey) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.multicastor.notify(notification, childKey);
	}
	
	/*
	 * Unicast data in an asynchronous way to a nearest child in terms of the similarity to the specified key. 09/15/2018, Bing Li
	 */
	public void asyncNearestNotify(String key, PrimitiveMulticastNotification notification)
	{
		this.asyncEventer.asyncNearestNotify(key, notification);
	}

	/*
	 * Unicast data in a synchronous way to a nearest child in terms of the similarity to the specified key. 09/15/2018, Bing Li
	 */
	public void syncNearestNotify(String key, PrimitiveMulticastNotification notification) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.multicastor.nearestNotify(key, notification);
	}

	/*
	 * Unicast data in an asynchronous way to a child that is selected randomly. 09/15/2018, Bing Li
	 */
	public void asyncRandomNotify(PrimitiveMulticastNotification notification)
	{
		this.asyncEventer.asyncRandomNotify(notification);
	}

	/*
	 * Unicast data in a asynchronous way to a child that is selected randomly. 09/15/2018, Bing Li
	 */
	public void syncRandomNotify(PrimitiveMulticastNotification notification) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.multicastor.randomNotify(notification);
	}
}
