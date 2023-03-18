package edu.greatfree.cry.multicast.root;

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
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Builder;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastRequest;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;
import edu.greatfree.cry.server.CryPeer;
import edu.greatfree.cry.server.CryptoCSDispatcher;

/**
 * 
 * @author libing
 * 
 * 04/06/2022
 *
 */
public final class RootClient<Dispatcher extends CryptoCSDispatcher>
{
	private RootEventer<Dispatcher> eventer;
	private RootReader<Dispatcher> reader;
	private RootSyncMulticastor<Dispatcher> multicastor;

	/*
	public RootClient(Peer eventer, int rootBranchCount, int treeBranchCount, long waitTime, ThreadPool pool, int cryptoOption)
	{
		this.multicastor = new RootSyncMulticastor(eventer, rootBranchCount, treeBranchCount, cryptoOption);
		this.eventer = new RootEventer(this.multicastor, pool);
		this.reader = new RootReader(this.multicastor, waitTime, pool);
	}
	*/
	
	public RootClient(RootClientBuilder<Dispatcher> builder)
	{
		this.multicastor = new RootSyncMulticastor<Dispatcher>(builder.getEventer(), builder.getRootBranchCount(), builder.getTreeBranchCount(), builder.getCryptoOption());
		this.eventer = new RootEventer<Dispatcher>(this.multicastor, builder.getPool());
		this.reader = new RootReader<Dispatcher>(this.multicastor, builder.getWaitTime(), builder.getPool());
	}
	
	public static class RootClientBuilder<Dispatcher extends CryptoCSDispatcher> implements Builder<RootClient<Dispatcher>>
	{
//		private Peer eventer;
//		private CryptoPeer<MulticastRootDispatcher> eventer;
		private CryPeer<Dispatcher> eventer;
		private int rootBranchCount;
		private int treeBranchCount;
		private long waitTime;
		private ThreadPool pool;
		private int cryptoOption;

		public RootClientBuilder()
		{
		}

		/*
		public RootClientBuilder eventer(Peer eventer)
		{
			this.eventer = eventer;
			return this;
		}
		*/
		
		public RootClientBuilder<Dispatcher> eventer(CryPeer<Dispatcher> eventer)
		{
			this.eventer = eventer;
			return this;
		}
		
		public RootClientBuilder<Dispatcher> rootBranchCount(int rootBranchCount)
		{
			this.rootBranchCount = rootBranchCount;
			return this;
		}
		
		public RootClientBuilder<Dispatcher> treeBranchCount(int treeBranchCount)
		{
			this.treeBranchCount = treeBranchCount;
			return this;
		}
		
		public RootClientBuilder<Dispatcher> waitTime(long waitTime)
		{
			this.waitTime = waitTime;
			return this;
		}
		
		public RootClientBuilder<Dispatcher> pool(ThreadPool pool)
		{
			this.pool = pool;
			return this;
		}
		
		public RootClientBuilder<Dispatcher> cryptoOption(int cryptoOption)
		{
			this.cryptoOption = cryptoOption;
			return this;
		}

		@Override
		public RootClient<Dispatcher> build() throws IOException
		{
			return new RootClient<Dispatcher>(this);
		}

		/*
		public Peer getEventer()
		{
			return this.eventer;
		}
		*/
		
		public CryPeer<Dispatcher> getEventer()
		{
			return this.eventer;
		}

		public int getRootBranchCount()
		{
			return this.rootBranchCount;
		}
		
		public int getTreeBranchCount()
		{
			return this.treeBranchCount;
		}
		
		public long getWaitTime()
		{
			return this.waitTime;
		}
		
		public ThreadPool getPool()
		{
			return this.pool;
		}
		
		public int getCryptoOption()
		{
			return this.cryptoOption;
		}
	}
	
	public void close() throws IOException, InterruptedException
	{
		this.multicastor.dispose();
		this.eventer.dispose();
		this.reader.dispose();
	}
	
	public int getCryptoOption()
	{
		return this.multicastor.getCryptoOption();
	}
	
	public void setCryptoOption(int co)
	{
		this.multicastor.setCryptoOption(co);
	}
	
	public void clearChildren()
	{
		this.multicastor.clearChildren();
	}
	
	public void addChild(IPAddress ip) throws PeerNameIsNullException
	{
		this.multicastor.addPartner(ip);
	}

	/*
	public void addChild(String ip, int port)
	{
		this.multicastor.addIP(ip, port);
	}
	*/

	/*
	 * Expose the rendezvous point of the root. 09/03/2018, Bing Li
	 */
	public RootRendezvousPoint getRP()
	{
		return this.reader.getRP();
	}
	
	public String getRandomChildKey()
	{
		return this.eventer.getRandomClientKey();
	}

	/*
	 * The method is invoked when intercasting is performed. Since the remote application-level client has the application-partner ID only. The ID must have a corresponding cluster-child/client. The method return the child/client ID in the cluster. 03/12/2019, Bing Li 
	 */
	public String getNearestChildKey(String key)
	{
		return this.eventer.getNearestChildKey(key);
	}
	
