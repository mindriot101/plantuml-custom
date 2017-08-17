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
package net.sourceforge.plantuml.preproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import net.sourceforge.plantuml.CharSequence2;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.utils.StartUtils;

public class StartDiagramExtractReader implements ReadLine {

	private final ReadLine raw;
	private boolean finished = false;

	public StartDiagramExtractReader(CharSequence2 s, File f, String uid, String charset) {
		this(getReadLine(s, f, charset), uid, charset);
	}

	public StartDiagramExtractReader(CharSequence2 s, URL url, String uid, String charset) {
		this(getReadLine(s, url, charset), uid, charset);
	}

	private StartDiagramExtractReader(ReadLine raw, String suf, String charset) {
		int bloc = 0;
		String uid = null;
		if (suf != null && suf.matches("\\d+")) {
			bloc = Integer.parseInt(suf);
		} else {
			uid = suf;
		}
		if (bloc < 0) {
			bloc = 0;
		}
		this.raw = raw;
		CharSequence2 s = null;
		try {
			while ((s = raw.readLine()) != null) {
				if (StartUtils.isArobaseStartDiagram(s) && checkUid(uid, s)) {
					if (bloc == 0) {
						return;
					}
					bloc--;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.error("Error " + e);
		}
		finished = true;
	}

	private boolean checkUid(String uid, CharSequence2 s) {
		if (uid == null) {
			return true;
		}
		if (s.toString().matches(".*id=" + uid + "\\W.*")) {
			return true;
		}
		return false;
	}

	private static ReadLine getReadLine(CharSequence2 s, File f, String charset) {
		try {
			if (charset == null) {
				Log.info("Using default charset");
				return new UncommentReadLine(new ReadLineReader(new FileReader(f), f.getAbsolutePath()));
			}
			Log.info("Using charset " + charset);
			return new UncommentReadLine(new ReadLineReader(new InputStreamReader(new FileInputStream(f), charset),
					f.getAbsolutePath()));
		} catch (IOException e) {
			return new ReadLineSimple(s, e.toString());
		}
	}

	private static ReadLine getReadLine(CharSequence2 s, URL url, String charset) {
		try {
			if (charset == null) {
				Log.info("Using default charset");
				return new UncommentReadLine(
						new ReadLineReader(new InputStreamReader(url.openStream()), url.toString()));
			}
			Log.info("Using charset " + charset);
			return new UncommentReadLine(new ReadLineReader(new InputStreamReader(url.openStream(), charset),
					url.toString()));
		} catch (IOException e) {
			return new ReadLineSimple(s, e.toString());
		}
	}

	static public boolean containsStartDiagram(CharSequence2 s, File f, String charset) throws IOException {
		final ReadLine r = getReadLine(s, f, charset);
		return containsStartDiagram(r);
	}

	static public boolean containsStartDiagram(CharSequence2 s, URL url, String charset) throws IOException {
		final ReadLine r = getReadLine(s, url, charset);
		return containsStartDiagram(r);
	}

	private static boolean containsStartDiagram(final ReadLine r) throws IOException {
		try {
			CharSequence2 s = null;
			while ((s = r.readLine()) != null) {
				if (StartUtils.isArobaseStartDiagram(s)) {
					return true;
				}
			}
		} finally {
			if (r != null) {
				r.close();
			}
		}
		return false;
	}

	public CharSequence2 readLine() throws IOException {
		if (finished) {
			return null;
		}
		final CharSequence2 result = raw.readLine();
		if (result != null && StartUtils.isArobaseEndDiagram(result)) {
			finished = true;
			return null;
		}
		return result;
	}

	public void close() throws IOException {
		raw.close();
	}

}
