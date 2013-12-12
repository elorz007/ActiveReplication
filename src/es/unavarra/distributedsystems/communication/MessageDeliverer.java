package es.unavarra.distributedsystems.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import es.unavarra.distributedsystems.common.Logger;
import es.unavarra.distributedsystems.common.Request;

public class MessageDeliverer implements Runnable {

	private class ResponseHandler implements Runnable {
		private final Request request;

		private ResponseHandler(Request request) {
			this.request = request;
		}

		@Override
		public void run() {
			receiver.receive(request);
		}
	}

	private Receiver receiver;
	private NetworkNode myself;
	
	public MessageDeliverer(NetworkNode myself, Receiver receiver) {
		this.myself = myself;
		this.receiver = receiver;
	}

	@Override
	public void run() {
		try {
			Logger.log("[" + this.receiver.getClass().getSimpleName() + "] Creating server socket on " + this.myself);
			ServerSocket serverSocket = new ServerSocket(this.myself.getPort(), 0, InetAddress.getByName(this.myself.getAddress()));
			while (true) {
				Socket connectionSocket = serverSocket.accept();
				InputStream inputStream = connectionSocket.getInputStream();
				ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
				try {
					Object objectFromClient = objectInputStream.readObject();
					inputStream.close();
					objectInputStream.close();
					connectionSocket.close();
					if (objectFromClient instanceof Request) {
						Request request = (Request) objectFromClient;
						new Thread(new ResponseHandler(request)).start();
					}
					else {
						Logger.eLog("Received something that was not understood");
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
