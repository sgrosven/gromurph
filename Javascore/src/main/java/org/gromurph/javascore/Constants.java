//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Constants.java,v 1.4 2006/01/15 21:10:37 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore;

import java.util.ResourceBundle;

/**
 * constants applicable to javascore
 */
public interface Constants {
	
	/**
     * these are the constants that defined the penalty values They come in
     * three groups:
     * 
     * Non-Finish penalty values are for boats that do not have a valid Finish
     * these are used in BOTH the FinishPosition class and in the Penalty class
     * These are not-bitwise penalties and a boat cannot have more than one of
     * these at a time. See FinishPosition class
     * 
     * Disqualification penalties are the various ways a boat can be
     * disqualified. These also are not bitwise, as a boat can only be
     * disqualified once. But a boat can be disqualified with or without a valid
     * finish. So a boat can carry both a non-finish penalty and a
     * disqualification penalty. See Penalty class
     * 
     * Other penalties are the various other ways a boat can be "dinged". This
     * includes check-in penalties, redress, percentage penalties, etc. These
     * ARE Bit-wise penalties, and a boat can have more than one of them. Also,
     * a boat CAN have a non-finish penalty and an other penalty, but a boat may
     * not have a disqualification penalty and "other" penalty. See Penalty
     * class
     * 
     * These penalty values are/should be used only internally to the program
     * and NOT written out to persistent storage (that is done by string name).
     * Therefore it should be safe to reset the values, provided the orders are
     * not changed and the types of penalties keep their bit-boundaries
     * straight.
     */

    // These masks break up the integer into the portions reserved for
    // each penalty type.
    public final static long HIGHEST_FINISH = 0x00001FFF; // highest possible
    // real finish

    public final static long NO_PENALTY = 0x00000000;

    // Disqualification penalties
    public final static long DSQ_MASK = 0x00000007;

    public final static long DSQ = 0x00000001; // these are not stable numbers!
    public final static long DNE = 0x00000002;
    public final static long RET = 0x00000003;
    public final static long OCS = 0x00000004;
    public final static long BFD = 0x00000005; // added for 2001 rules
    public final static long DGM = 0x00000006; // disqualified, gross
					       // misconduct, 2005 addition

    // Scoring penalties, these ARE BITWISE
    // available = 0x00000008;
    public final static long TME = 0x00000010; // new apr03, an amount of time
					       // applied to elapsed time
    public final static long ZFP = 0x00000020;
    public final static long AVG = 0x00000040;
    public final static long SCP = 0x00000080; // scoring penalty, pct of finish
					       // position
    public final static long RDG = 0x00000100;
    public final static long DPI = 0x00000200;
    public final static long CNF = 0x00000400;
    public final static long TMP = 0x00000800; // new apr03 time percent penalty
    public final static long ZFP2 = 0x00010000;
    public final static long ZFP3 = 0x00020000;
    public final static long TMC = 0x00040000; // new apr 09, corrected time

    public final static long OTHER_MASK = 0x000F1FF8;
    public final static long ZFP_MASK = 0x00030020;

    // available = 0x000F1000;

    // Non-finishing penalties, these show up in the finish order column
    // and can get set as Finish "Positions"
    public final static long NOFINISH_MASK = 0x0000E000;
    public final static long NOFINISH = 0x0000E000; // means no finish recorded
						    // yet
    public final static long DNC = 0x0000C000;
    public final static long DNS = 0x0000A000;
    public final static long DNF = 0x00008000;
    public final static long TLE = 0x00006000;
    // available = 0x00004000;
    // available = 0x00002000;

 // 12/12 - ISAF 2013-2016 scoring codes:
    // in XRR   '[ARB, BFD, DGM, DNC, DNE, DNF, DNS, DPI, DSQ, OCS, PTS, RAF, RDG, RET, SCP, ZFP, RTD, ]'
    // in App A '[     BFD, DGM, DNC, DNE, DNF, DNS, DPI, DSQ, OCS,           RDG, RET, SCP, ZFP       ]'
    // JS 7     '[     BFD, DGM, DNC, DNE, DNF, DNS, DPI, DSQ, OCS,           RDG, RET  SCP, ZFP,      ]' 
    //             + TME, AVG, CNF, TME, ZFP2, ZFP3, TMC
    
    // JS 5.8   '[     BFD, DGM, DNC, DNE, DNF, DNS,      DSQ, OCS,      RAF, RDG,      SCP, ZFP,      ]' 
    //             + TME, AVG, CNF, TME, ZFP2, ZFP3, TMC, ARP
    // JS 7.0 changes RAF becomes RET, deletes ARP - in favor of SCP
    //      FOR xrr export: TME, AVG, TMC -> RDG;   ZFP2, ZFP3 -> ZFP; 
    //      new support for DPI

	static ResourceBundle rb = JavaScoreProperties.getResources();

    public static enum ScoreCarryOver { 	
    	NONE { @Override public String toString() { return rb.getString( "ScoreCarryOver.NONE");}},
    	ALL { @Override public String toString() { return rb.getString( "ScoreCarryOver.ALL");}},
    	SEEDINGRACE { @Override public String toString() { return rb.getString( "ScoreCarryOver.SEEDINGRACE");}}
    }
    
    public static enum ThrowoutCarryOver { 	
    	NONE { @Override public String toString() { return rb.getString( "ThrowoutCarryOver.NONE");}},
    	ALL { @Override public String toString() { return rb.getString( "ThrowoutCarryOver.ALL");}},
    	MAX1 { @Override public String toString() { return rb.getString( "ThrowoutCarryOver.MAX1");}}
    }
    	
    public static enum TiebreakCarryOver { 	
    	NONE { @Override public String toString() { return rb.getString( "TiebreakCarryOver.NONE");}},
    	ALL { @Override public String toString() { return rb.getString( "TiebreakCarryOver.ALL");}}
    }

	public static final int THROWOUT_BYNUMRACES = 1;
	public static final int THROWOUT_PERXRACES = 2;
	public static final int THROWOUT_BESTXRACES = 3;
	public static final int THROWOUT_NONE = 4;

	public static final int TLE_DNF = 0;
	public static final int TLE_FINISHERSPLUS1 = 1;
	public static final int TLE_FINISHERSPLUS2 = 2;
	public static final int TLE_AVERAGE = 3;

	public static final int TIE_RRS_DEFAULT = 1;
	public static final int TIE_RRS_A82_ONLY = 2;
	public static final int TIE_RRS_B8 = 3;  
	public static final int TIE_NOTIEBREAKER = 4;

	public static String NOADVANCE = "NoAdvance";
    	
}

/**
 * $Log: Constants.java,v $ Revision 1.4 2006/01/15 21:10:37 sandyg resubmit at
 * 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:09 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:01 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.2.4.1 2005/11/01 02:36:01 sandyg Java5 update - using generics
 * 
 * Revision 1.2.2.1 2005/08/13 21:57:06 sandyg Version 4.3.1.03 - bugs 1215121,
 * 1226607, killed Java Web Start startup code
 * 
 * Revision 1.2 2004/04/10 20:49:28 sandyg Copyright year update
 * 
 * Revision 1.1 2003/04/20 15:43:58 sandyg added javascore.Constants to
 * consolidate penalty defs, and added new penaltys TIM (time value penalty) and
 * TMP (time percentage penalty)
 * 
 **/
