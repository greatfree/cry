package org.greatfree.cry.framework.bitcoin.peer;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import org.greatfree.cry.framework.bitcoin.CoinBlock;
import org.greatfree.cry.framework.bitcoin.Transaction;

import com.google.common.collect.Sets;

/**
 * 
 * @author Bing Li
 * 
 * 02/14/2022
 *
 */
class TransactionPool
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.bitcoin.peer");

	private Queue<Transaction> transactionQueue;
	private Set<String> transKeys;
	
//	private Date precedingTime;
//	private long timespan;

	private TransactionPool()
	{
	}
	
	private static TransactionPool instance = new TransactionPool();
	
	public static TransactionPool COIN()
	{
		if (instance == null)
		{
			instance = new TransactionPool();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose()
	{
		/*
		 * Transactions retained in the queue need to be persisted. 02/15/2022, Bing Li
		 */
	}
	
//	public void init(long maxTimespan)
	public void init()
	{
		this.transactionQueue = new LinkedBlockingQueue<Transaction>();
//		this.precedingTime = UtilConfig.NO_TIME;
//		this.timespan = maxTimespan;
		this.transKeys = Sets.newHashSet();
	}
	
	public synchronized void clear()
	{
		this.transKeys.clear();
		this.transactionQueue.clear();
	}

	/*
	 * The transactions between the earliest one and the one at the time stamp, precedingTime, are verified by the newly created block chain. So the time stamp is critical and it should be retained. 02/15/2022, Bing Li
	 */
//	public synchronized boolean enqueue(Transaction trans)
//	public synchronized Date enqueue(Transaction trans)
	public synchronized void enqueue(Transaction trans)
	{
		log.info("One transaction is enqueued *******");
		/*
		if (this.transactionQueue.isEmpty())
		{
			this.transactionQueue.add(trans);
			this.precedingTime = trans.getTime();
			log.info("1) It passes " + Time.getTimespanInMilliSecond(trans.getTime(), this.precedingTime) + " ms, but it needs " + this.timespan + " ms to start verifying ...");
		}
		else
		{
			this.transactionQueue.add(trans);
			log.info("2) It passes " + Time.getTimespanInMilliSecond(trans.getTime(), this.precedingTime) + " ms, but it needs " + this.timespan + " ms to start verifying ...");
			if (Time.getTimespanInMilliSecond(trans.getTime(), this.precedingTime) > this.timespan)
			{
				return this.precedingTime;
			}
			this.precedingTime = trans.getTime();
		}
		return UtilConfig.NO_TIME;
		*/
		this.transKeys.add(trans.getKey());
		log.info("Transaction-" + trans.getKey() + " is enqueued!");
		this.transactionQueue.add(trans);
	}
	
	public synchronized void enqueue(List<Transaction> trans)
	{
//		log.info("============= Transactions are enqueued =============");
//		log.info("============= " + trans.size() + " transactions are enqueued into the pool ... =============");
		for (Transaction entry : trans)
		{
			log.info("Transaction-" + entry.getKey() + " is enqueued!");
			this.transKeys.add(entry.getKey());
		}
		this.transactionQueue.addAll(trans);
	}
	
	/*
	public Transaction dequeue()
	{
		return this.transactionQueue.poll();
	}
	*/
	
	public boolean isEmpty()
	{
		return this.transactionQueue.isEmpty();
	}
	
	/*
	 * Since the transactions are transmitted via the network, it is reconstructed during the procedure. Thus, they are different in terms of physical objects although they are the same logically. Thus, the judgment is incorrect. 02/25/2022, Bing Li
	 */
	public synchronized boolean isExisted(Transaction trans)
	{
//		return this.transactionQueue.contains(trans);
		return this.transKeys.contains(trans.getKey());
	}

	/*
	public Date peekTransactionTime()
	{
		Transaction trans = this.transactionQueue.peek();
		if (trans != null)
		{
			return trans.getTime();
		}
		return null;
	}
	*/
	
	/*
	 * Only the head of the networking invokes the method. 02/28/2022, Bing Li
	 */
	public void loadTransactions(CoinBlock block, List<Transaction> trans)
	{
		for (Transaction entry : trans)
		{
			block.addTransaction(entry);
			if (entry.isOutput())
			{
				log.info("trans.getOutputFrom() = " + entry.getOutputFrom());
				block.addOutputs(entry.getOutputFrom(), TransactionStorage.COIN().getOutputs(entry.getOutputFrom()));
				block.addInputs(entry.getOutputFrom(), TransactionStorage.COIN().getInputs(entry.getOutputFrom()));
			}
			else
			{
				log.info("trans.getInputTo() = " + entry.getInputTo());
				block.addOutputs(entry.getInputTo(), TransactionStorage.COIN().getOutputs(entry.getInputTo()));
				block.addInputs(entry.getInputTo(), TransactionStorage.COIN().getInputs(entry.getInputTo()));
			}
		}
	}

	/*
	 * In the current version, the method is invoked by the intermediate peer and the end peer of the chain once if the peers receive the request from the head of the networking. 02/28/2022, Bing Li
	 */
	public void loadTransactions(CoinBlock block)
	{
//		Date transTime;
//		boolean isDone = false;
		Transaction trans;
//		log.info("Transactions are being loaded ...");
//		log.info("transactionQueue.size = " + this.transactionQueue.size());
//		log.info("transactionQueue.isEmpty() = " + this.transactionQueue.isEmpty());
		while (!this.transactionQueue.isEmpty())
		{
//			log.info("One transaction is being dequeued ...");
			trans = this.transactionQueue.poll();
//			log.info("One transaction is dequeued ...");
			block.addTransaction(trans);
			if (trans.isOutput())
			{
				log.info("trans.getOutputFrom() = " + trans.getOutputFrom());
				block.addOutputs(trans.getOutputFrom(), TransactionStorage.COIN().getOutputs(trans.getOutputFrom()));
				block.addInputs(trans.getOutputFrom(), TransactionStorage.COIN().getInputs(trans.getOutputFrom()));
			}
			else
			{
				log.info("trans.getInputTo() = " + trans.getInputTo());
				block.addOutputs(trans.getInputTo(), TransactionStorage.COIN().getOutputs(trans.getInputTo()));
				block.addInputs(trans.getInputTo(), TransactionStorage.COIN().getInputs(trans.getInputTo()));
			}
		}
		log.info("Transactions are loaded ...");
	}

	public List<Transaction> loadTransactions()
	{
		List<Transaction> trans = new ArrayList<Transaction>();
		while (!this.transactionQueue.isEmpty())
		{
			trans.add(this.transactionQueue.poll());
		}
		log.info("Transactions are loaded ...");
		return trans;
	}
}
