//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ResourcesToTxt.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs through all java files in a directory's hierarchy and checks for
 * missing resources strings, creates an output file MissingResources.csv
 * of the missing links
 */
public class ResourcesToTxt
{

    String fParentProp;
    String fResName;
    PrintWriter fWriter = null;
    List<ResourceBundle> fLocales = new ArrayList<ResourceBundle>(5);
    ResourceBundle fResources;

    public ResourcesToTxt( String baseDir, String pkgName, String propName)
    {
        try
        {
            fWriter = new PrintWriter( new FileWriter( propName + "_resources.txt"));
            fParentProp = propName;
            fResName = pkgName + "." + propName;
            fResources =  ResourceBundle.getBundle( fResName);
            findResourceFiles( baseDir);
            processResourceFiles();
            fWriter.close();
        }
        catch (Exception e)
        {
    		Logger l = LoggerFactory.getLogger(this.getClass());
			l.error( "Exception=" + e.toString(), e);
        }
    }

    public void findResourceFiles( String pathname) throws IOException
    {
        File f = new File( pathname);
        if ( !f.isDirectory())
        {
    		Logger l = LoggerFactory.getLogger(this.getClass());
			l.error( "Named directory is not a directory");
            return;
        }

        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            String thisName = files[i].getName();

            if (thisName.startsWith( fParentProp ) && thisName.endsWith( ".properties"))
            {
                int st = fParentProp.length();
                int end = thisName.length() - 11; // 11 length of '.properties'
                if (st == end) fLocales.add( ResourceBundle.getBundle( fResName, new Locale("en", "US")));
                else
                {
                    String l1 = thisName.substring( st+1, st+3);
                    String l2 = thisName.substring( st+4, st+6);
                    fLocales.add( ResourceBundle.getBundle( fResName, new Locale( l1, l2)));
                }
            }
        }
    }

    public void processResourceFiles()
    {
        ResourceBundle[] l = new ResourceBundle[ fLocales.size()];
        fWriter.print("Key\t");

        // write out header row
        for (int i = 0; i < fLocales.size(); i++)
        {
            l[i] = fLocales.get(i);
            String lname = l[i].getLocale().toString();
            if (lname.length() == 0) lname = Locale.getDefault().toString();
            fWriter.print( lname);
            fWriter.print( TAB);
        }
        fWriter.println();

        // writer out the values
        Enumeration iter = fResources.getKeys();
        while (iter.hasMoreElements())
        {
            String key = (String) iter.nextElement();

            fWriter.print( key);
            fWriter.print( TAB);

            for (int i = 0; i < l.length; i++)
            {
                fWriter.print( QUOTE);
                fWriter.print( Util.stringReplace( l[i].getString(key), "\n", "\\n"));
                fWriter.print( QUOTE);
                fWriter.print( TAB);
            }
            fWriter.println();
        }
    }

    static final String TAB = "\t";
    static final String QUOTE = "\"";

    public static void main(String[] args)
    {
        if ( args.length < 3)
        {
            args = new String[3];
            args[0]="c:/javascore/src/org/gromurph/javascore";
            args[1]="org.gromurph.javascore";
            args[2]="JavaScore";
        }
        new ResourcesToTxt( args[0], args[1], args[2]);
    }
}
/**
 * $Log: ResourcesToTxt.java,v $
 * Revision 1.4  2006/01/15 21:10:35  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.3.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.3  2004/04/10 20:49:39  sandyg
 * Copyright year update
 *
 * Revision 1.2  2003/01/04 17:53:05  sandyg
 * Prefix/suffix overhaul
 *
*/
