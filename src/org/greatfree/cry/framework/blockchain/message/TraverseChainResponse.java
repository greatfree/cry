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
public class TraverseChainResponse extends ServerMessage
{
	private static final long serialVersionUID = 2875737779773262234L;
	
	private List<String> blocksInJSON;

	public TraverseChainResponse(List<String> blocksInJSON)
	{
		super(ChainAppID.TRAVERSE_CHAIN_RESPONSE);
		this.blocksInJSON = blocksInJSON;
	}

	public List<String> getBlocksInJSON()
	{
		return this.blocksInJSON;
	}
}
