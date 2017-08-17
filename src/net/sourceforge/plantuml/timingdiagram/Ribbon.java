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
 */
package net.sourceforge.plantuml.timingdiagram;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Ribbon implements TimeDrawing {

	private final List<ChangeState> changes = new ArrayList<ChangeState>();
	private final List<TimeConstraint> constraints = new ArrayList<TimeConstraint>();

	private final double delta = 12;
	private final ISkinParam skinParam;
	private final TimingRuler ruler;
	private String initialState;
	private Colors initialColors;

	public Ribbon(TimingRuler ruler, ISkinParam skinParam) {
		this.ruler = ruler;
		this.skinParam = skinParam;
	}

	public IntricatedPoint getTimeProjection(StringBounder stringBounder, TimeTick tick) {
		final double x = ruler.getPosInPixel(tick);
		final double y = delta * 0.5 + getHeightForConstraints();
		for (ChangeState change : changes) {
			if (change.getWhen().compareTo(tick) == 0) {
				return new IntricatedPoint(new Point2D.Double(x, y), new Point2D.Double(x, y));
			}
		}
		return new IntricatedPoint(new Point2D.Double(x, y - delta), new Point2D.Double(x, y + delta));
	}

	public void addChange(ChangeState change) {
		this.changes.add(change);
	}

	private double getPosInPixel(ChangeState change) {
		return ruler.getPosInPixel(change.getWhen());
	}

	private FontConfiguration getFontConfiguration() {
		return new FontConfiguration(skinParam, FontParam.ACTIVITY, null);
	}

	private TextBlock getTextBlock(String value) {
		final Display display = Display.getWithNewlines(value);
		return display.create(getFontConfiguration(), HorizontalAlignment.LEFT, skinParam);
	}

	public void drawU(UGraphic ug) {

		UGraphic ugDown = ug.apply(new UTranslate(0, getHeightForConstraints()));

		final TextBlock inital;
		final StringBounder stringBounder = ugDown.getStringBounder();
		if (initialState == null) {
			inital = null;
		} else {
			inital = getTextBlock(initialState);
			final double a = getPosInPixel(changes.get(0));
			drawPentaA(ugDown.apply(new UTranslate(-getInitialWidth(stringBounder), -delta / 2)),
					getInitialWidth(stringBounder) + a, changes.get(0));
		}

		for (int i = 0; i < changes.size() - 1; i++) {
			final double a = getPosInPixel(changes.get(i));
			final double b = getPosInPixel(changes.get(i + 1));
			assert b > a;
			if (changes.get(i).isCompletelyHidden() == false) {
				drawHexa(ugDown.apply(new UTranslate(a, -delta / 2)), b - a, changes.get(i));
			}
		}
		if (changes.size() >= 1) {
			final ChangeState last = changes.get(changes.size() - 1);
			final double a = getPosInPixel(last);
			if (last.isCompletelyHidden() == false) {
				drawPentaB(ugDown.apply(new UTranslate(a, -delta / 2)), ruler.getWidth() - a, last);
			}
		}

		ugDown = ugDown.apply(new UTranslate(0, delta / 2));

		if (inital != null) {
			final Dimension2D dimInital = inital.calculateDimension(stringBounder);
			inital.drawU(ugDown.apply(new UTranslate(-getDelta() - dimInital.getWidth(), -dimInital.getHeight() / 2)));
		}
		for (int i = 0; i < changes.size(); i++) {
			final ChangeState change = changes.get(i);
			final double x = ruler.getPosInPixel(change.getWhen());
			if (change.isBlank() == false && change.isCompletelyHidden() == false) {
				final TextBlock state = getTextBlock(change.getState());
				final Dimension2D dim = state.calculateDimension(stringBounder);
				final double xtext;
				if (i == changes.size() - 1) {
					xtext = x + getDelta();
				} else {
					final double x2 = ruler.getPosInPixel(changes.get(i + 1).getWhen());
					xtext = (x + x2) / 2 - dim.getWidth() / 2;
				}
				state.drawU(ugDown.apply(new UTranslate(xtext, -dim.getHeight() / 2)));
			}
			final String commentString = change.getComment();
			if (commentString != null) {
				final TextBlock comment = getTextBlock(commentString);
				final Dimension2D dimComment = comment.calculateDimension(stringBounder);
				comment.drawU(ugDown.apply(new UTranslate(x + getDelta(), -delta - dimComment.getHeight())));
			}
		}

		for (TimeConstraint constraint : constraints) {
			constraint.drawU(ug.apply(new UTranslate(0, 15)), ruler, skinParam);
		}

	}

	private double getInitialWidth(final StringBounder stringBounder) {
		return getTextBlock(initialState).calculateDimension(stringBounder).getWidth() + 2 * delta;
	}

	private void drawHexa(UGraphic ug, double len, ChangeState change) {
		final HexaShape shape = HexaShape.create(len, 2 * delta, change.getContext());
		shape.drawU(ug);
	}

	private void drawPentaB(UGraphic ug, double len, ChangeState change) {
		final PentaBShape shape = PentaBShape.create(len, 2 * delta, change.getContext());
		shape.drawU(ug);
	}

	private void drawPentaA(UGraphic ug, double len, ChangeState change) {
		SymbolContext context = change.getContext();
		final HtmlColor back = initialColors.getColor(ColorType.BACK);
		if (back != null) {
			context = context.withBackColor(back);
		}
		final PentaAShape shape = PentaAShape.create(len, 2 * delta, context);
		shape.drawU(ug);
	}

	private double getHeightForConstraints() {
		if (constraints.size() == 0) {
			return 0;
		}
		return 30;
	}

	public double getHeight() {
		return 3 * delta + getHeightForConstraints();
	}

	public double getDelta() {
		return delta;
	}

	public TextBlock getWidthHeader(StringBounder stringBounder) {
		if (initialState != null) {
			return TextBlockUtils.empty(getInitialWidth(stringBounder), 2 * delta);
		}
		return TextBlockUtils.empty(0, 0);
	}

	public void setInitialState(String initialState, Colors initialColors) {
		this.initialState = initialState;
		this.initialColors = initialColors;
	}

	public void addConstraint(TimeConstraint constraint) {
		this.constraints.add(constraint);
	}

}
