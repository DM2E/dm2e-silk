package eu.dm2e.silk;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetUp {


	static Logger log = LoggerFactory.getLogger(SetUp.class);


	public static void main(String[] args)
			throws IOException {
		log.trace("SetUp happens in the tests now.");
	}
}
