package org.greatfree.cry.framework.multisigned.participant;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
class ParticipantMenu
{
	public final static String TAB = "	";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";

	public final static String REQUEST_OWNERSHIP = TAB + "1) Request Ownership;";

	public final static String UNI_CONSENSUS_NOTIFICATION = TAB + "2) Uni-Consensus Notify;";
	public final static String ANY_CONSENSUS_NOTIFICATION = TAB + "3) Any-Consensus Notify;";
	public final static String BROAD_CONSENSUS_NOTIFICATION = TAB + "4) Broad-Consensus Notify;";
	
	public final static String UNI_CONSENSUS_REQUEST = TAB + "5) Uni-Consensus Request;";
	public final static String ANY_CONSENSUS_REQUEST = TAB + "6) Any-Consensus Request;";
	public final static String BROAD_CONSENSUS_REQUEST = TAB + "7) Broad-Consensus Request;";
	
	public final static String STOP_SERVER = TAB + "8) Stop Server;";
	
	public final static String QUIT = TAB + "0) Quit.";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	public final static String WRONG_OPTION = "Wrong option!";
}
