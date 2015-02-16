//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Mailer.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mailer
{
    public static void sendMail( String toEmail, String to, String from, String header, String message)
    {
        sendMail( to, from, header, message, "");
    }

    private static String adminEmail;

    public static void setAdminEmail( String admin)
    {
        adminEmail = admin;
    }

    public static void setMailHost( String host)
    {
        System.getProperties().put("mail.host", host);
    }

    public static void send( String toEmail, String to, String fromEmail, String from, String header, String message)
    {
        try
        {
            String e = toEmail;
            if (adminEmail != null) e = e + "," + adminEmail;
            URL u = new URL("mailto:" + e);
            URLConnection c = u.openConnection();
            c.setDoInput(false);
            c.setDoOutput(true);

            c.connect();

            PrintWriter out = new PrintWriter(
                new OutputStreamWriter( c.getOutputStream()));

            out.println("From: \"" + from + "\" <" + fromEmail + ">" );
            out.println( "To: " + to);
            out.println( "Subject: " + header);
            out.println(); // blank line to show end of headers
            out.println( message);
            out.close();
        }
        catch (Exception e)
        {
            logger.error( e.toString(), e);
        }
    }

	private static Logger logger = LoggerFactory.getLogger(Mailer.class);
}
/**
 * $Log: Mailer.java,v $
 * Revision 1.4  2006/01/15 21:10:35  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.4  2004/04/10 20:49:39  sandyg
 * Copyright year update
 *
 * Revision 1.3  2003/01/04 17:53:05  sandyg
 * Prefix/suffix overhaul
 *
*/
