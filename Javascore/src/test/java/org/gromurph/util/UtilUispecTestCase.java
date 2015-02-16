// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: UtilJfcTestCase.java,v 1.7 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.util;

import java.awt.Component;
import java.awt.Container;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.text.JTextComponent;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uispec4j.Button;
import org.uispec4j.CheckBox;
import org.uispec4j.ComboBox;
import org.uispec4j.Panel;
import org.uispec4j.RadioButton;
import org.uispec4j.TabGroup;
import org.uispec4j.Table;
import org.uispec4j.TextBox;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.BasicHandler;
import org.uispec4j.interception.WindowInterceptor;

/**
 * extends Junit testcase to supply some gromurph specific checks
 */
public class UtilUispecTestCase extends UISpecTestCase {

	public static Test suite(Class c) {
		return new TestSuite(c);
	}

	public UtilUispecTestCase(String name) {
		super(name);
	}

	/**
	 * Quick wrapper interface for passing a "command" to run in a test such as assertException
	 */

	public interface Runner {
		public void run() throws Exception;
	}

	/**
	 * Tests to ensure that a user specified command generates a specific exception instance (or subclass thereof)
	 * 
	 * @param exeClass
	 *            the Class of the expected exception
	 * @param exec
	 *            The runner to be executed, but firing exec.run()
	 * 
	 */
	public void assertException(Class excClass, Runner exec) {
		StringBuffer failMsg = new StringBuffer();
		try {
			exec.run();

			failMsg.append("Expected exception, ");
			failMsg.append(excClass.getName());
			failMsg.append(", was not thrown");
			fail(failMsg.toString());
		}
		catch (Exception exc) {
			failMsg.append("UnExpected exception, ");
			failMsg.append(exc);
			failMsg.append(" thrown");
			if (!(excClass.isInstance(exc))) fail(failMsg.toString());
		}
	}

	protected void sendStringAndEnter(String tableName, int row, int col, String newtext) {
		Table ut = uiWindow.getTable( tableName);
		assertNotNull( ut);
		ut.editCell( row, col, newtext, true);
	}

	protected void sendToText( String fieldName, String newtext) {
		assertNotNull( uiWindow);
		TextBox tb = uiWindow.getTextBox( fieldName);
		assertNotNull( tb);
		tb.clear();
		tb.appendText( newtext);
	}
	protected void sendStringAndEnter(String fieldName, String newtext) {
		assertNotNull( uiWindow);
		TextBox tb = uiWindow.getTextBox( fieldName);
		assertNotNull( tb);
		tb.setText( newtext);
	}
	protected void sendStringAndEnter( JTextComponent field, String newtext) {
		Container c = field.getParent();
		Panel p = new Panel( c);
		TextBox tb = p.getTextBox( field.getName());
		assertNotNull( tb);
		tb.setText( newtext);
	}
	@Deprecated
	protected void sendStringAndChangeFocus( JTextComponent field, String newText, Component altfocus) {
		sendStringAndEnter( field, newText);
	}
	
	private DialogBaseEditor fBaseDialog;
	private BaseEditor fBaseEditor;	
	private Window uiWindow;
	
