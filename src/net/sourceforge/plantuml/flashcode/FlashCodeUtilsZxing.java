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
package net.sourceforge.plantuml.flashcode;

import java.awt.image.BufferedImage;
import java.util.Hashtable;

import net.sourceforge.plantuml.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class FlashCodeUtilsZxing implements FlashCodeUtils {

	public BufferedImage exportFlashcode(String s) {
		try {
			final QRCodeWriter writer = new QRCodeWriter();
			final Hashtable hints = new Hashtable();
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			hints.put(EncodeHintType.CHARACTER_SET, "UTF8");
			final int multiple = 1;
			final BitMatrix bit = writer.encode(s, BarcodeFormat.QR_CODE, multiple, hints);
			return MatrixToImageWriter.toBufferedImage(bit);
		} catch (WriterException e) {
			Log.debug("Cannot create flashcode " + e);
			// e.printStackTrace();
			return null;
		}
	}

}
