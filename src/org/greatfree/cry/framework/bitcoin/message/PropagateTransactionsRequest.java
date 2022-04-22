package org.greatfree.cry.framework.bitcoin.message;

import java.util.List;

import org.greatfree.cry.framework.bitcoin.Transaction;
import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 02/25/2022
 *
 */
public class PropagateTransactionsRequest extends Request
{
	private static final long serialVersionUID = 1575709931708461875L;
	
	private List<Transaction> trans;

	public PropagateTransactionsRequest(List<Transaction> trans)
	{
		super(CoinAppID.PROPAGATE_TRANSACTIONS_REQUEST);
		this.trans = trans;
	}

	public List<Transaction> getTransactions()
	{
		return this.trans;
	}
}
