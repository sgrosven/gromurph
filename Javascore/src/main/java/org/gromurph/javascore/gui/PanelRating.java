// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelEntry.java,v 1.7 2006/09/03 20:12:21 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.ratings.Rating;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.BaseEditorContainer;
import org.gromurph.util.EditorManager;

/**
 * The Entry class handles information related to a entry in a race, the combination of a boat, its crew, skipper
 **/
public class PanelRating extends BaseEditor<Rating> {
	static ResourceBundle res = JavaScoreProperties.getResources();
	Rating fRating;

	public PanelRating(BaseEditorContainer parent) {
		super(parent);
		addFields();
	}

	@Override public void setObject(Rating inObj) {
		fRating = inObj;
		super.setObject(inObj);
	}

	BaseEditor fPanelRatingOneDesign;
	BaseEditor fPanelRatingDouble;
	
	public static String CARD_BLANK = "Blank";
	public static String CARD_DOUBLE = PanelRatingDouble.class.getName();
	public static String CARD_ONEDESIGN = PanelRatingOneDesign.class.getName();

	CardLayout cardLayout;

	public void addFields() {
		cardLayout = new CardLayout();
		setLayout(cardLayout);

		fPanelRatingDouble = new PanelRatingDouble( this.getEditorParent());
		fPanelRatingOneDesign = new PanelRatingOneDesign( this.getEditorParent());

		JPanel pBlank = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pBlank.add(new JLabel(""));

		this.add( CARD_BLANK, pBlank);
		this.add( CARD_DOUBLE, fPanelRatingDouble);
		this.add( CARD_ONEDESIGN, fPanelRatingOneDesign);
	}

		@Override public void updateFields() {
			if (fRating == null) cardLayout.show( this, CARD_BLANK);
			else {
				cardLayout.show( this, CARD_BLANK);

				Class ec = EditorManager.lookupEditorClass( fRating);
				cardLayout.show( this, ec.getName());
				
				//this.revalidate();

				if ( fRating.isOneDesign()) {
					fPanelRatingDouble.setObject( null);
					fPanelRatingOneDesign.setObject( fRating);
				} else {
					fPanelRatingDouble.setObject( fRating);
					fPanelRatingOneDesign.setObject( null);
				}
			} 
		}


	@Override public void start() {
		if (fPanelRatingOneDesign != null) fPanelRatingOneDesign.startUp();
		if (fPanelRatingDouble != null) fPanelRatingDouble.startUp();
	}

	@Override public void stop() {
		if (fPanelRatingOneDesign != null) fPanelRatingOneDesign.shutDown();
		if (fPanelRatingDouble != null) fPanelRatingDouble.shutDown();
	}

	// not sure if I still need this (sg, 2/16/2013)
	public void propertyChange( PropertyChangeEvent event) {
		if ( getEditorParent() != null) getEditorParent().eventOccurred( this, event);
	}

}
/**
 * $Log: PanelEntry.java,v $ Revision 1.7 2006/09/03 20:12:21 sandyg fixes bug 1551523 about crew data when boat changes
 */
