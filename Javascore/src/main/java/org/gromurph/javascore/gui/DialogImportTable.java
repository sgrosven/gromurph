//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DialogImportTable.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.gromurph.javascore.*;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Displays panel containing a JTable with a row for each entry in regatta
 *  and column for each race, and column for series total.
**/
public abstract class DialogImportTable extends JDialog 
{
    /**
     * the list of possible field names, a SKIP field will be added to this list
     */
    abstract void initFieldNames();
    
    protected Logger logger = LoggerFactory.getLogger( this.getClass());

	protected Regatta getRegatta() {
		return JavaScoreProperties.getRegatta();
	}

    /**
     * parses and converts an Arraylist of Arraylist of strings into desired form
     */
    abstract void setValue( int datarow, int fieldindex, String cell);

    abstract String getDirections();

    protected static ResourceBundle res = JavaScoreProperties.getResources();
    protected static ResourceBundle resUtil = org.gromurph.util.Util.getResources();

    protected String[] fFieldNames;

    private static String SKIP = res.getString("ImportLabelSkip");
    private static String COL0_NAME = res.getString("ImportLabelColZero");

    List<List<String>>        fPastedRows;
    ImportTableModel fTableModel;
    private WarningList fWarnings = new WarningList();

	JTable fTableImport;
    
	protected Action fActionCancel;
	protected Action fActionOk;
	protected Action fActionPaste;
	
	JComboBox[] fComboBoxes;
	JLabel[] fLabels;
	JPanel   fPanelSubs;

	public class ActionPaste extends AbstractAction
	{
		public ActionPaste()
		{
			super();
			putValue( Action.NAME, 							res.getString("ImportButtonPaste"));
			putValue( Action.SHORT_DESCRIPTION,	res.getString("ImportButtonPaste"));
			putValue( Action.LONG_DESCRIPTION,	res.getString("ImportButtonPasteToolTip"));
			putValue( Action.MNEMONIC_KEY,  			new Integer( res.getString("ImportButtonPaste").charAt(0)));
		}

		public void actionPerformed(ActionEvent evt)
		{
			fPastedRows = new ArrayList<List<String>>();

			try
			{
				parseClipboardToArray();
			}
			catch (Exception e)
			{
				logger.error( res.getString( "ImportErrorMessage") + e);
				// replace this with optionbox askin user to do a copy to clipboard
			}
			TableColumn t = fTableImport.getColumn( COL0_NAME);

			String[] fields = new String[ fFieldNames.length+1];
			fields[0] = SKIP;
			for ( int i = 0; i < fFieldNames.length; i++) fields[i+1] = fFieldNames[i];

			JComboBox jc = new JComboBox( fields);
			if (t != null) t.setCellEditor(
				new DefaultCellEditor( jc));
			t.setPreferredWidth( 125);
		}				
	}

	public class ActionOk extends AbstractAction
	{
		public ActionOk()
		{
			super();
			putValue( Action.NAME, 							res.getString("ImportButtonExit"));
			putValue( Action.SHORT_DESCRIPTION,	res.getString("ImportButtonExit"));
			putValue( Action.LONG_DESCRIPTION,	res.getString("ImportButtonExitToolTip"));
			putValue( Action.MNEMONIC_KEY,  			new Integer( res.getString("ImportButtonExitMnemonic").charAt(0)));
		}

		public void actionPerformed(ActionEvent evt)
		{
			convertTableToRegatta();
			setVisible(false);
			JavaScore.backgroundSave();
			JavaScore.updateMainTitle();
			closeWindow();
		}				
	}

	public class ActionCancel extends AbstractAction
	{
		public ActionCancel()
		{
			super();
			putValue( Action.NAME, 							res.getString("ImportButtonCancel"));
			putValue( Action.SHORT_DESCRIPTION,	res.getString("ImportButtonCancel"));
			putValue( Action.LONG_DESCRIPTION,	res.getString("ImportButtonCancelToolTip"));
			putValue( Action.MNEMONIC_KEY,  			new Integer( res.getString("ImportButtonCancelMnemonic").charAt(0)));
		}

		public void actionPerformed(ActionEvent evt)
		{
			closeWindow();
		}				
	}

