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
package net.sourceforge.plantuml.ugraphic;

import net.sourceforge.plantuml.activitydiagram3.ftile.Snake;
import net.sourceforge.plantuml.graphic.UGraphicDelegator;

public class UGraphicCompress2 extends UGraphicDelegator {

	public UGraphic apply(UChange change) {
		if (change instanceof UTranslate) {
			return new UGraphicCompress2(getUg(), compressionTransform, translate.compose((UTranslate) change));
		} else if (change instanceof UStroke || change instanceof UChangeBackColor || change instanceof UChangeColor) {
			return new UGraphicCompress2(getUg().apply(change), compressionTransform, translate);
		}
		throw new UnsupportedOperationException();
	}

	private final CompressionTransform compressionTransform;
	private final UTranslate translate;

	public UGraphicCompress2(UGraphic ug, CompressionTransform compressionTransform) {
		this(ug, compressionTransform, new UTranslate());
	}

	private UGraphicCompress2(UGraphic ug, CompressionTransform compressionTransform, UTranslate translate) {
		super(ug);
		this.compressionTransform = compressionTransform;
		this.translate = translate;
	}

	public void draw(UShape shape) {
		final double x = translate.getDx();
		final double y = translate.getDy();
		if (shape instanceof ULine) {
			drawLine(x, y, (ULine) shape);
		} else if (shape instanceof Snake) {
			drawSnake(x, y, (Snake) shape);
		} else {
			getUg().apply(new UTranslate(ct(x), y)).draw(shape);
		}
	}

	private void drawSnake(double x, double y, Snake shape) {
		final Snake transformed = shape.translate(new UTranslate(x, y)).transformX(compressionTransform);
		getUg().draw(transformed);
	}

	private void drawLine(double x, double y, ULine shape) {
		drawLine(ct(x), y, ct(x + shape.getDX()), y + shape.getDY());
	}

	private double ct(double v) {
		return compressionTransform.transform(v);
	}

	private void drawLine(double x1, double y1, double x2, double y2) {
		final double xmin = Math.min(x1, x2);
		final double xmax = Math.max(x1, x2);
		final double ymin = Math.min(y1, y2);
		final double ymax = Math.max(y1, y2);
		getUg().apply(new UTranslate(xmin, ymin)).draw(new ULine(xmax - xmin, ymax - ymin));
	}

}
