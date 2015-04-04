//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: UtilTestCase.java,v 1.5 2006/01/19 02:27:41 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

import java.awt.Component;
import java.awt.Container;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * extends Junit testcase to supply some gromurph specific checks
 */
public class TestUtils 
{
	
	public static double ERR_MARGIN = 0.00001;
	
   public static Test suite( Class c)
    {
        return new TestSuite( c);
    }

    public static void baseMain(Class c)
    {
        String[] testCases = { c.getName() };
        junit.textui.TestRunner.main( testCases);
    }

    /**
     * creates new baseobject using the default constructor
     * then runs the xml read/write test
     */
    public static boolean xmlEquals( BaseObject obj)
    {
        return xmlEquals( obj, null);
    }

    /**
     * creates new baseobject using the default constructor
     * then runs the xml read/write test
     */
    public static boolean xmlEquals( BaseObject obj, Object toproot)
    {
        try
        {
            BaseObject obj2 = (BaseObject) obj.getClass().newInstance();
            return xmlEquals( obj, obj2, toproot);
        }
        catch (Exception e)
        {
            return false;
        }
    }

	public static String toXml( BaseObject obj)
	{
		try
		{
			return new String(xmlObjectToByteArray( "Test", obj));
		}
		catch (IOException e)
		{
			return (e.toString());
		}
	}
	
	public static String toFromXml( BaseObject obj)
	{
		try
		{
			byte[] ba = xmlObjectToByteArray( "Test", obj);
			BaseObject obj2 = (BaseObject) obj.getClass().newInstance();
			xmlByteArrayToObject( obj2, ba, null);
			return new String( xmlObjectToByteArray( "Test", obj2));
		}
		catch (Exception e)
		{
			return (e.toString());
		}
	}
	
    /**
     * writes an obj to a test file in XML format, then reads it back
     * into a second object and compares the two for equality
     */
    public static boolean xmlEquals( BaseObject obj, BaseObject obj2, Object toproot)
    {
        try
        {
            if (obj == null || obj2 == null) return false;
            byte[] ba = null;
            if (!xmlToFile)
            {
                ba = xmlObjectToByteArray( "Test", obj);
                xmlByteArrayToObject( obj2, ba, toproot);
            }
            else
            {
            	StringWriter writer = new StringWriter();
            	
            	obj.xmlWriteToWriter( writer, "Test");
            	writer.close();
            	
            	StringReader reader = new StringReader( writer.toString());
            	obj2.xmlReadFromReader( reader, toproot);
            }
            if (obj.junitEquals( obj2))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            return false;
        }
    }

	public static boolean xmlToFile = true;

	public static void setXmlToFile( boolean t)
	{
		xmlToFile = t;
	}

    public static void xmlObjectToObject( BaseObject src, BaseObject dest) throws Exception
    {
        String tag = "OtoO";
        byte[] xmlbytes = null;
            xmlbytes = xmlObjectToByteArray( tag, src);
            
			// logger.info("xmlObjectoObject xml=");
			// logger.info( new String(xmlbytes));
            
            xmlByteArrayToObject( dest, xmlbytes, null);
    }

    public static boolean xmlByteArrayToObject( BaseObject obj, byte[] ba, Object toproot)
        throws IOException
    {
        try
        {
            obj.xmlReadFromReader( 
            	new InputStreamReader( new BufferedInputStream(	new ByteArrayInputStream( ba))),
            	toproot);
            return true;
        }
        catch( java.io.IOException e)
        {
            throw e;
        }
        catch( Exception saxEx2) // next encoding
        {
        }
        return false;
    }

    /**
     * writes to disk in xml format
     * @param tag the XML tag for the parent of the object
     * @param obj the object to be sent to a file
     *
     * @throws IOException if unable to open the file
     */
    public static byte[] xmlObjectToByteArray( String tag, BaseObject obj)
        throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	Writer w = new OutputStreamWriter( bos);
    	
        obj.xmlWriteToWriter( w, tag);

//        w.flush();
//        w.close();
        return bos.toByteArray();
    }

    /**
     * Quick wrapper interface for passing a "command" to run in a test
     * such as assertException
     */

    public interface Runner
    {
        public void run() throws Exception;
    }

    /**
     * Tests to ensure that a user specified command generates a
     * specific exception instance (or subclass thereof)
     *
     * @param exeClass the Class of the expected exception
     * @param exec The runner to be executed, but firing exec.run()
     *
     */
    public static void assertException( Class excClass, Runner exec)
    {
        StringBuffer failMsg = new StringBuffer();
        try
        {
            exec.run();

            failMsg.append( "Expected exception, ");
            failMsg.append( excClass.getName());
            failMsg.append( ", was not thrown");
            TestCase.fail(failMsg.toString());
        }
        catch (Exception exc)
        {
            failMsg.append( "UnExpected exception, ");
            failMsg.append( exc);
            failMsg.append( " thrown");
            if ( !(excClass.isInstance( exc))) TestCase.fail( failMsg.toString());
        }
    }

    /**
     * returns a map by component name (string) of the components found
     * in a Module (and its various sub-panels).
     */
    static Map findMemberComponents( BaseEditor m)
    {
        return m.findMemberComponents();
    }

    /**
     * recursive routine that builds a map of components in a container and
     * its sub-containers. The returned map contains a list for each
     * unique JComponent class found (the map's key is the Class) and
     * the element is a vector of compoents of that class
     */
    static Map<Class, List<JComponent>> buildComponentMap( Map<Class, List<JComponent>> parentMap, Container container)
    {
        Map<Class, List<JComponent>> localMap = (parentMap == null) ? new HashMap<Class, List<JComponent>>() : parentMap;

        if (container == null) return localMap;

        Component[] cArray = container.getComponents();
        for (int i = 0; i < cArray.length; i++)
        {
            if ( cArray[i] instanceof JComponent)
            {
                JComponent jc = (JComponent) cArray[i];
                List<JComponent> clist = localMap.get( jc.getClass());
                if (clist == null)
                {
                    clist = new ArrayList<JComponent>(5);
                    localMap.put( jc.getClass(), clist);
                }
                clist.add( jc);
                buildComponentMap( localMap, jc);
            }
        }
        return localMap;
    }

    /**
     * internal test/debug routine to display a map of components to the console
     */
    public static void showComponents(Map componentMap) {
    	showComponents(componentMap, null);
    }
    public static void showComponents(Map componentMap, Logger log)
    {
    	Logger logger = (log != null) ? log : LoggerFactory.getLogger( "TestUtils");
        Iterator t = componentMap.keySet().iterator();
        while (t.hasNext())
        {
            String name = (String) t.next();
            JComponent jc = (JComponent) componentMap.get(name);
            logger.info( "{}: {}", name, jc.getClass().getName());
        }
    }

    /**
     * internal test/debug routine to show the Component maps of
     * component classes and the size of the component.
     */
    public static void showMapSizes( Map componentMap) {
    	showMapSizes( componentMap, null);
    }
    public static void showMapSizes( Map componentMap, Logger log)
    {
    	Logger logger = (log != null) ? log : LoggerFactory.getLogger( "TestUtils");

        Iterator keys = componentMap.keySet().iterator();
        while (keys.hasNext())
        {
            Class k = (Class) keys.next();
            List l = (List) componentMap.get(k);
            logger.info( "{}: {}", k.getName(), l.size());
        }
    }

}

