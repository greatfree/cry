package edu.greatfree.cry.framework.p2p.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 01/11/2022, Bing Li
 *
 */
public class PeerRequest extends Request
{
	private static final long serialVersionUID = 3854861217037518157L;
	
	private Greetings gts;

	public PeerRequest(Greetings gts)
	{
		super(P2PAppID.PEER_REQUEST);
		this.gts = gts;
	}

	public Greetings getGreetings()
	{
		return this.gts;
	}
}
