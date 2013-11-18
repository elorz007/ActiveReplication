package es.unavarra.distributedsystems.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import es.unavarra.distributedsystems.common.Request;

public class MessageDeliverer implements Runnable {

	private class ResponseHandler implements Runnable {
		private final Request request;

		private ResponseHandler(Request request) {
			this.request = request;
		}

		@Override
		public void run() {
			receiver.receive(request, null); //TODO put from object
		}
	}

	private int port;
	private Receiver receiver;
	
	public MessageDeliverer(int port, Receiver receiver) {
		this.port = port;
		this.receiver = receiver;
	}

	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket connectionSocket = serverSocket.accept();
				ObjectInputStream objectInputStream = new ObjectInputStream(connectionSocket.getInputStream());
				try {
					Object objectFromClient = objectInputStream.readObject();
					if (objectFromClient instanceof Request) {
						Request request = (Request) objectFromClient;
						new Thread(new ResponseHandler(request));
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
