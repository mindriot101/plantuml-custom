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
package net.sourceforge.plantuml.webp;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

public class Portrait {

	private final String name;
	private final int age;
	private final String quote;
	private final byte webp[];

	public Portrait(String name, int age, String quote, byte webp[]) throws IOException {
		this.name = name;
		this.quote = quote;
		this.age = age;
		this.webp = webp;
	}

	public BufferedImage getBufferedImage() {
		try {
			final InputStream is = new ByteArrayInputStream(webp);
			final ImageInputStream iis = ImageIO.createImageInputStream(is);
			final VP8Decoder vp8Decoder = new VP8Decoder();
			vp8Decoder.decodeFrame(iis, false);
			iis.close();
			return vp8Decoder.getFrame().getBufferedImage();
		} catch (IOException e) {
			return null;
		}

	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public String getQuote() {
		return quote;
	}

}
