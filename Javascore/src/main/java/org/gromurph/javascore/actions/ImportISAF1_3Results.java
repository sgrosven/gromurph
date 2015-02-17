// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionExport.java,v 1.7 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.actions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.manager.RegattaManager;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Boat;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Fleet;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RaceList;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.util.Exporter;
import org.gromurph.util.Person;
import org.gromurph.util.Util;
import org.gromurph.xml.DocumentException;
import org.gromurph.xml.IDocumentWriter;
import org.gromurph.xml.PersistentNode;
import org.gromurph.xml.XmlUtil;

public class ImportISAF1_3Results implements Exporter {
	static ResourceBundle res = JavaScoreProperties.getResources();
	//static ResourceBundle resUtil = Util.getResources();

	private Regatta regatta = JavaScoreProperties.getRegatta();

	private String fileName;
	
	public void setFilename(String fileName) throws IOException {
		this.fileName = fileName;
	}

	public void export( Regatta reg) throws IOException {
		if (reg == null) return;
		
		try {
	    	IDocumentWriter dw = XmlUtil.createDocumentWriter("", fileName);
    		regatta = reg;
    		root = dw.createRootNode(ROOT);
    		initializeExport();
    		buildXML();
	    	dw.saveObject( root, false);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int nextID;

	private void initializeExport() {
		nextID = 0;
		root = null;
		teamKeys = new TreeMap<Entry, String>();

		root = dw.createRootNode(ROOT);

	}

	IDocumentWriter dw;
	PersistentNode root;

	Map<Entry, String> teamKeys;

	private static final String ROOT = "SailingXRR";
	private static final String TYPE = "Type";
	private static final String VERSION = "Version";
	private static final String DATE = "Date";
	private static final String TIME = "Time";
	private static final String SCHEMATAG = "xsi:noNamespaceSchemaLocation";
	private static final String XSI = "xmlns:xsi";

	private static final String PERSON = "Person";
	private static final String BOAT = "Boat";
	private static final String RACE = "Race";
	private static final String EVENT = "Event";
	private static final String EVENTTITLE = "Title";
	private static final String EVENTID = "EventID";
	private static final String IFEVENTID = "IFEventID";
	private static final String DIVISION = "Division";
	private static final String TEAM = "Team";
	private static final String XSD_URL = "http://www.sailing.org/uploads/xml/sailingXRR_v1.3.xsd";

	private String createKey(String possibleDefault) {
		if (possibleDefault == null) return Integer.toString(nextID++);
		else return possibleDefault;
	}

	// eg <?xml version='1.0'?>
	//    <SailingXRR Date="20100216" Time="195923" Type="SailingXRR" Version="1.3" 
	//			xsi:noNamespaceSchemaLocation="http://www.sailing.org/uploads/xml/sailingXRR_v1.3.xsd" 
	//			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	//    	<Person FamilyName="Wang-Hansen" GivenName="Aleksander" IFPersonID="noraw2" NOC="NOR" PersonID="noraw2"/>
	//    	<Boat BoatID="0" BoatName="" SailNumber="NOR 1"/>
	//    	<Team Gender="Open" NOC="NOR" TeamID="NORAW2" TeamName="" BoatID="0">
	//    		<Crew PersonID="NORAW2" Position="S"/>
	//    		<Crew PersonID="NORPK4" Position="C"/>
	//    		<Crew PersonID="NORMS4" Position="C"/>
	//    	</Team>
	//    	<Event EventID="9" IFEventID="9756" Title="Sonar">
	//    		<Division DivisionID="1" Gender="Open" IFDivisionID="15983" Title="Sonar">
	//    			<Race RaceID="30" RaceName="1" RaceNumber="1" RaceStartDate="20100126" RaceStartTime="021255" RaceStatus="Provisional"/>
	//    			<RaceResult TeamID="FRABJ" Discard="false" RaceID="111" RacePoints="2" ScoreCode=""/>
	//    			<SeriesResult TeamID="NORAW2" NetPoints="16" Rank="1" Tied="false" TotalPoints="46"/>
	//    		</Division>
	//    	</Event>
	//    </SailingXRR>

	private void buildXML() {

		//<xsd:element name="SailingXRR">
		//	<xsd:complexType>
		//		<xsd:choice maxOccurs="unbounded" minOccurs="1">
		//			<xsd:element name="Person" type="Person" maxOccurs="unbounded" minOccurs="1"/>
		//			<xsd:element name="Boat" type="Boat" maxOccurs="unbounded" minOccurs="1"/>
		//			<xsd:element name="Team" type="Team" maxOccurs="unbounded" minOccurs="1"/>
		//			<xsd:element name="Race" type="Race"  maxOccurs="unbounded" minOccurs="1"/>
		//			<xsd:element name="Event" type="Event" maxOccurs="unbounded" minOccurs="1"/>
		//		</xsd:choice>
		//		<xsd:attribute name="Date" type="xsd:string" use="required"/>
		//		<xsd:attribute name="Time" type="xsd:string" use="required"/>
		//		<xsd:attribute name="Type" type="xsd:string" use="required"/>
		//		<xsd:attribute name="Version" type="xsd:string" use="required"/>
		//	</xsd:complexType>
		//</xsd:element>

		root.setAttribute(TYPE, "SailingXRR");
		root.setAttribute(VERSION, "1.3");
		root.setAttribute(DATE, new SimpleDateFormat("yyyyMMdd").format(regatta.getSaveDate()));
		root.setAttribute(TIME, new SimpleDateFormat("HHmmss").format(regatta.getSaveDate()));
		root.setAttribute(SCHEMATAG, XSD_URL);
		root.setAttribute(XSI, "http://www.w3.org/2001/XMLSchema-instance");

		processEntries(regatta.getAllEntries());

		createEventElement();

	}

	private void processEntries(EntryList entryList) {

		if (entryList == null || entryList.size() == 0) return;

		for (Entry entry : entryList) {
			processSailorsAndTeams(entry);
		}

	}

	private static final String TEAMID = "TeamID";
	private static final String TEAMNAME = "TeamName";
	private static final String CREW = "Crew";
	private static final String POSITION = "Position";
	private static final String POSITION_SKIPPER = "S";
	private static final String POSITION_CREW = "C";
	private static final String GENDER = "Gender";

	private static int sTeamIDIndex = 0;

	public PersistentNode processSailorsAndTeams(Entry entry) {

		//<xsd:complexType name="Team">
		//	<xsd:sequence>
		//		<xsd:element name="Crew" type="Crew" minOccurs="0" maxOccurs="unbounded"/>
		//	</xsd:sequence>
		//	<xsd:attribute name="TeamID" type="xsd:string" use="required"/>
		//	<xsd:attribute name="BoatID" type="xsd:string" use="optional"/>
		//	<xsd:attribute name="NOC" type="xsd:string" use="optional"/>
		//	<xsd:attribute name="TeamName" type="xsd:string" use="optional"/>
		//	<xsd:attribute name="Gender" type="Genders" use="optional"/>
		//	
		//</xsd:complexType>

		PersistentNode teamNode = root.createChildElement( TEAM);

		String boatID = createBoatElement(entry.getBoat());

		String teamID = null;
		Person skipper = entry.getSkipper();
		if (skipper == null) {
			teamID = Integer.toString(sTeamIDIndex++);
		} else {
			teamID = skipper.getSailorId();
		}
		teamKeys.put(entry, teamID);

		teamNode.setAttribute(TEAMID, teamID);
		teamNode.setAttribute(NOC, sailorIDToCountry(skipper));
		teamNode.setAttribute(TEAMNAME, "");
		teamNode.setAttribute(GENDER, entry.getDivision().getGender());
		teamNode.setAttribute(BOATID, boatID);

		if ((entry.getSkipper() != null)) {
			createPersonElement(entry.getSkipper());
			createCrewElement(teamNode, entry.getSkipper(), POSITION_SKIPPER);
		}
		for (Person crew : entry.getCrewList()) {
			createPersonElement(crew);
			createCrewElement(teamNode, crew, POSITION_CREW);
		}

		return teamNode;
	}

	private static final String PERSONID = "PersonID";
	private static final String FAMILYNAME = "FamilyName";
	private static final String GIVENNAME = "GivenName";
	private static final String NOC = "NOC";
	private static final String SAILORID = "IFPersonID";

	private PersistentNode createPersonElement(Person p) {
		//<xsd:complexType name="Person">
		//	<xsd:attribute name="PersonID" type="xsd:string" use="required"/>
		//	<xsd:attribute name="IFPersonID" type="xsd:string" use="required"/>
		//	<xsd:attribute name="FamilyName" type="xsd:string" use="required"/>
		//	<xsd:attribute name="GivenName" type="xsd:string" use="optional"/>
		//	<xsd:attribute name="NOC" type="xsd:string" use="optional"/>
		//	<xsd:attribute name="Gender" type="Genders" use="optional"/>
		//</xsd:complexType>
		String key = createKey(p.getSailorId().toLowerCase());
		PersistentNode node = root.createChildElement( PERSON);
		node.setAttribute(PERSONID, key);
		node.setAttribute(FAMILYNAME, p.getLast());
		node.setAttribute(GIVENNAME, p.getFirst());
		node.setAttribute(NOC, sailorIDToCountry(p));
		node.setAttribute(SAILORID, p.getSailorId().toLowerCase());

		return node;
	}

	private void createCrewElement(PersistentNode teamNode, Person crew, String position) {
		//<xsd:complexType name="Crew">
		//	<xsd:attribute name="PersonID" type="xsd:string" use="required"/>
		//	<xsd:attribute name="Position" type="CrewPositions" use="required"/>
		//</xsd:complexType>
		PersistentNode crewNode = teamNode.createChildElement( CREW);
		crewNode.setAttribute(PERSONID, crew.getSailorId());
		crewNode.setAttribute(POSITION, position);
	}

	private String sailorIDToCountry(Person p) {
		if (p.getSailorId() != null && p.getSailorId().length() >= 3) {
			return p.getSailorId().substring(0, 3).toUpperCase();
		} else {
			return "";
		}
	}

	private static final String BOATID = "BoatID";
	private static final String BOATNAME = "BoatName";
	private static final String SAILNUMBER = "SailNumber";

	public String createBoatElement(Boat boat) {
		//<xsd:complexType name="Boat">
		//	<xsd:attribute name="BoatID" type="xsd:string" use="required"/>
		//	<xsd:attribute name="BoatName" type="xsd:string" use="optional"/>
		//	<xsd:attribute name="SailNumber" type="xsd:string" use="required"/>
		//</xsd:complexType>
		if ((boat != null)) {

			String key = createKey(null);
			PersistentNode node = root.createChildElement( BOAT);
			node.setAttribute(BOATID, key);
			node.setAttribute(SAILNUMBER, boat.getSailId().toString());
			node.setAttribute(BOATNAME, boat.getName());
			return key;
		}
		return null;
	}

	private void createEventElement() {

		//<xsd:complexType name="Event">
		//	<xsd:choice maxOccurs="unbounded" minOccurs="1">
		//		<xsd:element name="Division" type="Division" maxOccurs="unbounded" minOccurs="0"/>
		//		<xsd:element name="RegattaSeriesResult" type=" RegattaSeriesResult" maxOccurs="unbounded" minOccurs="0"/>
		//	</xsd:choice>
		//	<xsd:attribute name="EventID" type="xsd:string" use="required"/>
		//	<xsd:attribute name="IFEventID" type="xsd:string" use="optional"/>
		//	<xsd:attribute name="Type" type="EventTypes" use="optional" default="Regatta" />
		//	<xsd:attribute name="Title" type="xsd:string" use="required"/>
		//</xsd:complexType>

		PersistentNode event = root.createChildElement( EVENT);
		event.setAttribute(EVENTTITLE, regatta.getName());
		event.setAttribute(EVENTID, createKey(null));
		event.setAttribute(IFEVENTID, regatta.getIfEventId());

		for (Division div : regatta.getDivisions()) {
			processDivision(event, div);
		}

		if (!regatta.isMultistage()) {
			for (Fleet div : regatta.getFleets()) {
				processDivision(event, div);
			}
			for (SubDivision div : regatta.getSubDivisions()) {
				processDivision(event, div);
			}
		}
	}

	private static final String DIVID = "DivisionID";
	private static final String DIVTITLE = "Title";
	private static final String IFCLASSID = "IFClassID";

	private PersistentNode createDivisionElement(PersistentNode divisionList, AbstractDivision div) {
		//<xsd:complexType name="Division">
		//	<xsd:choice ... />
		//	<xsd:attribute name="Type" type="DivisionTypes" use="optional" default="FleetRace" />
		//	<xsd:attribute name="DivisionID" type="xsd:string" use="required"/>
		//	<xsd:attribute name="IFDivisionID" type="xsd:string" use="optional"/>
		//	<xsd:attribute name="Title" type="xsd:string" use="required"/>
		//	<xsd:attribute name="IFClassID" type="xsd:string" use="optional"/>
		//	<xsd:attribute name="Gender" type="Genders" use="optional"/>
		//</xsd:complexType>

		PersistentNode divNode = divisionList.createChildElement( DIVISION);
		divNode.setAttribute(DIVID, Integer.toString(divisionList.getElements().length));
		divNode.setAttribute(DIVTITLE, div.getName());
		divNode.setAttribute(IFCLASSID, div.getIfClassId());
		if (div.getGender() != null) divNode.setAttribute(GENDER, div.getGender());

		return divNode;
	}

	private static final String RACEID = "RaceID";
	private static final String RACENUMBER = "RaceNumber";
	private static final String RACENAME = "RaceName";
	private static final String RACESTARTDATE = "RaceStartDate";
	private static final String RACESTARTTIME = "RaceStartTime";
	private static final String RACESTATUS = "RaceStatus";

	private void processDivision(PersistentNode parentNode, AbstractDivision div) {

		//<xsd:complexType name="Division">
		//	<xsd:choice minOccurs="0" maxOccurs="unbounded">
		//		<xsd:element name="Race" type="Race" minOccurs="0" maxOccurs="unbounded"/>
		//		<xsd:element name="SeriesResult" type="SeriesResult" minOccurs="0" maxOccurs="unbounded"/>
		//		<xsd:element name="RaceResult" type="RaceResult" minOccurs="0" maxOccurs="unbounded"/>
		//		<xsd:element name="TRResult" type="TRResult" minOccurs="0" maxOccurs="unbounded"/>
		//	</xsd:choice>
		//	<xsd:att...
		//</xsd:complexType>

		//	    		<Division DivisionID="1" Gender="Open" IFDivisionID="15983" Title="Sonar">
		//	    			<Race RaceID="30" RaceName="1" RaceNumber="1" RaceStartDate="20100126" RaceStartTime="021255" RaceStatus="Provisional"/>
		//	    			<RaceResult TeamID="FRABJ" Discard="false" RaceID="111" RacePoints="2" ScoreCode=""/>
		//	    			<SeriesResult TeamID="NORAW2" NetPoints="16" Rank="1" Tied="false" TotalPoints="46"/>
		//	    		</Division>

		// only run this if there are actual entries and series results for the specified division
		PersistentNode divNode = createDivisionElement(parentNode, div);

		SeriesPointsList spl = regatta.getScoringManager().getAllSeriesPoints(div);
		if (spl == null || spl.size() == 0) return;

		RaceList races = new RaceList();
		for (Race r : regatta.getRaces()) {
			if ( div.isRacing(r)) races.add(r);
		}

		races.sort();
		int raceNum = 1;
		for (Race r : races) {
			createRaceElement(divNode, r, raceNum++);
		}

		EntryList entries = div.getEntries();
		for (Entry e : entries) {
			double totalPoints = 0;
			for (Race r : races) {
				if (r.isSailing(e)) {
					RacePoints p = regatta.getScoringManager().getRacePointsList().find(r, e, div);
					createRaceResultElement(divNode, p);
					totalPoints += p.getPoints();
				}
			}
			createSeriesResultElement(divNode, div, e, totalPoints);
		}
	}

	private void createRaceElement(PersistentNode parentNode, Race r, int raceNum) {

		//<xsd:complexType name="Race">
		//	<xsd:attribute name="RaceID" type="xsd:string" use="required"/>
		//	<xsd:attribute name="RaceNumber" type="xsd:positiveInteger" use="required"/>
		//	<xsd:attribute name="RaceName" type="xsd:string" use="optional"/>
		//	<xsd:attribute name="RaceStartDate" type="xsd:string" use="optional"/>
		//	<xsd:attribute name="RaceStartTime" type="xsd:string" use="optional"/>
		//	<xsd:attribute name="RaceStatus" type="xsd:string" use="optional"/>   
		//	<xsd:attribute name="Stage" type="xsd:string" use="optional"/>   
		//	<xsd:attribute name="Flight" type="xsd:string" use="optional"/>   
		//	<xsd:attribute name="Match" type="xsd:string" use="optional"/>   
		//</xsd:complexType>

		PersistentNode node = parentNode.createChildElement( RACE);
		node.setAttribute(RACEID, Integer.toString(r.getId()));
		node.setAttribute(RACENUMBER, Integer.toString(raceNum++));
		node.setAttribute(RACENAME, r.getName());
		node.setAttribute(RACESTARTDATE, new SimpleDateFormat("yyyyMMdd").format(r.getStartDate()));
		AbstractDivision firstStartingDivision = r.getDivisionsByStartOrder(true).get(0);
		node.setAttribute(RACESTARTTIME,
				new SimpleDateFormat("HHmmss").format(r.getStartTimeRaw(firstStartingDivision)));
		node.setAttribute(RACESTATUS, regatta.isFinal() ? "Final" : "Provisional");
	}

	private void createRaceResultElement(PersistentNode parentNode, RacePoints p) {
		//<xsd:complexType name="RaceResult">
		//	<xsd:sequence>
		//		<xsd:element name="Crew" type="Crew" minOccurs="0" maxOccurs="unbounded"/>
		//	</xsd:sequence>
		//	<xsd:attribute name="RaceID" type="xsd:string" use="required"/>
		//	<xsd:attribute name="TeamID" type="xsd:string" use="required"/>
		//	<xsd:attribute name="BoatID" type="xsd:string" use="optional"/>
		//	<xsd:attribute name="RacePoints" type="xsd:decimal" use="required"/>
		//	<xsd:attribute name="RaceRank" type="xsd:positiveInteger" use="optional"/>
		//	<xsd:attribute name="Discard" type="xsd:boolean" use="optional" default="false" />
		//	<xsd:attribute name="ScoreCode" type="xsd:string" use="optional" default=""/>
		//	<xsd:attribute name="Entry" type="MatchRaceEntries" use="optional"/>
		//</xsd:complexType>

		// eg <RaceResult TeamID="FRABJ" Discard="false" RaceID="111" RacePoints="2" ScoreCode=""/>

		PersistentNode resultNode = parentNode.createChildElement( RACERESULT);
		resultNode.setAttribute(TEAMID, teamKeys.get(p.getEntry()));
		resultNode.setAttribute(RACEID, Integer.toString(p.getRace().getId()));
		resultNode.setAttribute(RACEPOINTS, pointsFormat.format(p.getPoints()));
		resultNode.setAttribute(DISCARD, Boolean.toString(p.isThrowout()));
		resultNode.setAttribute(SCORECODE, p.getFinish().getPenalty().toString());
	}

	private void createSeriesResultElement(PersistentNode parentNode, AbstractDivision div, Entry entry,
			double totalPoints) {
		//<xsd:complexType name="SeriesResult">
		//	<xsd:attribute name="TeamID" type="xsd:string" use="required"/>
		//	<xsd:attribute name="NetPoints" type="xsd:decimal" use="optional"/>
		//	<xsd:attribute name="TotalPoints" type="xsd:decimal" use="optional"/>
		//	<xsd:attribute name="Tied" type="xsd:boolean" use="optional" default="false" />
		//	<xsd:attribute name="Rank" type="xsd:positiveInteger" use="required"/>
		//</xsd:complexType>

		// <SeriesResult TeamID="NORAW2" NetPoints="16" Rank="1" Tied="false" TotalPoints="46"/>

		SeriesPoints sp = regatta.getScoringManager().getRegattaRanking(entry, div);
		if (sp == null) return;

		PersistentNode seriesNode = parentNode.createChildElement( SERIESRESULT);
		seriesNode.setAttribute(TEAMID, teamKeys.get(sp.getEntry()));
		seriesNode.setAttribute(TOTALPOINTS, pointsFormat.format(totalPoints));
		seriesNode.setAttribute(NETPOINTS, pointsFormat.format(sp.getPoints()));
		seriesNode.setAttribute(RANK, FinishPosition.toString(sp.getPosition()));
		seriesNode.setAttribute(TIED, Boolean.toString(sp.isTied()));
	}

	private static final String RACERESULT = "RaceResult";
	private static final String SERIESRESULT = "SeriesResult";
	private static final String NETPOINTS = "NetPoints";
	private static final String RANK = "Rank";
	private static final String TIED = "Tied";
	private static final String RACEPOINTS = "RacePoints";
	private static final String DISCARD = "Discard";
	private static final String SCORECODE = "ScoreCode";
	private static final String TOTALPOINTS = "TotalPoints";

	private DecimalFormat pointsFormat = new DecimalFormat("#.##");

	public static void main(String[] args) {
		try {
			Regatta reg = RegattaManager.readTestRegatta(
					"US_SAILING's_Rolex_Miami_OCR_-_470.regatta");
			Xrr1_3Manager exporter = new Xrr1_3Manager();
			exporter.setFilename("testExportISAFREsults.xml");

			exporter.export(reg);

		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}

/**
 * $Log: ActionExport.java,v$
 * 
 */
