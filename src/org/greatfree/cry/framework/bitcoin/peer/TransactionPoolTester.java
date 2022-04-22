package org.greatfree.cry.framework.bitcoin.peer;

import org.greatfree.cry.framework.bitcoin.Input;
import org.greatfree.cry.framework.bitcoin.Script;
import org.greatfree.cry.framework.bitcoin.Transaction;

/**
 * 
 * @author Bing Li
 * 
 * 02/19/2022
 *
 */
class TransactionPoolTester
{

	public static void main(String[] args)
	{
		TransactionPool.COIN().init();
		Input i = new Input("1", "0", 3.3f, new Script());
		Transaction t1 = new Transaction(i);
		Input i2 = new Input("2", "0", 3.3f, new Script());
		Transaction t2 = new Transaction(i2);
		TransactionPool.COIN().enqueue(t1);
		TransactionPool.COIN().enqueue(t2);
		if (TransactionPool.COIN().isExisted(t1))
		{
			System.out.println("Transaction existed ...");
		}
		else
		{
			System.out.println("Transaction NOT existed ...");
		}
	}

}
