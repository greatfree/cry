package org.greatfree.cry.framework.bitcoin;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 01/26/2022, Bing Li
 *
 */
public final class Transaction implements Serializable
{
	private static final long serialVersionUID = -8921665679896209126L;
	
	/*
	 * The owner should be identical to the value of "from". 02/14/2022, Bing Li
	 */
//	private final String owner;
//	private final String from;
//	private final String to;

//	private final float coins;
	// The below information increases gradually. It is not proper to save them in each transaction since it results in highly redundant storage. 02/14/2022, Bing Li
//	private final List<Input> inputs;
//	private final List<Output> outputs;
	private final String key;
	private final Input input;
	private final Output output;
	
	public String toString()
	{
//		return CoinConfig.BAR + "TransHash = " + this.hashCode() + "\nTransaction-" + this.key + "\n" + this.input + "\n" + this.output + CoinConfig.BAR;
		return CoinConfig.BAR + "Transaction-" + this.key + "\n" + this.input + "\n" + this.output + CoinConfig.BAR;
	}
	
	private final Date time;
	
//	public Transaction(String owner, String from, String to, float coins, List<Input> inputs, List<Output> outputs)
//	public Transaction(String owner, String from, String to, float coins, Output output)
//	public Transaction(String from, String to, float coins, Output output)
	public Transaction(Output output)
	{
		this.key = Tools.generateUniqueKey();
//		this.owner = owner;
//		this.from = from;
//		this.to = to;
//		this.coins = coins;
//		this.inputs = inputs;
//		this.outputs = outputs;
		this.input = null;
		this.output = output;
		this.output.setTransactionKey(this.key);
		this.time = Calendar.getInstance().getTime();
	}
	
	public Transaction(Input input)
	{
		this.key = Tools.generateUniqueKey();
		this.input = input;
		this.input.setTransactionKey(this.key);
		this.output = null;
		this.time = Calendar.getInstance().getTime();
	}
	
	public String getKey()
	{
		return this.key;
	}

	/*
	public String getOwner()
	{
		return this.owner;
	}
	*/
	
	public String getInputFrom()
	{
		return this.input.getFrom();
	}
	
	public String getInputTo()
	{
		return this.input.getTo();
	}
	
	public float getInputCurrency()
	{
		return this.input.getValueInCurrency();
	}
	
	public String getOutputFrom()
	{
		return this.output.getFrom();
	}
	
	public String getOutputTo()
	{
		return this.output.getTo();
	}
	
	public float getOutputCurrency()
	{
		return this.output.getValueInCurrency();
	}

	public boolean isOutput()
	{
		return this.output != null ? true : false;
	}

	/*
	public List<Input> getInputs()
	{
		return this.inputs;
	}
	
	public List<Output> getOutputs()
	{
		return this.outputs;
	}
	*/
	
	public Output getOutput()
	{
		return this.output;
	}
	
	public Input getInput()
	{
		return this.input;
	}
	
	public Date getTime()
	{
		return this.time;
	}
}
