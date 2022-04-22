package org.greatfree.cry.multicast.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.multicast.root.ChildKeyMulticastRequest;
import org.greatfree.multicast.root.ChildrenMulticastRequest;
import org.greatfree.multicast.root.ChildrenSizeMulticastRequest;
import org.greatfree.multicast.root.NearestKeyMulticastRequest;
import org.greatfree.multicast.root.NearestKeysMulticastRequest;
import org.greatfree.multicast.root.RandomChildrenMulticastRequest;
import org.greatfree.multicast.root.RootRendezvousPoint;
import org.greatfree.multicast.root.SizeMulticastRequest;

/**
 * 
 * @author libing
 * 
 * 04/08/2022
 *
 */
final class RootReader
{
	private RootSyncMulticastor multicastor;

	private RootAsyncMulticastReader asyncReader;

	private RootRendezvousPoint rp;

	public RootReader(RootSyncMulticastor multicastor, long waitTime, ThreadPool pool)
	{
//		this.asyncReader = new RootAsyncMulticastReader(multicastor, cryptoOption, pool);
		this.asyncReader = new RootAsyncMulticastReader(multicastor, pool);

		this.rp = new RootRendezvousPoint(waitTime);
		this.multicastor = multicastor;
		this.multicastor.setRP(this.rp);
	}

	/*
	 * Dispose the broadcast requestor. 11/28/2014, Bing Li
	 */
	public void dispose() throws IOException, InterruptedException
	{
		this.asyncReader.dispose();
		this.rp.dispose();
	}

	public RootRendezvousPoint getRP()
	{
		return this.rp;
	}
	
	/*
	 * For reading, only the task to send requests is processed asynchronously. It has to wait for responses from children. 09/15/2018, Bing Li
	 */
	public List<MulticastResponse> asyncRead(MulticastRequest request)
	{
		this.asyncReader.asyncRead(request);
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Broadcast the request in a synchronous way. In a two-node case, reading is always performed synchronously. But in a one-to-many case, the request can be sent either synchronously or asynchronously. The method does that synchronously. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> syncRead(MulticastRequest request) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.multicastor.read(request);
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	public List<MulticastResponse> asyncReadWithNResponses(MulticastRequest request, int n)
	{
		this.asyncReader.asyncRead(new SizeMulticastRequest(request, n));
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Anycast the request synchronously to the certain number of children. In a two-node case, reading is always performed synchronously. But in a one-to-many case, the request can be sent either synchronously or asynchronously. The method does that synchronously. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> syncReadWithNResponses(MulticastRequest request, int n) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.multicastor.readWithNResponses(request, n);
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	public List<MulticastResponse> asyncRead(Set<String> childrenKeys, MulticastRequest request)
	{
		this.asyncReader.asyncRead(new ChildrenMulticastRequest(request, childrenKeys));
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Broadcast the request synchronously to the specified children. In a two-node case, reading is always performed synchronously. But in a one-to-many case, the request can be sent either synchronously or asynchronously. The method does that synchronously. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> syncReadWithinNChildren(Set<String> childrenKeys, MulticastRequest request) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.multicastor.readWithinNChildren(childrenKeys, request);
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Broadcasting a request synchronously within randomly selected N children. 09/11/2020, Bing Li
	 */
	public List<MulticastResponse> syncReadWithinNChildren(int n, MulticastRequest request) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.multicastor.readWithinNChildren(request, n);
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	/*
	 * Broadcasting a request asynchronously within randomly selected N children. 09/11/2020, Bing Li
	 */
	public List<MulticastResponse> asyncReadWithinNChildren(int n, MulticastRequest request)
	{
		this.asyncReader.asynRead(new RandomChildrenMulticastRequest(request, n));
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Broadcasting a request synchronously within one partition which contains the specified N children. 09/11/2020, Bing Li
	 */
	public MulticastResponse syncReadUponPartition(Set<String> childrenKeys, MulticastRequest request) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.multicastor.readWithinNChildren(childrenKeys, request);
		return this.rp.waitForResponseUponPartition(request.getCollaboratorKey());
	}

	/*
	 * Broadcasting a request asynchronously within one partition which contains the specified N children. 09/11/2020, Bing Li
	 */
	public MulticastResponse asyncReadUponPartition(Set<String> childrenKeys, MulticastRequest request)
	{
		this.asyncReader.asyncRead(new ChildrenMulticastRequest(request, childrenKeys));
		return this.rp.waitForResponseUponPartition(request.getCollaboratorKey());
	}

	public List<MulticastResponse> asyncRead(Set<String> childrenKeys, MulticastRequest request, int n)
	{
		this.asyncReader.asyncRead(new ChildrenSizeMulticastRequest(request, childrenKeys, n));
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Anycast the request synchronously to the specified children and only expect a certain number of responses from the children. In a two-node case, reading is always performed synchronously. But in a one-to-many case, the request can be sent either synchronously or asynchronously. The method does that synchronously. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> syncRead(Set<String> childrenKeys, MulticastRequest request, int n) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.multicastor.read(childrenKeys, request, n);
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	public List<MulticastResponse> asyncNearestRead(Set<String> dataKeys, MulticastRequest request)
	{
		this.asyncReader.asyncRead(new NearestKeysMulticastRequest(request, dataKeys));
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	/*
	 * Broadcast the request to nearest children synchronously in terms of the similarity to the specified data keys respectively. In a two-node case, reading is always performed synchronously. But in a one-to-many case, the request can be sent either synchronously or asynchronously. The method does that synchronously. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> syncNearestRead(Set<String> dataKeys, MulticastRequest request) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.multicastor.nearestRead(dataKeys, request);
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	public List<MulticastResponse> asyncRead(String childKey, MulticastRequest request)
	{
		this.asyncReader.asyncRead(new ChildKeyMulticastRequest(request, childKey));
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	/*
	 * Unicast the request synchronously to one specified client. In a two-node case, reading is always performed synchronously. But in a one-to-many case, the request can be sent either synchronously or asynchronously. The method does that synchronously. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> syncRead(String childKey, MulticastRequest request) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.multicastor.read(childKey, request);
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	public List<MulticastResponse> asyncRandomRead(MulticastRequest request)
	{
		this.asyncReader.asyncRandomRead(request);
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	/*
	 * Unicast the request synchronously to one client that is selected randomly. In a two-node case, reading is always performed synchronously. But in a one-to-many case, the request can be sent either synchronously or asynchronously. The method does that synchronously. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> syncRandomRead(MulticastRequest request) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.multicastor.randomRead(request);
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}
	
	public List<MulticastResponse> asyncNearestRead(String key, MulticastRequest request)
	{
		this.asyncReader.asyncRead(new NearestKeyMulticastRequest(request, key));
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Unicast the request synchronously to one nearest client in terms of the similarity to a specified key. In a two-node case, reading is always performed synchronously. But in a one-to-many case, the request can be sent either synchronously or asynchronously. The method does that synchronously. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> syncNearestRead(String key, MulticastRequest request) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.multicastor.nearestRead(key, request);
		return this.rp.waitForResponses(request.getCollaboratorKey());
	}

}
