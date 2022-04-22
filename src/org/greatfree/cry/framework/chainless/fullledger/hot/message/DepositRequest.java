package org.greatfree.cry.framework.chainless.fullledger.hot.message;

import org.greatfree.message.multicast.container.ClusterRequest;

/**
 * 
 * @author libing
 * 
 * 04/06/2022
 *
 */
public class DepositRequest extends ClusterRequest
{
	private static final long serialVersionUID = -3753406442406433552L;
	
	private String userKey;
	private String userName;
	private float funds;
	private String currencyType;
	private String ledgerAddress;

	public DepositRequest(String userKey, String userName, float funds, String currencyType, String ledgerAddress)
	{
		super(userKey, HotAppID.DEPOSIT_REQUEST);
		this.userKey = userKey;
		this.userName = userName;
		this.funds = funds;
		this.currencyType = currencyType;
		this.ledgerAddress = ledgerAddress;
	}

	public String getUserKey()
	{
		return this.userKey;
	}
	
	public String getUserName()
	{
		return this.userName;
	}
	
	public float getFunds()
	{
		return this.funds;
	}
	
	public String getCurrencyType()
	{
		return this.currencyType;
	}
	
	public String getLedgerAddress()
	{
		return this.ledgerAddress;
	}
}
