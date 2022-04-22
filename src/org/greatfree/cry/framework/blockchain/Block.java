package org.greatfree.cry.framework.blockchain;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

import org.greatfree.cry.framework.bitcoin.Input;
import org.greatfree.cry.framework.bitcoin.Output;
import org.greatfree.cry.framework.bitcoin.Transaction;

import com.google.gson.GsonBuilder;

/**
 * 
 * 02/12/2022, Bing Li
 * 
 * The block is used to save new transactions and verify them.
 * 
 * After verification, the current blockchain formed by the blocks on the nodes coexists with others.
 * 
 * It implies that there are multiple blockchains in the P2P network.
 * 
 * Those chains can be persisted after verification, i.e., they are closed in memory.
 * 
 * @author libing
 * 
 * 01/26/2022, Bing Li
 *
 */
public class Block
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.blockchain");

	private int sequenceNO;
	private final String localPeerName;
	private String fingerPrint;
	private String headPeerName;
	private String precedingFingerPrint;
//	private IPAddress succeedingIP;
	private String succeedingPeerName;
//	private Map<String, List<Transaction>> transactions;
	private List<Transaction> transactions;
	private long timeStamp;
	private int nonce;
	// The below attribute is suggested by some solutions. 02/12/2022, Bing Li
//	private long maxSize;
	private ReentrantReadWriteLock lock;
	
	/*
	public Block(String precedingFingerPrint) throws IOException
	{
		this.headPeerName = null;
		this.transactions = new ArrayList<Transaction>();
		this.precedingFingerPrint = precedingFingerPrint;
		this.timeStamp = Calendar.getInstance().getTimeInMillis();
		this.fingerPrint = BlockCryptor.calculateFingerPrint(this.precedingFingerPrint, this.timeStamp, this.nonce, this.transactions);
		this.succeedingPeerName = null;
	}
	*/
	
	public Block(String localPeerName) throws IOException
	{
		this.localPeerName = localPeerName;
		this.headPeerName = null;
//		this.transactions = new ConcurrentHashMap<String, List<Transaction>>();
		this.transactions = new CopyOnWriteArrayList<Transaction>();
		
		this.precedingFingerPrint = BlockConfig.NO_PRECEDING_FINGER_PRINT;
		this.timeStamp = Calendar.getInstance().getTimeInMillis();
//		this.fingerPrint = BlockCryptor.calculateFingerPrint(this.precedingFingerPrint, this.timeStamp, this.nonce, this.transactions);
		this.fingerPrint = BlockConfig.NO_FINGER_PRINT;
		this.succeedingPeerName = BlockConfig.NO_SUCCEEDING_FINGER_PRINT;
		this.lock = new ReentrantReadWriteLock();
	}
	
	public BlockValue getValue()
	{
		return new BlockValue.BlockValueBuilder()
				.sequenceNO(this.sequenceNO)
				.localPeerName(this.localPeerName)
				.fingerPrint(this.fingerPrint)
				.headPeerName(this.headPeerName)
				.precedingFingerPrint(this.precedingFingerPrint)
				.succeedingPeerName(this.succeedingPeerName)
				.timeStamp(this.timeStamp)
				.nonce(this.nonce)
				.build();
	}
	
	public void close()
	{
		/*
		 * After verification, transactions must be put into TransactionStorage. 02/14/2022, Bing Li
		 */
		this.transactions.clear();
		this.transactions = null;
	}

	/*
	public void calculateFingerPrint(String precedingFingerPrint) throws IOException
	{
		this.lock.writeLock().lock();
		this.fingerPrint = BlockCryptor.calculateFingerPrint(this.precedingFingerPrint, this.timeStamp, this.nonce, this.transactions);
		this.lock.writeLock().unlock();
	}
	*/

	/*
	public void setSessionKey(String sessionKey)
	{
		this.sessionKey = sessionKey;
	}
	*/
	
	public String getLocalPeerName()
	{
		return this.localPeerName;
	}
	
	public void setSequenceNO(int seqNO)
	{
		this.lock.writeLock().lock();
		this.sequenceNO = seqNO;
		this.lock.writeLock().unlock();
	}
	
	public int getSequenceNO()
	{
		this.lock.readLock().lock();
		try
		{
			return this.sequenceNO;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	public String getFingerPrint()
	{
		this.lock.readLock().lock();
		try
		{
			return this.fingerPrint;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	public void setHead(String headPeerName)
	{
		this.lock.writeLock().lock();
		this.headPeerName = headPeerName;
		this.lock.writeLock().unlock();
	}
	
	public String getHead()
	{
		this.lock.readLock().lock();
		try
		{
			return this.headPeerName;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}

	public String getPrecedingFingerPrint()
	{
		this.lock.readLock().lock();
		try
		{
			return this.precedingFingerPrint;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	public void setPrecedingFingerPrint(String fp)
	{
		this.lock.writeLock().lock();
		this.precedingFingerPrint = fp;
		log.info("precedingFingerPrint is set to " + fp);
		this.lock.writeLock().unlock();
	}
	
	public void setSucceedingPeerName(String spn)
	{
		this.lock.writeLock().lock();
		this.succeedingPeerName = spn;
		this.lock.writeLock().unlock();
	}
	
	public String getSucceedingPeerName()
	{
		this.lock.readLock().lock();
		try
		{
			return this.succeedingPeerName;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	public boolean isPrecedingExisted()
	{
		this.lock.readLock().lock();
		try
		{
			return (this.precedingFingerPrint.equals(BlockConfig.NO_PRECEDING_FINGER_PRINT) ? false: true);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	public boolean isSucceedingExisted()
	{
		this.lock.readLock().lock();
		try
		{
			return (this.succeedingPeerName.equals(BlockConfig.NO_SUCCEEDING_FINGER_PRINT) ? false: true);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}

	public boolean validate(String precedingFingerPrint) throws IOException
	{
		this.lock.readLock().lock();
		try
		{
			if (!this.fingerPrint.equals(BlockCryptor.calculateFingerPrint(this.precedingFingerPrint, this.timeStamp, this.nonce, this.transactions)))
			{
				log.info("The current fingerprint is fake!");
				return false;
			}
			if (!this.precedingFingerPrint.equals(BlockConfig.NO_PRECEDING_FINGER_PRINT))
			{
				if (!this.precedingFingerPrint.equals(precedingFingerPrint))
				{
					log.info("The preceding fingerprint is fake!");
					return false;
				}
			}
			if (!this.fingerPrint.subSequence(0, BlockConfig.BLOCK_FINGER_PRINT_DIFFICULTY).equals(BlockConfig.HASH_TARGET))
			{
				log.info("The current fingerprint does not fulfill the difficulty requirement!");
				return false;
			}
			return true;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}

	public boolean validate(String precedingFingerPrint, Map<String, List<Input>> inputs, Map<String, List<Output>> outputs) throws IOException
	{
		log.info("Starting to valid the block ...");
		this.lock.readLock().lock();
		try
		{
			if (!this.fingerPrint.equals(BlockCryptor.calculateFingerPrint(this.precedingFingerPrint, this.timeStamp, this.nonce, this.transactions, inputs, outputs)))
			{
				log.info("The current fingerprint is fake!");
				return false;
			}

			log.info("The fiingerPrint is validated ...");

			if (!this.precedingFingerPrint.equals(BlockConfig.NO_PRECEDING_FINGER_PRINT))
			{
				if (!this.precedingFingerPrint.equals(precedingFingerPrint))
				{
					log.info("The preceding fingerprint is fake!");
					return false;
				}
			}

			log.info("The precedingFingerPrint is validated ...");

			if (!this.fingerPrint.subSequence(0, BlockConfig.BLOCK_FINGER_PRINT_DIFFICULTY).equals(BlockConfig.HASH_TARGET))
			{
				log.info("The current fingerprint does not fulfill the difficulty requirement!");
				return false;
			}

			log.info("The fingerPrint meets the difficulty requirements ...");
			return true;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	public void mine(int difficulty) throws IOException
	{
		log.info("fingerPrint = " + this.fingerPrint);
		String target = BlockCryptor.getDifficultyString(difficulty);
		this.lock.readLock().lock();
		while (!this.fingerPrint.substring(0, difficulty).equals(target))
		{
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.nonce++;
			this.fingerPrint = BlockCryptor.calculateFingerPrint(this.precedingFingerPrint, this.timeStamp, this.nonce, this.transactions);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		this.lock.readLock().unlock();
	}

	public void mine(int difficulty, Map<String, List<Input>> inputs, Map<String, List<Output>> outputs) throws IOException
	{
		log.info("fingerPrint = " + this.fingerPrint);
		String target = BlockCryptor.getDifficultyString(difficulty);
		this.lock.readLock().lock();
		while (!this.fingerPrint.substring(0, difficulty).equals(target))
		{
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.nonce++;
			this.fingerPrint = BlockCryptor.calculateFingerPrint(this.precedingFingerPrint, this.timeStamp, this.nonce, this.transactions, inputs, outputs);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		this.lock.readLock().unlock();
	}

	public String getBlockView()
	{
		return new GsonBuilder().setPrettyPrinting().create().toJson(this.getValue());
	}
	
	public void addTransaction(Transaction transaction)
	{
		/*
		if (!this.transactions.containsKey(transaction.getFrom()))
		{
			this.transactions.put(transaction.getFrom(), new ArrayList<Transaction>());
		}
		this.transactions.get(transaction.getFrom()).add(transaction);
		*/
		this.transactions.add(transaction);
	}
	
	public List<Transaction> getTransactions()
	{
		return this.transactions;
	}
	
	public boolean isEmpty()
	{
		return this.transactions.size() <= 0;
	}
}
