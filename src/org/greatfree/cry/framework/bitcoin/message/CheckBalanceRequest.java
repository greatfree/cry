package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author Bing Li
 * 
 * 02/07/2022
 *
 */
public class CheckBalanceRequest extends Request
{
	private static final long serialVersionUID = 3603889057177563989L;

	public CheckBalanceRequest()
	{
		super(CoinAppID.CHECK_BALANCE_REQUEST);
	}
}
