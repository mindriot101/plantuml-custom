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

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.plantuml.CharSequence2;
import net.sourceforge.plantuml.CharSequence2Impl;
import net.sourceforge.plantuml.LineLocation;

class ReadLineInsertable implements ReadLine {

	private final ReadLine source;
	private final List<CharSequence2> inserted = new LinkedList<CharSequence2>();

	public ReadLineInsertable(ReadLine source) {
		this.source = source;
	}

	public void close() throws IOException {
		source.close();
	}

	public CharSequence2 readLine() throws IOException {
		if (inserted.size() > 0) {
			final Iterator<CharSequence2> it = inserted.iterator();
			final CharSequence2 result = it.next();
			it.remove();
			return result;
		}
		return source.readLine();
	}

	public void insert(List<? extends CharSequence> data, LineLocation location) {
		for (CharSequence s : data) {
			insert(s, location);
		}
	}

	public void insert(CharSequence s, LineLocation location) {
		inserted.add(new CharSequence2Impl(s, location));
	}

}
