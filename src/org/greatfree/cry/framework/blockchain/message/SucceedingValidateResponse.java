package org.greatfree.cry.framework.blockchain.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 01/27/2022, Bing Li
 *
 */
public class SucceedingValidateResponse extends ServerMessage
{
	private static final long serialVersionUID = 1904306680462805404L;

	private int sequenceNO;
	private boolean isValid;

	public SucceedingValidateResponse(int sequenceNO, boolean isValid)
	{
		super(ChainAppID.SUCCEEDING_VALIDATE_RESPONSE);
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
