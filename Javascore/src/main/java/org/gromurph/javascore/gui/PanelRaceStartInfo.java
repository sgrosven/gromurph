// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelRace.java,v 1.10 2006/05/19 05:48:42 sandyg Exp
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RaceList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.StartingDivisionList;
import org.gromurph.util.HelpManager;
import org.gromurph.util.JTextFieldSelectAll;
import org.gromurph.util.PanelStartStop;
import org.gromurph.util.Util;

/**
 * The Race class handles a single Race. It has covering information about the race and a list of Finishes for the race
 **/
public class PanelRaceStartInfo extends PanelStartStop {
	static ResourceBundle res = JavaScoreProperties.getResources();

	Race fRace;
	StartingDivisionList fDivisions = null;

	List<JTextFieldSelectAll> fListStartTimes = new ArrayList<JTextFieldSelectAll>(10);
	List<JTextFieldSelectAll> fListLengths = new ArrayList<JTextFieldSelectAll>(10);
	List<JTextFieldSelectAll> fListLengthsPursuit = new ArrayList<JTextFieldSelectAll>(10);
	List<JCheckBox> fListIsRacing = new ArrayList<JCheckBox>(10);
	List<JCheckBox> fListNextDay = new ArrayList<JCheckBox>(10);
	
	JLabel headerPursuitLength; 

	JPanel fPanelDivInfo;
	JScrollPane fScrollClasses;
	
	JButton fButtonClearAll;
	JButton fButtonSetAll;

	public PanelRaceStartInfo() {
		super();
		addFields();
	}

	public void setRace(Race r) {

		if (fRace == r) return;
		fRace = r;
		
		if (isStarted) stop();
		initializeDivisions();	
		updateFields();
		if (isStarted) start();
	}

	private void addFields() {
		this.setLayout(new BorderLayout());
		
		JPanel scrollInner = new JPanel( new BorderLayout());

		fPanelDivInfo = new JPanel(new GridBagLayout());
		fPanelDivInfo.setName("panelDivInfo");
		scrollInner.add( fPanelDivInfo, BorderLayout.CENTER);

		fScrollClasses = new JScrollPane(scrollInner);
		fScrollClasses.setBorder(BorderFactory.createTitledBorder(res.getString("RaceLabelDivInfo")));
		this.add(fScrollClasses, BorderLayout.CENTER);

		// add the headers
		
		gridbagAdd(fPanelDivInfo, new JLabel(res.getString("RaceLabelIsRacing")), 0, 0);
		gridbagAdd(fPanelDivInfo, new JLabel(res.getString("RaceLabelStartTime")), 1, 0);
		gridbagAdd(fPanelDivInfo, new JLabel(res.getString("RaceLabelLength")), 2, 0);
		gridbagAdd(fPanelDivInfo, new JLabel(res.getString("RaceLabelNextDay")), 3, 0);
		
		headerPursuitLength = new JLabel(res.getString("RaceLabelLengthPursuit"));
		gridbagAdd(fPanelDivInfo, headerPursuitLength, 4, 0);

		JPanel bottom = new JPanel( new FlowLayout( FlowLayout.CENTER));
		
		fButtonClearAll = new JButton( res.getString( "RaceClearAllLabel"));
		fButtonClearAll.setName("fButtonClearAll");
		fButtonClearAll.setMnemonic(res.getString("RaceClearAllMnemonic").charAt(0));
		fButtonClearAll.setToolTipText(res.getString("RaceClearAllToolTip"));
		HelpManager.getInstance().registerHelpTopic(fButtonClearAll, "race.fButtonClearAll");

		fButtonSetAll = new JButton( res.getString( "RaceSetAllLabel"));
		fButtonSetAll.setName("fButtonSetAll");
		fButtonSetAll.setMnemonic(res.getString("RaceSetAllMnemonic").charAt(0));
		fButtonSetAll.setToolTipText(res.getString("RaceSetAllToolTip"));
		HelpManager.getInstance().registerHelpTopic(fButtonSetAll, "race.fButtonSetAll");

		bottom.add(fButtonClearAll);
		bottom.add(fButtonSetAll);
		scrollInner.add( bottom, BorderLayout.SOUTH);
	}

