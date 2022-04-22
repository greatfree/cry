package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author Bing Li
 * 
 * 02/07/2022
 *
 */
public class CheckBalanceResponse extends ServerMessage
{
	private static final long serialVersionUID = -2914619942821735391L;
	
	private int coins;
	private float valuesInCurrency;
	private boolean isSucceeded;

	public CheckBalanceResponse(int coins, float valuesInCurrency, boolean isSucceeded)
	{
		super(CoinAppID.CHECK_BALANCE_RESPONSE);
		this.coins = coins;
		this.valuesInCurrency = valuesInCurrency;
		this.isSucceeded = isSucceeded;
	}

	public int getCoins()
	{
		return this.coins;
	}
	
	public float getValueInCurrency()
	{
		return this.valuesInCurrency;
	}
	
	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
