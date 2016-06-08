package org.gromurph.util;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class JLabelWrapped extends JTextPane {

	public JLabelWrapped(String text) {
		super();
		initializeLabelLook( text);
	}

	private void initializeLabelLook( String text) {
		setText( text);

		setOpaque(false);
		setEditable(false);
		setFocusable(false);
		setBackground(UIManager.getColor("Label.background"));
		setFont(UIManager.getFont("Label.font"));
		setBorder(UIManager.getBorder("Label.border"));

		StyledDocument doc = getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
	}

}
