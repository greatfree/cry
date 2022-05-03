package org.greatfree.cry.framework.bitcoin.peer;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.concurrency.Scheduler;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.JoinChainFailedException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.bitcoin.CoinBlock;
import org.greatfree.cry.framework.bitcoin.Coin;
import org.greatfree.cry.framework.bitcoin.CoinConfig;
import org.greatfree.cry.framework.bitcoin.NeighborPeers;
import org.greatfree.cry.framework.bitcoin.Transaction;
import org.greatfree.cry.framework.bitcoin.message.ChainLengthRequest;
import org.greatfree.cry.framework.bitcoin.message.ChainLengthResponse;
import org.greatfree.cry.framework.bitcoin.message.CoinGeneratedNotification;
import org.greatfree.cry.framework.bitcoin.message.JoinChainNotification;
import org.greatfree.cry.framework.bitcoin.message.JoinChainSucceedinglyRequest;
import org.greatfree.cry.framework.bitcoin.message.JoinChainSucceedinglyResponse;
import org.greatfree.cry.framework.bitcoin.message.JoinCoinSystemRequest;
import org.greatfree.cry.framework.bitcoin.message.JoinCoinSystemResponse;
import org.greatfree.cry.framework.bitcoin.message.JoinStateNotification;
import org.greatfree.cry.framework.bitcoin.message.LeaveCoinSystemRequest;
import org.greatfree.cry.framework.bitcoin.message.LeaveCoinSystemResponse;
import org.greatfree.cry.framework.bitcoin.message.LeaveNotification;
import org.greatfree.cry.framework.bitcoin.message.LinkPrecedingNotification;
import org.greatfree.cry.framework.bitcoin.message.PropagateSucceedinglyRequest;
import org.greatfree.cry.framework.bitcoin.message.PropagateSucceedinglyResponse;
import org.greatfree.cry.framework.bitcoin.message.FinalizeTransactionMiningNotification;
import org.greatfree.cry.framework.bitcoin.message.RetainTransactionNotification;
import org.greatfree.cry.framework.blockchain.message.SucceedingValidateResponse;
import org.greatfree.cry.framework.blockchain.message.ValidateChainResponse;
import org.greatfree.cry.framework.tncs.CryptoConfig;
import org.greatfree.cry.messege.OwnerInfo;
import org.greatfree.cry.server.Peer;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author Bing Li
 * 
 * 02/14/2022
 *
 */
final class CoinNode
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.bitcoin.peer");
	
	private Peer peer;
	private Map<String, CoinBlock> blocks;
