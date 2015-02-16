//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ResourceKeyEditor.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility editor for reviewing and editing resource names
 */
public class ResourceKeyEditor extends JDialog
{
	File   fTopDir;
	SortedProperties fResourceList;
	String fResourceFile = "";
	String fBaseDir = null;

	static final String QUOTE = "\"";
	static final String COMMA = ",";

	JList fListResourceNames;
	JTextField fTextKey;
	JTextArea fTextValue;

	JLabel fTextResults;
	JButton fButtonQuick;
	JButton fButtonDetails;
	JButton fButtonReplace;

	StringBuffer fSearchResults;

	public ResourceKeyEditor( String baseDir, String searchDir, String propName)
	{
		fBaseDir = searchDir;
		try
		{
			fTopDir = Util.getFile( baseDir, searchDir);
			fResourceList = new SortedProperties();
			fResourceFile = baseDir + searchDir + "/" + propName;
			fResourceList.load( new FileInputStream( fResourceFile));
		}
		catch (Exception e)
		{
			Logger l = LoggerFactory.getLogger(this.getClass());
			l.error( "Exception=" + e.toString(), e);
		}

		this.setSize( 800, 600);
		this.setTitle( "Resource editor/browser: " + propName + ".properties");
		getContentPane().setLayout( new BorderLayout());

		// create panels
		// +--splitter--+---------------
		// | panelList  | +----panelEast-----
		// |            | | panelKeyValue
		// |            | +--------------------
		// |            | | panelSource
		// |            | +--------------------
		// +----------------------------------

		JPanel panelList = new JPanel( new BorderLayout());
		panelList.setBorder( BorderFactory.createTitledBorder( "Resource names:"));

		JPanel panelEast = new JPanel( new BorderLayout());

		JSplitPane splitter = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, panelList, panelEast);
		getContentPane().add( splitter, BorderLayout.CENTER);
		splitter.setDividerLocation( 200);

		// flesh out panelList
		fListResourceNames = new JList();
		updateResourceList();
		panelList.add( new JScrollPane(fListResourceNames), BorderLayout.CENTER);

		// flesh out panelEast
		JPanel panelKeyValue = new JPanel( new GridBagLayout());
		panelKeyValue.setBorder( BorderFactory.createTitledBorder( "Selected resource:"));
		panelEast.add( panelKeyValue, BorderLayout.NORTH);

		fTextKey = new JTextField();
		fTextValue = new JTextArea( 5, 40);
		gridbagAdd( panelKeyValue, new JLabel("Key:"),   0, 0);
		gridbagAdd( panelKeyValue, new JLabel("Value:"), 0, 1);
		gridbagAdd( panelKeyValue, fTextKey, 1, 0);
		gridbagAdd( panelKeyValue, new JScrollPane( fTextValue), 1, 1, 1, 1,
			GridBagConstraints.WEST, GridBagConstraints.BOTH, fInsets);

