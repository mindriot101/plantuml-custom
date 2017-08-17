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
package net.sourceforge.plantuml.utils;

import net.sourceforge.plantuml.CharSequence2;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.core.DiagramType;

public class StartUtils {

	public static final Pattern2 patternFilename = MyPattern
			.cmpile("^[@\\\\]start[^%s{}%g]+[%s{][%s%g]*([^%g]*?)[%s}%g]*$");

	public static final String PAUSE_PATTERN = "(?i)((?:\\W|\\<[^<>]*\\>)*)[@\\\\]unpause";
	public static final String START_PATTERN = "(?i)((?:[^\\w~]|\\<[^<>]*\\>)*)[@\\\\]start";

	public static String beforeStartUml(final CharSequence2 result) {
		boolean inside = false;
		for (int i = 0; i < result.length(); i++) {
			final CharSequence2 tmp = result.subSequence(i, result.length());
			if (startsWithSymbolAnd("start", tmp)) {
				return result.subSequence(0, i).toString();
			}
			final String single = result.subSequence(i, i + 1).toString();
			if (inside) {
				if (single.equals(">")) {
					inside = false;
				}
				continue;
			}
			if (single.equals("<")) {
				inside = true;
			} else if (single.matches("[\\w~]")) {
				return null;
			}
		}
		return null;
		// final Matcher m = MyPattern.cmpile(START_PATTERN).matcher(result);
		// if (m.find()) {
		// return m.group(1);
		// }
		// return null;
	}

	public static boolean isArobaseStartDiagram(CharSequence s) {
		final String s2 = StringUtils.trinNoTrace(s);
		return DiagramType.getTypeFromArobaseStart(s2) != DiagramType.UNKNOWN;
	}

	public static boolean startsWithSymbolAnd(String value, final CharSequence2 tmp) {
		return tmp.startsWith("@" + value) || tmp.startsWith("\\" + value);
	}

	public static boolean startsWithSymbolAnd(String value, final String tmp) {
		return tmp.startsWith("@" + value) || tmp.startsWith("\\" + value);
	}

	public static boolean isArobaseEndDiagram(CharSequence s) {
		final String s2 = StringUtils.trinNoTrace(s);
		return startsWithSymbolAnd("end", s2);
	}

	public static boolean isArobasePauseDiagram(CharSequence s) {
		final String s2 = StringUtils.trinNoTrace(s);
		return startsWithSymbolAnd("pause", s2);
	}

	public static boolean isArobaseUnpauseDiagram(CharSequence s) {
		final String s2 = StringUtils.trinNoTrace(s);
		return startsWithSymbolAnd("unpause", s2);
	}

	private static final Pattern2 append = MyPattern.cmpile("^\\W*[@\\\\]append");

	public static CharSequence2 getPossibleAppend(CharSequence2 s) {
		final Matcher2 m = append.matcher(s);
		if (m.find()) {
			return s.subSequence(m.group(0).length(), s.length()).trin();
			// return StringUtils.trin(s.toString().substring(m.group(0).length()));
		}
		return null;
	}

}
