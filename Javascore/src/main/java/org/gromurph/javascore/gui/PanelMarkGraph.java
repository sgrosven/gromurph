//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelMarkGraph.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ResourceBundle;

import javax.swing.*;

import org.gromurph.javascore.JavaScoreProperties;

/*
 * This is like the FontDemo applet in volume 1, except that it
 * uses the Java 2D APIs to define and render the graphics and text.
 */
public class PanelMarkGraph extends JPanel {
	static ResourceBundle res = JavaScoreProperties.getResources();

	Color fLineColors[] = new Color[] { new Color(255, 0, 0), // red,
			new Color(100, 100, 100), // gray,
			new Color(0, 193, 0), // green,
			new Color(0, 150, 150), // light blue,
			new Color(255, 128, 0), // orange,
			new Color(0, 0, 255), // blue,
			new Color(128, 0, 128) }; // purple

	FontMetrics fFontMetrics;
	int fGridWidth;
	int fGridHeight;
	int fCanvasWidth;
	int fCanvasHeight;
	int fLeftBorder = 10;
	MarkGraphInterface fModel;
	GraphPane fGraph;

	public PanelMarkGraph(MarkGraphInterface model) {
		super();
		setModel(model);

		setLayout(new BorderLayout());
		fGraph = new GraphPane();
		add(new JScrollPane(fGraph,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER),
				BorderLayout.CENTER);

	}

	public PanelMarkGraph() {
		this(null);
	}

	public void setModel(MarkGraphInterface m) {
		fModel = m;
		if (fModel == null)
			return;
		fFontMetrics = getFontMetrics(getFont());

		fGridWidth = 30;
		fGridHeight = fFontMetrics.getHeight();

		fCanvasWidth = 640;
		fCanvasHeight = fGridHeight * (fModel.getNumEntries() + 2)
				+ fLeftBorder;

		fGraph.setSize(fCanvasWidth, fCanvasHeight);
		fGraph.setPreferredSize(new Dimension(fCanvasWidth, fCanvasHeight));

	}

	public double markToX(int mark) {
		return fLeftBorder / 2 + fGridWidth / 2 + (mark * fGridWidth);
	}

	public double markToY(int mark, int pos) {
		// if mark is same as max marks, was final finish, not a lookup
		String markName = "M" + Integer.toString(mark);
		int y = (int) ((mark < fModel.getNumMarks()) ? fModel.getMarkPosition(
				pos, markName) : pos + 1);
		return fGridHeight / 2 + fLeftBorder + y * fGridHeight;
	}

	public Color markColor(int pos) {
		int i = pos - (pos / fLineColors.length) * fLineColors.length;
		return fLineColors[i];
	}

	public class GraphPane extends JLabel {
		public GraphPane() {
			super("");
		}

		@Override public void paint(Graphics g) {
			if (fModel == null)
				return;

			int nComps = fModel.getNumEntries();
			int nMarks = fModel.getNumMarks();

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			int x = 10;
			int y = fGridHeight + 5;

			g2.setPaint(Color.black);
			for (int mark = 0; mark < nMarks; mark++) {
				g2
						.drawString(
								res.getString("MarkLabelMark") + (mark + 1), x,
								y);
				x += fGridWidth;
			}
			g2.drawString(res.getString("GenFin"), x, y);
			x += fGridWidth;
			g2.drawString(res.getString("MarkLabelPos"), x, y);
			x += fGridWidth * 2;
			g2.drawString(res.getString("FinishLabelSkipper"), x, y);

			int xName = x;

			x = 5;
			y += fGridHeight;

			for (int pos = 0; pos < nComps; pos++) {
				g2.setPaint(markColor(pos));
				g2.drawString(fModel.getEntryName(pos), xName, y + fGridHeight
						* pos);
				g2.drawString(fModel.getFinishString(pos), xName - fGridWidth
						* 2, y + fGridHeight * pos);

				double lastx = 0;
				double lasty = 0;

				for (int mark = 0; mark < nMarks; mark++) {
					if (fModel.getMarkPosition(pos, res
							.getString("MarkLabelMark")
							+ Integer.toString(mark)) >= 0) // missing rounding
					{
						double x1 = markToX(mark);
						double y1 = markToY(mark, pos);
						g2.draw(new Ellipse2D.Double(x1 - 3, y1 - 3, 6, 6));

						if (mark > 0) {
							double xright = markToX(mark);
							double yright = markToY(mark, pos);
							// not the first mark
							g2.draw(new Line2D.Double(lastx, lasty, xright,
									yright));
						}

						lastx = x1;
						lasty = y1;
					}
				}
				double x1 = markToX(nMarks) - 3;
				double y1 = markToY(nMarks, pos) - 3;
				g2.draw(new Ellipse2D.Double(x1, y1, 6, 6));

				double xright = markToX(nMarks);
				double yright = markToY(nMarks, pos);
				// to finish position
				if (lastx > 0)
					g2.draw(new Line2D.Double(lastx, lasty, xright, yright));
			}

		}
	} // inner class GraphPane

}
/**
 * $Log: PanelMarkGraph.java,v $ Revision 1.5 2006/05/19 05:48:42 sandyg final
 * release 5.1 modifications
 * 
 * Revision 1.4 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.6.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.6 2004/04/10 20:49:38 sandyg Copyright year update
 * 
 * Revision 1.5 2003/04/27 21:06:00 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.4 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
