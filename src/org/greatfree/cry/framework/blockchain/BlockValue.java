package org.greatfree.cry.framework.blockchain;

import org.greatfree.util.Builder;

/**
 * 
 * @author libing
 * 
 * 01/27/2022, Bing Li
 *
 */
public class BlockValue
{
	public int sequenceNO;
	public String localPeerName;
	public String fingerPrint;
	public String headPeerName;
	public String precedingFingerPrint;
	public String succeedingPeerName;
	public long timeStamp;
	public int nonce;
	
	public BlockValue(BlockValueBuilder builder)
	{
		this.sequenceNO = builder.getSequenceNO();
		this.localPeerName = builder.getLocalPeerName();
		this.fingerPrint = builder.getFingerPrint();
		this.headPeerName = builder.getHeadPeerName();
		this.precedingFingerPrint = builder.getPrecedingFingerPrint();
		this.succeedingPeerName = builder.getSucceedingPeerName();
		this.timeStamp = builder.getTimeStamp();
		this.nonce = builder.getNonce();
	}
	
	public static class BlockValueBuilder implements Builder<BlockValue>
	{
		private int sequenceNO;
		public String localPeerName;
		private String fingerPrint;
		private String headPeerName;
		private String precedingFingerPrint;
		private String succeedingPeerName;
		private long timeStamp;
		private int nonce;
		
		public BlockValueBuilder()
		{
		}

		public BlockValueBuilder sequenceNO(int sequenceNO)
		{
			this.sequenceNO = sequenceNO;
			return this;
		}

		public BlockValueBuilder localPeerName(String localPeerName)
		{
			this.localPeerName = localPeerName;
			return this;
		}

		public BlockValueBuilder fingerPrint(String fingerPrint)
		{
			this.fingerPrint = fingerPrint;
			return this;
		}

		public BlockValueBuilder headPeerName(String headPeerName)
		{
			this.headPeerName = headPeerName;
			return this;
		}

		public BlockValueBuilder precedingFingerPrint(String precedingFingerPrint)
		{
			this.precedingFingerPrint = precedingFingerPrint;
			return this;
		}

		public BlockValueBuilder succeedingPeerName(String succeedingPeerName)
		{
			this.succeedingPeerName = succeedingPeerName;
			return this;
		}

		public BlockValueBuilder timeStamp(long timeStamp)
		{
			this.timeStamp = timeStamp;
			return this;
		}

		public BlockValueBuilder nonce(int nonce)
		{
			this.nonce = nonce;
			return this;
		}

		@Override
		public BlockValue build()
		{
			return new BlockValue(this);
		}
		
		public int getSequenceNO()
		{
			return this.sequenceNO;
		}
		
		public String getLocalPeerName()
		{
			return this.localPeerName;
		}
		
		public String getFingerPrint()
		{
			return this.fingerPrint;
		}
		
		public String getHeadPeerName()
		{
			return this.headPeerName;
		}
		
		public String getPrecedingFingerPrint()
		{
			return this.precedingFingerPrint;
		}
		
		public String getSucceedingPeerName()
		{
			return this.succeedingPeerName;
		}
		
		public long getTimeStamp()
		{
			return this.timeStamp;
		}
		
		public int getNonce()
		{
			return this.nonce;
		}
	}
}