	protected static final String FRAME_NAME = "TestFrame";

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		if (fBaseDialog != null) {
			fBaseDialog.shutDown();
			fBaseDialog.setVisible(false);
			fBaseDialog.removeAll();
		}
		if (fBaseEditor != null) {
			fBaseEditor.shutDown();
			fBaseEditor.setVisible(false);
		}
		fBaseEditor = null;
		fBaseDialog = null;
		uiWindow = null;
	}

	protected DialogBaseEditor showPanel(BaseObjectModel obj) {
		return showPanel(obj, null);
	}

	protected DialogBaseEditor getBaseDialog() {
		if (fBaseDialog == null) {
			fBaseDialog = new DialogBaseEditor();
			fBaseDialog.setTitle(FRAME_NAME);
		}
		return fBaseDialog;
	}
	protected DialogBaseEditor showPanel(final BaseObjectModel obj, final BaseEditor editor) {
		
		fBaseEditor = editor;
		
		//uiWindow = new Window( fBaseDialog);
		uiWindow = WindowInterceptor.run(new Trigger() {
		    public void run() {
		    	DialogBaseEditor d = getBaseDialog();	
				d.setObject(obj, editor);
				d.setVisible(true);
				
				if (fBaseEditor == null) fBaseEditor = d.getEditor();
		    }
		  });
		
		assertNotNull( uiWindow);
		assertNotNull( fBaseDialog);
		assertTrue( fBaseDialog.isVisible());
		assertNotNull( fBaseEditor);
		return fBaseDialog;
	}

	protected void displayDialog(final JDialog dialog) {
		// by-passing these guys
		fBaseDialog = null;
		fBaseEditor = null;
		
		uiWindow = WindowInterceptor.run(new Trigger() {
		    public void run() {
		    	dialog.setVisible(true);
		    }
		  });

	}

	protected void clickOKButton( JDialog dialog) {
		clickOKButton( new Window( dialog));
	}
	protected void clickOKButton(String dialogName) {
		Panel uipanel = uiWindow.getContainer( dialogName);
		clickOKButton( uipanel);
	}	
	protected void clickOKButton() {
		clickOKButton( uiWindow);
	}
	protected void clickOKButton( Panel uipanel) {
		assertNotNull( uipanel);
		Button bb = null;
		try {bb = uipanel.getButton( "fButtonExit");} catch (org.uispec4j.ItemNotFoundException e) {}
		if (bb == null) try {bb = uipanel.getButton( "OK");} catch (org.uispec4j.ItemNotFoundException e) {}
		assertNotNull("Can't find button named 'fButtonExit' or 'OK' " + uipanel.toString(), bb);
		bb.click();
		
		assertFalse( "Panel did not close", uipanel.isVisible());
	}

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void tracePanel(Component c, int indent) {
		StringBuffer sb = new StringBuffer(64);
		for (int i = 0; i < indent; i++)
			sb.append("    ");
		String cl = c.getClass().getName();
		if (cl.startsWith("javax.swing.")) cl = cl.substring(12);
		sb.append(cl);
		sb.append(": ");
		sb.append(c.getName());
		if (c instanceof JTextComponent) {
			sb.append(", text=" + ((JTextComponent) c).getText());
		} else if (c instanceof AbstractButton) {
			sb.append(", label=" + ((AbstractButton) c).getText());
		}

		logger.info( sb.toString());

		if (c instanceof JMenu) {
			Component[] clist = ((JMenu) c).getMenuComponents();

			for (int i = 0; i < clist.length; i++) {
				tracePanel(clist[i], indent + 1);
			}
		} else if (c instanceof Container) {
			Component[] clist = ((Container) c).getComponents();

			for (int i = 0; i < clist.length; i++) {
				tracePanel(clist[i], indent + 1);
			}
		}
	}

	protected <T extends java.awt.Component> T findComponent(Class<T> cl, String title) {
		return findComponentInPanel( cl, title, uiWindow);
	}
	
	protected <T extends java.awt.Component> T findComponent(Class<T> cl, String title, Container parentContainer) {
		Panel p = new Panel(parentContainer);
		return findComponentInPanel( cl, title, p);
	}
	
	private <T extends java.awt.Component> T findComponentInPanel(Class<T> cl, String title, Panel panel) {
		if (panel == null) return null;
		
		Component c = panel.findSwingComponent( cl, title);
		return (T) c;
	}

	protected JDialog findDialog(String name) {
		if (uiWindow == null) return null;
		
		Panel p = uiWindow.getContainer(name);
		if (p == null) return null;
		Object x = p.getContainer();
		if (x instanceof JDialog) return (JDialog) x;
		return null;
	}

	protected JDialog findDialog(Component comp) {
		Component next = comp;
		while ( (next != null) && !(next instanceof JDialog)) {
			next = next.getParent();
		}
		if (next != null && next instanceof JDialog) return (JDialog) next;
		else return null;
	}

	protected void clickOnButton(String buttonName) {
		clickOnButton( buttonName, false);
	}
	protected void clickOnButton(String buttonName, boolean shouldCloseWindow) {
		assertNotNull( uiWindow);
		Button bb = uiWindow.getButton( buttonName);
		assertNotNull( bb);
		bb.click();
		
		if (shouldCloseWindow) {
			assertFalse("Window did not close", uiWindow.isVisible());
		}
	}
	protected void clickOnButtonInPopup(String buttonName, JDialog containingDialog) {
		Window w = new Window( containingDialog);
		Button bb = w.getButton( buttonName);
		assertNotNull( bb);
		bb.click();
	}
	
	protected String clickOnComboBox( String name, String item) {
		ComboBox cb = uiWindow.getComboBox(name);
		assertNotNull( cb);
		
		cb.select( item);
		return cb.getAwtComponent().getSelectedItem().toString();
	}

	protected void clickOnCheckBox( String name) {
		CheckBox cb = uiWindow.getCheckBox(name);
		assertNotNull( cb);
		
		cb.click();
	}
	protected void clickOn( JCheckBox c) {
		CheckBox cb = new CheckBox(c);
		assertNotNull(cb);
		cb.click();
	}
	protected void clickOn( JRadioButton c) {
		RadioButton rb = new RadioButton(c);
		assertNotNull( rb);
		rb.click();
	}
	protected void clickOn( JButton c) {
		Button rb = new Button(c);
		assertNotNull( rb);
		rb.click();
	}

	protected JPanel clickOnTabPane( JTabbedPane pane, String tabname) {
		TabGroup tg = new TabGroup(pane);
		tg.selectTab(tabname);
		Panel p = tg.getSelectedTab();
		assertTrue( "selected tab is not a JPanel", p.getAwtContainer() instanceof JPanel);
		
		return (JPanel) p.getAwtContainer();
	}
	
	protected JPanel clickOnTabPane(String name, String tabName) {
		TabGroup tg = uiWindow.getTabGroup( name);
		assertNotNull(tg);
		
		assertTrue( tg.getAwtComponent() instanceof JTabbedPane);
		JTabbedPane tabPane = (JTabbedPane) tg.getAwtComponent();

		return clickOnTabPane( tabPane, tabName);
	}

	protected JDialog getPopupForButtonClick( String buttonName) {
		Window pw = WindowInterceptor.run(
				uiWindow.getButton(buttonName).triggerClick());
		assertNotNull(pw);
		assertTrue( pw.getAwtContainer() instanceof JDialog);
		return (JDialog) pw.getAwtContainer();
	}
	
	protected void setTextInModalPopup( String showPopupButtonName, String popupTextFieldName, String popupOKName, String textToPutInPopup) {
		
    	 WindowInterceptor
    	   .init(
    			   uiWindow.getButton(showPopupButtonName).triggerClick())
    	   .process(BasicHandler.init()
    	            .assertContainsText(popupTextFieldName)
    	            .setText(textToPutInPopup)
    	            .triggerButtonClick(popupOKName))
    	   .run();
	}

	protected void clickButtonInModalPopup( String showPopupButtonName, String buttonName) {
		
   	 WindowInterceptor
   	   .init(
   			   uiWindow.getButton(showPopupButtonName).triggerClick())
   	   .process(BasicHandler.init()
   	            .triggerButtonClick(buttonName))
   	   .run();
	}

