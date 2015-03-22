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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.manager.RegattaManager;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Boat;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Fleet;
import org.gromurph.javascore.model.Penalty;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SailId;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.javascore.model.scoring.ScoringLowPoint;
import org.gromurph.javascore.model.scoring.ScoringOptions;
import org.gromurph.javascore.model.scoring.SingleStageScoring;
import org.gromurph.util.Exporter;
import org.gromurph.util.Person;
import org.gromurph.util.Util;
import org.gromurph.util.WarningList;
import org.gromurph.xml.DocumentException;
import org.gromurph.xml.IDocumentWriter;
import org.gromurph.xml.PersistentNode;
import org.gromurph.xml.XmlUtil;
import org.gromurph.xml.legacy.DocumentWriterImpl_legacy;
import org.sailing.util.SailingServices;

public class Xrr1_3Manager implements Exporter, Constants {
	static ResourceBundle res = JavaScoreProperties.getResources();

	//static ResourceBundle resUtil = Util.getResources();

	public Xrr1_3Manager() {
		nextID = 0;
		root = null;
		teamKeys = new TreeMap<Entry, String>();
	}

	private Regatta regatta;

	String filename;

	public void setFilename(String f) throws IOException {
		filename = f;
	}

	//    public void setWriter(Writer aWriter) {
	//	dw = new DocumentWriter(aWriter);
	//    }

	public void export( Regatta reg) throws IOException {
		File file = Util.getFile( "", filename);
		Writer w = new FileWriter(file);
		export(w, reg);
	}

	public String exportToString( Regatta reg) throws IOException {
		StringWriter sw = new StringWriter();
		export(sw, reg);
		return sw.toString();
	}