//	private AtomicBoolean isHead;
	
	/*
	 * 
	 * Now the topology of the peers in the coin system is a doubly-linked ring. 02/17/2022, Bing Li
	 * 
	 * The head is useful? 02/17/2022, Bing Li
	 * 
	 * A better solution is to organize those peers into a tree. The current version is a ring or a doubly-linked list, which is not good enough. 02/17/2022, Bing Li
	 * 
	 * The head and the neighbors are used to form a linked list to perform multicasting of transactions among peers. That is the unique usage of the attribute. It has nothing to do with the block chain. 02/16/2022, Bing Li 
	 */
	private String head;
	private NeighborPeers neighbors;
	
	private CoinNode()
	{
	}
	
	private static CoinNode instance = new CoinNode();
	
	public static CoinNode COIN()
	{
		if (instance == null)
		{
			instance = new CoinNode();
			return instance;
		}
		else
  		{
			return instance;
		}
	}

	public boolean stop() throws InterruptedException
	{
		this.blocks.clear();
		this.blocks = null;
		TransactionStorage.COIN().dispose();
		TransactionPool.COIN().dispose();
		WalletStorage.COIN().dispose();
		TerminateSignal.SIGNAL().notifyAllTermination();
		MiningTaskManager.COIN().stopCoinMiningTask();
		MiningTaskManager.COIN().stopTransactionTask();
		Scheduler.GREATFREE().shutdown(RegistryConfig.SCHEDULER_SHUTDOWN_TIMEOUT);
		boolean isDone = false;
		try
		{
			isDone = this.leaveCoinSystem();
		}
		catch (ClassNotFoundException | RemoteReadException | IOException | DistributedNodeFailedException | InterruptedException e)
		{
			isDone = false;
		}
		try
		{
			this.peer.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
		}
		catch (ClassNotFoundException | InterruptedException | RemoteReadException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | CryptographyMismatchException | DistributedNodeFailedException | PublicKeyUnavailableException | SymmetricKeyUnavailableException e)
		{
			isDone = false;
		}
		return isDone;
	}

	public void start(String nodeName, int nodePort, String registryIP, int registryPort) throws IOException, ClassNotFoundException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, DistributedNodeFailedException, CryptographyMismatchException, InterruptedException
	{
		this.blocks = new ConcurrentHashMap<String, CoinBlock>();
		TransactionStorage.COIN().init();
//		TransactionPool.COIN().init(CoinConfig.TRANSACTION_PROCESSING_TIMESPAN);
		TransactionPool.COIN().init();
		WalletStorage.COIN().init();
//		this.isHead = new AtomicBoolean(false);
		this.peer = new Peer.PeerBuilder()
				.peerName(nodeName)
				.port(nodePort)
				.registryServerIP(registryIP)
				.registryServerPort(registryPort)
				.task(new CoinNodeTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(CryptoConfig.RSA)
				.asymCipherKeyLength(CryptoConfig.RSA_LENGTH)
				.symCipherAlgorithm(CryptoConfig.AES)
				.symCipherSpec(CryptoConfig.AES_SPEC)
				.symCipherKeyLength(CryptoConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(CryptoConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(CryptoConfig.SHA_WITH_RSA)
				.signature(nodeName + CryptoConfig.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.build();

		this.peer.start();
		this.peer.inviteAsymPartner(CoinConfig.COIN_COORDINATOR);
//		this.joinCoinSystem();
		/*
		 * The joining-system operation is performed without any cryptography algorithms to raise the performance. 02/16/2022, Bing Li 
		 */
		JoinCoinSystemResponse response = (JoinCoinSystemResponse)this.peer.read(CoinConfig.COIN_COORDINATOR, new JoinCoinSystemRequest(this.peer.getPeerName()));
		this.head = response.getHead();
		log.info("head = " + this.head);
		this.neighbors = response.getNeighbors();
 		
		log.info(this.peer.getPeerName() + "'s current preceding is " + this.neighbors.getPrecedingPN());
		log.info(this.peer.getPeerName() + "'s current succeeding is " + this.neighbors.getSucceedingPN());
		
//		if (this.neighbors.getPrecedingPN() != CoinConfig.NO_PEER)
		if (!this.neighbors.getPrecedingPN().equals(this.neighbors.getHostPN()))
		{
//			this.linkPreceding(this.neighbors.getPrecedingPN());
			log.info(this.peer.getPeerName() + " is linking " + this.neighbors.getPrecedingPN());
			this.peer.syncNotify(this.neighbors.getPrecedingPN(), new LinkPrecedingNotification(this.peer.getPeerName()));
		}

		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
		if (this.isHead())
		{
			ChainHead.COIN().init();
			/*
			 * Only the networking head is able to start the block chain validation to keep consistent. 02/19/2022, Bing Li
			 */
			MiningTaskManager.COIN().setTransactionTask(Scheduler.GREATFREE().submit(new TransactionsMiningTask(), CoinConfig.TRANSACTION_PROCESSING_DELAY, CoinConfig.TRANSACTION_PROCESSING_PERIOD));
		}
	}
	
	private boolean isHead()
	{
		return this.head.equals(this.peer.getPeerName());
	}
	
	/*
	 * The joining-system operation is performed without any cryptography algorithms to raise the performance. 02/16/2022, Bing Li 
	 */
	/*
	private void joinCoinSystem() throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, InterruptedException
	{
		JoinCoinSystemResponse response = (JoinCoinSystemResponse)this.peer.read(CoinConfig.COIN_COORDINATOR, new JoinCoinSystemRequest(this.peer.getPeerName()));
//		this.head = response.getHead();
		this.neighbors = response.getNeighbors();
//		if (this.neighbors.getPrecedingPN() != CoinConfig.NO_PEER)
		if (!this.neighbors.getPrecedingPN().equals(this.neighbors.getHostPN()))
		{
//			this.linkPreceding(this.neighbors.getPrecedingPN());
			this.peer.syncNotify(this.neighbors.getPrecedingPN(), new LinkPrecedingNotification(this.peer.getPeerName()));
		}
	}
	*/
	
	public boolean leaveCoinSystem() throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, InterruptedException
	{
		/*
		 * The leaving-system operation is performed without any cryptography algorithms to raise the performance. 02/16/2022, Bing Li 
		 */
		if (!this.neighbors.getPrecedingPN().equals(this.neighbors.getHostPN()) || !this.neighbors.getSucceedingPN().equals(this.neighbors.getHostPN()))
		{
			/*
			 * If the branch is reached, it denotes that the coin system contains two peers at least. 02/17/2022, Bing Li
			 */
			if (!this.neighbors.getSucceedingPN().equals(this.neighbors.getPrecedingPN()))
			{
				/*
				 * If the branch is reached, it denotes the coin system contains three peers at least. That is the common case. 02/17/2022, Bing Li
				 */
				this.peer.syncNotify(this.neighbors.getPrecedingPN(), new LeaveNotification(this.neighbors.getSucceedingPN()));
			}
			else
			{
				/*
				 * If the branch is reached, it denotes that the coin system contains two peers only before the current peer leaves. If it leaves, only one peer is left in the system. Then, the only left peer's preceding and succeeding peers become itself. 02/17/2022, Bing Li
				 */
				this.peer.syncNotify(this.neighbors.getPrecedingPN(), new LeaveNotification(this.neighbors.getPrecedingPN()));
			}
		}
		else
		{
			/*
			 * If the branch is reached, it denotes that only one peer exists in the coin system before it leaves. After it leaves, the coin system crashes. 02/17/2022, Bing Li
			 */
			/*
			if (!this.neighbors.getSucceedingPN().equals(CoinConfig.NO_PEER))
			{
				this.peer.syncNotify(this.neighbors.getSucceedingPN(), new NoPrecedingNotification());
			}
			*/
		}
		return ((LeaveCoinSystemResponse)this.peer.read(CoinConfig.COIN_COORDINATOR, new LeaveCoinSystemRequest(this.peer.getPeerName()))).isDone();
	}
	
	/*
	 * The neighboring construction between peers is performed without any cryptography algorithms to raise the performance. 02/16/2022, Bing Li 
	 */
	/*
	public void linkPreceding(String precedingPN) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.peer.syncNotify(precedingPN, new LinkPrecedingNotification(this.peer.getPeerName()));
	}
	*/

	public void setSucceedingPeer(String succeedingPN) throws InvalidKeyException, NoSuchAlgorithmException, ClassNotFoundException, SignatureException, RemoteReadException, IOException, DistributedNodeFailedException, CryptographyMismatchException
	{
		log.info(this.peer.getPeerName() + " gets its succeeding peer, " + succeedingPN);
		this.neighbors.setSucceedingPN(succeedingPN);
		/*
		 * If the current node is the head, which joins the system as the first one, its preceding peer is the last one, i.e., the succeedingPN, which joins the system just now. 02/17/2022, Bing Li
		 */
		if (this.neighbors.getPrecedingPN().equals(this.neighbors.getHostPN()))
		{
			log.info(this.peer.getPeerName() + " gets its preceding peer, " + succeedingPN);
			this.neighbors.setPrecedingPN(succeedingPN);
		}
		this.peer.inviteAsymPartner(succeedingPN);
	}

	/*
	public void setAsHead()
	{
		this.neighbors.setPrecedingPN(CoinConfig.NO_PEER);
	}
	*/

	/*
	 * In the current version, the method is invoked by the intermediate peer and the end peer of the chain once if the peers receive the request from the head of the networking. 02/25/2022, Bing Li
	 */
//	public void joinChain(String sessionKey, Date timeStamp) throws IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException
	/*
	public boolean joinChain(String sessionKey) throws IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException
	{
		return Joint.joinChain(this.peer, this.blocks, sessionKey);
	}
	*/

	/*
	 * Only the head of the networking invokes the method. 02/25/2022, Bing Li
	 */
	/*
	public void joinChain(String sessionKey, List<Transaction> trans) throws IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException
	{
		Joint.joinChain(this.peer, this.blocks, sessionKey, trans);
	}
	*/

	/*
	 * After joining, it is necessary to notify the coordinator, which collects the notifications to count whether all the peers join the chain such that it is possible to start up the chain verification. 02/16/2022, Bing Li
	 */
	/*
	public void notifyJoinDone(String sessionKey) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		log.info("Sending JoinDoneNotification ...");
		this.peer.syncNotifyBySignature(CoinConfig.COIN_COORDINATOR, new JoinDoneNotification(sessionKey, this.peer.getPeerName()));
		log.info("JoinDoneNotification is sent ...");
	}
	*/

	/*
	public void setHead(boolean isHead)
	{
		this.isHead.set(isHead);
	}
	*/
	
	/*
	 * The session key of one block chain is created upon the head peer name and the transaction time stamp. 02/15/2022, Bing Li
	 */
	/*
	public String createChainSession(Date timeStamp) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
//		return ((CreateBlockChainResponse)this.peer.read(CoinConfig.COIN_COORDINATOR, new CreateBlockChainRequest(this.peer.getPeerName()))).getSessionKey();
		return Tools.getHash(String.valueOf(timeStamp.getTime()));
	}
	*/
	
	public void notifyCoinGenerated(String owner, Coin coin) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException
	{
		try
		{
			this.peer.syncNotifyBySignature(owner, new CoinGeneratedNotification(coin));
		}
		catch (PublicKeyUnavailableException e)
		{
			e.printStackTrace();
		}
	}
	
	public Collection<OwnerInfo> getOwners()
	{
		return this.peer.getOwners();
	}
	
	public int getChainLength() throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		return ((ChainLengthResponse)this.peer.read(CoinConfig.COIN_COORDINATOR, new ChainLengthRequest())).getChainLength();
	}
	
	/*
	 * The method is not used after rapid joining is designed. 02/27/2022, Bing Li
	 * 
	 * The recursive traversal is not good since it is performed in a totally synchronous way. It is very slow, especially when the length of the chain is long. 02/26/2022, Bing Li
	 */
	public boolean joinChainTogether(String sessionKey) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, IOException, RemoteReadException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, PublicKeyUnavailableException
	{
		if (!this.neighbors.getSucceedingPN().equals(this.neighbors.getHostPN()))
		{
			return this.joinChainSucceedingly(sessionKey);
		}
		return true;
	}

	/*
	 * The method is not used after rapid joining is designed. 02/27/2022, Bing Li
	 */
	public boolean joinChainSucceedingly(String sessionKey) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, IOException, RemoteReadException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, PublicKeyUnavailableException
	{
//		this.joinChain(sessionKey);
		Joint.joinChain(this.peer, this.blocks, sessionKey);
		if (!this.neighbors.getSucceedingPN().equals(this.head))
		{
			return ((JoinChainSucceedinglyResponse)this.peer.readAsymmetrically(this.neighbors.getSucceedingPN(), new JoinChainSucceedinglyRequest(sessionKey))).isDone();
		}
		else
		{
			return true;
		}
	}

	/*
	 * The validation is performed in a synchronous way. That is slow. Only when the chain is short, it is reasonable. 02/26/2022, Bing Li
	 */
	public ValidateChainResponse validate(String sessionKey) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException
	{
		return Validator.validate(this.peer, this.blocks.get(sessionKey), sessionKey);
	}

	public SucceedingValidateResponse validateSucceedingly(String sessionKey, String precedingFingerPrint) throws IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException
	{
		return Validator.validateSucceedingly(this.peer, this.blocks.get(sessionKey), sessionKey, precedingFingerPrint);
	}

	/*
	 * Only if the method is located at the networking head, it can be invoked. Otherwise, it is not invoked. 02/26/2022, Bing Li
	 * 
	 * The validation is performed in an asynchronous way. That is fast. When the chain is long, it is preferred to invoke the method. 02/26/2022, Bing Li
	 */
//	public void rapidValidate(String sessionKey, int chainLength) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, InterruptedException
	/*
	public void startRapidValidate(String sessionKey) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, InterruptedException
	{
//		Validator.rapidValidate(this.peer, this.blocks.get(sessionKey), sessionKey, chainLength);
		Validator.rapidValidate(this.peer, this.blocks.get(sessionKey), sessionKey);
	}
	*/
	
	/*
	 * The method is invoked only if the node is the networking head. 02/26/2022, Bing Li
	 */
	public void collectValidationResult(String sessionKey, boolean isValid) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		Validator.collectValidationResult(this.peer, this.blocks.get(sessionKey), sessionKey, isValid);
	}

	/*
	 * If the node is not the head, when validating, the method is invoked. 02/26/2022, Bing Li
	 */
	public void rapidValidate(String sessionKey) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, InterruptedException, CryptographyMismatchException, PublicKeyUnavailableException
	{
		Validator.rapidValidate(this.peer, this.blocks.get(sessionKey)	, this.head, sessionKey);
	}

	/*
	 * 
	 * Fortunately, the transactions only need to be retained in the pool, which does not take much time. So the recursive traversal is acceptable. 02/27/2022, Bing Li
	 *
	 * The recursive traversal is not good since it is performed in a totally synchronous way. It is very slow, especially when the length of the chain is long. 02/26/2022, Bing Li
	 * 
	 * The recursive traversal approach is recommended to be employed. The notification is not preferred. With the recursive traversal, the head is able to be aware that the task is completed and then it can perform additional ones. 02/25/2022, Bing Li
	 */
	public boolean propagateTransactions(List<Transaction> trans) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, ShortBufferException
	{
		System.out.println("============= One new round transactions mining is Started! =============");
		log.info("this.neighbors.getSucceedingPN() = " + this.neighbors.getSucceedingPN());
		log.info("this.neighbors.getHostPN() = " + this.neighbors.getHostPN());
//		List<Transaction> trans = TransactionPool.COIN().loadTransactions();
		if (!this.neighbors.getSucceedingPN().equals(this.neighbors.getHostPN()))
		{
			return this.propagateSucceedingly(trans);
		}
		return true;
	}
	
	public boolean propagateSucceedingly(List<Transaction> trans) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
//		System.out.println("============= propagateSucceedingly() is executed! =============");
		/*
		 * The networking head holds all the transactions such that it is unnecessary for it to enqueue those transactions into the pool. Only the peers other than the head need to keep the transactions into the pool. 02/28/2022, Bing Li
		 * 
		 * The condition is missed in the previous version. To add it, it took me one entire day! 02/28/2022, Bing Li
		 */
		if (!this.isHead())
		{
			TransactionPool.COIN().enqueue(trans);
		}
		if (!this.neighbors.getSucceedingPN().equals(this.head))
		{
//			System.out.println("============= propagateSucceedingly() is performed further! =============");
			return ((PropagateSucceedinglyResponse)this.peer.readAsymmetrically(this.neighbors.getSucceedingPN(), new PropagateSucceedinglyRequest(trans))).isDone();
		}
		else
		{
			/*
			 * If the condition is fulfilled, it indicates that the current peer is the end of the chain. 02/25/2022, Bing Li
			 */
			return true;
		}
	}

	/*
	 * The method is invoked by the networking head. 02/27/2022, Bing Li
	 */
	public void rapidJoinChain(String sessionKey, int chainLength, List<Transaction> trans) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, JoinChainFailedException
	{
		ChainHead.COIN().setChainLength(sessionKey, chainLength);
		if (!this.neighbors.getSucceedingPN().equals(this.neighbors.getHostPN()))
		{
			this.peer.asyncNotifyAsymmetrically(this.neighbors.getSucceedingPN(), new JoinChainNotification(sessionKey));
		}
		try
		{
			Joint.joinChain(this.peer, this.blocks, sessionKey, trans);
			log.info("Head joining done ...");
			ChainHead.COIN().incrementJoin(sessionKey);
			log.info("Head is counted ...");
			if (ChainHead.COIN().isJoinDone(sessionKey))
			{
				log.info("Starting to validate rapidly ...");
				Validator.rapidValidate(this.peer, this.blocks.get(sessionKey), sessionKey);
			}
			else
			{
				log.info("Chain peers do not join totally ...");
			}
		}
		catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
				| ShortBufferException | SignatureException | IOException | RemoteReadException | InterruptedException
				| DistributedNodeFailedException | CryptographyMismatchException e)
		{
			throw new JoinChainFailedException(this.peer.getPeerName(), sessionKey);
		}
	}
	
	/*
	 * The method is invoked by the peers other than the networking head. 02/27/2022, Bing Li
	 */
	public void rapidJoinChain(String sessionKey) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		if (!this.neighbors.getSucceedingPN().equals(this.head))
		{
			this.peer.asyncNotifyAsymmetrically(this.neighbors.getSucceedingPN(), new JoinChainNotification(sessionKey));
		}
		try
		{
			Joint.joinChain(this.peer, this.blocks, sessionKey);
			this.peer.asyncNotifyAsymmetrically(this.head, new JoinStateNotification(this.peer.getPeerName(), sessionKey, true));
		}
		catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
				| ShortBufferException | SignatureException | IOException | RemoteReadException | InterruptedException
				| DistributedNodeFailedException | CryptographyMismatchException e)
		{
			this.peer.asyncNotifyAsymmetrically(this.head, new JoinStateNotification(this.peer.getPeerName(), sessionKey, false));
		}
	}
	
	public void collectJoinState(String peerName, String sessionKey, boolean isDone) throws JoinChainFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, InterruptedException
	{
		if (isDone)
		{
			ChainHead.COIN().incrementJoin(sessionKey);
			if (ChainHead.COIN().isJoinDone(sessionKey))
			{
				Validator.rapidValidate(this.peer, this.blocks.get(sessionKey), sessionKey);
			}
		}
		else
		{
			throw new JoinChainFailedException(peerName, sessionKey);
		}
	}

	/*
	 * 
	 * The method might cause a dead loop around the chain. 02/28/2022, Bing Li
	 * 
	 * The networking-head based propagation replaces the forwarding solution. 02/28/2022, Bing Li
	 * 
	 * Since the forwarding/propagation is started from the head of the networking, the propagation is terminated if the peer is the head. The peers other than the head keep forwarding. 02/25/2022, Bing Li 
	 */
	/*
	public void forwardTransaction(Transaction trans) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		log.info("Transaction is being attempted to forward ...");
		log.info(trans.toString());

		if (!this.isHead())
		{
			if (!this.neighbors.getSucceedingPN().equals(this.neighbors.getHostPN()))
			{
				this.peer.syncNotifyBySignature(this.neighbors.getSucceedingPN(), new RetainTransactionNotification(trans));
				log.info("Transaction is forwarded ...");
			}
			else
			{
				log.info("Transaction is NOT forwarded because only one coin node exists ...");
			}
		}
	}
	*/

	/*
	 * 
	 * The method is called at the peers other than the networking head. 02/28/2022, Bing Li
	 * 
	 * The networking-head based propagation replaces the forwarding solution. 02/28/2022, Bing Li
	 * 
	 * The head of the networking is responsible for managing the propagation. Before validation, all the transactions are retained in the pool of the head. When to start the validation, the transactions are propagated to all the peers of the chain. After the propagation is ensured to be done, the validation can be performed. 02/25/2022, Bing Li
	 */
	public void forwardToHeadIfApplicable(Transaction trans) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		if (!this.isHead())
		{
			/*
			 * I need to spend time later on the options of cryptography algorithms. 02/28/2022, Bing Li
			 */
//			this.peer.syncNotifyBySignature(this.head, new RetainTransactionNotification(trans));
			this.peer.syncNotifyAsymmetrically(this.head, new RetainTransactionNotification(trans));
		}
		else
		{
			if (!TransactionPool.COIN().isExisted(trans))
			{
				TransactionPool.COIN().enqueue(trans);
			}
		}
	}

	/*
	 * The method is called at the networking head. Transactions are retained at the transaction pool temporarily before propagation is started. 02/28/2022, Bing Li
	 */
	public void retainTransaction(Transaction trans)
	{
		if (!TransactionPool.COIN().isExisted(trans))
		{
			TransactionPool.COIN().enqueue(trans);
		}
	}
	
	public void finalizeTransactionMining(String sessionKey) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		if (!this.neighbors.getSucceedingPN().equals(this.head))
		{
			this.peer.asyncNotifyAsymmetrically(this.neighbors.getSucceedingPN(), new FinalizeTransactionMiningNotification(sessionKey));
		}
		if (this.isHead())
		{
			ChainHead.COIN().remove(sessionKey);
		}
		CoinBlock block = this.blocks.get(sessionKey);
		TransactionStorage.COIN().addTransactions(block.getTransactions());
		this.blocks.remove(sessionKey);
	}

	/*
	public void putIntoPool(Transaction trans) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		this.forwardTransaction(trans);
		TransactionPool.COIN().enqueue(trans);
	}
	*/

	/*
	public void processTransactions() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, IOException, RemoteReadException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, PublicKeyUnavailableException
	{
		String sessionKey = Tools.generateUniqueKey();
		this.joinChain(sessionKey);
		this.notifyJoinDone(sessionKey);
	}
	*/

	/*
	 * Only if the method is located at the networking head, it can be invoked. Otherwise, it is not invoked. 02/26/2022, Bing Li
	 */
	/*
	public void notifyValidationStates(String sessionKey, boolean isValid) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		Validator.notifyValidationStates(this.peer, this.blocks.get(sessionKey), sessionKey, isValid);
	}
	*/
}
