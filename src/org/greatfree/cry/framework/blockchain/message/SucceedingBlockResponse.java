package org.greatfree.cry.framework.blockchain.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 01/27/2022, Bing Li
 *
 */
public class SucceedingBlockResponse extends ServerMessage
{
	private static final long serialVersionUID = 3256919686984521111L;
	
	private List<String> blocksInJSON;

	public SucceedingBlockResponse(List<String> blocksInJSON)
	{
		super(ChainAppID.SUCCEEDING_BLOCK_RESPONSE);
		this.blocksInJSON = blocksInJSON;
	}

	public List<String> getBlocksInJSON()
	{
		return this.blocksInJSON;
	}
}
