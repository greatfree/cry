package org.greatfree.cry.framework.bitcoin;

/**
 * 
 * @author Bing Li
 * 
 * 02/10/2022
 *
 */
public class UTXO
{
	private String transactionKey;
	private float leftValue;

	public UTXO(String transactionKey, float leftValue)
	{
		this.transactionKey = transactionKey;
		this.leftValue = leftValue;
	}
	
	public String getTransactionKey()
	{
		return this.transactionKey;
	}
	
	public float getLeftValue()
	{
		return this.leftValue;
	}
}
