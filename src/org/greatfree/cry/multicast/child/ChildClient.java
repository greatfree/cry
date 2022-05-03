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
import org.greatfree.cry.server.CryPeer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.server.ServerDispatcher;
import org.greatfree.util.Builder;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
// public final class ChildClient<Dispatcher extends CryptoCSDispatcher>
public final class ChildClient<Dispatcher extends ServerDispatcher<ServerMessage>>
{
	private ChildEventer<Dispatcher> eventer;
	private ChildReader<Dispatcher> reader;
	private ChildSyncMulticastor<Dispatcher> multicastor;

	/*
	public ChildClient(Peer peer, String localIPKey, int treeBranchCount, ThreadPool pool, int cryptoOption)
	{
		this.multicastor = new ChildSyncMulticastor(peer, localIPKey, treeBranchCount, cryptoOption);
		this.eventer = new ChildEventer(this.multicastor, pool);
		this.reader = new ChildReader(this.multicastor, pool);
	}
	*/

	public ChildClient(ChildClientBuilder<Dispatcher> builder)
	{
		this.multicastor = new ChildSyncMulticastor<Dispatcher>(builder.getEventer(), builder.getLocalIPKey(), builder.getTreeBranchCount(), builder.getCryptoOption());
		this.eventer = new ChildEventer<Dispatcher>(this.multicastor, builder.getPool());
		this.reader = new ChildReader<Dispatcher>(this.multicastor, builder.getPool());
	}

//	public static class ChildClientBuilder<Dispatcher extends CryptoCSDispatcher> implements Builder<ChildClient<Dispatcher>>
	public static class ChildClientBuilder<Dispatcher extends ServerDispatcher<ServerMessage>> implements Builder<ChildClient<Dispatcher>>
	{
//		private Peer eventer;
		private CryPeer<Dispatcher> eventer;
		private int treeBranchCount;
		private String localIPKey;
		private ThreadPool pool;
		private int cryptoOption;

		public ChildClientBuilder()
		{
		}
		
//		public ChildClientBuilder eventer(Peer eventer)
		public ChildClientBuilder<Dispatcher> eventer(CryPeer<Dispatcher> eventer)
		{
			this.eventer = eventer;
			return this;
		}
		
		public ChildClientBuilder<Dispatcher> treeBranchCount(int treeBranchCount)
		{
			this.treeBranchCount = treeBranchCount;
			return this;
		}
		
		public ChildClientBuilder<Dispatcher> localIPKey(String localIPKey)
		{
			this.localIPKey = localIPKey;
			return this;
		}
		
		public ChildClientBuilder<Dispatcher> pool(ThreadPool pool)
		{
			this.pool = pool;
			return this;
		}
		
		public ChildClientBuilder<Dispatcher> cryptoOption(int cryptoOption)
		{
			this.cryptoOption = cryptoOption;
			return this;
		}

		@Override
		public ChildClient<Dispatcher> build() throws IOException
		{
			return new ChildClient<Dispatcher>(this);
		}
		
//		public Peer getEventer()
		public CryPeer<Dispatcher> getEventer()
		{
			return this.eventer;
		}
		
		public int getTreeBranchCount()
		{
			return this.treeBranchCount;
		}
		
		public String getLocalIPKey()
		{
			return this.localIPKey;
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
		this.eventer.dispose();
		this.reader.dispose();
		this.multicastor.dispose();
	}
	
	public void setCryptoOption(int co)
	{
		this.multicastor.setCryptoOption(co);
	}

	public void notify(MulticastNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, ClassNotFoundException, RemoteReadException
	{
		this.eventer.syncNotify(notification);
	}
	
	public void asynNotify(MulticastNotification notification)
	{
		this.eventer.asyncNotify(notification);
	}
	
	public void read(MulticastRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, ClassNotFoundException, RemoteReadException
	{
		this.reader.syncRead(request);
	}
	
	public void asyncRead(MulticastRequest request)
	{
		this.reader.asyncRead(request);
	}
}
