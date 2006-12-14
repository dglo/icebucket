/*
 * class: ReadXml
 *
 * Version $Id: ReadXml.java,v 1.1 2005/09/22 11:19:51 patton Exp $
 *
 * Date: September 19 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.icebucket.jaxb.example;

import icecube.icebucket.jaxb.Gromit;
import icecube.icebucket.jaxb.WallabyType;
import icecube.icebucket.jaxb.Wallace;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This class ...does what?
 *
 * @author patton
 * @version $Id: ReadXml.java,v 1.1 2005/09/22 11:19:51 patton Exp $
 */
public class ReadXml
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    /**
     * Array to return is there are no Wallace entries.
     */
    private static final int[] NULL_WALLACE = new int[0];

    // private static member data

    // private instance member data

    /**
     * Collection of Strings from Gromit objects read.
     */
    private Collection gromitValues = new ArrayList(0);

    /**
     * int[] of Wallace values read.
     */
    private int[] wallaceValues;

    // constructors

    /**
     * Create an instance of this class. Default constructor is declared, but
     * private, to stop accidental creation of an instance of the class.
     */
    private ReadXml()
    {
    }

    // instance member method (alphabetic)

    public Collection getGromitValues()
    {
        return gromitValues;
    }

    public int[] getWallaceValues()
    {
        if (null == wallaceValues) {
            return NULL_WALLACE;
        }
        return wallaceValues;
    }

    public void readXml(URL url)
            throws JAXBException
    {
        JAXBContext context =
                JAXBContext.newInstance("icecube.icebucket.jaxb");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        WallabyType rootElement = (WallabyType) unmarshaller.unmarshal(url);
        List wallaby = rootElement.getGromitOrWallace();

        List wallaceObjects = new ArrayList(0);
        Iterator iterator = wallaby.iterator();
        while (iterator.hasNext()) {
            Object element = iterator.next();
            if (element instanceof Gromit) {
                Gromit gromit = (Gromit) element;
                gromitValues.add(gromit.getValue());

            } else if (element instanceof Wallace) {
                wallaceObjects.add(element);
            }
        }

        final int finished = wallaceObjects.size();
        if (0 != finished) {
            wallaceValues = new int[finished];
            iterator = wallaceObjects.iterator();
            for (int index = 0;
                 finished != index;
                 index++) {
                wallaceValues[index] =
                        ((Wallace) wallaceObjects.get(index)).getValue();
            }
        }
    }

    // static member methods (alphabetic)

    // Description of this object.
    // public String toString() {}

    // public static void main(String args[]) {}
}