		JPanel buttons = new JPanel( new FlowLayout( FlowLayout.CENTER));
		gridbagAdd( panelKeyValue, buttons, 0, 2, 2, 1,
			GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0));
		fButtonQuick = new JButton("Lookup source files");
		fButtonDetails = new JButton( "Lookup source details");
		fButtonReplace = new JButton( "Replace");
		buttons.add( fButtonQuick);
		buttons.add( fButtonDetails);
		buttons.add( fButtonReplace);


		// flesh out panelSource
		JPanel panelSource = new JPanel( new BorderLayout());
		panelSource.setBorder( BorderFactory.createTitledBorder( "Source code occurrences:"));
		panelEast.add( panelSource, BorderLayout.CENTER);
		fTextResults = new JLabel();
		fTextResults.setVerticalAlignment( SwingConstants.TOP);
		panelSource.add( new JScrollPane( fTextResults), BorderLayout.CENTER);

		addListeners();
	}

	private void updateResourceList()
	{
		List<String> keys = new ArrayList<String>( 100);
		for( Object el : fResourceList.keySet())
		{
			keys.add( (String) el);
		}
		Object[] array = keys.toArray();
		Arrays.sort( array);

		fListResourceNames.setListData( array);
	}

	private void addListeners()
	{
		fListResourceNames.addListSelectionListener( new ListSelectionListener()
		{
			public void valueChanged( ListSelectionEvent e)
			{
				fListResourceNames_valueChanged();
			}
		});

		fButtonQuick.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e)
			{
				lookupKey( false);
			}
		});

		fButtonDetails.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e)
			{
				lookupKey( true);
			}
		});

		final JDialog parent = this;

		fButtonReplace.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e)
			{
				String newkey = fTextKey.getText();
				if (fResourceList.get( newkey) != null)
				{
					StringBuffer sb = new StringBuffer( 72);
					sb.append("Key, ");
					sb.append( newkey);
					sb.append(", already exists.");
					JOptionPane.showMessageDialog(
						null,
						sb.toString(),
						"Can't overwrite existing key",
						JOptionPane.ERROR_MESSAGE);
					return;
				}

				if ( !fFilesResourceName.equals( fListResourceNames.getSelectedValue()))
				{
					lookupKey( true);
				}

				int yesno = JOptionPane.showConfirmDialog(
					parent,
					"Are you sure you want make these\nsource code replacements?",
					"Are you sure",
					JOptionPane.YES_NO_OPTION );

				if (yesno == JOptionPane.YES_OPTION)
				{
					replaceKey();
				}
			}
		});
	}

	private void replaceKey()
	{
		// replace in resource list
		String oldkey = (String) fListResourceNames.getSelectedValue();
		String newkey = fTextKey.getText();
		String value = (String) fResourceList.get( oldkey);

		// loop through files and perform text replacement
		for (Iterator it = fFilesFound.iterator(); it.hasNext(); )
		{
			try
			{
				replaceAll( (File) it.next(), oldkey, newkey);
			}
			catch (Exception e)
			{
				Logger l = LoggerFactory.getLogger(this.getClass());
				l.error( "Exception=" + e.toString(), e);
			}
		}

		fResourceList.remove( oldkey);
		fResourceList.put( newkey, value);

		updateResourceList();

		// reset the selected link
		fListResourceNames.setSelectedIndex( -1);

		// save the resource file
		try
		{
			fResourceList.store( new FileOutputStream( fResourceFile), fResourceFile);
		}
		catch (Exception e)
		{}
	}

	private void replaceAll( File f, String resName, String newkey)  throws IOException
	{
		String fileName = f.getPath();
		String tmpFileName = f.getPath() + "~";

		File inFile = new File( fileName);
		BufferedReader rdr = new BufferedReader( new FileReader( inFile), 2048);

		// establish output file
		File outFile = new File( tmpFileName);
		FileWriter outWriter = new FileWriter( tmpFileName, false);

		StreamTokenizer stream = new StreamTokenizer( rdr);
		stream.eolIsSignificant(true);

		// make space and tabs be ordinary (dont skip over them)
		stream.ordinaryChar( ' ');
		stream.ordinaryChar( '\t');
		stream.ordinaryChar( '/');
		stream.ordinaryChar( '.');
		stream.ordinaryChars( '0', '9');
		stream.ordinaryChar( '"');
		stream.ordinaryChar( '-');
		stream.ordinaryChar( '_');
		stream.ordinaryChar( '\'');
		stream.slashSlashComments( false);
		stream.slashStarComments( false);

		boolean inResString = false;

		String last1 = "";
		while (stream.nextToken() != StreamTokenizer.TT_EOF)
		{
			if (stream.ttype != StreamTokenizer.TT_WORD)
			{
				outWriter.write( stream.ttype);
				if (stream.ttype == ')') inResString = false;
			}
			else
			{
				if ( inResString && stream.sval.equals( resName))
				{
					outWriter.write( newkey);
					inResString = false;
				}
				else
				{
					outWriter.write( stream.sval);

					if (last1.equals( "res") && stream.sval.equals( "getString")) inResString = true;
					last1 = stream.sval;
				}
			}
		}

		outWriter.flush();
		outWriter.close();
		rdr.close();

		inFile.delete();
		outFile.renameTo( inFile);
	}

	private void fListResourceNames_valueChanged()
	{
		String key = (String) fListResourceNames.getSelectedValue();
		if (key != null)
		{
			fTextKey.setText( key);
			fTextValue.setText( (String) fResourceList.get( key));
		}
	}

	List<File> fFilesFound = new ArrayList<File>(5);
	String fFilesResourceName = "";

	private void lookupKey( boolean details)
	{
		String key = (String) fListResourceNames.getSelectedValue();

		fSearchResults = new StringBuffer();
		fSearchResults.append( "<html>");
		try
		{
			fFilesFound.clear();
			fFilesResourceName = key;
			processFile( fTopDir, key, details);
		}
		catch (Exception e)
		{
			try
			{
				fSearchResults.append("<p><pre>Exception occurred:");
				fSearchResults.append( e.toString());
				fSearchResults.append( "\n");

				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter( sw);
				e.printStackTrace( pw);
				pw.flush();
				pw.close();
				sw.flush();
				sw.close();
				fSearchResults.append( sw);
				fSearchResults.append( "</pre></p>");
			}
			catch (IOException x) {}
		}

		fSearchResults.append( "</html>");
		fTextResults.setText( fSearchResults.toString());
	}

	private void processFile( File f, String resName, boolean details) throws IOException
	{
		if ( f.isDirectory())
		{
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				processFile( files[i], resName, details);
			}
		}
		else
		{
			if (f.getName().endsWith(".java")) findResources( f, resName, details);
		}
	}

	int NEWLINE = '\n';

	private void findResources( File f, String resName, boolean details) throws IOException
	{
		LineNumberReader rdr = new LineNumberReader( new FileReader( f), 2048);
		StreamTokenizer stream = new StreamTokenizer( rdr);
		stream.eolIsSignificant(true);

		StringBuffer thisline = new StringBuffer( 72);
		boolean firstOccurence = true;
		boolean inResString = false;

		while (stream.nextToken() != StreamTokenizer.TT_EOF)
		{
			if (stream.ttype == StreamTokenizer.TT_EOL)
			{
				rdr.mark( 512);
			}
			else if (stream.ttype == ')')
			{
				inResString = false;
			}

			if (stream.sval != null && stream.sval.equals( "res.getString"))
			{
				inResString = true;
			}
			else if ( inResString && stream.sval != null && stream.sval.equals( resName))
			{
				// found an occurence
				if (firstOccurence)
				{
					fFilesFound.add( f);
					fSearchResults.append( "<b>");
					fSearchResults.append( f.getPath());
					fSearchResults.append( "</b><br>");
					firstOccurence = false;
				}

				if (!details)
				{
					rdr.close();
					return;
				}
				else
				{
					thisline = new StringBuffer( 72);
					thisline.append( "&nbsp;&nbsp;&nbsp;");
					thisline.append( rdr.getLineNumber()+1);
					thisline.append( ": ");

					rdr.reset();
					String line = rdr.readLine();
					thisline.append( line);

					thisline.append( "<br>");
					fSearchResults.append( thisline.toString());
				}
			}
		}
		rdr.close();
	}

	/**
	 * a supporting method for children with GridBagLayouts.  Adds component
	 * into specified x,y location with user specified width, height of 1, and specified
	 * anchor and fill
	**/
	protected void gridbagAdd( JComponent target, JComponent newComp, int x, int y, int w, int h)
	{
		gridbagAdd( target, newComp, x,y, w, h,
			GridBagConstraints.NORTHWEST,
			GridBagConstraints.HORIZONTAL, fInsets);
	}

	/**
	 * a supporting method for children with GridBagLayouts.  Adds component
	 * into specified x,y location with user specified width, height of 1, and specified
	 * anchor and fill
	**/
	protected void gridbagAdd( JComponent target, JComponent newComp, int x, int y)
	{
		gridbagAdd( target, newComp, x,y, 1, 1,
			GridBagConstraints.NORTHWEST,
			GridBagConstraints.HORIZONTAL, fInsets);
	}

	/**
	 * a supporting method for children with GridBagLayouts.  Adds component
	 * into specified x,y location with user specified width, height of 1, and specified
	 * anchor and fill
	**/
	protected void gridbagAdd( JComponent target, JComponent newComp, int x, int y, int width,
		int anchor, int fill)
	{
		gridbagAdd( target, newComp, x,y, width, 1, anchor, fill, fInsets);
	}

	private GridBagConstraints gbc = new GridBagConstraints();

	/**
	 * a supporting method for children with GridBagLayouts.  Adds component
	 * into specified x,y location with user specified width, height of 1, and specified
	 * anchor and fill
	**/
	protected void gridbagAdd( JComponent target, JComponent newComp, int x, int y, int wh,
		int ht, int anchor, int fill, Insets ins)
	{
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = wh;
		gbc.gridheight = ht;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = anchor;
		gbc.fill = fill;
		gbc.insets = ins;
		((GridBagLayout) target.getLayout()).setConstraints( newComp, gbc);
		target.add( newComp);
	}

	private Insets fInsets = new Insets(5,5,5,5);

	public static void main(String[] args)
	{
		ResourceKeyEditor editor = new ResourceKeyEditor( "/javascore/src/", "org/gromurph/javascore", "JavaScore.properties");
		editor.addWindowListener( new WindowAdapter()
		{
			@Override public void windowClosing(WindowEvent e) { System.exit(0);}
		});
		editor.setVisible( true);
	}

	public class SortedProperties extends Properties
	{
		/**
		 * oversides parent to apply Set skeys and ordered writing out
		 */
		@Override public synchronized void store(OutputStream out, String header)
			throws IOException
		{
			BufferedWriter awriter = new BufferedWriter( new OutputStreamWriter( out, "8859_1"));

			if ( header != null) writeln( awriter, "#" + header);
			writeln( awriter, "#" + new Date().toString());

			Set<Object> skeys = new TreeSet<Object>( keySet());

			for ( Iterator iter = skeys.iterator(); iter.hasNext();)
			{
				String key = (String) iter.next();
				String val = (String) get( key);
				key = saveConvert( key, true);

				/* No need to escape embedded and trailing spaces for value, hence
				 * pass false to flag.
				 */
				val = saveConvert( val, false);
				writeln( awriter, key + "=" + val);
			}
			awriter.flush();
		}


		/*
		 * cut & paste from private access parent Properties
		 */
		private void writeln(BufferedWriter bw, String s) throws IOException
		{
			bw.write(s);
			bw.newLine();
		}


		/*
		 * cut & paste from private access parent Properties
		 */
		private String saveConvert(String theString, boolean escapeSpace) {
			int len = theString.length();
			StringBuffer outBuffer = new StringBuffer(len*2);

			for(int x=0; x<len; x++) {
				char aChar = theString.charAt(x);
				switch(aChar) {
			case ' ':
				if (x == 0 || escapeSpace)
				outBuffer.append('\\');

				outBuffer.append(' ');
				break;
					case '\\':outBuffer.append('\\'); outBuffer.append('\\');
							  break;
					case '\t':outBuffer.append('\\'); outBuffer.append('t');
							  break;
					case '\n':outBuffer.append('\\'); outBuffer.append('n');
							  break;
					case '\r':outBuffer.append('\\'); outBuffer.append('r');
							  break;
					case '\f':outBuffer.append('\\'); outBuffer.append('f');
							  break;
					default:
						if ((aChar < 0x0020) || (aChar > 0x007e)) {
							outBuffer.append('\\');
							outBuffer.append('u');
							outBuffer.append(toHex((aChar >> 12) & 0xF));
							outBuffer.append(toHex((aChar >>  8) & 0xF));
							outBuffer.append(toHex((aChar >>  4) & 0xF));
							outBuffer.append(toHex( aChar        & 0xF));
						} else {
							if (specialSaveChars.indexOf(aChar) != -1)
								outBuffer.append('\\');
							outBuffer.append(aChar);
						}
				}
			}
			return outBuffer.toString();
		}

		/**
		 * Convert a nibble to a hex character
		 * @param	nibble	the nibble to convert.
		 */
		private char toHex(int nibble) {
		return hexDigit[(nibble & 0xF)];
		}

		/** A table of hex digits */
		private final char[] hexDigit = {
		'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
		};

		private final String specialSaveChars = "=: \t\r\n\f#!";

	}
}
/**
 * $Log: ResourceKeyEditor.java,v $
 * Revision 1.4  2006/01/15 21:10:35  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.7.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.7  2005/05/26 01:45:43  sandyg
 * fixing resource access/lookup problems
 *
 * Revision 1.6  2004/04/10 20:49:39  sandyg
 * Copyright year update
 *
 * Revision 1.5  2003/05/07 01:17:05  sandyg
 * removed unneeded method parameters
 *
 * Revision 1.4  2003/04/27 21:35:33  sandyg
 * more cleanup of unused variables... ALL unit tests now working
 *
 * Revision 1.3  2003/04/27 21:03:30  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.2  2003/01/04 17:53:05  sandyg
 * Prefix/suffix overhaul
 *
*/
