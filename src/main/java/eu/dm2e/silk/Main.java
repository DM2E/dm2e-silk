package eu.dm2e.silk;

import eu.dm2e.silk.wsmanager.ManageService;
import eu.dm2e.ws.Config;

import java.io.IOException;


public class Main {


	public static void main(String[] args)
			throws IOException {

		if (null == Config.config) {
			System.err.println("No config was found. Create 'config.xml'.");
			System.exit(1);
		}

		// Grizzly 2 initialization
		ManageService.startServer();
		System.out.println(String.format(
				"SILK main services started (Data, Config, File). WADL at\n"
				+"\n"
				+"Hit enter to stop"));
        System.in.read();
		ManageService.stopAll();

	}
}
