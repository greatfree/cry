package org.greatfree.cry.framework.multicast.client;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.chat.ChatMenu;
import org.greatfree.chat.ChatOptions;
import org.greatfree.cry.exceptions.CheatingException;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.OwnerCheatingException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.multicast.message.ClientAnycastNotification;
import org.greatfree.cry.framework.multicast.message.ClientAnycastRequest;
import org.greatfree.cry.framework.multicast.message.ClientAnycastResponse;
import org.greatfree.cry.framework.multicast.message.ClientBroadcastNotification;
import org.greatfree.cry.framework.multicast.message.ClientBroadcastRequest;
import org.greatfree.cry.framework.multicast.message.ClientBroadcastResponse;
import org.greatfree.cry.framework.multicast.message.ClientUnicastNotification;
import org.greatfree.cry.framework.multicast.message.ClientUnicastRequest;
import org.greatfree.cry.framework.multicast.message.ClientUnicastResponse;
import org.greatfree.cry.framework.multicast.message.HelloWorld;
import org.greatfree.cry.framework.multicast.message.HelloWorldAnycastResponse;
import org.greatfree.cry.framework.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.cry.framework.multicast.message.HelloWorldUnicastResponse;
import org.greatfree.cry.multicast.MulticastConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Time;

/**
 * 
 * @author libing
 * 
 * 04/11/2022
 *
 */
final class MulticastUI
{
//	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.multicast.client");

	private Scanner in = new Scanner(System.in);

	private MulticastUI()
	{
	}

	private static MulticastUI instance = new MulticastUI();
	
