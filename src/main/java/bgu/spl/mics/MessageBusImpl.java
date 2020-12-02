package bgu.spl.mics;
import bgu.spl.mics.application.services.C3POMicroservice;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance;
	// for each  microservice who registered should create
	// A queue of messages and should save which
	// type of broadcasts and events he is subscribed to
	private HashMap<MicroService, LinkedList<Message>> microServiceMessages;
	private HashMap<MicroService,LinkedList<Class<? extends Message>>> microServiceSubscriptions;

	/**
	 * Private constructor
	 * Added*
	 */
	private MessageBusImpl() { //Singleton pattern
		microServiceMessages = new HashMap<>();
		microServiceSubscriptions = new HashMap<>();
	}

	public static MessageBusImpl getInstance(){ //Singleton pattern
		if(instance == null){
			//only on creation of first instance synchronize:
			// this is to make sure only one thread creates the first instance
			synchronized (MessageBusImpl.class){
				if(instance==null)
					instance = new MessageBusImpl();
			}
		}
		return instance;
	}
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		
        return null;
	}

	@Override
	public void register(MicroService m) {
		LinkedList<Message> messagesQueue = new LinkedList<Message>();
		LinkedList<Class<? extends Message>> messageTypeQueue = new LinkedList<Class<? extends Message>>();
		microServiceMessages.put(m,messagesQueue);
		microServiceSubscriptions.put(m,messageTypeQueue);
	}

	@Override
	public void unregister(MicroService m) {
		microServiceMessages.remove(m);
		microServiceSubscriptions.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		
		return null;
	}
}
