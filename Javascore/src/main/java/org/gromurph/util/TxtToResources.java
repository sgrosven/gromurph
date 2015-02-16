//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: TxtToResources.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs through all java files in a directory's hierarchy and checks for
 * missing resources strings, creates an output file MissingResources.csv
 * of the missing links
 */
public class TxtToResources
{

    String fParentProp;
    String fResName;
    String fBaseDir;
    List<String> fLocales;
    List<List<String>> fCells;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public TxtToResources( String baseDir, String pkgName, String propName)
    {
        try
        {
            fParentProp = propName;
            fResName = pkgName + "." + propName;
            fBaseDir = baseDir;
            String filename = fParentProp + "_resources.txt";
            processTxtFiles( filename);
            writeResourceFiles();
        }
        catch (Exception e)
        {
            logger.error("Exception=" + e.toString());
            e.printStackTrace( System.err);
        }
    }

    public void processTxtFiles( String filename)
    {
        int row = 0;
        try
        {
            LineNumberReader fReader = new LineNumberReader( new BufferedReader( new FileReader( filename)));

            fLocales = readline( fReader);
            row++;

            fCells = new ArrayList< List< String>>( fLocales.size());
            for (int i = 0; i < fCells.size(); i++)
            {
                fCells.add( new ArrayList<String>( 10));
            }

            List<String> cellrow = readline( fReader);

            if (cellrow.size() != fCells.size())
            {
                logger.warn( "size mismatch at row " + row + ", expecting " + fCells.size() + " cells, found " + cellrow.size());
            }
            
            while ( cellrow != null)
            {
                for (int i = 0; i < Math.min( cellrow.size(), fCells.size()); i++)
                {
                    fCells.get(i).add( cellrow.get(i));
                }
                row++;
                cellrow = readline( fReader);
            }

            fReader.close();
        }
        catch (Exception e)
        {
            logger.warn("Exception in processTxtFiles, row " + row + ": " + e);
        }
    }

    static final String TAB = "\t";
    static final String QUOTE = "\"";

    /**
     * reads line from reader file and parses out tabs and quotes, returns
     * array of cells
     */
    public List<String> readline( LineNumberReader r) throws IOException
    {
        String line = r.readLine();
        if (line == null) return null; // end of file

        return Util.stringSplit( line, TAB, QUOTE);
    }

    public void writeResourceFiles()
    {
        int col = -1;
        int row = -1;;
        try
        {
        	List<String> keys = fCells.get(0);

            for (col = 1; col < fCells.size(); col++)
            {
                String filename;
                filename = fBaseDir + "/" + fParentProp + "_" + fLocales.get(col) + ".properties";

                PrintWriter writer = new PrintWriter( new FileWriter( filename));
                
                for ( row = 0; row < keys.size(); row++)
                {
                    String value = fCells.get(col).get(row);
                    if ( value.length() > 0)
                    {
                        writer.print( keys.get(row));
                        writer.print("=");
                        writer.println(value);
                    }
                }
                writer.close();
            }
        }
        catch (IOException e)
        {
            logger.error( "IOException at col=" + col + ", row=" + row + ":" + e);
        }
    }

    public static void main(String[] args)
    {
        if ( args.length < 3)
        {
            args = new String[3];
            args[0]="c:/javascore/src/org/gromurph/javascore";
            args[1]="org.gromurph.javascore";
            args[2]="JavaScore";
        }
        new TxtToResources( args[0], args[1], args[2]);
    }
}
/**
 * $Log: TxtToResources.java,v $
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