	private void export( Writer w, Regatta reg) throws IOException {
		if (reg == null) return;
		
		try {
	    	IDocumentWriter dw = new DocumentWriterImpl_legacy( w);
    		regatta = reg;
    		root = dw.createRootNode(ROOT);
    		buildXML();
	    	dw.saveObject( root, false);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	PersistentNode root;
	IDocumentWriter dw;

	public static final String ISAF_REPLY = "Reply from ISAF";
	public static final String VALIDATION_ERRORS = "Validation Errors";
	public static final String XML = "XML";
	
	public boolean postXrr( Regatta reg, Map<String,String> elist) {
		boolean isValid = false;
		try {
			String x = exportToString( reg);
			
			// dump the xml to file
			String baseFileName = Util.getWorkingDirectory() + "/xrr" + Long.toString(System.currentTimeMillis());
			String dumpFileName = baseFileName + ".xml";
			FileOutputStream fos = new FileOutputStream(dumpFileName);
			PrintWriter fw = new PrintWriter(fos);
			fw.println(x);
			fw.close();

			List<String> errors = new ArrayList<String>();
			SailingServices ss = new SailingServices();
			
			isValid = ss.validateXrrString(x, errors);
			if (!isValid) {
				elist.put( VALIDATION_ERRORS, errors.toString());
				elist.put( XML, x);
				elist.put( ISAF_REPLY,  "Not sent");
			} else {
				String response = "No response from isaf";
				response = ss.sendResults( x);
				
				// dump reply to file
				dumpFileName = baseFileName + ".reply.txt";
				fos = new FileOutputStream(dumpFileName);
				fw = new PrintWriter(fos);
				fw.println(response);
				fw.close();
				
				elist.put( ISAF_REPLY,  response);
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			elist.put("Exception",  e.toString());
			elist.put("Reply from ISAF",  "Exception");
		}
		return isValid;
	}
	
	private int nextID;

	Map<Entry, String> teamKeys;
	public WarningList warnings;

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
	private static final String TEAM = "Team";
	private static final String XSD_URL = org.sailing.util.XmlUtil.XRR_SCHEMA_1_3;

	private String createKey() {
		return Integer.toString(nextID++);
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
		root.setAttribute(DATE, xmlDateFormatter.format(regatta.getSaveDate()));
		root.setAttribute(TIME, xmlTimeFormatter.format(regatta.getSaveDate()));
		root.setAttribute(SCHEMATAG, XSD_URL);
		root.setAttribute(XSI, "http://www.w3.org/2001/XMLSchema-instance");

		exportEntries(regatta.getAllEntries());
		exportEventElement();
	}

	private void exportEntries(EntryList entryList) {
		if (entryList == null || entryList.size() == 0)
			return;
		for (Entry entry : entryList) {
			exportSailorsAndTeams(entry);
		}
	}
	
	private SimpleDateFormat xmlDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	private SimpleDateFormat xmlDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat xmlTimeFormatter = new SimpleDateFormat("HH:mm:ss");


	List<String> importLog = new ArrayList<String>();

	Map<String, Person> personList;
	Map<String, Boat> boatList;
	Map<String, Entry> teamList;
	Map<String, Division> divList;
	Map<String, Race> raceList;
	Map<String, Long> raceStartList;

	PersistentNode event;

	public Regatta parse() throws IOException {
		try {
			root = XmlUtil.readDocument(new FileReader(filename));
		} catch (DocumentException de) {
			throw new IOException(de.toString());
		}

		regatta = new Regatta();
		regatta.removeAllDivisions(); // add what comes in from xml
		warnings = new WarningList();

		parseXML();
		return regatta;
	}

	private void parseXML() {

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

		parsePersonList_();
		parseBoatList();
		parseTeamList();

		parseEventElement();
		parseRaceList();
		parseDivisionList();
		parseRaceResultList();

		regatta.scoreRegatta();

		parseAndCheckSeriesResultList();
	}

	private List<PersistentNode> getElements(PersistentNode node, String tag) {
		List<PersistentNode> list = new ArrayList<PersistentNode>();
		for (PersistentNode child : node.getElements()) {
			//String n = child.getName();
			if (child.getName().equals(tag))
				list.add(child);
		}
		return list;
	}

	private void parsePersonList_() {
		List<Person> persons = xmlToPersonList(root);
		for ( Person p : persons) {
			personList.put( p.getKey(), p);
		}
		
	}

	public List<Person> xmlToPersonList( PersistentNode topNode) {
		List<Person> persons = new ArrayList<Person>();
		for (PersistentNode node : getElements(topNode, PERSON)) {
			Person p = parsePersonElement(node);
			String key = node.getAttribute(PERSONID);
			persons.add(p);
		}
		return persons;
	}

	private void parseBoatList() {
		boatList = new HashMap<String, Boat>();
		for (PersistentNode node : getElements(root, BOAT)) {
			Boat b = parseBoatElement(node);
			String key = node.getAttribute(BOATID);
			boatList.put(key, b);
		}
	}

	private void parseDivisionList() {
		event = root.getElement(EVENT);
		divList = new HashMap<String, Division>();
		for (PersistentNode divNode : getElements(event, DIVISION)) {
			Division div = parseDivisionElement(divNode);
			String key = divNode.getAttribute(DIVID);
			divList.put(key, div);
			regatta.addDivision(div);
		}
	}

	private void parseRaceList() {
		raceList = new HashMap<String, Race>();
		raceStartList = new HashMap<String, Long>();
		for (PersistentNode raceNode : getElements(event, RACE)) {
			Race race = parseRaceElement(raceNode);
			regatta.addRace(race);
		}
	}

	private void parseRaceResultList() {

		// run through linking teams with divisions, and noting number of throwouts
		for (PersistentNode divNode : getElements(event, DIVISION)) {
			Division div = regatta.getDivision(divNode.getAttribute(DIVTITLE));

			parseRaceResultEntries(divNode, div);
		}

		// now run through for the results
		for (PersistentNode divNode : getElements(event, DIVISION)) {
			Division div = regatta.getDivision(divNode.getAttribute(DIVTITLE));
			parseRaceResultFinish(divNode, div);
		}
	}

	private void parseRaceResultFinish(PersistentNode divNode, Division div) {
		for (PersistentNode rrNode : getElements(divNode, RACERESULT)) {
			Entry entry = teamList.get(rrNode.getAttribute(TEAMID));
			Race race = raceList.get(rrNode.getAttribute(RACEID));
			Finish finish = new Finish(race, entry);

			String rankString = rrNode.getAttribute(RACERANK);
			if (rankString != null)
				rankString = rankString.trim();
			boolean haveRank = (rankString != null && rankString.length() > 0);
			long rank = Penalty.NOFINISH;
			if (haveRank) {
				try {
					rank = (long) Integer.parseInt(rankString);
				} catch (Exception e) {
					warnings.add("Non-numeric rank, " + rankString + ", for " + entry.toString() + " in race "
							+ race.getName());
				}
			}

			String code = rrNode.getAttribute(SCORECODE);
			if (code != null)
				code = code.trim();

			String ptsString = rrNode.getAttribute(RACEPOINTS);
			if (ptsString != null)
				ptsString = ptsString.trim();
			double pts = 0;
			if (ptsString != null && ptsString.length() > 0) {
				try {
					pts = Double.parseDouble(ptsString);
				} catch (Exception e) {
					warnings.add("Non-numeric points, " + ptsString + ", for " + entry.toString() + " in race "
							+ race.getName());
				}
			}

			if (code == null || code.length() == 0) {
				// no scoring code, so no penalty
				finish.setPenalty(new Penalty(Penalty.NO_PENALTY));
				if (haveRank)
					finish.setFinishPosition(new FinishPosition(rank));
				else
					finish.setFinishPosition(new FinishPosition((int) Math.floor(pts)));
			} else {
				Penalty p = Penalty.parsePenalty(code);
				finish.setPenalty(p);
				FinishPosition fp = null;
				// first settle the finish position (rank)
				if (p.isFinishPenalty()) {
					// no finish pos expected
					fp = new FinishPosition(p.getPenalty());
				} else if (haveRank) {
					fp = new FinishPosition(rank);
				} else { // no rank, use points, truncated
					fp = new FinishPosition((int) Math.floor(pts));
				}
				finish.setFinishPosition(fp);

				// now cope with other penalty issues
				// have to do % penalties after we set all entries...cuz we have to back track it
				if (!p.isFinishPenalty() && !p.isDsqPenalty()) {
					// only care about any of this for penalties that
					// should have finishes.. dsq/dnf already handled
					// we get here if we have resulting points, but not original rank
					// have to calculate it
					if (p.hasPenalty(Penalty.RDG)) {
						p.setPoints(pts);
					} else {
						int p20times = 0;
						if (p.hasPenalty(Penalty.ZFP))
							p20times++;
						if (p.hasPenalty(Penalty.ZFP2))
							p20times++;
						if (p.hasPenalty(Penalty.ZFP3))
							p20times++;
						if (p.hasPenalty(Penalty.SCP))
							p20times++;

						if (p20times > 0) {
							double dncPoints = div.getNumEntries() + 1;
							if (dncPoints == pts) {
								// points are maxed out at DNC, no way to know what finish was
								p = new Penalty(Penalty.RDG);
								p.setRedressLabel("MAN");
								p.setPoints(pts);
								p.setNote("Percentage penalty maxed at DNC, " + code);
								finish.setPenalty(p);
								finish.setFinishPosition(new FinishPosition(Penalty.NOFINISH));
								warnings.add("Unable to deduce finish position, setting pts to manual, for "
										+ entry.toString() + " in race " + race.getName());
							} else {
								double p20Points = p20times * Math.round(div.getNumEntries() * 0.20);
								rank = (int) (pts - p20Points);
								finish.setFinishPosition(new FinishPosition(rank));
							}
						}
						// nothing to do if code is AVG
						// import of TME, TMC, and ARP not supported
					}
				}
			}

			race.setFinish(finish);
		}
	}

	private void parseRaceResultEntries(PersistentNode divNode, Division div) {
		int nThrowouts = 0;
		String throwRaceId = null;

		for (PersistentNode rrNode : getElements(divNode, RACERESULT)) {
			Entry entry = teamList.get(rrNode.getAttribute(TEAMID));
			String raceId = rrNode.getAttribute(RACEID);

			// work on calculating number of throwouts
			if (throwRaceId == null)
				throwRaceId = rrNode.getAttribute(RACEID);
			if (throwRaceId.equals(raceId)) {
				String thrown = rrNode.getAttribute(DISCARD);
				if (thrown != null && Boolean.parseBoolean(thrown))
					nThrowouts++;
			}

			// set the entry's division if not already done
			if (entry.getDivision().contains(Division.NONE)) {
				try {
					entry.setDivision(div);
					regatta.addEntry(entry);
				} catch (Exception e) {
					warnings.add("Unable to put entry, " + entry.toString() + ", into division, " + div.getName()
							+ ", e=" + e.toString());
				}
			} else if (entry.getDivision() != div) {
				warnings.add("Entry, " + entry.toString() + ", has conflicting division, keeping "
						+ entry.getDivision().getName() + ", ignoring " + div.getName());
			}

			// link race and starttime, now that we have a division for the race
			Race r = raceList.get(raceId);
			long t = raceStartList.get(raceId);
			r.setIsRacing(div, true);
			r.setStartTime(div, t);
		}

		// set up throwouts based on the number we saw in the input
		SingleStageScoring mgr = (SingleStageScoring) regatta.getScoringManager();
		if (mgr.getModel() instanceof ScoringLowPoint) {
			ScoringOptions s = mgr.getModel().getOptions();
			if (nThrowouts == 0)
				s.setThrowoutScheme(ScoringLowPoint.THROWOUT_NONE);
			List<Integer> t = new ArrayList<Integer>(nThrowouts);
			for (int x = 0; x < nThrowouts; x++)
				t.add(x + 2);
			s.setThrowouts(t);
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

	private PersistentNode exportSailorsAndTeams(Entry entry) {

		PersistentNode teamNode = root.createChildElement(TEAM);

		String boatID = exportBoatElement(entry.getBoat());

		String teamID = Integer.toString(sTeamIDIndex++);
		Person skipper = entry.getSkipper();

		teamKeys.put(entry, teamID);

		teamNode.setAttribute(TEAMID, teamID);
		
		teamNode.setAttribute(NOC, (skipper != null) ? sailorIDToCountry(skipper) : "");
		teamNode.setAttribute(TEAMNAME, "");
		teamNode.setAttribute(GENDER, entry.getDivision().getGender());
		teamNode.setAttribute(BOATID, boatID);

		if ((entry.getSkipper() != null)) {
			String key = exportPersonElement(entry.getSkipper());
			exportCrewElement(teamNode, key, entry.getSkipper(), POSITION_SKIPPER);
		}
		for (Person crew : entry.getCrewList()) {
			String key = exportPersonElement(crew);
			exportCrewElement(teamNode, key, crew, POSITION_CREW);
		}

		return teamNode;
	}

	private void parseTeamList() {
		teamList = new HashMap<String, Entry>();
		for (PersistentNode node : getElements(root, TEAM)) {
			String boatid = node.getAttribute(BOATID);
			Boat boat = boatList.get(boatid);
			String teamid = node.getAttribute(TEAMID);
			String teamname = node.getAttribute(TEAMNAME);

			Entry team = new Entry();
			team.setBoat(boat);
			team.setBoatName(teamname);
			team.setSailId(boat.getSailId());

			for (PersistentNode crew : getElements(node, CREW)) {
				String personid = crew.getAttribute(PERSONID);
				String position = crew.getAttribute(POSITION);
				Person person = personList.get(personid);
				if (position.equals(POSITION_SKIPPER)) {
					team.setSkipper(person);
				} else {
					team.addCrew(person);
				}
			}
			teamList.put(teamid, team);
		}
	}

	private static final String PERSONID = "PersonID";
	private static final String FAMILYNAME = "FamilyName";
	private static final String GIVENNAME = "GivenName";
	private static final String NOC = "NOC";
	private static final String SAILORID = "IFPersonID";

	//<xsd:complexType name="Person">
	//	<xsd:attribute name="PersonID" type="xsd:string" use="required"/>
	//	<xsd:attribute name="IFPersonID" type="xsd:string" use="required"/>
	//	<xsd:attribute name="FamilyName" type="xsd:string" use="required"/>
	//	<xsd:attribute name="GivenName" type="xsd:string" use="optional"/>
	//	<xsd:attribute name="NOC" type="xsd:string" use="optional"/>
	//	<xsd:attribute name="Gender" type="Genders" use="optional"/>
	//</xsd:complexType>

	private String exportPersonElement(Person p) {
		String key = createKey(); //.toLowerCase());
		PersistentNode node = root.createChildElement( PERSON);
		node.setAttribute(PERSONID, key);
		node.setAttribute(FAMILYNAME, p.getLast());
		node.setAttribute(GIVENNAME, p.getFirst());
		node.setAttribute(NOC, sailorIDToCountry(p));
		node.setAttribute(SAILORID, p.getSailorId().toUpperCase()); //.toLowerCase());
		return key;
	}

	private Person parsePersonElement(PersistentNode node) {
		Person p = new Person();
		p.setSailorId(node.getAttribute(SAILORID));
		p.setFirst(node.getAttribute(GIVENNAME));
		p.setLast(node.getAttribute(FAMILYNAME));
		return p;
	}

	private void exportCrewElement(PersistentNode teamNode, String key, Person crew, String position) {
		//<xsd:complexType name="Crew">
		//	<xsd:attribute name="PersonID" type="xsd:string" use="required"/>
		//	<xsd:attribute name="Position" type="CrewPositions" use="required"/>
		//</xsd:complexType>
		PersistentNode crewNode = teamNode.createChildElement( CREW);
		crewNode.setAttribute(PERSONID, key);
		crewNode.setAttribute(POSITION, position);
	}

	private String sailorIDToCountry(Person p) {
		if (p == null) return "";
		if (p.getSailorId() != null && p.getSailorId().length() >= 3) {
			return p.getSailorId().substring(0, 3).toUpperCase();
		} else {
			return "";
		}
	}

	private static final String BOATID = "BoatID";
	private static final String BOATNAME = "BoatName";
	private static final String SAILNUMBER = "SailNumber";

	//<xsd:complexType name="Boat">
	//	<xsd:attribute name="BoatID" type="xsd:string" use="required"/>
	//	<xsd:attribute name="BoatName" type="xsd:string" use="optional"/>
	//	<xsd:attribute name="SailNumber" type="xsd:string" use="required"/>
	//</xsd:complexType>

	public String exportBoatElement(Boat boat) {
		if ((boat != null)) {
			String key = createKey();
			PersistentNode node = root.createChildElement( BOAT);
			node.setAttribute(BOATID, key);
			node.setAttribute(SAILNUMBER, boat.getSailId().toString());
			node.setAttribute(BOATNAME, boat.getName());
			return key;
		}
		return null;
	}

	public Boat parseBoatElement(PersistentNode node) {
		Boat b = new Boat();
		b.setName(node.getAttribute(BOATNAME));
		b.setSailId(new SailId(node.getAttribute(SAILNUMBER)));
		return b;
	}

	private static final String EVENT = "Event";
	private static final String EVENTTITLE = "Title";
	private static final String EVENTID = "EventID";
	private static final String IFEVENTID = "IFEventID";
	private static final String DIVISION = "Division";

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

	private void exportEventElement() {

		event = root.createChildElement( EVENT);
		event.setAttribute(EVENTTITLE, regatta.getName());
		event.setAttribute(EVENTID, createKey());
		event.setAttribute(IFEVENTID, regatta.getIfEventId());

		exportRaces();

		for (Division div : regatta.getDivisions()) {
			exportDivision(event, div);
		}

		if (!regatta.isMultistage()) {
			for (Fleet div : regatta.getFleets()) {
				exportDivision(event, div);
			}
			for (SubDivision div : regatta.getSubDivisions()) {
				exportDivision(event, div);
			}
		}
	}

	private void parseEventElement() {
		event = root.getElement(EVENT);
		regatta.setName(event.getAttribute(EVENTTITLE));
		regatta.setIfEventId(event.getAttribute(IFEVENTID));
		//return event;
	}

	private static final String DIVID = "DivisionID";
	private static final String DIVTITLE = "Title";
	private static final String IFCLASSID = "IFClassID";

	private PersistentNode exportDivisionElement(PersistentNode divisionList, AbstractDivision div) {
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
		if (div.getGender() != null)
			divNode.setAttribute(GENDER, div.getGender());

		return divNode;
	}

	public Division parseDivisionElement(PersistentNode node) {
		Division div = new Division();
		div.setName(node.getAttribute(DIVTITLE));
		div.setIfClassId(node.getAttribute(IFCLASSID));
		String gender = node.getAttribute(GENDER);
		if (gender != null)
			div.setGender(gender);
		return div;
	}

	private static final String RACEID = "RaceID";
	private static final String RACENUMBER = "RaceNumber";
	private static final String RACENAME = "RaceName";
	private static final String RACESTARTDATE = "RaceStartDate";
	private static final String RACESTARTTIME = "RaceStartTime";
	private static final String RACESTATUS = "RaceStatus";

	private void exportDivision(PersistentNode parentNode, AbstractDivision div) {

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
		PersistentNode divNode = exportDivisionElement(parentNode, div);

		SeriesPointsList spl = regatta.getScoringManager().getAllSeriesPoints(div);
		if (spl == null || spl.size() == 0)
			return;

//		RaceList races = new RaceList();
//		for (Race r : regatta.getRaces()) {
//			if (r.isSailing(div))
//				races.add(r);
//		}

		EntryList entries = div.getEntries();
		for (Entry e : entries) {
			double totalPoints = 0;
			for (Race r : regatta.getRaces()) {
				if (r.isSailing(e)) {
					RacePoints p = regatta.getScoringManager().getRacePointsList().find(r, e, div);
					if (p != null) {
    					Finish f = r.getFinish(e);
    					exportRaceResultElement(divNode, p, f);
    					totalPoints += p.getPoints();
					}
				}
			}
			exportSeriesResultElement(divNode, div, e, totalPoints);
		}
	}

	private void exportRaces() {
		regatta.getRaces().sort();
		int raceNum = 1;
		for (Race r : regatta.getRaces()) {
			exportRaceElement(r, raceNum++);
		}
	}

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

	private void exportRaceElement(Race r, int raceNum) {
		PersistentNode node = event.createChildElement( RACE);
		node.setAttribute(RACEID, Integer.toString(r.getId()));
		node.setAttribute(RACENUMBER, Integer.toString(raceNum++));
		node.setAttribute(RACENAME, r.getName());
		node.setAttribute(RACESTARTDATE, xmlDateFormatter.format(r.getStartDate()));

		AbstractDivision firstStartingDivision = r.getDivisionsByStartOrder(true).get(0);
		node.setAttribute(RACESTARTTIME, xmlTimeFormatter.format(r.getStartTimeRaw(firstStartingDivision)));
		node.setAttribute(RACESTATUS, regatta.isFinal() ? "final" : "provisional");
	}

	private Race parseRaceElement(PersistentNode node) {
		String key = node.getAttribute(RACEID);
		Race r = new Race();
	
		r.setName(node.getAttribute(RACENAME));
	
		if (r.getName().toLowerCase().contains("medal")) {
			r.setMedalRace(true);
			// tiebreaking to tie rrs a82 medal
			// create two subdivisions
			// handle grouping of them and group rank
			// later - any entry with in the medal race gets put into the medal subdivision, others in the not medal
			
			// see regattamanager.splitfleettopbottom
		}
		
		String att = node.getAttribute(RACESTARTDATE);
		try {
			r.setStartDate(xmlDateFormatter.parse(att));
		} catch (Exception e) {
			importLog.add("error parsing date " + att + " for race " + r.getName());
		}
		att = node.getAttribute(RACESTARTTIME);
		try {
			raceStartList.put(key, xmlTimeFormatter.parse(att).getTime());
		} catch (Exception e) {
			importLog.add("error parsing time " + att + " for race " + r.getName());
		}

		raceList.put(key, r);
		return r;
	}

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
	//
	// eg <RaceResult TeamID="FRABJ" Discard="false" RaceID="111" RacePoints="2" ScoreCode=""/>
	private void exportRaceResultElement(PersistentNode parentNode, RacePoints p, Finish f) {
		PersistentNode resultNode = parentNode.createChildElement( RACERESULT);
		resultNode.setAttribute(TEAMID, teamKeys.get(p.getEntry()));
		resultNode.setAttribute(RACEID, Integer.toString(p.getRace().getId()));
		resultNode.setAttribute(RACEPOINTS, pointsFormat.format(p.getPoints()));
		if (!f.getPenalty().isFinishPenalty()) {
			resultNode.setAttribute(RACERANK, f.getFinishPosition().toString());
		} else {
			int fauxRank = (int) p.getPoints() - 1;
			resultNode.setAttribute(RACERANK, pointsFormat.format(fauxRank));
		}
		resultNode.setAttribute(DISCARD, Boolean.toString(p.isThrowout()));
		resultNode.setAttribute(SCORECODE, penaltyToIsafCode(p.getFinish().getPenalty()));
	}
	
    // in XRR   '[ARB, BFD, DGM, DNC, DNE, DNF, DNS, DPI, DSQ, OCS, PTS, RAF, RDG, RET, SCP, ZFP, RTD, ]'
    // JS 7     '[     BFD, DGM, DNC, DNE, DNF, DNS, DPI, DSQ, OCS,           RDG, RET  SCP, ZFP,      ]' 
    //             + TME, AVG, CNF, TME, ZFP2, ZFP3, TMC

	private String penaltyToIsafCode( Penalty penalty) {
		String code = penalty.toString(false);

		if (penalty.hasPenalty( Penalty.TLE)) {
			//TODO this is not always true... but isaf doesnt support anything else
			code = "DNF";
		}
		long pen = (penalty.getPenalty() & Constants.OTHER_MASK);
		if ((pen & ZFP2) != 0) code = "ZFP";
		if ((pen & ZFP3) != 0) code = "ZFP";

		// TEMP TEMP TEMP TODO add DPI to interface, then change SCP back to SCP
		if ((pen & SCP) != 0) code = "DPI";

		if ((pen & CNF) != 0) code = "DPI";
		if ((pen & TME) != 0) code = "DPI";
		if ((pen & TMC) != 0) code = "DPI";
		if ((pen & TMP) != 0) code = "DPI";
		
		if ((pen & RDG) != 0) code = "RDG";
		if ((pen & AVG) != 0) code = "RDG";
		
		return code;
	}

	private void exportSeriesResultElement(PersistentNode parentNode, AbstractDivision div, Entry entry,
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
		if (sp == null)
			return;

		PersistentNode seriesNode = parentNode.createChildElement( SERIESRESULT);
		seriesNode.setAttribute(TEAMID, teamKeys.get(sp.getEntry()));
		seriesNode.setAttribute(TOTALPOINTS, pointsFormat.format(totalPoints));
		seriesNode.setAttribute(NETPOINTS, pointsFormat.format(sp.getPoints()));
		seriesNode.setAttribute(RANK, FinishPosition.toString(sp.getPosition()));
		seriesNode.setAttribute(TIED, Boolean.toString(sp.isTied()));
	}

	private void parseAndCheckSeriesResultList() {

		// run through linking teams with divisions, and noting number of throwouts
		for (PersistentNode divNode : getElements(event, DIVISION)) {
			Division div = regatta.getDivision(divNode.getAttribute(DIVTITLE));
			SeriesPointsList seriesList = regatta.getScoringManager().getAllSeriesPoints(div);

			for (PersistentNode srNode : getElements(divNode, SERIESRESULT)) {
				Entry entry = teamList.get(srNode.getAttribute(TEAMID));
				SeriesPoints myPts = seriesList.find(entry, div);

				String s = srNode.getAttribute(NETPOINTS);
				if (s != null)
					s = s.trim();
				try {
					double netPoints = (double) Double.parseDouble(s);
					double myPoints = myPts.getPoints();
					myPoints = Math.floor(myPoints * 100) / 100;
					if (netPoints != myPoints) {
						warnings.add("Imported net points, " + s + ", for " + entry.toString()
								+ " do not match calculated net, " + Double.toString(myPoints));
					}
				} catch (Exception e) {
					warnings.add("Non-numeric total points, " + s + ", for " + entry.toString());
				}

				s = srNode.getAttribute(RANK);
				if (s != null)
					s = s.trim();
				try {
					long rank = (long) Long.parseLong(s);
					long myRank = myPts.getPosition();
					if (rank != myRank) {
						warnings.add("Imported rank, " + s + ", for " + entry.toString()
								+ " does not match calculated rank, " + Double.toString(myRank));
					}
				} catch (Exception e) {
					warnings.add("Non-numeric total points, " + s + ", for " + entry.toString());
				}
			}
		}

		// now run through for the results
		for (PersistentNode divNode : getElements(event, DIVISION)) {
			Division div = regatta.getDivision(divNode.getAttribute(DIVTITLE));
			parseRaceResultFinish(divNode, div);
		}
	}

	private static final String RACERESULT = "RaceResult";
	private static final String SERIESRESULT = "SeriesResult";
	private static final String NETPOINTS = "NetPoints";
	private static final String RANK = "Rank";
	private static final String TIED = "Tied";
	private static final String RACEPOINTS = "RacePoints";
	private static final String RACERANK = "RaceRank";
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

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

/**
 * $Log: ActionExport.java,v$
 * 
 */
