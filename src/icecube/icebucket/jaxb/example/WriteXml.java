/*
 * class: WriteXml
 *
 * Version $Id: WriteXml.java,v 1.1 2005/09/22 11:19:51 patton Exp $
 *
 * Date: September 19 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.icebucket.jaxb.example;

import icecube.icebucket.jaxb.Gromit;
import icecube.icebucket.jaxb.ObjectFactory;
import icecube.icebucket.jaxb.WallabyType;
import icecube.icebucket.jaxb.Wallace;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * This class ...does what?
 *
 * @version $Id: WriteXml.java,v 1.1 2005/09/22 11:19:51 patton Exp $
 * @author patton
 */
public class WriteXml
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    // private static member data

    // private instance member data

    /**
     * The ObjectFactory used to create elements of the data structure.
     */
    private ObjectFactory factory;

    /**
     * The root element for the data structure.
     */
    private WallabyType wallaby;

    // constructors

    /**
     * Create an instance of this class.
     */
    public WriteXml()
            throws JAXBException
    {
        factory = new ObjectFactory();
        wallaby = factory.createWallaby();
    }

    // instance member method (alphabetic)

    public void addGromitValue(String value)
            throws JAXBException
    {
        Gromit gromit = factory.createGromit(value);
        wallaby.getGromitOrWallace().add(gromit);
    }

    public void addWallaceValue(int value)
            throws JAXBException
    {
        Wallace wallace = factory.createWallace(value);
        wallaby.getGromitOrWallace().add(wallace);
    }

    public void writeXml(String filename)
            throws JAXBException,
                   IOException
    {
        Writer stream = new FileWriter(filename);

        JAXBContext context =
                JAXBContext.newInstance("icecube.icebucket.jaxb");
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output",
                               Boolean.TRUE);
        marshaller.marshal(wallaby,
                           stream);
    }


    // static member methods (alphabetic)

    // Description of this object.
    // public String toString() {}

    // public static void main(String args[]) {}
}