	private void initializeDivisions() {
		fDivisions = fRace.getDivisionsByStartOrder(false);
		if ( fDivisions.size() > 0 && fRace.getStartTimeAdjusted(fDivisions.get(0)) == SailTime.NOTIME) {
			// have no time for "first" start, try order on the last race of regatta
			Regatta regatta = JavaScoreProperties.getRegatta();
			if (regatta != null) {
				RaceList races = new RaceList();
				races.addAll( regatta.getRaces());
				races.sort();
				Race sortRace = null;
				for (Race r : races) {
					if (r == fRace) break;
					sortRace = r;
				}
				if (sortRace != null && sortRace != fRace) {
					fDivisions = sortRace.getDivisionsByStartOrder(false);
				}
			}
		} 
	}

	@Override public void updateFields() {
		for (int i = 0; i < fListIsRacing.size(); i++) {
			fPanelDivInfo.remove(fListIsRacing.get(i));
			fPanelDivInfo.remove(fListNextDay.get(i));
			fPanelDivInfo.remove(fListStartTimes.get(i));
			fPanelDivInfo.remove(fListLengths.get(i));
			fPanelDivInfo.remove(fListLengthsPursuit.get(i));
		}
		fListIsRacing.clear();
		fListNextDay.clear();
		fListStartTimes.clear();
		fListLengths.clear();
		fListLengthsPursuit.clear();

		if (fRace == null) {
			this.revalidate();
			return;
		}

		if (fDivisions == null || fDivisions.size() == 0) return;

		headerPursuitLength.setVisible( fRace.isPursuit());
		
		int row = 1;
		for (AbstractDivision div : fDivisions) {
			addDivInfoRow(row++, div);
			updateFieldValues(div);
		}
		this.revalidate();
	}
	
	private void updateFieldValues(AbstractDivision div) {
		
		// set the text for the fields
		// if textName not enabled, then
		// these should not be either
		int i = fDivisions.indexOf(div);
		boolean isRacing = div.isRacing(fRace);

		JCheckBox checkIsRacing = fListIsRacing.get(i);
		JCheckBox checkNextDay = fListNextDay.get(i);
		JTextFieldSelectAll textTime = fListStartTimes.get(i);
		JTextFieldSelectAll textLength = fListLengths.get(i);

		checkIsRacing.setSelected(isRacing);
		checkNextDay.setSelected(fRace.isNextDay(div));

		String time = SailTime.toSeconds(fRace.getStartTimeRaw(div));
		textTime.setText(time);

		textLength.setText(Double.toString(fRace.getLength(div)));

		textTime.setEnabled(isRacing);
		textLength.setEnabled(isRacing);
		checkNextDay.setEnabled(isRacing);

		JTextFieldSelectAll textLengthPursuit = fListLengthsPursuit.get(i);
		textLengthPursuit.setText(Double.toString(fRace.getLengthPursuit(div)));
		
		textLengthPursuit.setEnabled(isRacing && fRace.isPursuit());
		textLengthPursuit.setVisible(fRace.isPursuit());
	}



	private DecimalFormat fLengthFormat = new DecimalFormat("###0.0##");

	private void addDivInfoRow(int i, AbstractDivision div) {

		JCheckBox checkIsRacing = new JCheckBox();
		checkIsRacing.setName("fCheckIsRacing" + i);
		checkIsRacing.setText(div.getName());
		fListIsRacing.add(checkIsRacing);

		JTextFieldSelectAll textTime = new JTextFieldSelectAll(8);
		textTime.setName("fTextStartTime" + i);
		fListStartTimes.add(textTime);

		JTextFieldSelectAll textLength = new JTextFieldSelectAll(fLengthFormat);
		textLength.setColumns(6);
		textLength.setName("fTextLength" + i);
		fListLengths.add(textLength);

		JTextFieldSelectAll textLengthPursuit = new JTextFieldSelectAll(fLengthFormat);
		textLengthPursuit.setColumns(6);
		textLengthPursuit.setName("fTextLengthPursuit" + i);
		fListLengthsPursuit.add(textLengthPursuit);

		JCheckBox checkNextDay = new JCheckBox();
		checkNextDay.setName("fCheckNextDay" + i);
		checkNextDay.setText("");
		fListNextDay.add(checkNextDay);

		int row = i + 1; // first row are the headers

		gridbagAdd( fPanelDivInfo, checkIsRacing, 0, row, GridBagConstraints.WEST, 1);
		checkIsRacing.setToolTipText(res.getString("RaceLabelIsRacingToolTip"));
		HelpManager.getInstance().registerHelpTopic(checkIsRacing, "race.fCheckIsRacing");

		gridbagAdd(fPanelDivInfo, textTime, 1, row);
		textTime.setToolTipText(res.getString("RaceLabelStartTimeToolTip"));
		HelpManager.getInstance().registerHelpTopic(textTime, "race.fTextStartTime");

		gridbagAdd(fPanelDivInfo, textLength, 2, row);
		textLength.setToolTipText(res.getString("RaceLabelLengthToolTip"));
		HelpManager.getInstance().registerHelpTopic(textLength, "race.fTextLength");

		gridbagAdd(fPanelDivInfo, checkNextDay, 3, row);
		checkNextDay.setToolTipText(res.getString("RaceLabelNextDayToolTip"));
		HelpManager.getInstance().registerHelpTopic(checkNextDay, "race.fCheckNextDay");

		gridbagAdd(fPanelDivInfo, textLengthPursuit, 4, row);
		textLengthPursuit.setToolTipText(res.getString("RaceLabelLengthPursuitToolTip"));
		HelpManager.getInstance().registerHelpTopic(textLengthPursuit, "race.fTextLengthPursuit");
		
	}

