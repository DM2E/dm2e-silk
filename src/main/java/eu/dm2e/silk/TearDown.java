package eu.dm2e.silk;

import eu.dm2e.silk.wsmanager.ManageService;

import java.io.IOException;

public class TearDown {

	public static void main(String[] args)
			throws IOException {
        System.out.println("TEAR DOWN: Stopping all servers...");
        ManageService.stopAll();
        eu.dm2e.silk.wsmanager.ManageService.stopAll();

    }
}
