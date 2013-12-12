package es.unavarra.distributedsystems.communication;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import es.unavarra.distributedsystems.common.Logger;
import es.unavarra.distributedsystems.common.Request;

public class Connector {
	private ArrayList<NetworkNode> broadCastGroup;
	private NetworkNode from;

	public Connector(NetworkNode myself) {
		this.setFrom(myself);
	}

	public void send(Request request, NetworkNode to) {
		if (to != null && request != null) {
			NetworkNode originalFrom = request.getFrom();
			try {
				request.setFrom(this.getFrom());
				Socket clientSocket = new Socket(to.getAddress(), to.getPort());
				OutputStream outputStream = clientSocket.getOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
				objectOutputStream.writeObject(request);
				outputStream.close();
				objectOutputStream.close();
				clientSocket.close();
			} catch (IOException e) {
				Logger.eLog("> Unable to reach: " + to);
				//e.printStackTrace();
			} finally {
				request.setFrom(originalFrom); //Restore request to its original data
			}
		}
	}

	public void bCast(Request request) {
		for (NetworkNode to : broadCastGroup) {
			send(request, to);
		}
	}

	public ArrayList<NetworkNode> getBroadCastGroup() {
		return broadCastGroup;
	}

	public void setBroadCastGroup(ArrayList<NetworkNode> broadCastGroup) {
		this.broadCastGroup = broadCastGroup;
	}

	public NetworkNode getFrom() {
		return from;
	}

	public void setFrom(NetworkNode from) {
		this.from = from;
	}
}
