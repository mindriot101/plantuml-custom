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
package net.sourceforge.plantuml;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.preproc.Defines;
import net.sourceforge.plantuml.preproc.FileWithSuffix;
import net.sourceforge.plantuml.preproc.Preprocessor;
import net.sourceforge.plantuml.preproc.ReadLineReader;
import net.sourceforge.plantuml.preproc.UncommentReadLine;
import net.sourceforge.plantuml.utils.StartUtils;

public final class BlockUmlBuilder implements DefinitionsContainer {

	private final List<BlockUml> blocks = new ArrayList<BlockUml>();
	private Set<FileWithSuffix> usedFiles = new HashSet<FileWithSuffix>();
	private final UncommentReadLine reader2;
	private final Defines defines;

	public BlockUmlBuilder(List<String> config, String charset, Defines defines, Reader reader, File newCurrentDir,
			String desc) throws IOException {
		Preprocessor includer = null;
		this.defines = defines;
		try {
			reader2 = new UncommentReadLine(new ReadLineReader(reader, desc));
			includer = new Preprocessor(reader2, charset, defines, newCurrentDir, this);
			init(includer, config);
		} finally {
			if (includer != null) {
				includer.close();
				usedFiles = includer.getFilesUsed();
			}
		}
	}

	public BlockUmlBuilder(List<String> config, String charset, Defines defines, Reader reader) throws IOException {
		this(config, charset, defines, reader, null, null);
	}

	private void init(Preprocessor includer, List<String> config) throws IOException {
		CharSequence2 s = null;
		List<CharSequence2> current2 = null;
		boolean paused = false;
		int startLine = 0;
		while ((s = includer.readLine()) != null) {
			if (StartUtils.isArobaseStartDiagram(s)) {
				current2 = new ArrayList<CharSequence2>();
				paused = false;
				startLine = includer.getLineNumber();
			}
			if (StartUtils.isArobasePauseDiagram(s)) {
				paused = true;
				reader2.setPaused(true);
			}
			if (current2 != null && paused == false) {
				current2.add(s);
			} else if (paused) {
				final CharSequence2 append = StartUtils.getPossibleAppend(s);
				if (append != null) {
					current2.add(append);
				}
			}

			if (StartUtils.isArobaseUnpauseDiagram(s)) {
				paused = false;
				reader2.setPaused(false);
			}
			if (StartUtils.isArobaseEndDiagram(s) && current2 != null) {
				current2.addAll(1, convert(config, new LineLocationImpl(null, null).oneLineRead()));
				blocks.add(new BlockUml(current2, startLine - config.size(), defines.cloneMe()));
				current2 = null;
				reader2.setPaused(false);
			}
		}
	}

	private Collection<CharSequence2> convert(List<String> config, LineLocation location) {
		final List<CharSequence2> result = new ArrayList<CharSequence2>();
		for (String s : config) {
			result.add(new CharSequence2Impl(s, location));
		}
		return result;
	}

	public List<BlockUml> getBlockUmls() {
		return Collections.unmodifiableList(blocks);
	}

	public final Set<FileWithSuffix> getIncludedFiles() {
		return Collections.unmodifiableSet(usedFiles);
	}

	public List<? extends CharSequence> getDefinition(String name) {
		for (BlockUml block : blocks) {
			if (block.isStartDef(name)) {
				this.defines.importFrom(block.getLocalDefines());
				return block.getDefinition();
			}
		}
		return Collections.emptyList();
	}

}