//	public void testDummy() {
//		// empty test just to keep package level eclipse testing happy
//		assertTrue(true);
//	}

}
/**
 * $Log: UtilJfcTestCase.java,v $ Revision 1.7 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.5 2006/01/14 21:06:55 sandyg final bug fixes for 5.01.1. All tests work
 * 
 * Revision 1.4 2006/01/11 02:27:46 sandyg updating copyright years
 * 
 * Revision 1.3 2006/01/11 02:16:46 sandyg updated to use relative class path
 * 
 * Revision 1.2 2006/01/02 22:30:20 sandyg re-laidout scoring options, added alternate A8.2 only tiebreaker, added unit
 * tests for both
 * 
 * Revision 1.1 2006/01/01 02:27:03 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.11.2.2 2005/11/30 02:52:30 sandyg focuslost commit added to JTextFieldSelectAll
 * 
 * Revision 1.11.2.1 2005/11/26 17:44:21 sandyg implement race weight & nondiscardable, did some gui test cleanups.
 * 
 * Revision 1.11 2005/06/26 22:48:08 sandyg Xml overhaul to remove xerces dependence
 * 
 * Revision 1.10 2004/04/10 22:19:41 sandyg Copyright update
 * 
 * Revision 1.9 2003/05/13 22:32:33 sandyg improved component tracing
 * 
 * Revision 1.8 2003/05/02 02:42:55 sandyg added trace method
 * 
 * Revision 1.7 2003/04/30 00:57:09 sandyg added component tracer() method
 * 
 * Revision 1.6 2003/04/27 21:01:16 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.5 2003/04/20 15:44:31 sandyg added javascore.Constants to consolidate penalty defs, and added new penaltys
 * TIM (time value penalty) and TMP (time percentage penalty)
 * 
 * Revision 1.4 2003/03/30 00:04:09 sandyg gui test cleanup, moved fFrame, fPanel to UtilJfcTestCase
 * 
 * Revision 1.3 2003/03/19 02:39:51 sandyg added awtSleep to keep dialogs straight
 * 
 * Revision 1.2 2003/03/16 20:40:15 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.1 2003/02/22 13:55:12 sandyg no message
 * 
 */