    public DialogImportTable( JFrame parent)
    {
        super( parent, true);
        
        setTitle(res.getString("ImportTitle"));
        getContentPane().setLayout(new BorderLayout(0,0));

        JPanel fPanelSouth = new JPanel(new FlowLayout());
        getContentPane().add( fPanelSouth, BorderLayout.SOUTH);

        fActionPaste = new ActionPaste();
		JButton button = new JButton( fActionPaste);  
        HelpManager.getInstance().registerHelpTopic( button, "import.fButtonPaste");
        fPanelSouth.add( button);

		fActionCancel = new ActionCancel();
		button = new JButton( fActionCancel);
       HelpManager.getInstance().registerHelpTopic(button, "import.fButtonCancel");
        fPanelSouth.add(button);

		fActionOk = new ActionOk(); 
		button = new JButton( fActionOk); 
        HelpManager.getInstance().registerHelpTopic( button, "import.fButtonExit");
        fPanelSouth.add( button);

        JPanel panelCenter = new JPanel( new BorderLayout());

        fTableModel = new ImportTableModel();

        fTableImport = new JTable( fTableModel);
        fTableImport.setToolTipText("ImportTableToolTip");
        HelpManager.getInstance().registerHelpTopic(fTableImport, "import.fTableImport");
        fTableImport.setAutoResizeMode( JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        getContentPane().add( panelCenter, BorderLayout.CENTER);

        panelCenter.add( new JScrollPane( fTableImport), BorderLayout.CENTER);

        JPanel panelSouth = new JPanel( new BorderLayout());
        panelCenter.add( panelSouth, BorderLayout.SOUTH);

        fPanelSubs = new JPanel();
        panelSouth.add( fPanelSubs, BorderLayout.CENTER);

        JTextPane fTextDirections = new JTextPane();
        fTextDirections.setText( getDirections());
        fTextDirections.setBackground( panelCenter.getBackground());
        Font f = new JTextField().getFont();
        f = new Font( f.getFamily(), Font.PLAIN, f.getSize()-1);
        fTextDirections.setFont( f);
        panelSouth.add( fTextDirections, BorderLayout.SOUTH);

        // add listeners
        addWindowListener( new LocalWindowAdapator());

        setSize( 500, 450);

        initFieldNames();
    }

    protected void addWarning( String msg)
    {
        fWarnings.add( msg);
    }

    /**
     * does the real work of importing the data, likely to be extended in subclasses
     */
    public void convertTableToRegatta( )
    {
        convertTableToRegatta( fTableModel.getFields(), fPastedRows);
    }

    public void convertTableToRegatta( List fields, List rows)
    {
        fWarnings.clear();
        for (int row = 0; row < rows.size(); row++)
        {
            List rowList = (List) rows.get(row);

            for (int f = 0; f < fFieldNames.length; f++)
            {
                int col = fields.indexOf( fFieldNames[f]);
                if (col >= 0)
                {
                    String cell = (String) rowList.get( col);
                    setValue( row, f, cell);
                }
            }
        } // row loop

        fWarnings.showPopup( this);
        JavaScoreProperties.getRegatta().scoreRegatta();
        JavaScore.backgroundSave();
    }

    private class ImportTableModel extends AbstractTableModel
    {
        List<List<String>> array;
        ArrayList<String> fields = new ArrayList<String>();
        int colCount;

        public ArrayList getFields()
        {
            return fields;
        }

        public ImportTableModel()
        {
            array = new ArrayList<List<String>>();
            colCount = 0;
        }

        public void setArray( List<List<String>> inbound)
        {
            array = inbound;
            colCount = 0;
            for (int r = 0; r < array.size(); r++)
            {
                colCount = Math.max( colCount, ( (ArrayList) array.get(r)).size());
            }

            // add new fields if necessary
            for (int r = 0; r < colCount; r++)
            {
                if (fields.size() <= r) fields.add( SKIP );
            }

            // kill excess entries in fields array
            for (int f = colCount; f < fields.size(); f++) fields.remove(f);

            fireTableStructureChanged();
       }

        @Override public Class<?> getColumnClass(int columnIndex) { return String.class; }
        public int getColumnCount()
        {
            int i = Math.min( array.size()+1, 4);
            return i;
        }
        public int getRowCount() { return colCount; }

        public Object getValueAt( int row, int column)
        {
            Object obj = null;
            if (column == 0)
            {
                obj = fields.get( row);
            }
            else
            {
                try
                {
                    obj = ( (ArrayList) array.get( column-1)).get(row);
                }
                catch (Exception e)
                {
                    obj = "x";
                }
            }
            return obj;
        }

        @Override public boolean isCellEditable( int row, int column)
        {
            return (column == 0);
        }

        @Override public String getColumnName(int columnIndex)
        {
            switch (columnIndex)
            {
                case 0: return COL0_NAME;
                case 1: return res.getString( "ImportLabelRow1");
                case 2: return res.getString( "ImportLabelRow2");
                case 3: return res.getString( "ImportLabelRow3");
            }
            return "";
        }

        @Override public void setValueAt(Object aValue, int row, int column)
        {
            if (column == 0)
            {
                fields.set( row, aValue.toString());
            }
            else
            {
                array.get( column-1).set(row, aValue.toString());
            }
        }
    }

    /**
     * panel for classes specific to sub-instances
     */
    protected JPanel getSubPanel()
    {
        return fPanelSubs;
    }

    /**
     * a supporting method for children with GridBagLayouts.  Adds component
     * into specified x,y location with user specified width, height of 1, and specified
     * anchor and fill
    **/
    protected void gridbagAdd( JComponent target, JComponent newComp, int x, int y)
    {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0,0,0,0);
        ((GridBagLayout) target.getLayout()).setConstraints( newComp, gbc);
        target.add( newComp);
    }

