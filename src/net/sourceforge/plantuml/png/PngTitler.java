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
package net.sourceforge.plantuml.png;

import java.awt.Font;
import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UFont;

public class PngTitler {

	private final HtmlColor textColor;
	private final HtmlColor hyperlinkColor;
	private final Display text;
	private final int fontSize;
	private final String fontFamily;
	private final HorizontalAlignment horizontalAlignment;
	private final boolean useUnderlineForHyperlink;

	public PngTitler(HtmlColor textColor, Display text, int fontSize, String fontFamily,
			HorizontalAlignment horizontalAlignment, HtmlColor hyperlinkColor, boolean useUnderlineForHyperlink) {
		this.textColor = textColor;
		this.text = text;
		this.fontSize = fontSize;
		this.fontFamily = fontFamily;
		this.horizontalAlignment = horizontalAlignment;
		this.hyperlinkColor = hyperlinkColor;
		this.useUnderlineForHyperlink = useUnderlineForHyperlink;

	}

	public Dimension2D getTextDimension(StringBounder stringBounder) {
		final TextBlock textBloc = getTextBlock();
		if (textBloc == null) {
			return null;
		}
		return textBloc.calculateDimension(stringBounder);
	}

	public TextBlock getTextBlock() {
		if (Display.isNull(text) || text.size() == 0) {
			return null;
		}
		final UFont normalFont = new UFont(fontFamily, Font.PLAIN, fontSize);
		return text.create(new FontConfiguration(normalFont, textColor, hyperlinkColor, useUnderlineForHyperlink),
				horizontalAlignment, new SpriteContainerEmpty());
	}

	private double getOffsetX(double imWidth, StringBounder stringBounder) {
		final TextBlock textBloc = getTextBlock();
		if (textBloc == null) {
			return 0;
		}
		final Dimension2D dimText = textBloc.calculateDimension(stringBounder);

		if (imWidth >= dimText.getWidth()) {
			return 0;
		}
		return (dimText.getWidth() - imWidth) / 2;
	}

	private double getOffsetY(StringBounder stringBounder) {
		final TextBlock textBloc = getTextBlock();
		if (textBloc == null) {
			return 0;
		}
		final Dimension2D dimText = textBloc.calculateDimension(stringBounder);
		final double height = dimText.getHeight();
		return height;
	}
}