	public static MulticastUI CLUSTER()
	{
		if (instance == null)
		{
			instance = new MulticastUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		this.in.close();
	}

	public void printMenu()
	{
		System.out.println(ChatMenu.MENU_HEAD);
		System.out.println(ChatMenu.TYPE_MESSAGE);
		System.out.println(ChatMenu.QUIT);
		System.out.println(ChatMenu.MENU_TAIL);
		System.out.println(ChatMenu.INPUT_PROMPT);
	}
	
	/*
	 * Send messages to the distributed nodes within the cluster. 04/23/2017, Bing Li
	 */
	public void send(int highOption, int option) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, ClassNotFoundException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, ShortBufferException, SignatureException, SymmetricKeyUnavailableException, OwnerCheatingException, CheatingException
	{
		int index = 0;
		ClientBroadcastResponse broadcastResponse;
		ClientAnycastResponse anycastResponse;
		ClientUnicastResponse unicastResponse;
		Date startTime;
		Date endTime;
		switch (option)
		{
			case ChatOptions.TYPE_CHAT:
				System.out.println("Please type your message: ");
				String message = in.nextLine();
				switch (highOption)
				{
					case MultiOptions.BROADCAST_NOTIFICATION:
						startTime = Calendar.getInstance().getTime();
//						ClientUI.FRONT().notify(new ClientBroadcastNotification(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.PLAIN));
						ClientUI.FRONT().notify(new ClientBroadcastNotification(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.PLAIN), MulticastConfig.PLAIN);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You notification is broadcast!");
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
						break;
						
					case MultiOptions.ANYCAST_NOTIFICATION:
						startTime = Calendar.getInstance().getTime();
						ClientUI.FRONT().notify(new ClientAnycastNotification(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.PLAIN), MulticastConfig.PLAIN);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You notification is anycast!");
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
						break;
						
					case MultiOptions.UNICAST_NOTIFICATION:
						startTime = Calendar.getInstance().getTime();
						ClientUI.FRONT().notify(new ClientUnicastNotification(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.PLAIN), MulticastConfig.PLAIN);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You notification is unicast!");
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
						break;
						
					case MultiOptions.BROADCAST_REQUEST:
						startTime = Calendar.getInstance().getTime();
						broadcastResponse = (ClientBroadcastResponse)ClientUI.FRONT().read(new ClientBroadcastRequest(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.PLAIN), MulticastConfig.PLAIN);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You request is broadcast!");
						System.out.println("You got " + broadcastResponse.getResponses().size() + " responses");
						for (HelloWorldBroadcastResponse response : broadcastResponse.getResponses())
						{
							System.out.println(++index + ") response = " + response.getHello().getMessage());
						}
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
						break;
						
					case MultiOptions.ANYCAST_REQUEST:
						startTime = Calendar.getInstance().getTime();
						anycastResponse = (ClientAnycastResponse)ClientUI.FRONT().read(new ClientAnycastRequest(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.PLAIN), MulticastConfig.PLAIN);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You request is anycast!");
						System.out.println("You got " + anycastResponse.getResponses().size() + " responses");
						for (HelloWorldAnycastResponse response : anycastResponse.getResponses())
						{
							System.out.println(++index + ") response = " + response.getHello().getMessage());
						}
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
						break;
						
					case MultiOptions.UNICAST_REQUEST:
						startTime = Calendar.getInstance().getTime();
						unicastResponse = (ClientUnicastResponse)ClientUI.FRONT().read(new ClientUnicastRequest(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.PLAIN), MulticastConfig.PLAIN);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You request is unicast!");
						System.out.println("You got " + unicastResponse.getResponses().size() + " responses");
						for (HelloWorldUnicastResponse response : unicastResponse.getResponses())
						{
							System.out.println(++index + ") response = " + response.getHello().getMessage());
						}
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
						break;

					case MultiOptions.SYM_BROADCAST_NOTIFICATION:
						startTime = Calendar.getInstance().getTime();
						ClientUI.FRONT().notify(new ClientBroadcastNotification(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.SYM), MulticastConfig.SYM);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You notification is broadcast!");
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
						break;
						
					case MultiOptions.SYM_ANYCAST_NOTIFICATION:
						startTime = Calendar.getInstance().getTime();
						ClientUI.FRONT().notify(new ClientAnycastNotification(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.SYM), MulticastConfig.SYM);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You notification is anycast!");
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
						break;
						
					case MultiOptions.SYM_UNICAST_NOTIFICATION:
						startTime = Calendar.getInstance().getTime();
						ClientUI.FRONT().notify(new ClientUnicastNotification(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.SYM), MulticastConfig.SYM);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You notification is unicast!");
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
						break;
						
					case MultiOptions.SYM_BROADCAST_REQUEST:
						startTime = Calendar.getInstance().getTime();
						broadcastResponse = (ClientBroadcastResponse)ClientUI.FRONT().read(new ClientBroadcastRequest(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.SYM), MulticastConfig.SYM);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You request is broadcast!");
						System.out.println("You got " + broadcastResponse.getResponses().size() + " responses");
						for (HelloWorldBroadcastResponse response : broadcastResponse.getResponses())
						{
							System.out.println(++index + ") response = " + response.getHello().getMessage());
						}
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
						break;
						
					case MultiOptions.SYM_ANYCAST_REQUEST:
						startTime = Calendar.getInstance().getTime();
						anycastResponse = (ClientAnycastResponse)ClientUI.FRONT().read(new ClientAnycastRequest(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.SYM), MulticastConfig.SYM);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You request is anycast!");
						System.out.println("You got " + anycastResponse.getResponses().size() + " responses");
						for (HelloWorldAnycastResponse response : anycastResponse.getResponses())
						{
							System.out.println(++index + ") response = " + response.getHello().getMessage());
						}
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
						break;
						
					case MultiOptions.SYM_UNICAST_REQUEST:
						startTime = Calendar.getInstance().getTime();
						unicastResponse = (ClientUnicastResponse)ClientUI.FRONT().read(new ClientUnicastRequest(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.SYM), MulticastConfig.SYM);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You request is unicast!");
						System.out.println("You got " + unicastResponse.getResponses().size() + " responses");
						for (HelloWorldUnicastResponse response : unicastResponse.getResponses())
						{
							System.out.println(++index + ") response = " + response.getHello().getMessage());
						}
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
						break;

					case MultiOptions.ASYM_BROADCAST_NOTIFICATION:
						startTime = Calendar.getInstance().getTime();
						ClientUI.FRONT().notify(new ClientBroadcastNotification(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.ASYM), MulticastConfig.ASYM);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You notification is broadcast!");
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
						break;
						
					case MultiOptions.ASYM_ANYCAST_NOTIFICATION:
						startTime = Calendar.getInstance().getTime();
						ClientUI.FRONT().notify(new ClientAnycastNotification(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.ASYM), MulticastConfig.ASYM);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You notification is anycast!");
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
						break;
						
					case MultiOptions.ASYM_UNICAST_NOTIFICATION:
						startTime = Calendar.getInstance().getTime();
						ClientUI.FRONT().notify(new ClientUnicastNotification(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.ASYM), MulticastConfig.ASYM);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You notification is unicast!");
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
						break;
						
					case MultiOptions.ASYM_BROADCAST_REQUEST:
						startTime = Calendar.getInstance().getTime();
						broadcastResponse = (ClientBroadcastResponse)ClientUI.FRONT().read(new ClientBroadcastRequest(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.ASYM), MulticastConfig.ASYM);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You request is broadcast!");
						System.out.println("You got " + broadcastResponse.getResponses().size() + " responses");
						for (HelloWorldBroadcastResponse response : broadcastResponse.getResponses())
						{
							System.out.println(++index + ") response = " + response.getHello().getMessage());
						}
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
						break;
						
					case MultiOptions.ASYM_ANYCAST_REQUEST:
						startTime = Calendar.getInstance().getTime();
						anycastResponse = (ClientAnycastResponse)ClientUI.FRONT().read(new ClientAnycastRequest(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.ASYM), MulticastConfig.ASYM);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You request is anycast!");
						System.out.println("You got " + anycastResponse.getResponses().size() + " responses");
						for (HelloWorldAnycastResponse response : anycastResponse.getResponses())
						{
							System.out.println(++index + ") response = " + response.getHello().getMessage());
						}
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
						break;
						
					case MultiOptions.ASYM_UNICAST_REQUEST:
						startTime = Calendar.getInstance().getTime();
						unicastResponse = (ClientUnicastResponse)ClientUI.FRONT().read(new ClientUnicastRequest(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.ASYM), MulticastConfig.ASYM);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You request is unicast!");
						System.out.println("You got " + unicastResponse.getResponses().size() + " responses");
						for (HelloWorldUnicastResponse response : unicastResponse.getResponses())
						{
							System.out.println(++index + ") response = " + response.getHello().getMessage());
						}
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
						break;

					case MultiOptions.SIGNED_BROADCAST_NOTIFICATION:
						startTime = Calendar.getInstance().getTime();
						ClientUI.FRONT().notify(new ClientBroadcastNotification(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.SIGNED), MulticastConfig.SIGNED);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You notification is broadcast!");
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
						break;
						
					case MultiOptions.SIGNED_ANYCAST_NOTIFICATION:
						startTime = Calendar.getInstance().getTime();
						ClientUI.FRONT().notify(new ClientAnycastNotification(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.SIGNED), MulticastConfig.SIGNED);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You notification is anycast!");
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
						break;
						
					case MultiOptions.SIGNED_UNICAST_NOTIFICATION:
						startTime = Calendar.getInstance().getTime();
						ClientUI.FRONT().notify(new ClientUnicastNotification(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.SIGNED), MulticastConfig.SIGNED);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You notification is unicast!");
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
						break;
						
					case MultiOptions.SIGNED_BROADCAST_REQUEST:
						startTime = Calendar.getInstance().getTime();
						broadcastResponse = (ClientBroadcastResponse)ClientUI.FRONT().read(new ClientBroadcastRequest(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.SIGNED), MulticastConfig.SIGNED);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You request is broadcast!");
						System.out.println("You got " + broadcastResponse.getResponses().size() + " responses");
						for (HelloWorldBroadcastResponse response : broadcastResponse.getResponses())
						{
							System.out.println(++index + ") response = " + response.getHello().getMessage());
						}
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
						break;
						
					case MultiOptions.SIGNED_ANYCAST_REQUEST:
						startTime = Calendar.getInstance().getTime();
						anycastResponse = (ClientAnycastResponse)ClientUI.FRONT().read(new ClientAnycastRequest(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.SIGNED), MulticastConfig.SIGNED);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You request is anycast!");
						System.out.println("You got " + anycastResponse.getResponses().size() + " responses");
						for (HelloWorldAnycastResponse response : anycastResponse.getResponses())
						{
							System.out.println(++index + ") response = " + response.getHello().getMessage());
						}
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
						break;
						
					case MultiOptions.SIGNED_UNICAST_REQUEST:
						startTime = Calendar.getInstance().getTime();
						unicastResponse = (ClientUnicastResponse)ClientUI.FRONT().read(new ClientUnicastRequest(new HelloWorld(message, Calendar.getInstance().getTime()), MulticastConfig.SIGNED), MulticastConfig.SIGNED);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You request is unicast!");
						System.out.println("You got " + unicastResponse.getResponses().size() + " responses");
						for (HelloWorldUnicastResponse response : unicastResponse.getResponses())
						{
							System.out.println(++index + ") response = " + response.getHello().getMessage());
						}
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
						break;
						
					case MultiOptions.CLAIM_OWNERSHIP:
						break;

					case MultiOptions.ABANDON_OWNERSHIP:
						break;
				}
				break;

			case ChatOptions.QUIT_CHAT:
				break;
		}
	}
}
