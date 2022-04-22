package org.greatfree.cry.framework.bitcoin;

import java.io.Serializable;

/**
 * 
 * @author Bing Li
 * 
 * 02/10/2022
 *
 */
public class Output implements Serializable
{
	private static final long serialVersionUID = 9176944663231331463L;

	private String transactionKey;
//	private final int index;
	private final String from;
	private final String to;
	private final float valueInCurrency;
	
	private final Script script;
	
	public String toString()
	{
		return "Output-" + this.transactionKey + ": from " + this.from + ", to " + this.to + ", value = " + this.valueInCurrency;
	}
	
//	public Output(String transactionKey, int index, String from, String to, float value, Script script)
//	public Output(String transactionKey, String from, String to, float value, Script script)
	public Output(String from, String to, float value, Script script)
	{
//		this.transactionKey = transactionKey;
//		this.index = index;
		this.from = from;
		this.to = to;
		this.valueInCurrency = value;
		this.script = script;
	}
	
//	public Input getInput(int index)
	/*
	public Input getInput()
	{
//		return new Input(this.transactionKey, index, this.from, this.to, this.valueInCurrency, this.script);
		return new Input(this.transactionKey, this.from, this.to, this.valueInCurrency, this.script);
	}
	*/
	
	public void setTransactionKey(String key)
	{
		this.transactionKey = key;
	}

	public String getTransactionKey()
	{
		return this.transactionKey;
	}

	/*
	public int getIndex()
	{
		return this.index;
	}
	*/
	
	public String getFrom()
	{
		return this.from;
	}
	
	public String getTo()
	{
		return this.to;
	}
	
	public float getValueInCurrency()
	{
		return this.valueInCurrency;
	}
	
	public Script getScript()
	{
		return this.script;
	}
}
