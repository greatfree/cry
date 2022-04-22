package org.greatfree.cry.framework.blockchain.message;

/**
 * 
 * @author libing
 * 
 * 01/26/2022, Bing Li
 *
 */
public class ChainAppID
{
	public final static int JOIN_CHAIN_REQUEST = 80000;
	public final static int JOIN_CHAIN_RESPONSE = 80001;

	public final static int CHECK_CHAIN_VIEW_REQUEST = 80002;
	public final static int CHECK_CHAIN_VIEW_RESPONSE = 80003;
	
	public final static int STOP_COORDINATOR_NOTIFICATION = 80004;
	public final static int SUCCEEDING_PEER_NOTIFICATION = 80005;
	
	public final static int TRAVERSE_CHAIN_REQUEST = 80006;
	public final static int TRAVERSE_CHAIN_RESPONSE = 80007;
	
	public final static int SUCCEEDING_BLOCK_REQUEST = 80008;
	public final static int SUCCEEDING_BLOCK_RESPONSE = 80009;
	
	public final static int VALIDATE_CHAIN_REQUEST = 80010;
	public final static int VALIDATE_CHAIN_RESPONSE = 80011;
	
	public final static int SUCCEEDING_VALIDATE_REQUEST = 80012;
	public final static int SUCCEEDING_VALIDATE_RESPONSE = 80013;
	
	public final static int CHAIN_UPDATE_NOTIFICATION = 80014;
	
	public final static int PRECEDING_FINGER_PRINT_REQUEST = 80015;
	public final static int PRECEDING_FINGER_PRINT_RESPONSE = 80016;
}
