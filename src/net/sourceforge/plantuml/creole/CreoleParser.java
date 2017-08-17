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
package net.sourceforge.plantuml.creole;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.plantuml.EmbededDiagram;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;

public class CreoleParser {

	private final FontConfiguration fontConfiguration;
	private final ISkinSimple skinParam;
	private final HorizontalAlignment horizontalAlignment;
	private final CreoleMode modeSimpleLine;

	public CreoleParser(FontConfiguration fontConfiguration, HorizontalAlignment horizontalAlignment,
			ISkinSimple skinParam, CreoleMode modeSimpleLine) {
		this.modeSimpleLine = modeSimpleLine;
		this.fontConfiguration = fontConfiguration;
		this.skinParam = skinParam;
		if (skinParam == null) {
			throw new IllegalArgumentException();
		}
		this.horizontalAlignment = horizontalAlignment;
	}

	private Stripe createStripe(String line, CreoleContext context, Stripe lastStripe) {
		if (lastStripe instanceof StripeTable && isTableLine(line)) {
			final StripeTable table = (StripeTable) lastStripe;
			table.analyzeAndAddNormal(line);
			return null;
		} else if (lastStripe instanceof StripeTree && isTreeStart(StringUtils.trinNoTrace(line))) {
			final StripeTree tree = (StripeTree) lastStripe;
			tree.analyzeAndAdd(line);
			return null;
		} else if (isTableLine(line)) {
			return new StripeTable(fontConfiguration, skinParam, line);
		} else if (isTreeStart(line)) {
			return new StripeTree(fontConfiguration, skinParam, line);
		}
		return new CreoleStripeSimpleParser(line, context, fontConfiguration, skinParam, modeSimpleLine)
				.createStripe(context);
	}

	private static boolean isTableLine(String line) {
		return line.matches("^(\\<#\\w+\\>)?\\|(\\=)?.*\\|$");
	}

	public static boolean doesStartByColor(String line) {
		return line.matches("^(\\<#\\w+\\>).*");
	}

	public static boolean isTreeStart(String line) {
		// return false;
		return line.startsWith("|_");
	}

	public Sheet createSheet(Display display) {
		final Sheet sheet = new Sheet(horizontalAlignment);
		if (Display.isNull(display) == false) {
			final CreoleContext context = new CreoleContext();
			for (CharSequence cs : display) {
				final Stripe stripe;
				if (cs instanceof EmbededDiagram) {
					final Atom atom = new AtomEmbededSystem((EmbededDiagram) cs);
					stripe = new Stripe() {
						public List<Atom> getAtoms() {
							return Arrays.asList(atom);
						}
					};
				} else {
					stripe = createStripe(cs.toString(), context, sheet.getLastStripe());
				}
				if (stripe != null) {
					sheet.add(stripe);
				}
			}
		}
		return sheet;
	}
}
