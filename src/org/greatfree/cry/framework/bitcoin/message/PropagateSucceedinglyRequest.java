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
public class PropagateSucceedinglyRequest extends Request
{
	private static final long serialVersionUID = 6061326945971517082L;
	
	private List<Transaction> trans;

	public PropagateSucceedinglyRequest(List<Transaction> trans)
	{
		super(CoinAppID.PROPAGATE_SUCCEEDINGLY_REQUEST);
		this.trans = trans;
	}

	public List<Transaction> getTransactions()
	{
		return this.trans;
	}
}
