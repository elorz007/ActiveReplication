package es.unavarra.distributedsystems;

import java.io.IOException;
import java.util.Properties;

import es.unavarra.distributedsystems.communication.Connector;
import es.unavarra.distributedsystems.communication.MessageDeliverer;
import es.unavarra.distributedsystems.communication.NetworkNode;
import es.unavarra.distributedsystems.configuration.ConfigurationLoader;
import es.unavarra.distributedsystems.end.FO;

public class FOMain {

	public static void main(String[] args) {
		try {
			String settingsFile = null;
			if (args.length > 0) {
				settingsFile = args[0];
	        }
			Properties properties = new ConfigurationLoader().loadOrUseDefault(settingsFile);
			String replicasAddress = properties.getProperty("FO_ADDRESS");
			int numberOfReplicas = Integer.valueOf(properties.getProperty("FO_NUMBER"));
			int replicasBasePort = Integer.valueOf(properties.getProperty("FO_PORT_START"));
			spawnReplicas(numberOfReplicas, replicasAddress, replicasBasePort);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void spawnReplicas(int numberOfReplicas, String address, int basePort) {
		for (int i = 0; i < numberOfReplicas; i++) {
			NetworkNode myself = new NetworkNode(address, basePort + i);
			Connector connector = new Connector(myself);
			FO fo = new FO(i, connector);
			MessageDeliverer messageDeliverer = new MessageDeliverer(myself, fo);
			new Thread(messageDeliverer).start();
		}
	}

}
