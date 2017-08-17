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
package net.sourceforge.plantuml.cucadiagram;

import net.sourceforge.plantuml.ugraphic.UStroke;

public enum LinkStyle {

	NORMAL, DASHED, DOTTED, BOLD, INVISIBLE,

	DOUBLE_tobedone, __toremove_INTERFACE_PROVIDER, __toremove_INTERFACE_USER;

	public static UStroke getStroke(LinkStyle style) {
		return getStroke(style, 1);
	}

	public static UStroke getStroke(LinkStyle style, double thickness) {
		if (style == LinkStyle.DASHED) {
			return new UStroke(6, 6, thickness);
		}
		if (style == LinkStyle.DOTTED) {
			return new UStroke(1, 3, thickness);
		}
		if (style == LinkStyle.BOLD) {
			return new UStroke(2.5);
		}
		return new UStroke();
	}

	public static LinkStyle fromString(String s) {
		if ("dashed".equalsIgnoreCase(s)) {
			return DASHED;
		}
		if ("dotted".equalsIgnoreCase(s)) {
			return DOTTED;
		}
		if ("bold".equalsIgnoreCase(s)) {
			return BOLD;
		}
		if ("hidden".equalsIgnoreCase(s)) {
			return INVISIBLE;
		}
		return LinkStyle.NORMAL;
	}

}
