package org.greatfree.cry.framework.bitcoin.peer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.cry.framework.bitcoin.Input;
import org.greatfree.cry.framework.bitcoin.Output;
import org.greatfree.cry.framework.bitcoin.Transaction;

/**
 * 
 * Transactions are currently saved in memory as a simplified implementation. Later, I will implement the persistent feature. 02/14/2022, Bing Li
 * 
 * @author Bing Li
 * 
 * 02/14/2022
 *
 */
class TransactionStorage
{
	/*
	 * Transaction information is placed into the below structures. 02/15/2022, Bing Li
	 */
	private Map<String, List<Transaction>> transactions;
	private Map<String, List<Input>> inputs;
	private Map<String, List<Output>> outputs;

	private TransactionStorage()
	{
	}
	
	private static TransactionStorage instance = new TransactionStorage();
	
	public static TransactionStorage COIN()
	{
		if (instance == null)
		{
			instance = new TransactionStorage();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		// Data needs to persist in the method. 02/14/2022, Bing Li
	}
	
	public void init()
	{
		this.transactions = new ConcurrentHashMap<String, List<Transaction>>();
		this.inputs = new ConcurrentHashMap<String, List<Input>>();
		this.outputs = new ConcurrentHashMap<String, List<Output>>();
	}
	
	public void addTransactions(List<Transaction> trans)
	{
		for (Transaction entry : trans)
		{
			this.addTransaction(entry);
		}
	}

	public void addTransaction(Transaction trans)
	{
		if (trans.isOutput())
		{
			if (!this.transactions.containsKey(trans.getOutputFrom()))
			{
				this.transactions.put(trans.getOutputFrom(), new ArrayList<Transaction>());
			}
			this.transactions.get(trans.getOutputFrom()).add(trans);

			if (!this.outputs.containsKey(trans.getOutputFrom()))
			{
				this.outputs.put(trans.getOutputFrom(), new ArrayList<Output>());
			}
			this.outputs.get(trans.getOutputFrom()).add(trans.getOutput());
		}
		else
		{
			if (!this.transactions.containsKey(trans.getInputTo()))
			{
				this.transactions.put(trans.getInputTo(), new ArrayList<Transaction>());
			}
			this.transactions.get(trans.getInputTo()).add(trans);

			if (!this.inputs.containsKey(trans.getInputTo()))
			{
				this.inputs.put(trans.getInputTo(), new ArrayList<Input>());
			}
			this.inputs.get(trans.getInputTo()).add(trans.getInput());
		}
	}

	public List<Input> getInputs(String owner)
	{
		if (this.inputs.containsKey(owner))
		{
			return this.inputs.get(owner);
		}
		return null;
	}
	
	public List<Output> getOutputs(String owner)
	{
		if (this.outputs.containsKey(owner))
		{
			return this.outputs.get(owner);
		}
		return null;
	}
}
