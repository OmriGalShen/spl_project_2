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
	private HashMap<Event,Future> eventFutureMap;

	/**
	 * Private constructor
	 * Added*
	 */
	private MessageBusImpl() { //Singleton pattern
		microServiceMessages = new HashMap<>();
		microServiceSubscriptions = new HashMap<>();
		eventFutureMap = new HashMap<>();
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

	/**
	 * Subscribes {@code m} to receive {@link Event}s of type {@code type}.
	 * <p>
	 * @param <T>  The type of the result expected by the completed event.
	 * @param type The type to subscribe to,
	 * @param m    The subscribing micro-service.
	 */
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		microServiceSubscriptions.get(m).add(type);
		// TODO: implement checks and exceptions (for example m was not registered)
	}

	/**
	 * Subscribes {@code m} to receive {@link Broadcast}s of type {@code type}.
	 * <p>
	 * @param type 	The type to subscribe to.
	 * @param m    	The subscribing micro-service.
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		microServiceSubscriptions.get(m).add(type);
		// TODO: implement checks and exceptions
    }

	/**
	 * Notifies the MessageBus that the event {@code e} is completed and its
	 * result was {@code result}.
	 * When this method is called, the message-bus will resolve the {@link Future}
	 * object associated with {@link Event} {@code e}.
	 * <p>
	 * @param <T>    The type of the result expected by the completed event.
	 * @param e      The completed event.
	 * @param result The resolved result of the completed event.
	 */
	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		eventFutureMap.get(e).resolve(result);
		// TODO: implement checks and exceptions
	}

	/**
	 * Adds the {@link Broadcast} {@code b} to the message queues of all the
	 * micro-services subscribed to {@code b.getClass()}.
	 * <p>
	 * @param b 	The message to added to the queues.
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		
	}

	/**
	 * Adds the {@link Event} {@code e} to the message queue of one of the
	 * micro-services subscribed to {@code e.getClass()} in a round-robin
	 * fashion. This method should be non-blocking.
	 * <p>
	 * @param <T>    	The type of the result expected by the event and its corresponding future object.
	 * @param e     	The event to add to the queue.
	 * @return {@link Future<T>} object to be resolved once the processing is complete,
	 * 	       null in case no micro-service has subscribed to {@code e.getClass()}.
	 */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> eventFuture = new Future<>(); // the future associated with the event
		eventFutureMap.put(e,eventFuture); // store the association of the future and the event
		/*
		TODO: implement the round-robin
		 */
        return eventFuture;
	}

	/**
	 * Allocates a message-queue for the {@link MicroService} {@code m}.
	 * <p>
	 * @param m the micro-service to create a queue for.
	 */
	@Override
	public void register(MicroService m) {
		LinkedList<Message> messagesQueue = new LinkedList<Message>();
		LinkedList<Class<? extends Message>> messageTypeQueue = new LinkedList<Class<? extends Message>>();
		microServiceMessages.put(m,messagesQueue);
		microServiceSubscriptions.put(m,messageTypeQueue);
	}

	/**
	 * Removes the message queue allocated to {@code m} via the call to
	 * {@link #register(bgu.spl.mics.MicroService)} and cleans all references
	 * related to {@code m} in this message-bus. If {@code m} was not
	 * registered, nothing should happen.
	 * <p>
	 * @param m the micro-service to unregister.
	 */
	@Override
	public void unregister(MicroService m) {
		microServiceMessages.remove(m);
		microServiceSubscriptions.remove(m);
	}

	/**
	 * Using this method, a <b>registered</b> micro-service can take message
	 * from its allocated queue.
	 * This method is blocking meaning that if no messages
	 * are available in the micro-service queue it
	 * should wait until a message becomes available.
	 * The method should throw the {@link IllegalStateException} in the case
	 * where {@code m} was never registered.
	 * <p>
	 * @param m The micro-service requesting to take a message from its message
	 *          queue.
	 * @return The next message in the {@code m}'s queue (blocking).
	 * @throws InterruptedException if interrupted while waiting for a message
	 *                              to became available.
	 */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		
		return null;
	}

	/**
	 * *Added by Omri*
	 * @param m the MicroService to check
	 * @return if MicroService is currently register to the MessageBus
	 */
	private boolean isRegistered(MicroService m){
		return microServiceMessages.containsKey(m);
	}
}
