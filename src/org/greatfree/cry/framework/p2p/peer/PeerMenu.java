package org.greatfree.cry.framework.p2p.peer;

/**
 * 
 * @author libing
 * 
 * 01/11/2022, Bing Li
 *
 */
public class PeerMenu
{
	public final static String TAB = "	";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String TYPE_NOTIFICATION = TAB + "1) Type a piece of notification;";
	public final static String TYPE_REQUEST = TAB + "2) Type a piece of request;";
	public final static String CHANGE_SIGNATURE_UNILATERALLY = TAB + "3) Change signature unilaterally;";
//	public final static String SHUTDOWN_SERVER = TAB + "3) Shutdown registry server;";
	public final static String QUIT = TAB + "0) Quit.";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	
	public final static String WRONG_OPTION = "Wrong option!";
}
