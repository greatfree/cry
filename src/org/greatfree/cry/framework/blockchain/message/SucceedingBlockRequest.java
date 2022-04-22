package org.greatfree.cry.framework.blockchain.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 01/27/2022, Bing Li
 *
 */
public class SucceedingBlockRequest extends Request
{
	private static final long serialVersionUID = -1170471341219339835L;

	public SucceedingBlockRequest()
	{
		super(ChainAppID.SUCCEEDING_BLOCK_REQUEST);
	}

}
