package org.greatfree.cry.framework.blockchain.message;

import org.greatfree.cry.framework.blockchain.BlockInfo;
import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 01/26/2022, Bing Li
 *
 */
public class JoinChainRequest extends Request
{
	private static final long serialVersionUID = -6780960434126196914L;

	private BlockInfo bi;

	public JoinChainRequest(BlockInfo bi)
	{
		super(ChainAppID.JOIN_CHAIN_REQUEST);
		this.bi = bi;
	}
	
	public BlockInfo getBlockInfo()
	{
		return this.bi;
	}
}
