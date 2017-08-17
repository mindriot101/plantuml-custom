/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 *
 *
 */
package net.sourceforge.plantuml.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.BackSlash;
import net.sourceforge.plantuml.CharSequence2;
import net.sourceforge.plantuml.CharSequence2Impl;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.utils.StartUtils;
import net.sourceforge.plantuml.version.IteratorCounter2;
import net.sourceforge.plantuml.version.IteratorCounter2Impl;

/**
 * Represents the textual source of some diagram. The source should start with a <code>@startfoo</code> and end with
 * <code>@endfoo</code>.
 * <p>
 * So the diagram does not have to be a UML one.
 * 
 * @author Arnaud Roques
 * 
 */
final public class UmlSource {

	final private List<String> source;
	final private List<CharSequence2> source2;

	// final private int startLine;
	// final private LineLocation startLocation;

	/**
	 * Build the source from a text.
	 * 
	 * @param source
	 *            the source of the diagram
	 * @param checkEndingBackslash
	 *            <code>true</code> if an ending backslash means that a line has to be collapsed with the following one.
	 * @param startLine
	 */
	public UmlSource(List<CharSequence2> source, boolean checkEndingBackslash, int startLine) {
		// this.startLocation = source.get(0).getLocation();
		// this.startLine = startLine;
		final List<String> tmp = new ArrayList<String>();
		final List<CharSequence2> tmp2 = new ArrayList<CharSequence2>();

		if (checkEndingBackslash) {
			final StringBuilder pending = new StringBuilder();
			for (CharSequence2 cs : source) {
				final String s = cs.toString2();
				if (StringUtils.endsWithBackslash(s)) {
					pending.append(s.substring(0, s.length() - 1));
				} else {
					pending.append(s);
					tmp.add(pending.toString());
					tmp2.add(new CharSequence2Impl(pending.toString(), cs.getLocation()));
					pending.setLength(0);
				}
			}
		} else {
			for (CharSequence2 s : source) {
				tmp.add(s.toString2());
				tmp2.add(s);
			}
		}
		this.source = Collections.unmodifiableList(tmp);
		this.source2 = Collections.unmodifiableList(tmp2);
	}

	/**
	 * Retrieve the type of the diagram. This is based on the first line <code>@startfoo</code>.
	 * 
	 * @return the type of the diagram.
	 */
	public DiagramType getDiagramType() {
		return DiagramType.getTypeFromArobaseStart(source.get(0));
	}

	/**
	 * Allows to iterator over the source.
	 * 
	 * @return a iterator that allow counting line number.
	 */
	public IteratorCounter2 iterator2() {
		return new IteratorCounter2Impl(source2);
	}

	/**
	 * Return the source as a single String with <code>\n</code> as line separator.
	 * 
	 * @return the whole diagram source
	 */
	public String getPlainString() {
		final StringBuilder sb = new StringBuilder();
		for (String s : source) {
			sb.append(s);
			sb.append('\r');
			sb.append(BackSlash.CHAR_NEWLINE);
		}
		return sb.toString();
	}

	public long seed() {
		long h = 1125899906842597L; // prime
		final String string = getPlainString();
		final int len = string.length();

		for (int i = 0; i < len; i++) {
			h = 31 * h + string.charAt(i);
		}
		return h;
	}

	/**
	 * Return a specific line of the diagram description.
	 * 
	 * @param n
	 *            line number, starting at 0
	 * @return
	 */
	private String getLine(int n) {
		if (n < 0 || n >= source.size()) {
			return "";
		}
		return source.get(n);
	}

	public String getLine(LineLocation n) {
		for (CharSequence2 s : source2) {
			if (s.getLocation().compareTo(n) == 0) {
				return s.toString();
			}
		}
		return null;
	}

	/**
	 * Return the number of line in the diagram.
	 * 
	 * @return
	 */
	public int getTotalLineCount() {
		return source.size();
	}

	/**
	 * Check if a source diagram description is empty. Does not take comment line into account.
	 * 
	 * @return <code>true<code> if the diagram does not contain information.
	 */
	public boolean isEmpty() {
		for (String s : source) {
			if (StartUtils.isArobaseStartDiagram(s)) {
				continue;
			}
			if (StartUtils.isArobaseEndDiagram(s)) {
				continue;
			}
			if (s.matches("\\s*'.*")) {
				continue;
			}
			if (StringUtils.trin(s).length() != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Retrieve the title, if defined in the diagram source. Never return <code>null</code>.
	 * 
	 * @return
	 */
	public Display getTitle() {
		final Pattern2 p = MyPattern.cmpile("(?i)^[%s]*title[%s]+(.+)$");
		for (String s : source) {
			final Matcher2 m = p.matcher(s);
			final boolean ok = m.matches();
			if (ok) {
				return Display.create(m.group(1));
			}
		}
		return Display.empty();
	}

	public boolean isStartDef() {
		return source.get(0).startsWith("@startdef");
	}

	public String getId() {
		final Pattern p = Pattern.compile("id=([\\w]+)\\b");
		final Matcher m = p.matcher(source.get(0));
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}

	// public final int getStartLine() {
	// return startLine;
	// }
}