	/**
	 * a supporting method for children with GridBagLayouts.  Adds component
	 * into specified x,y location with user specified width, height of 1, and specified
	 * anchor and fill
	**/
	protected void gridbagAdd( JComponent target, JComponent newComp, int x, int y,
		int width, int height, int anchor)
	{
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = anchor;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0,0,0,0);
		((GridBagLayout) target.getLayout()).setConstraints( newComp, gbc);
		target.add( newComp);
	}

    GridBagConstraints gbc = new GridBagConstraints();

    /**
     * Reads data from the clipboard. expecting Excell format, limited (at best)
     * error handling if data is not from Excel
     */
    public void parseClipboardToArray()
        throws IOException, UnsupportedFlavorException
    {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String pasteString = (String) ( clipboard.getContents(this).
            getTransferData(DataFlavor.stringFlavor));

        fPastedRows = new ArrayList<List<String>>();

        // for mac compatibility swap \r with \n
        pasteString = pasteString.replace( '\r', '\n');
        
        StringTokenizer rowString = new StringTokenizer( pasteString, "\n");
        for(int row = 0; rowString.hasMoreTokens(); row++)
        {
            int cLeft = 0;
            int cRight = 0;
            String columnString = rowString.nextToken();
            List<String> pastedCols = new ArrayList<String>(20);
            fPastedRows.add( pastedCols);

            while (cRight >= 0)
            {
                cRight = columnString.indexOf( "\t", cLeft);
                if (cRight > cLeft)
                {
                    String sss = columnString.substring( cLeft, cRight);
                    StringBuffer sb = new StringBuffer(sss);
                    pastedCols.add( newTrim( sb.toString()));
                }
                else if (cRight == cLeft)
                {
                    pastedCols.add( "");
                }
                else if (cRight < 0)
                {
                    StringBuffer sb = new StringBuffer( columnString.substring( cLeft));
                    pastedCols.add( newTrim(sb.toString()));
                }
                cLeft = cRight + 1;
            }

        }
        fTableModel.setArray( fPastedRows);
    }

    /**
     * some odd in the standard Trim() is not killing all spaces
     * see if this is still needed when switching to java 1.3
     */
    public String newTrim( String str)
    {
        char[] cc = str.toCharArray();

        int left = 0;
        while( (left < cc.length) && ( Character.isSpaceChar( cc[left]))) left++;
        if (left == cc.length) return "";

        int right = cc.length-1;
        while ( (right >= 0) && ( Character.isSpaceChar( cc[right]))) right--;

        StringBuffer sb = new StringBuffer();
        for (int i = left; i <= right; i++) sb.append( cc[i]);
        return sb.toString();
    }

	private class LocalWindowAdapator extends WindowAdapter
	{	
		@Override public void windowClosing( WindowEvent event)
	    {
	        if (event.getSource() == this)
	        {
	            closeWindow();
	        }
	    }
	}

//    public void windowActivated(WindowEvent event) {} // do nothing
//    public void windowDeactivated(WindowEvent event) {} // do nothing
//    public void windowDeiconified(WindowEvent event) {} // do nothing
//    public void windowIconified(WindowEvent event) {} // do nothing
//    public void windowOpened(WindowEvent event) {} // do nothing
//    public void windowClosed(WindowEvent event) {} // do nothing


     private void closeWindow()
    {
        setVisible(false);
    }


    private Point fInitLocation = null;

    @Override public void setVisible( boolean vis)
    {
        if (vis && fInitLocation == null)
        {
//			pack();
            fInitLocation = Util.getLocationToCenterOnScreen( this);
            setLocation( fInitLocation);
        }

        super.setVisible(vis);
    }


}
/**
 * $Log: DialogImportTable.java,v $
 * Revision 1.5  2006/05/19 05:48:42  sandyg
 * final release 5.1 modifications
 *
 * Revision 1.4  2006/01/15 21:10:40  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:20:26  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.8.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.8  2005/03/06 22:39:21  sandyg
 * Fixed bug 1157928, updates title bar after import.
 *
 * Revision 1.7  2004/04/10 20:49:30  sandyg
 * Copyright year update
 *
 * Revision 1.6  2004/01/18 18:32:49  sandyg
 * Now NO default on append/replace, must select one or the other.
 *
 * Revision 1.5  2003/05/07 01:17:06  sandyg
 * removed unneeded method parameters
 *
 * Revision 1.4  2003/04/27 21:35:34  sandyg
 * more cleanup of unused variables... ALL unit tests now working
 *
 * Revision 1.3  2003/04/27 21:05:59  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.2  2003/01/04 17:39:32  sandyg
 * Prefix/suffix overhaul
 *
*/
