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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.activitydiagram3.Branch;
import net.sourceforge.plantuml.activitydiagram3.ForkStyle;
import net.sourceforge.plantuml.activitydiagram3.Instruction;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.PositionedNote;
import net.sourceforge.plantuml.activitydiagram3.ftile.BoxStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileAssemblySimple;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileBox;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileCircleEnd;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileCircleStart;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileCircleStop;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileDecorateIn;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileDecorateOut;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.ugraphic.UFont;

public class VCompactFactory implements FtileFactory {

	private final ISkinParam skinParam;
	private final Rose rose = new Rose();
	private final StringBounder stringBounder;

	public StringBounder getStringBounder() {
		return stringBounder;
	}

	public VCompactFactory(ISkinParam skinParam, StringBounder stringBounder) {
		this.skinParam = skinParam;
		this.stringBounder = stringBounder;
	}

	public Ftile start(Swimlane swimlane) {
		final HtmlColor color = rose.getHtmlColor(skinParam, ColorParam.activityStart);
		return new FtileCircleStart(skinParam(), color, swimlane);
	}

	public Ftile stop(Swimlane swimlane) {
		final HtmlColor color = rose.getHtmlColor(skinParam, ColorParam.activityEnd);
		return new FtileCircleStop(skinParam(), color, swimlane);
	}

	public Ftile end(Swimlane swimlane) {
		final HtmlColor color = rose.getHtmlColor(skinParam, ColorParam.activityEnd);
		return new FtileCircleEnd(skinParam(), color, swimlane);
	}

	public Ftile activity(Display label, Swimlane swimlane, BoxStyle style, Colors colors) {
		// final HtmlColor borderColor = rose.getHtmlColor(skinParam, ColorParam.activityBorder);
		// final HtmlColor backColor = color == null ? rose.getHtmlColor(skinParam, ColorParam.activityBackground) :
		// color;
		final UFont font = skinParam.getFont(null, false, FontParam.ACTIVITY);
		return new FtileBox(colors.mute(skinParam), label, font, swimlane, style);
	}

	public Ftile addNote(Ftile ftile, Swimlane swimlane, Collection<PositionedNote> notes) {
		return ftile;
	}

	public Ftile addUrl(Ftile ftile, Url url) {
		return ftile;
	}

	public Ftile assembly(Ftile tile1, Ftile tile2) {
		return new FtileAssemblySimple(tile1, tile2);
	}

	public Ftile repeat(Swimlane swimlane, Swimlane swimlaneOut, Display startLabel, Ftile repeat, Display test, Display yes,
			Display out, HtmlColor color, LinkRendering backRepeatLinkRendering, Ftile backward) {
		return repeat;
	}

	public Ftile createWhile(Swimlane swimlane, Ftile whileBlock, Display test, Display yes, Display out,
			LinkRendering afterEndwhile, HtmlColor color, Instruction specialOut) {
		return whileBlock;
	}

	public Ftile createIf(Swimlane swimlane, List<Branch> thens, Branch elseBranch, LinkRendering afterEndwhile,
			LinkRendering topInlinkRendering) {
		final List<Ftile> ftiles = new ArrayList<Ftile>();
		for (Branch branch : thens) {
			ftiles.add(branch.getFtile());
		}
		ftiles.add(elseBranch.getFtile());
		return new FtileForkInner(ftiles);
	}

	public Ftile createParallel(Swimlane swimlane, List<Ftile> all, ForkStyle style, String label) {
		return new FtileForkInner(all);
	}

	public Ftile createGroup(Ftile list, Display name, HtmlColor backColor, HtmlColor titleColor, PositionedNote note,
			HtmlColor borderColor) {
		return list;
	}

	public Ftile decorateIn(final Ftile ftile, final LinkRendering linkRendering) {
		return new FtileDecorateIn(ftile, linkRendering);
	}

	public Ftile decorateOut(final Ftile ftile, final LinkRendering linkRendering) {
		// if (ftile instanceof FtileWhile) {
		// if (linkRendering != null) {
		// ((FtileWhile) ftile).changeAfterEndwhileColor(linkRendering.getColor());
		// }
		// return ftile;
		// }
		return new FtileDecorateOut(ftile, linkRendering);
	}

	public ISkinParam skinParam() {
		return skinParam;
	}

}
