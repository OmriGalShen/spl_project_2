package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;

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
	private HashMap<MicroService, LinkedList<Message>> messagesMap;
	private HashMap<MicroService,LinkedList<Class<? extends Message>>> subscriptionMap;
	private HashMap<Event,Future> eventFutureMap;
	private HashMap<Class<? extends Message>, LinkedList<MicroService>> eventReceiveQueues;

	/**
	 * Private constructor
	 * Added*
	 */
	private MessageBusImpl() { //Singleton pattern
		messagesMap = new HashMap<>();
		subscriptionMap = new HashMap<>();
		eventFutureMap = new HashMap<>();
		eventReceiveQueues = new HashMap<>();
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
		if(isRegistered(m)) {
			if (subscriptionMap.get(m).contains(type))
				return; //MicroService was already subscribed
			subscriptionMap.get(m).add(type); // add subscription
			if (eventReceiveQueues.containsKey(type)) { // add to event map
				eventReceiveQueues.get(type).add(m);
			}
			else{ // new type of event
				// create new queue of microservices subscribed to this event type
				eventReceiveQueues.put(type,new LinkedList<>());
				eventReceiveQueues.get(type).add(m); // add the MicroService to the queue
			}
		}
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
		if(isRegistered(m)) {
			if (subscriptionMap.get(m).contains(type))
				return;//MicroService was already subscribed
			subscriptionMap.get(m).add(type);
		}
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
		subscriptionMap.forEach((microService,subscription)-> {
			//check if microservice is subscribe this type of broadcast
			if(subscription.contains(b.getClass()))
				messagesMap.get(microService).add(b); // add b to microservice message queue
		});
		// notify microservices waiting for messages to check again
//		notifyAll();
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
		LinkedList<MicroService> receivingQueue = eventReceiveQueues.get(e.getClass());
		MicroService receivingMicro = receivingQueue.remove(); // remove the first micro in receiving queue
		receivingQueue.add(receivingMicro); // add to the back of the receiving queue
		messagesMap.get(receivingMicro).add(e); // add message to micro
		// notify microservices waiting for messages to check again
//		notifyAll();
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
		messagesMap.put(m,messagesQueue);
		subscriptionMap.put(m,messageTypeQueue);
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
		messagesMap.remove(m);
		subscriptionMap.remove(m);
		eventReceiveQueues.forEach((eventType,receiveQueues)->{
			receiveQueues.remove(m);
		});
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
		// checks MicroService is registered to this MessageBus
		if(!isRegistered(m)) throw new IllegalStateException();
		LinkedList<Message> messageQueue = messagesMap.get(m); // get MicroService messageQueue
		while(messageQueue.isEmpty()){
//			wait();
			Thread.sleep(100); // TODO: this is temporary!!
			if(Thread.currentThread().isInterrupted()) {
				throw new InterruptedException("interrupted while waiting for a message\n" +
						"\t *                              to became available.");
			}
		}
		//TODO: synchronize?  who should notify?

		// remove message from message queue and return it
		return messageQueue.remove();
	}

	/**
	 * *Added by Omri*
	 * @param m the MicroService to check
	 * @return if MicroService is currently register to the MessageBus
	 */
	private boolean isRegistered(MicroService m){
		return messagesMap.containsKey(m);
	}
}
