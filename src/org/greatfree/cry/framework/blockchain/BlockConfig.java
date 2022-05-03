package org.greatfree.cry.framework.blockchain;

import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 01/26/2022, Bing Li
 *
 */
public class BlockConfig
{
	public final static String BC_REGISTRY_IP = "192.168.1.18";
//	public final static String BC_REGISTRY_IP = "192.168.3.8";
	public final static int BC_REGISTRY_PORT = 8941;
	
	public final static String BC_COORDINATOR = "BlockChainCoordinator";
	public final static String BC_COORDINATOR_KEY = Tools.getHash(BC_COORDINATOR);
	public final static int BC_COORDINATOR_PORT = 8944;
	public final static int CHAIN_NODE_PORT = 8945;

//	public final static int RSA_LENGTH = 4096;
//	public final static int SYMMETRIC_KEY_LENGTH = 256;
//	public final static int SYMMETRIC_IV_KEY_LENGTH = 128;
	
	public final static String NO_FINGER_PRINT = "INIT_" + Tools.generateUniqueKey();
	public final static String NO_PRECEDING_FINGER_PRINT = "0";
	public final static String NO_SUCCEEDING_FINGER_PRINT = "0";
	
//	public final static int BLOCK_FINGER_PRINT_DIFFICULTY = 5;
	public final static int BLOCK_FINGER_PRINT_DIFFICULTY = 3;
	public final static String HASH_TARGET = new String(new char[BLOCK_FINGER_PRINT_DIFFICULTY]).replace('\0', '0');
}
