package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author Bing Li
 * 
 * 02/17/2022
 *
 */
public class VerificationResultNotification extends Notification
{
	private static final long serialVersionUID = 1183294979942156493L;
	
//	private List<String> transactions;
	private String transactionKey;
	private boolean isValid;

//	public VerificationResultNotification(List<String> transactions, boolean isValid)
	public VerificationResultNotification(String transactionKey, boolean isValid)
	{
		super(CoinAppID.VERIFICATION_RESULT_NOTIFICATION);
		this.transactionKey = transactionKey;
		this.isValid = isValid;
	}

	/*
	public List<String> getTransactions()
	{
		return this.transactions;
	}
	*/
	
	public String getTransactionKey()
	{
		return this.transactionKey;
	}
	
	public boolean isValid()
	{
		return this.isValid;
	}
}
