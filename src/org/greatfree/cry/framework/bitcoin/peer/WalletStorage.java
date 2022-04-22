package org.greatfree.cry.framework.bitcoin.peer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.greatfree.cry.framework.bitcoin.Coin;
import org.greatfree.cry.framework.bitcoin.Input;
import org.greatfree.cry.framework.bitcoin.Output;
import org.greatfree.cry.framework.bitcoin.Transaction;
import org.greatfree.cry.framework.bitcoin.UTXO;

/**
 * 
 * @author Bing Li
 * 
 * 02/15/2022
 *
 */
class WalletStorage
{
	/*
	 * The below structures keep the wallet information. 02/15/2022, Bing Li
	 */
	private List<Coin> coins;
	private List<UTXO> utxos;
	private float balance;

	private WalletStorage()
	{
	}
	
	private static WalletStorage instance = new WalletStorage();
	
	public static WalletStorage COIN()
	{
		if (instance == null)
		{
			instance = new WalletStorage();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		// Data needs to persist in the method. 02/15/2022, Bing Li
	}
	
	public void init()
	{
		this.coins = new CopyOnWriteArrayList<Coin>();
		this.utxos = new CopyOnWriteArrayList<UTXO>();
		this.balance = 0f;
	}

	public void add(Coin c)
	{
		this.coins.add(c);
	}

	public synchronized void setUTXO(Transaction t)
	{
		List<Input> inputs;
		List<Output> outputs;
		if (t.isOutput())
		{
			inputs = TransactionStorage.COIN().getInputs(t.getOutputFrom());
			outputs = TransactionStorage.COIN().getOutputs(t.getOutputFrom());
		}
		else
		{
			inputs = TransactionStorage.COIN().getInputs(t.getInputTo());
			outputs = TransactionStorage.COIN().getOutputs(t.getInputTo());
		}

		float input = 0f;
		for (Input entry : inputs)
		{
			input += entry.getValueInCurrency();
		}
		
		float output = 0f;
		for (Output entry : outputs)
		{
			output += entry.getValueInCurrency();
		}
		this.utxos.add(new UTXO(t.getKey(), input - output));
	}
	
	public synchronized float getBalance()
	{
		if (this.utxos.size() > 0)
		{
			this.balance = this.utxos.get(this.utxos.size() - 1).getLeftValue();
		}
		return this.balance;
	}
}
