package org.greatfree.cry.framework.tncs.client;

/**
 * 
 * @author libing
 * 
 * 01/07/2022, Bing Li
 *
 */
class ClientMenu
{
	public final static String TAB = "	";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String TYPE_NOTIFICATION = ClientMenu.TAB + "1) Type a piece of notification;";
	public final static String TYPE_REQUEST = ClientMenu.TAB + "2) Type a piece of request;";
	public final static String SHUTDOWN_SERVER = TAB + "3) Shutdown server;";
	public final static String QUIT = ClientMenu.TAB + "0) Quit.";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	
	public final static String WRONG_OPTION = "Wrong option!";
}
