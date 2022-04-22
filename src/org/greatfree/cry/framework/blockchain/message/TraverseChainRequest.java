package org.greatfree.cry.framework.blockchain.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 01/27/2022, Bing Li
 *
 */
public class TraverseChainRequest extends Request
{
	private static final long serialVersionUID = 7678667781713923386L;
	
//	private String queryPeerName;

//	public TraverseChainRequest(String queryPeerName)
	public TraverseChainRequest()
	{
		super(ChainAppID.TRAVERSE_CHAIN_REQUEST);
//		this.queryPeerName = queryPeerName;
	}

	/*
	public String getQueryPeerName()
	{
		return this.queryPeerName;
	}
	*/
}
