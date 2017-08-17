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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.preproc.Defines;
import net.sourceforge.plantuml.preproc.FileWithSuffix;

public class SourceFileReader implements ISourceFileReader {

	private final File file;
	private final File outputDirectory;

	private final BlockUmlBuilder builder;
	private FileFormatOption fileFormatOption;

	public SourceFileReader(File file) throws IOException {
		this(file, file.getAbsoluteFile().getParentFile());
	}

	public SourceFileReader(File file, File outputDirectory, String charset) throws IOException {
		this(Defines.createWithFileName(file), file, outputDirectory, Collections.<String> emptyList(), charset,
				new FileFormatOption(FileFormat.PNG));
	}

	public SourceFileReader(final File file, File outputDirectory) throws IOException {
		this(Defines.createWithFileName(file), file, outputDirectory, Collections.<String> emptyList(), null,
				new FileFormatOption(FileFormat.PNG));
	}

	public SourceFileReader(final File file, File outputDirectory, FileFormatOption fileFormatOption)
			throws IOException {
		this(Defines.createWithFileName(file), file, outputDirectory, Collections.<String> emptyList(), null,
				fileFormatOption);
	}

	public SourceFileReader(Defines defines, final File file, File outputDirectory, List<String> config,
			String charset, FileFormatOption fileFormatOption) throws IOException {
		this.file = file;
		this.fileFormatOption = fileFormatOption;
		if (file.exists() == false) {
			throw new IllegalArgumentException();
		}
		FileSystem.getInstance().setCurrentDir(file.getAbsoluteFile().getParentFile());
		if (outputDirectory == null) {
			outputDirectory = file.getAbsoluteFile().getParentFile();
		} else if (outputDirectory.isAbsolute() == false) {
			outputDirectory = FileSystem.getInstance().getFile(outputDirectory.getPath());
		}
		if (outputDirectory.exists() == false) {
			outputDirectory.mkdirs();
		}
		this.outputDirectory = outputDirectory;

		builder = new BlockUmlBuilder(config, charset, defines, getReader(charset), file.getAbsoluteFile()
				.getParentFile(), file.getAbsolutePath());
	}

	public boolean hasError() {
		for (final BlockUml b : builder.getBlockUmls()) {
			if (b.getDiagram() instanceof PSystemError) {
				return true;
			}
		}
		return false;
	}

	private File getDirIfDirectory(String newName) {
		Log.info("Checking=" + newName);
		if (endsWithSlashOrAntislash(newName)) {
			Log.info("It ends with / so it looks like a directory");
			newName = newName.substring(0, newName.length() - 1);
			File f = new File(newName);
			Log.info("f=" + f);
			if (f.isAbsolute() == false) {
				Log.info("It's relative, so let's change it");
				f = new File(outputDirectory, newName);
				Log.info("f=" + f);
			}
			if (f.exists() == false) {
				Log.info("It does not exist: let's create it");
				try {
					f.mkdirs();
				} catch (Exception e) {
					Log.info("Error " + e);
				}
				if (f.exists() && f.isDirectory()) {
					Log.info("Creation ok");
					return f;
				}
				Log.info("We cannot create it");
			} else if (f.isDirectory() == false) {
				Log.info("It exists, but is not a directory: we ignore it");
				return null;
			}
			return f;

		}
		File f = new File(newName);
		Log.info("f=" + f);
		if (f.isAbsolute() == false) {
			Log.info("Relative, so let's change it");
			f = new File(outputDirectory, newName);
			Log.info("f=" + f);
		}
		if (f.exists() && f.isDirectory()) {
			Log.info("It's an existing directory");
			return f;
		}
		Log.info("It's not a directory");
		return null;

	}

	public List<GeneratedImage> getGeneratedImages() throws IOException {
		Log.info("Reading file: " + file);

		int cpt = 0;
		final List<GeneratedImage> result = new ArrayList<GeneratedImage>();

		for (BlockUml blockUml : builder.getBlockUmls()) {
			final String newName = blockUml.getFileOrDirname();
			SuggestedFile suggested = null;
			if (newName != null) {
				Log.info("name from block=" + newName);
				final File dir = getDirIfDirectory(newName);
				if (dir == null) {
					Log.info(newName + " is not taken as a directory");
					suggested = SuggestedFile.fromOutputFile(new File(outputDirectory, newName),
							fileFormatOption.getFileFormat(), 0);
				} else {
					Log.info("We are going to create files in directory " + dir);
					suggested = SuggestedFile.fromOutputFile(new File(dir, file.getName()),
							fileFormatOption.getFileFormat(), 0);
				}
				Log.info("We are going to put data in " + suggested);
			}
			if (suggested == null) {
				suggested = SuggestedFile.fromOutputFile(new File(outputDirectory, file.getName()),
						fileFormatOption.getFileFormat(), cpt++);
			}
			suggested.getParentFile().mkdirs();

			final Diagram system;
			try {
				system = blockUml.getDiagram();
			} catch (Throwable t) {
				final GeneratedImage image = new GeneratedImageImpl(suggested.getFile(0), "Crash Error", blockUml);
				OutputStream os = null;
				try {
					os = new BufferedOutputStream(new FileOutputStream(suggested.getFile(0)));
					UmlDiagram.exportDiagramError(os, t, fileFormatOption, 42, null, blockUml.getFlashData(),
							UmlDiagram.getFailureText2(t, blockUml.getFlashData()));
				} finally {
					if (os != null) {
						os.close();
					}
				}

				return Collections.singletonList(image);
			}

			final List<FileImageData> exportDiagrams = PSystemUtils.exportDiagrams(system, suggested, fileFormatOption);
			if (exportDiagrams.size() > 1) {
				cpt += exportDiagrams.size() - 1;
			}
			OptionFlags.getInstance().logData(file, system);

			for (FileImageData fdata : exportDiagrams) {
				final String desc = "[" + file.getName() + "] " + system.getDescription();
				final File f = fdata.getFile();
				if (OptionFlags.getInstance().isWord()) {
					final String warnOrError = system.getWarningOrError();
					if (warnOrError != null) {
						final String name = f.getName().substring(0, f.getName().length() - 4) + ".err";
						final File errorFile = new File(f.getParentFile(), name);
						final PrintStream ps = new PrintStream(new FileOutputStream(errorFile));
						ps.print(warnOrError);
						ps.close();
					}
				}
				final GeneratedImage generatedImage = new GeneratedImageImpl(f, desc, blockUml);
				result.add(generatedImage);
			}

		}

		Log.info("Number of image(s): " + result.size());

		return Collections.unmodifiableList(result);
	}

	private boolean endsWithSlashOrAntislash(String newName) {
		return newName.endsWith("/") || newName.endsWith("\\");
	}

	public List<BlockUml> getBlocks() {
		return builder.getBlockUmls();
	}

	private Reader getReader(String charset) throws FileNotFoundException, UnsupportedEncodingException {
		if (charset == null) {
			Log.info("Using default charset");
			return new InputStreamReader(new FileInputStream(file));
		}
		Log.info("Using charset " + charset);
		return new InputStreamReader(new FileInputStream(file), charset);
	}

	public final void setFileFormatOption(FileFormatOption fileFormatOption) {
		this.fileFormatOption = fileFormatOption;
	}

	public final Set<FileWithSuffix> getIncludedFiles() {
		return builder.getIncludedFiles();
	}

}
