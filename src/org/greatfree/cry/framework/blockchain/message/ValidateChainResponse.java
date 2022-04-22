package org.greatfree.cry.framework.blockchain.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 01/27/2022, Bing Li
 *
 */
public class ValidateChainResponse extends ServerMessage
{
	private static final long serialVersionUID = 760692216586230143L;

	private int sequenceNO;
	private boolean isValid;

	public ValidateChainResponse(int sequenceNO, boolean isValid)
	{
		super(ChainAppID.VALIDATE_CHAIN_RESPONSE);
		this.sequenceNO = sequenceNO;
		this.isValid = isValid;
	}
	
	public int getSequenceNO()
	{
		return this.sequenceNO;
	}

	public boolean isValid()
	{
		return this.isValid;
	}
}
