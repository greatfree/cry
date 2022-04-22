package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 02/26/2022
 *
 */
public class ChainLengthRequest extends Request
{
	private static final long serialVersionUID = 5116284198237780041L;

	public ChainLengthRequest()
	{
		super(CoinAppID.CHAIN_LENGTH_REQUEST);
	}

}