	public Set<String> getChildrenKeys(int size)
	{
		return this.multicastor.getChildrenKeys(size);
	}
	
	/*
	 * Broadcast notifications. 09/03/2018, Bing Li
	 */
	public void broadcastNotify(PrimitiveMulticastNotification notification) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.eventer.syncNotify(notification);
	}

	/*
	 * The method has not been tested although it should be correct. 09/15/2018, Bing Li
	 */
	public void asyncBroadcastNotify(PrimitiveMulticastNotification notification)
	{
		this.eventer.asyncNotify(notification);
	}
	
	/*
	 * Broadcast notifications. 02/23/2019, Bing Li
	 */
	public void broadcastNotify(PrimitiveMulticastNotification notification, Set<String> childrenKeys) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.eventer.syncNotify(notification, childrenKeys);
	}

	/*
	 * The method has not been tested although it should be correct. 02/23/2019, Bing Li
	 */
	public void asyncBroadcastNotify(PrimitiveMulticastNotification notification, Set<String> childrenKeys)
	{
		this.eventer.asyncNotify(notification, childrenKeys);
	}
	
	/*
	 * Broadcasting a notification synchronously within N randomly selected children. 09/11/2020, Bing Li
	 */
	public void broadcastNotify(PrimitiveMulticastNotification notification, int childrenSize) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.eventer.syncNotifyWithinNChildren(notification, childrenSize);
	}
	
	/*
	 * Broadcasting a notification asynchronously within N randomly selected children. 09/11/2020, Bing Li
	 */
	public void asyncBroadcastNotify(PrimitiveMulticastNotification notification, int childrenSize) throws IOException, DistributedNodeFailedException
	{
		this.eventer.asyncNotifyWithinNChildren(notification, childrenSize);
	}
	
	/*
	 * Anycast notifications. 09/03/2018, Bing Li
	 */
	public void anycastNotify(PrimitiveMulticastNotification notification) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.eventer.syncNotify(notification);
	}
	
	/*
	 * The method has not been tested although it should be correct. 09/15/2018, Bing Li
	 */
	public void asyncAnycastNotify(PrimitiveMulticastNotification notification)
	{
		this.eventer.asyncNotify(notification);
	}

	/*
	 * Anycast notifications. 09/03/2018, Bing Li
	 */
	public void anycastNotify(PrimitiveMulticastNotification notification, Set<String> childrenKeys) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.eventer.syncNotify(notification, childrenKeys);
	}
	
	/*
	 * The method has not been tested although it should be correct. 02/23/2019, Bing Li
	 */
	public void asyncAnycastNotify(PrimitiveMulticastNotification notification, Set<String> childrenKeys)
	{
		this.eventer.asyncNotify(notification, childrenKeys);
	}
	
	/*
	 * Unicast notifications. 09/03/2018, Bing Li
	 */
	public void unicastNotify(PrimitiveMulticastNotification notification) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.eventer.syncRandomNotify(notification);
	}
	
	/*
	 * Unicast notifications to the nearest child. 09/03/2018, Bing Li
	 */
	public void unicastNearestNotify(String clientKey, PrimitiveMulticastNotification notification) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.eventer.syncNearestNotify(clientKey, notification);
	}
	
	
	/*
	 * The method has not been tested although it should be correct. 09//15/2018, Bing Li
	 */
	public void asyncUnicastNotify(PrimitiveMulticastNotification notification)
	{
		this.eventer.asyncRandomNotify(notification);
	}
	
	/*
	 * Unicast notifications to the nearest child asynchronously. 09/03/2018, Bing Li
	 */
	public void asyncUnicastNearestNotify(String clientKey, PrimitiveMulticastNotification notification)
	{
		this.eventer.asyncNearestNotify(clientKey, notification);
	}
	
	/*
	 * Broadcast requests. 09/03/2018, Bing Li
	 */
	public List<PrimitiveMulticastResponse> broadcastRead(PrimitiveMulticastRequest request) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		return this.reader.syncRead(request);
	}
	
	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> broadcastRead(PrimitiveMulticastRequest request, Class<T> c) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		return Tools.filter(this.reader.syncRead(request), c);
	}
	
	/*
	 * The method has not been tested although it should be correct. 09//15/2018, Bing Li
	 */
	public List<PrimitiveMulticastResponse> asyncBroadcastRead(PrimitiveMulticastRequest request)
	{
		return this.reader.asyncRead(request);
	}
	
	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> aysncBroadcastRead(PrimitiveMulticastRequest request, Class<T> c)
	{
		return Tools.filter(this.reader.asyncRead(request), c);
	}
	
	public List<PrimitiveMulticastResponse> unicastRead(PrimitiveMulticastRequest request, String childKey) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		return this.reader.syncRead(childKey, request);
	}
	
	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> unicastRead(PrimitiveMulticastRequest request, String childKey, Class<T> c) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		return Tools.filter(this.reader.syncRead(childKey, request), c);
	}
	
	public List<PrimitiveMulticastResponse> broadcastRead(PrimitiveMulticastRequest request, Set<String> childrenKeys) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		return this.reader.syncReadWithinNChildren(childrenKeys, request);
	}

	public List<PrimitiveMulticastResponse> asyncBroadcastRead(PrimitiveMulticastRequest request, Set<String> childrenKeys)
	{
		return this.reader.asyncRead(childrenKeys, request);
	}

	/*
	 * The method waits for a single response from one particular partition. 09/08/2020, Bing Li
	 */
	public PrimitiveMulticastResponse broadcastReadByPartition(PrimitiveMulticastRequest request, Set<String> childrenKeys) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		return this.reader.syncReadUponPartition(childrenKeys, request);
	}
	
	/*
	 * The method waits for a single response from one particular partition. 09/08/2020, Bing Li
	 */
	public PrimitiveMulticastResponse asyncBroadcastReadByPartition(PrimitiveMulticastRequest request, Set<String> childrenKeys)
	{
		return this.reader.asyncReadUponPartition(childrenKeys, request);
	}
	
	/*
	 * Broadcasting a request synchronously within randomly selected N children. 09/11/2020, Bing Li
	 */
	public List<PrimitiveMulticastResponse> broadcastReadWithinNChildren(PrimitiveMulticastRequest request, int n) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		return this.reader.syncReadWithinNChildren(n, request);
	}
	
	/*
	 * Broadcasting a request asynchronously within randomly selected N children. 09/11/2020, Bing Li
	 */
	public List<PrimitiveMulticastResponse> asyncBroadcastReadWithinNChildren(PrimitiveMulticastRequest request, int n)
	{
		return this.reader.asyncReadWithinNChildren(n, request);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> broadcastRead(PrimitiveMulticastRequest request, Set<String> childrenKeys, Class<T> c) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		return Tools.filter(this.reader.syncReadWithinNChildren(childrenKeys, request), c);
	}

	/*
	 * Anycast requests.  09/03/2018, Bing Li
	 * 
	 * n: the least count of responses
	 * 
	 */
	public List<PrimitiveMulticastResponse> anycastRead(PrimitiveMulticastRequest request, int n) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		return this.reader.syncReadWithNResponses(request, n);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> anycastRead(PrimitiveMulticastRequest request, int n, Class<T> c) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		return Tools.filter(this.reader.syncReadWithNResponses(request, n), c);
	}

	/*
	 * The method has not been tested although it should be correct. 09//15/2018, Bing Li
	 */
	public List<PrimitiveMulticastResponse> asyncAnycastRead(PrimitiveMulticastRequest request, int n)
	{
		return this.reader.asyncReadWithNResponses(request, n);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> asyncAnycastRead(PrimitiveMulticastRequest request, int n, Class<T> c)
	{
		return Tools.filter(this.reader.asyncReadWithNResponses(request, n), c);
	}

	public List<PrimitiveMulticastResponse> anycastRead(PrimitiveMulticastRequest request, Set<String> childrenKeys) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		return this.reader.syncReadWithinNChildren(childrenKeys, request);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> anycastRead(PrimitiveMulticastRequest request, Set<String> childrenKeys, Class<T> c) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		return Tools.filter(this.reader.syncReadWithinNChildren(childrenKeys, request), c);
	}
	
	/*
	 * Unicast requests. 09/03/2018, Bing Li
	 */
	public List<PrimitiveMulticastResponse> unicastRead(PrimitiveMulticastRequest request) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		return this.reader.syncRandomRead(request);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> unicastRead(PrimitiveMulticastRequest request, Class<T> c) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		return Tools.filter(this.reader.syncRandomRead(request), c);
	}
	
	/*
	 * Unicast requests to the nearest child. 09/03/2018, Bing Li
	 */
	public List<PrimitiveMulticastResponse> unicastNearestRead(String clientKey, PrimitiveMulticastRequest request) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		return this.reader.syncNearestRead(clientKey, request);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> unicastNearestRead(String clientKey, PrimitiveMulticastRequest request, Class<T> c) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		return Tools.filter(this.reader.syncNearestRead(clientKey, request), c);
	}

	/*
	 * The method has not been tested although it should be correct. 09/15/2018, Bing Li
	 */
	public List<PrimitiveMulticastResponse> asyncUnicastRead(PrimitiveMulticastRequest request)
	{
		return this.reader.asyncRandomRead(request);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> asyncUnicastRead(PrimitiveMulticastRequest request, Class<T> c)
	{
		return Tools.filter(this.reader.asyncRandomRead(request), c);
	}

	/*
	 * Unicast requests to the nearest child asynchronously. 09/03/2018, Bing Li
	 */
	public List<PrimitiveMulticastResponse> asyncUnicastNearestRead(String clientKey, PrimitiveMulticastRequest request)
	{
		return this.reader.asyncNearestRead(clientKey, request);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> asyncUnicastNearestRead(String clientKey, PrimitiveMulticastRequest request, Class<T> c)
	{
		return Tools.filter(this.reader.asyncNearestRead(clientKey, request), c);
	}
}
