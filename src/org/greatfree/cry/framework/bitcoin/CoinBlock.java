package org.greatfree.cry.framework.bitcoin;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.cry.framework.blockchain.Block;

/**
 * 
 * @author Bing Li
 * 
 * 02/15/2022
 *
 */
public final class CoinBlock extends Block
{
//	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.bitcoin");

	private String sessionKey;
	private Map<String, List<Input>> inputs;
	private Map<String, List<Output>> outputs;
//	private Map<String, List<String>> ownerTrans;
	
	public CoinBlock(String sessionKey, String localPeerName) throws IOException
	{
		super(localPeerName);
		this.sessionKey = sessionKey;
		this.inputs = new ConcurrentHashMap<String, List<Input>>();
		this.outputs = new ConcurrentHashMap<String, List<Output>>();
//		this.ownerTrans = new ConcurrentHashMap<String, List<String>>();
	}

	public String getSessionKey()
	{
		return this.sessionKey;
	}
	
//	public synchronized void addInputs(String owner, List<Input> inputs)
	public void addInputs(String owner, List<Input> inputs)
	{
		if (inputs != null)
		{
			this.inputs.put(owner, inputs);
			/*
			if (!this.ownerTrans.containsKey(owner))
			{
				this.ownerTrans.put(owner, new ArrayList<String>());
			}
			for (Input entry : inputs)
			{
				log.info("owner = " + owner + "'s transaction, " + entry.getTransactionKey() + ", added ...");
				this.ownerTrans.get(owner).add(entry.getTransactionKey());
			}
			*/
		}
	}
	
//	public synchronized void addOutputs(String owner, List<Output> outputs)
	public void addOutputs(String owner, List<Output> outputs)
	{
		if (outputs != null)
		{
			this.outputs.put(owner, outputs);
			/*
			if (!this.ownerTrans.containsKey(owner))
			{
				this.ownerTrans.put(owner, new ArrayList<String>());
			}
			for (Output entry : outputs)
			{
				this.ownerTrans.get(owner).add(entry.getTransactionKey());
			}
			*/
		}
	}
	
	public Map<String, List<Input>> getInputs()
	{
		return this.inputs;
	}
	
	public Map<String, List<Output>> getOutputs()
	{
		return this.outputs;
	}

	/*
	public synchronized Map<String, List<String>> getOwners()
	{
		return this.ownerTrans;
	}
	*/

	/*
	public Transaction getTransaction(String owner, int index)
	{
		if (this.transactions.containsKey(owner))
		{
			return this.transactions.get(owner).get(index);
		}
		return null;
	}
	*/

	/*
	 * I misunderstand the concept of blocks when implementing the below code. The block is responsible for 
	 * 
	public List<Input> getInputs(String owner)
	{
		if (this.transactions.containsKey(owner))
		{
			Transaction lastTransaction = this.transactions.get(owner).get(this.transactions.get(owner).size() - 1);
			return lastTransaction.getInputs();
		}
		return null;
	}

	public List<Output> getOutputs(String owner)
	{
		if (this.transactions.containsKey(owner))
		{
			Transaction lastTransaction = this.transactions.get(owner).get(this.transactions.get(owner).size() - 1);
			return lastTransaction.getOutputs();
		}
		return null;
	}
	*/
}
