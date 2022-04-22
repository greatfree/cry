package org.greatfree.cry.framework.blockchain.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 01/27/2022, Bing Li
 *
 */
public class ValidateChainRequest extends Request
{
	private static final long serialVersionUID = -9157567315454300952L;

	public ValidateChainRequest()
	{
		super(ChainAppID.VALIDATE_CHAIN_REQUEST);
	}

}
