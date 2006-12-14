/*
* class: Ice3Jmx
*
* Version $Id: Ice3Jmx.java,v 1.18 2006/11/07 19:35:21 patton Exp $
*
* Date: August 14 2003
*
* (c) 2003 IceCube Collaboration
*/

package icecube.icebucket.jmx;

import org.jboss.mx.modelmbean.XMBean;
import org.jboss.mx.remoting.MBeanServerLocator;
import org.jboss.remoting.network.NetworkInstance;
import org.jboss.remoting.network.NetworkRegistryFinder;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class contains static function that are used by IceCube MBeans.
 *
 * @author patton
 * @version $Id: Ice3Jmx.java,v 1.18 2006/11/07 19:35:21 patton Exp $
 */
public class Ice3Jmx
{
    // private static final member data

    /**
     * The name of the default domain just by the MBean servers holding IceCube
     * IceCube MBeans.
     */
    public static final String SERVER_DEFAULT_DOMAIN = "jboss";

    // private static member data

    // private instance member data

    // constructors

    /**
     * Create an instance of this class. Default constructor is declared, but
     * private, to stop accidental creation of an instance of the class.
     */
    private Ice3Jmx()
    {
    }

    // instance member method (alphabetic)

    // static member methods (alphabetic)

    /**
     * Returns The name of the domain to be used buy IceCube MBeans.
     *
     * @return The name of the domain to be used buy IceCube MBeans.
     */
    public static String getDomain()
    {
        return "icecube";
    }

    /**
     * Returns The MBeanServer for the containing JBoss App, <code>null</code>
     * otherwise.
     *
     * @return The MBeanServer for the containing JBoss App, <code>null</code>
     *         otherwise.
     */
    public static MBeanServer getMBeanServer()
    {
        final List servers = MBeanServerFactory.findMBeanServer(null);
        final Iterator iterator = servers.iterator();
        MBeanServer server = null;
        boolean serverFound = false;
        while (iterator.hasNext() && (!serverFound)) {
            server = (MBeanServer) iterator.next();
            if (server.getDefaultDomain().equals(SERVER_DEFAULT_DOMAIN)) {
                serverFound = true;
            }
        }

        if (serverFound) {
            return server;
        }

        return null;
    }

