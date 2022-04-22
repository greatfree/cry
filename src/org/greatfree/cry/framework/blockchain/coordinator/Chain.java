package org.greatfree.cry.framework.blockchain.coordinator;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.greatfree.cry.framework.blockchain.BlockConfig;
import org.greatfree.cry.framework.blockchain.BlockInfo;

/**
 * 
 * @author libing
 * 
 * 01/26/2022, Bing Li
 *
 */
class Chain
{
	private List<BlockInfo> blockInfos;
	
	private Chain()
	{
		this.blockInfos = new CopyOnWriteArrayList<BlockInfo>();
	}
	
	private static Chain instance = new Chain();
	
	public static Chain BC()
	{
		if (instance == null)
		{
			instance = new Chain();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public String getPrecedingFingerPrint()
	{
		if (this.blockInfos.size() > 0)
		{
			return this.blockInfos.get(this.blockInfos.size() - 1).getFingerPrint();
		}
		return BlockConfig.NO_PRECEDING_FINGER_PRINT;
	}
	
	public int getSequenceNO()
	{
		return this.blockInfos.size() - 1;
	}
	
	public String getHeadPeerName()
	{
		if (this.blockInfos.size() > 0)
		{
			return this.blockInfos.get(0).getNodeName();
		}
		return null;
	}
	
	public void joinChain(BlockInfo bi)
	{
		this.blockInfos.add(bi);
	}
	
//	public String joinChain(BlockInfo blockInfo)
//	public BlockInfo getPrecedingChain(BlockInfo bi)
	public BlockInfo getPrecedingChain()
	{
		if (this.blockInfos.size() > 1)
		{
//			return bi.getFingerPrint();
			return this.blockInfos.get(this.blockInfos.size() - 2);
		}
		return null;
	}
}
