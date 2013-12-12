package es.unavarra.distributedsystems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import es.unavarra.distributedsystems.communication.Connector;
import es.unavarra.distributedsystems.communication.MessageDeliverer;
import es.unavarra.distributedsystems.communication.NetworkNode;
import es.unavarra.distributedsystems.communication.Receiver;
import es.unavarra.distributedsystems.configuration.ConfigurationLoader;
import es.unavarra.distributedsystems.configuration.NodeListMapper;
import es.unavarra.distributedsystems.middleware.ARH;
import es.unavarra.distributedsystems.middleware.DSS;

public class ARHMain {

	public static void main(String[] args) {
		try {
			String settingsFile = null;
			if (args.length > 0) {
				settingsFile = args[0];
	        }
			Properties properties = new ConfigurationLoader().loadOrUseDefault(settingsFile);
			String arhAddress = properties.getProperty("ARH_ADDRESS");
			int numberOfARH = Integer.valueOf(properties.getProperty("ARH_NUMBER"));
			int arhBasePort = Integer.valueOf(properties.getProperty("ARH_PORT_START"));
			int sequencerBasePort = Integer.valueOf(properties.getProperty("ARH_SEQUENCER_PORT_START"));
			String replicaNodeList = properties.getProperty("ARH_FO_LIST");
			ArrayList<NetworkNode> replicas = new NodeListMapper().map(replicaNodeList);
			String sequencerNodeList = properties.getProperty("ARH_SEQUENCER_LIST");
			ArrayList<NetworkNode> sequencers = new NodeListMapper().map(sequencerNodeList);
			spawnARH(numberOfARH, arhAddress, arhBasePort, sequencerBasePort, sequencers, replicas);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void spawnARH(int numberOfARH, String address, int basePort, int baseSequencerPort, ArrayList<NetworkNode> sequencerNodes, ArrayList<NetworkNode> replicas) {
		ArrayList<Connector> sequencerConnectors = new ArrayList<Connector>(numberOfARH);
		ArrayList<Thread> threads = new ArrayList<Thread>(numberOfARH);
		for (int i = 0; i < numberOfARH; i++) {
			NetworkNode arhNode = new NetworkNode(address, basePort + i);
			NetworkNode sequencerNode = new NetworkNode(address, baseSequencerPort + i);
			Connector arhConnector = new Connector(arhNode);
			Connector sequencerConnector = new Connector(sequencerNode);
			sequencerConnectors.add(sequencerConnector);
			DSS sequencer = new DSS(i, sequencerConnector);
			sequencerNodes.add(sequencerNode);
			Receiver arh = new ARH(i, arhConnector, sequencer, replicas);
			MessageDeliverer arhMessageDeliverer = new MessageDeliverer(arhNode, arh);
			threads.add(new Thread(arhMessageDeliverer));
			MessageDeliverer sequencerMessageDeliverer = new MessageDeliverer(sequencerNode, sequencer);
			threads.add(new Thread(sequencerMessageDeliverer));
		}
		for (Connector c : sequencerConnectors) {
			c.setBroadCastGroup(sequencerNodes);
		}
		// Run all threads when everything is correctly set up
		for (Thread thread : threads) {
			thread.start();
		}
	}
}
