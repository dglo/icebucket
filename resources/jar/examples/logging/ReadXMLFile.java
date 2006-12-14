/**
 * a simple class to read a xml file and setup
 * appenders.
 */

package icecube.icebucket.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import java.io.*;
 
public class ReadXMLFile {

  final static Logger logger = Logger.getLogger(ReadXMLFile.class);

    public static void installLogger(File inFile){
	try{
	    DOMConfigurator.configure(inFile.toString());
	}catch ( NullPointerException ex) {
	    System.err.println(ex.getMessage());
	}		     
    }
  public static void main(String[] args) {

      String path = "/home/akbarm/BFD_workspace/icebucket/resources/jar/examples/logging/example_1.xml";

      File infile = new File(path);
      ReadXMLFile.installLogger(infile);
      logger.info("Read file: " + infile.toString());
      logger.info("Entering application.");
      logger.debug("Debugging");
      logger.info("Exiting application.");    
  }
}