    /**
     * Returns List of all remote and local MBeanServerConnections.
     *
     * @return List of all remote and local MBeanServerConnections.
     */
    public static List getFullMBeanServerList()
    {
        final List serverList;
        // Start with list of local MBeanServers
        serverList = MBeanServerFactory.findMBeanServer(null);
        final List remoteServerList = new LinkedList();
        final Iterator serverIterator = serverList.listIterator();
        while (serverIterator.hasNext()) {
            final MBeanServer nextServer = (MBeanServer) serverIterator.next();
            // Find the local MBeanServerConnection that has the NetworkRegistry
            final ObjectName networkRegistry =
                    NetworkRegistryFinder.find(nextServer);
            if (null != networkRegistry) {
                try {
                    // Get the list of remote MBeanServerConnections
                    final Object servers =
                            nextServer.getAttribute(networkRegistry,
                                                    "Servers");
                    if (null != servers) {
                        final NetworkInstance[] remoteServers =
                                (NetworkInstance[]) servers;
                        for (int i = 0;
                             i < remoteServers.length;
                             i++) {
                            final MBeanServerLocator remoteMBeanServerLocator =
                                    new MBeanServerLocator(
                                            remoteServers[i].getIdentity());
                            remoteServerList
                                    .add(remoteMBeanServerLocator.getMBeanServer());
                        }
                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();
                } catch (InstanceNotFoundException e) {
                    e.printStackTrace();
                } catch (AttributeNotFoundException e) {
                    // I believe it requires a programming error to get here!
                    e.printStackTrace();
                } catch (MBeanException e) {
                    // I believe it requires a programming error to get here!
                    e.printStackTrace();
                } catch (ReflectionException e) {
                    // I believe it requires a programming error to get here!
                    e.printStackTrace();
                }
            }
        }
        // Add the Remote MBServerConnections to the list of Local MBeanServers
        serverList.addAll(remoteServerList);
        return serverList;
    }

    /**
     * Registers an object as an XBean using the icecube conventions on
     * locating the descriptor file.
     *
     * @param object the object to register.
     * @param objectName the name with which to register it.
     * @param server the server with which it should be registers
     * @throws NotCompliantMBeanException
     * @throws MBeanException
     * @throws InstanceAlreadyExistsException
     */
    public static void registerXMbean(Object object,
                                      ObjectName objectName,
                                      MBeanServer server)
            throws NotCompliantMBeanException,
                   MBeanException,
                   InstanceAlreadyExistsException
    {
        registerXMbean(object,
                       null,
                       objectName,
                       server);
    }

    /**
     * Registers an object as an XBean using the icecube conventions on
     * locating the descriptor file.
     *
     * @param object the object to register.
     * @param modifier the modifer to use when finding the descriptor file.
     * @param objectName the name with which to register it.
     * @param server the server with which it should be registers
     * @throws NotCompliantMBeanException
     * @throws MBeanException
     * @throws InstanceAlreadyExistsException
     */
    public static void registerXMbean(Object object,
                                      String modifier,
                                      ObjectName objectName,
                                      MBeanServer server)
            throws NotCompliantMBeanException,
                   MBeanException,
                   InstanceAlreadyExistsException
    {
        final Class clazz = object.getClass();
        final String extension;
        if ((null == modifier) ||
            "".equals(modifier)) {
            extension = "";
        } else {
            extension = '.' + modifier;
        }
        final String xmlFileName = "/XMBEAN-INF/" +
                                   clazz.getName() +
                                   extension +
                                   ".xml";
        final URL xmbeanXML = clazz.getResource(xmlFileName);
        if (null == xmbeanXML) {
            throw new NullPointerException("Could not find XML descriptor" +
                                           xmlFileName);
        }
        server.registerMBean(new XMBean(object,
                                        xmbeanXML),
                             objectName);
    }

    /**
     * Returns the first MBeanServerConnection which matches the specified
     * pattern.
     *
     * @param localServer the local MBeanServer from where to start the
     * search.
     * @param pattern the ObjectName pattern to use in the search.
     * @return the first MBeanServerConnection which matches the specified
     *         pattern.
     * @throws InstanceNotFoundException If not MBean matches the pattern.
     */
    public static MBeanServerConnection resolveMBean(MBeanServer localServer,
                                                     ObjectName pattern)
            throws InstanceNotFoundException
    {
        return resolveMBean(localServer,
                            pattern,
                            null);
    }

    /**
     * Returns the first MBeanServerConnection which matches the specified
     * pattern.
     *
     * @param localServer the local MBeanServer from where to start the
     * search.
     * @param pattern the ObjectName pattern to use in the search.
     * @param query Query to apply during search.
     * @return the first MBeanServerConnection which matches the specified
     *         pattern.
     * @throws InstanceNotFoundException If no MBean matches the pattern.
     */
    public static MBeanServerConnection resolveMBean(MBeanServer localServer,
                                                     ObjectName pattern,
                                                     QueryExp query)
            throws InstanceNotFoundException
    {
        final Iterator serverIterator;
        final List serverList = getFullMBeanServerList();

        // Now look through all available MBeanServerConnections for the
        // desired MBean. Only one is allowed by the IceCube MBean namespace
        // design, so stop at first one.
        serverIterator = serverList.listIterator();
        MBeanServerConnection beanServer = null;
        boolean beanFound = false;
        while (serverIterator.hasNext()) {
            beanServer = (MBeanServerConnection) serverIterator.next();
            try {
                final Set beanSet = beanServer.queryNames(pattern,
                                                          query);
                // This set seems to come back with a null entry if MBean is
                // not there, rather than an empty Set with no entries. Either
                // way, we've not found it.
                final Iterator beanIterator = beanSet.iterator();
                if (beanIterator.hasNext() && null != beanIterator.next()) {
                    beanFound = true;
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (beanFound) {
            return beanServer;
        } else {
            throw new InstanceNotFoundException();
        }
    }


    /**
     * Returns all instances of MBeans and their servers that match to
     * specified pattern.
     *
     * @param localServer the local MBeanServer from where to start the
     * search.
     * @param pattern the ObjectName pattern to use in the search.
     * @return a Map containing MBeans and their MBeanServerConnection
     *         instances.
     */
    public static Map resolveMBeans(MBeanServer localServer,
                                    ObjectName pattern)
    {
        return resolveMBeans(localServer,
                             pattern,
                             null);
    }

    /**
     * Returns all instances of MBeans and their servers that match to
     * specified pattern.
     *
     * @param localServer the local MBeanServer from where to start the
     * search.
     * @param pattern the ObjectName pattern to use in the search.
     * @param query Query to apply during search.
     * @return a Map containing MBeans and their MBeanServerConnection
     *         instances.
     */
    public static Map resolveMBeans(MBeanServer localServer,
                                    ObjectName pattern,
                                    QueryExp query)
    {
        final List serverList = getFullMBeanServerList();

        // Now look through all available MBeanServerConnections for the desired MBeans
        final Map result = new HashMap();
        final Iterator serverIterator = serverList.listIterator();
        while (serverIterator.hasNext()) {
            final MBeanServerConnection beanServer =
                    (MBeanServerConnection) serverIterator.next();
            Set beanSet = null;
            if (null != beanServer) {
                try {
                    beanSet = beanServer.queryNames(pattern,
                                                    query);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // This set seems to come back with a null entry if MBean is not there,
            // rather than an empty Set with no entries. Either way, we've not found it.
            if (null != beanSet) {
                final Iterator beanIterator = beanSet.iterator();
                while (beanIterator.hasNext()) {
                    final Object beanName = beanIterator.next();
                    if (null != beanName) {
                        result.put(beanName,
                                   beanServer);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Transforms the specified ObjectName by setting the value specified by
     * the key to the new specified value. The original name must already
     * include the specified key, otherwise and IllegalArgumentException is
     * thrown
     *
     * @param name the ObjectName to be specified.
     * @param key the key of the key-value pair to be changes.
     * @param newValue the new value for the key-value pair.
     * @return An ObjectName with the specifed change.
     * @throws IllegalArgumentException is the original name does not include
     * the specified key.
     */
    public static ObjectName transformName(ObjectName name,
                                           String key,
                                           String newValue)
    {
        if (null == name) {
            throw new IllegalArgumentException("The name to be transformed" +
                                               " must be specified.");
        }
        if (null == key) {
            throw new IllegalArgumentException("The key to be transformed" +
                                               " must be specified.");
        }
        if (null == newValue) {
            throw new IllegalArgumentException("The new value of the key" +
                                               " must be specified.");
        }

        final Map oldKeys = name.getKeyPropertyList();
        final String oldValue = (String) oldKeys.get(key);
        if (null == oldValue) {
            throw new IllegalArgumentException("key is not in the" +
                                               " original ObjectName");
        }
        if (oldValue.equals(newValue)) {
            return name;
        }

        final Hashtable newKeys = new Hashtable(oldKeys);
        newKeys.put(key,
                    newValue);
        try {
            return new ObjectName(name.getDomain(),
                                  newKeys);
        } catch (MalformedObjectNameException e) {
            // Should never be possible as original name was valid!
            e.printStackTrace();
        }
        return null;
    }
}
