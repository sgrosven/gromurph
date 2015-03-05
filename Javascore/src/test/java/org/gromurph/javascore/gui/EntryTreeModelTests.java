//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: EntryTreeModelTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.gui;

import javax.swing.tree.TreeNode;

import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.Util;

/**
 * Dummy template class for create unit test cases
 */
public class EntryTreeModelTests extends org.gromurph.javascore.JavascoreTestCase
{

    EntryTreeModel fModel = null;
    Regatta fRegatta = null;

    public void testInit()
    {
        assertEquals(
            fRegatta.getNumDivisions(),
            fModel.getChildCount( fModel.getRoot())
            );

        int leafCount = getLeafCount();
        assertEquals(
            fRegatta.getNumEntries(),
            leafCount
            );
    }

    public void testAdd()
    {

        EntryTreeModel.DivisionTreeNode divNode =
            (EntryTreeModel.DivisionTreeNode) ((TreeNode)fModel.getRoot()).getChildAt(2);
        EntryTreeModel.EntryTreeNode entNode =
            (EntryTreeModel.EntryTreeNode) divNode.getChildAt(3);

        int divCount = fModel.getChildCount( divNode);

        fModel.addEntry( new Object[] {
            fModel.getRoot(),
            divNode,
            entNode
        });

        assertEquals( divCount+1, fModel.getChildCount( divNode));
    }


    public void testDelete()
    {
        EntryTreeModel.DivisionTreeNode divNode =
            (EntryTreeModel.DivisionTreeNode) ((TreeNode)fModel.getRoot()).getChildAt(2);
        EntryTreeModel.EntryTreeNode entNode =
            (EntryTreeModel.EntryTreeNode) divNode.getChildAt(3);

        int divCount = fModel.getChildCount( divNode);

        fModel.deleteEntry( entNode.getEntry());

        assertEquals( divCount-1, fModel.getChildCount( divNode));
    }


    public int getLeafCount()
    {
        return countLeaves( (TreeNode) fModel.getRoot(), 0);
    }

    private int countLeaves( TreeNode startNode, int startCount)
    {
        java.util.Enumeration iter = startNode.children();
        while ( iter.hasMoreElements())
        {
            TreeNode node = (TreeNode) iter.nextElement();
            if (node.isLeaf()) startCount++;
            else startCount = countLeaves( node, startCount);
        }
        return startCount;
    }


    public void showTreeLevel1( EntryTreeModel model) 
    {
    	StringBuffer sb = new StringBuffer(32);
    	
        TreeNode top = (TreeNode) model.getRoot();
        sb.append(top);

        java.util.Enumeration iter = top.children();
        while ( iter.hasMoreElements())
        {
        	sb.append("  ");
        	sb.append( iter.nextElement());
        }
        
        logger.info( sb.toString());
    }

    @Override protected void setUp() throws Exception
    {
        super.setUp();
        try
        {
           fRegatta = loadTestRegatta( "0000-Test-v707-Solomons.regatta");


        }
        catch (Exception e)
        {
            Util.showError(e, true);
        }
        fModel = new EntryTreeModel( fRegatta);
    }

    @Override protected void tearDown() throws Exception
    {
        super.tearDown();
        fRegatta = null;
        fModel = null;
    }

    public EntryTreeModelTests( String name)
    {
        super(name);
    }

}
/**
* $Log: EntryTreeModelTests.java,v $
* Revision 1.4  2006/01/15 21:08:39  sandyg
* resubmit at 5.1.02
*
* Revision 1.2  2006/01/11 02:20:26  sandyg
* updating copyright years
*
* Revision 1.1  2006/01/01 02:27:02  sandyg
* preliminary submission to centralize code in a new module
*
* Revision 1.6.2.1  2005/11/01 02:36:58  sandyg
* java5 using generics
*
* Revision 1.6  2005/04/23 21:55:31  sandyg
* JWS mods for release 4.3.1
*
* Revision 1.5  2004/04/10 22:19:38  sandyg
* Copyright update
*
* Revision 1.4  2003/04/27 21:00:42  sandyg
* lots of cleanup, unit testing for 4.1.1 almost complete
*
* Revision 1.3  2003/03/16 20:39:16  sandyg
* 3.9.2 release: encapsulated changes to division list in Regatta,
* fixed a bad bug in PanelDivsion/Rating
*
* Revision 1.2  2003/01/04 17:09:27  sandyg
* Prefix/suffix overhaul
*
*/
