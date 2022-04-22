package org.greatfree.cry.framework.p2p.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 01/11/2022, Bing Li
 *
 */
public class PeerResponse extends ServerMessage
{
	private static final long serialVersionUID = -1199685529331982336L;
	
	private String message;

	public PeerResponse(String message)
	{
		super(P2PAppID.PEER_RESPONSE);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
