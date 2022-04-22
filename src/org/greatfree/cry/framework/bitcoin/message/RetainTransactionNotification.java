package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.cry.framework.bitcoin.Transaction;
import org.greatfree.message.container.Notification;

/**
 * 
 * @author Bing Li
 * 
 * 02/16/2022
 *
 */
// public class ForwardTransactionNotification extends Notification
public class RetainTransactionNotification extends Notification
{
	private static final long serialVersionUID = 6248550964285513133L;
	
	private Transaction trans;
//	private List<Transaction> trans;

//	public ForwardTransactionNotification(List<Transaction> trans)
	public RetainTransactionNotification(Transaction trans)
	{
		super(CoinAppID.RETAIN_TRANSACTION_NOTIFICATION);
		this.trans = trans;
	}

//	public List<Transaction> getTransactions()
	public Transaction getTransaction()
	{
		return this.trans;
	}
}