	private static GridBagConstraints gbc = new GridBagConstraints();

	/**
	 * a supporting method for children with GridBagLayouts. Adds component into specified x,y location with user
	 * specified width, height of 1, and specified anchor and fill
	 **/
	protected void gridbagAdd(JComponent target, JComponent newComp, int x, int y) {
		gridbagAdd( target, newComp, x, y, GridBagConstraints.CENTER, 1);
	}
	protected void gridbagAdd(JComponent target, JComponent newComp, int x, int y, int anchor, int width) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = anchor;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = inset0;
		((GridBagLayout) target.getLayout()).setConstraints(newComp, gbc);
		target.add(newComp);
	}

	private Insets inset5 = new Insets(5, 5, 5, 5);
	private Insets inset0 = new Insets(0, 0, 0, 0);

	RaceLengthListener raceLengthListener = new RaceLengthListener();
	RaceLengthPursuitListener raceLengthPursuitListener = new RaceLengthPursuitListener();
	StartTimeListener startTimeListener = new StartTimeListener();

	@Override public void start() {

		updateFields();

		SailTime.clearLastTime();

		if (fDivisions == null) return;
		
		for (JCheckBox box : fListIsRacing) box.addActionListener(isRacingListener);
		for (JCheckBox box : fListNextDay) box.addActionListener(nextDayListener);
		for (JTextFieldSelectAll field : fListStartTimes) {
			field.addActionListener(startTimeListener);
		}
		for (JTextFieldSelectAll field : fListLengths) {
			field.addActionListener(raceLengthListener);
		}
		for (JTextFieldSelectAll field : fListLengthsPursuit) {
			field.addActionListener(raceLengthPursuitListener);
		}
		
		fButtonClearAll.addActionListener(clearAllListener);
		fButtonSetAll.addActionListener(setAllListener);
		
		//addFocusListener(this);
	}

	@Override public void stop() {
		//removeFocusListener(this);
		if (fDivisions == null) return;
		
		for (JCheckBox box : fListIsRacing) box.removeActionListener(isRacingListener);
		for (JCheckBox box : fListNextDay) box.removeActionListener(nextDayListener);
		for (JTextField field : fListStartTimes) {
			field.removeActionListener(startTimeListener);
		}
		for (JTextField field : fListLengths) {
			field.removeActionListener(raceLengthListener);
		}
		for (JTextField field : fListLengthsPursuit) {
			field.removeActionListener(raceLengthPursuitListener);
		}
	}

	private NextDayListener nextDayListener = new NextDayListener();
	
	private class NextDayListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
    		int i = fListNextDay.indexOf(event.getSource());
    		JCheckBox check = fListNextDay.get(i);
    
    		AbstractDivision div = fDivisions.get(i);
    
    		boolean b = check.isSelected();
    		fRace.setNextDay(div, b);
		}
	}

	private ClearAllListener clearAllListener = new ClearAllListener();
	
	private class ClearAllListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			setAllIsRacing(false);
		}
	}
	
	private SetAllListener setAllListener = new SetAllListener();
	
	private class SetAllListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			setAllIsRacing(true);
		}
	}
	
	private void setAllIsRacing( boolean isracing) {
		if (fRace == null || fDivisions == null) return;
		
		for (AbstractDivision div : fDivisions) {
			fRace.setIsRacing(div,  isracing);
			
			int i = fDivisions.indexOf(div);
			fListIsRacing.get(i).setSelected(isracing);
			fListNextDay.get(i).setEnabled(isracing);
			fListStartTimes.get(i).setEnabled(isracing);
			fListLengths.get(i).setEnabled(isracing);
			fListLengthsPursuit.get(i).setEnabled(isracing && fRace.isPursuit());
		}
	}
	
	private IsRacingListener isRacingListener = new IsRacingListener();
	
	private class IsRacingListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
    		int i = fListIsRacing.indexOf(event.getSource());
    		JCheckBox check = fListIsRacing.get(i);
    
    		AbstractDivision div = fDivisions.get(i);
    
    		boolean b = check.isSelected();
    		if (div.isRacing(fRace) && !b) {
    			// turning isRacing off, check for preexisting finishes
    			int count = fRace.getNumberFinishers(div);
    			if (count > 0) {
    				String msg = MessageFormat.format(res.getString("RaceMessageDeleteExistingFinishes"), new Object[] {
    						div.getName(), new Integer(count) });
    				if (!Util.confirm(msg)) {
    					check.setSelected(true);
    					return;
    				}
    			}
    		}
    
    		fRace.setIsRacing(div, b);
			fListNextDay.get(i).setEnabled(b);
			fListStartTimes.get(i).setEnabled(b);
			fListLengths.get(i).setEnabled(b);
			fListLengthsPursuit.get(i).setEnabled(b && fRace.isPursuit());
    	}
	}

	private String[] dtFormatStrings = new String[] { "HH:mm", "HH:mm:ss", "HHmmss", "HHmm" };

	private class StartTimeListener implements ActionListener { //, FocusListener {

		public void actionPerformed( ActionEvent event) {
			processChange(event.getSource());
		}

		public void processChange(Object source) {

			int i = fListStartTimes.indexOf(source);
			if (i < 0) return;

			JTextFieldSelectAll textStartTime = fListStartTimes.get(i);
			AbstractDivision div = fDivisions.get(i);

			long oldTime = fRace.getStartTimeRaw(div);
			String oldTimeText = SailTime.toString(oldTime);

			String newTimeText = textStartTime.getText();
			
			if (oldTimeText.equals(newTimeText)) return; // same
			if (oldTimeText.equals(newTimeText + ".0")) return; // same

			long newTime = 0;
			if (newTimeText.equals(SailTime.NOTIME_STRING)) {
				fRace.setStartTime(div,  SailTime.NOTIME);
			} else {
				boolean haveValid = false;
				Date dt = null;
				for (int d = 0; d < dtFormatStrings.length && !haveValid; d++) {
					String formatString = dtFormatStrings[d];
					if (newTimeText.length() == formatString.length()) {
						DateFormat thisFormat = new SimpleDateFormat(formatString);
						try {
							dt = thisFormat.parse(newTimeText);
							haveValid = true;
						}
						catch (Exception e) {
							haveValid = false;
						}
					}

				}
				if (!haveValid) {
					// no good, put old text back
					textStartTime.setText(oldTimeText);
					textStartTime.requestFocusInWindow();
				} else {

					// we're good
					Calendar cal = new GregorianCalendar();
					cal.setTime(dt);
					
					newTime = (cal.get(Calendar.HOUR_OF_DAY) * 60 * 60 + cal.get(Calendar.MINUTE) * 60 + cal
							.get(Calendar.SECOND)) * 1000;

					fRace.setStartTime(div, newTime);
				}
			}
		}
	}

	private class RaceLengthListener implements ActionListener { //, FocusListener {

		public void actionPerformed(ActionEvent event) {
			processChange(event.getSource());
		}

		public void processChange(Object source) {

			int i = fListLengths.indexOf(source);
			JTextFieldSelectAll textLength = fListLengths.get(i);

			try {
				String t = textLength.getText();
				double n = Double.parseDouble(t);
				AbstractDivision div = fDivisions.get(i);
				fRace.setLength(div, n);
			}
			catch (NumberFormatException e) {
				// do nothing but don't allow the race value either
			}

		}

	}

	private class RaceLengthPursuitListener implements ActionListener { //, FocusListener {

		public void actionPerformed(ActionEvent event) {
			processChange(event.getSource());
		}

		public void processChange(Object source) {

			int i = fListLengthsPursuit.indexOf(source);
			JTextFieldSelectAll textLength = fListLengthsPursuit.get(i);

			try {
				String t = textLength.getText();
				double n = Double.parseDouble(t);
				AbstractDivision div = fDivisions.get(i);
				fRace.setLengthPursuit(div, n);
			}
			catch (NumberFormatException e) {
				// do nothing but don't allow the race value either
			}

		}

	}

}
/**
 * $Log: PanelRace.java,v $ Revision 1.10 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 */
