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
package net.sourceforge.plantuml;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import net.sourceforge.plantuml.api.ImageDataSimple;
import net.sourceforge.plantuml.asciiart.UmlCharArea;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.eggs.PSystemWelcome;
import net.sourceforge.plantuml.graphic.GraphicPosition;
import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorSimple;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.txt.UGraphicTxt;
import net.sourceforge.plantuml.version.PSystemVersion;

public class PSystemError extends AbstractPSystem {

	private final LineLocation higherErrorPosition;
	private final List<ErrorUml> printedErrors;
	private final List<String> debugLines = new ArrayList<String>();

	public PSystemError(UmlSource source, ErrorUml singleError, List<String> debugLines) {
		this(source, Collections.singletonList(singleError), debugLines);
	}

	private PSystemError(UmlSource source, List<ErrorUml> all, List<String> debugLines) {
		this.setSource(source);

		final LineLocation execution = getHigherErrorPosition2(ErrorUmlType.EXECUTION_ERROR, all);
		final LineLocation syntax = getHigherErrorPosition2(ErrorUmlType.SYNTAX_ERROR, all);

		if (execution == null && syntax == null) {
			throw new IllegalStateException();
		}

		if (execution != null && (syntax == null || execution.compareTo(syntax) >= 0)) {
			higherErrorPosition = execution;
			printedErrors = getErrorsAt2(execution, ErrorUmlType.EXECUTION_ERROR, all);
		} else {
			// assert syntax.compareTo(execution) > 0;
			higherErrorPosition = syntax;
			printedErrors = getErrorsAt2(syntax, ErrorUmlType.SYNTAX_ERROR, all);
		}

		if (debugLines != null) {
			this.debugLines.addAll(debugLines);
		}

	}

	private String getSuggestColor(boolean useRed) {
		if (useRed) {
			return "black";
		}
		return "white";
	}

	private String getRed(boolean useRed) {
		if (useRed) {
			return "#CD0A0A";
		}
		return "red";
	}

	@Override
	final protected ImageData exportDiagramNow(OutputStream os, int num, FileFormatOption fileFormat, long seed)
			throws IOException {
		if (fileFormat.getFileFormat() == FileFormat.ATXT || fileFormat.getFileFormat() == FileFormat.UTXT) {
			final UGraphicTxt ugt = new UGraphicTxt();
			final UmlCharArea area = ugt.getCharArea();
			area.drawStringsLR(getTextStrings(), 0, 0);
			area.print(new PrintStream(os));
			return new ImageDataSimple(1, 1);

		}
		final boolean useRed = fileFormat.isUseRedForError();
		final TextBlockBackcolored result = GraphicStrings.createForError(getHtmlStrings(useRed), useRed);

		TextBlock udrawable;
		final ImageBuilder imageBuilder = new ImageBuilder(new ColorMapperIdentity(), 1.0, result.getBackcolor(),
				getMetadata(), null, 0, 0, null, false);
		if (getSource().getTotalLineCount() < 5) {
			udrawable = addWelcome(result);
		} else {
			udrawable = result;
		}
		final int min = (int) (System.currentTimeMillis() / 60000L) % 60;
		if (min == 0) {
			udrawable = addMessage(udrawable);
		}
		imageBuilder.setUDrawable(udrawable);
		return imageBuilder.writeImageTOBEMOVED(fileFormat, seed(), os);
	}

	private TextBlockBackcolored getWelcome() throws IOException {
		return new PSystemWelcome(GraphicPosition.BACKGROUND_CORNER_TOP_RIGHT).getGraphicStrings();
	}

	private TextBlock addWelcome(final TextBlockBackcolored result) throws IOException {
		final TextBlockBackcolored welcome = getWelcome();
		return TextBlockUtils.mergeTB(welcome, result, HorizontalAlignment.LEFT);
	}

	private TextBlock addMessage(final TextBlock source) throws IOException {
		final TextBlock message = getMessage();
		TextBlock result = TextBlockUtils.mergeTB(message, source, HorizontalAlignment.LEFT);
		result = TextBlockUtils.mergeTB(result, message, HorizontalAlignment.LEFT);
		return result;
	}

	private TextBlockBackcolored getMessage() {
		final UImage message = new UImage(PSystemVersion.getTime());
		final HtmlColor backImage = new HtmlColorSimple(new Color(message.getImage().getRGB(0, 0)), false);
		final double imWidth = message.getWidth();
		final double imHeight = message.getHeight();
		return new TextBlockBackcolored() {

			public void drawU(UGraphic ug) {
				ug.apply(new UTranslate(1, 1)).draw(message);
			}

			public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
				return null;
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				return new Dimension2DDouble(imWidth + 1, imHeight + 1);
			}

			public HtmlColor getBackcolor() {
				return backImage;
			}
		};

	}

	private List<String> getTextStrings() {
		final List<String> result = new ArrayList<String>(getStack());
		if (result.size() > 0) {
			result.add(" ");
		}

		// final int limit = 4;
		// int start;
		// final int skip = higherErrorPosition - limit + 1;
		// if (skip <= 0) {
		// start = 0;
		// } else {
		// if (skip == 1) {
		// result.add("... (skipping 1 line) ...");
		// } else {
		// result.add("... (skipping " + skip + " lines) ...");
		// }
		// start = higherErrorPosition - limit + 1;
		// }
		// for (int i = start; i < higherErrorPosition; i++) {
		// result.add(getSource().getLine(i));
		// }
		for (String s : getPartialCode()) {
			result.add(s);
		}
		final String errorLine = getSource().getLine(higherErrorPosition);
		final String err = StringUtils.hideComparatorCharacters(errorLine);
		if (StringUtils.isNotEmpty(err)) {
			result.add(err);
		}
		final StringBuilder underscore = new StringBuilder();
		for (int i = 0; i < errorLine.length(); i++) {
			underscore.append("^");
		}
		result.add(underscore.toString());
		final Collection<String> textErrors = new LinkedHashSet<String>();
		for (ErrorUml er : printedErrors) {
			textErrors.add(er.getError());
		}
		for (String er : textErrors) {
			result.add(" " + er);
		}
		boolean first = true;
		for (String s : getSuggest()) {
			if (first) {
				result.add(" " + s);
			} else {
				result.add(s);
			}
			first = false;
		}
		result.addAll(this.debugLines);

		return result;
	}

	private List<String> getStack() {
		LineLocation lineLocation = getLineLocation();
		final List<String> result = new ArrayList<String>();
		if (lineLocation != null) {
			append(result, lineLocation);
			while (lineLocation.getParent() != null) {
				lineLocation = lineLocation.getParent();
				append(result, lineLocation);
			}
		}
		return result;
	}

	public LineLocation getLineLocation() {
		for (ErrorUml err : printedErrors) {
			if (err.getLineLocation() != null) {
				return err.getLineLocation();
			}
		}
		return null;
	}

	private void append(List<String> result, LineLocation lineLocation) {
		if (lineLocation.getDescription() != null) {
			result.add("[From " + lineLocation.getDescription() + " (line " + (lineLocation.getPosition() + 1) + ") ]");
		}
	}

	private List<String> getPartialCode() {
		List<String> result = new ArrayList<String>();
		for (Iterator<CharSequence2> it = getSource().iterator2(); it.hasNext();) {
			final CharSequence2 s = it.next();
			if (s.getLocation().compareTo(higherErrorPosition) < 0) {
				result.add(s.toString());
			}
		}
		final int limit = 4;
		if (result.size() > limit) {
			final int skip = result.size() - limit + 1;
			final String skipMessage;
			if (skip == 1) {
				skipMessage = "... (skipping 1 line) ...";
			} else {
				skipMessage = "... (skipping " + skip + " lines) ...";
			}
			result = new ArrayList<String>(result.subList(skip, result.size()));
			result.add(0, skipMessage);
		}
		return result;

	}

	private List<String> getHtmlStrings(boolean useRed) {
		final List<String> htmlStrings = new ArrayList<String>(getStack());
		if (htmlStrings.size() > 0) {
			htmlStrings.add("----");
		}

		// final int limit = 4;
		// int start;
		// final int skip = higherErrorPosition - limit + 1;
		// if (skip <= 0) {
		// start = 0;
		// } else {
		// if (skip == 1) {
		// htmlStrings.add("... (skipping 1 line) ...");
		// } else {
		// htmlStrings.add("... (skipping " + skip + " lines) ...");
		// }
		// start = higherErrorPosition - limit + 1;
		// }
		// for (int i = start; i < higherErrorPosition; i++) {
		// htmlStrings.add(StringUtils.hideComparatorCharacters(getSource().getLine(i)));
		// }
		for (String s : getPartialCode()) {
			htmlStrings.add(StringUtils.hideComparatorCharacters(s));
		}
		final String errorLine = getSource().getLine(higherErrorPosition);
		final String err = StringUtils.hideComparatorCharacters(errorLine);
		if (StringUtils.isNotEmpty(err)) {
			htmlStrings.add("<w:" + getRed(useRed) + ">" + err + "</w>");
		}
		final Collection<String> textErrors = new LinkedHashSet<String>();
		for (ErrorUml er : printedErrors) {
			textErrors.add(er.getError());
		}
		for (String er : textErrors) {
			htmlStrings.add(" <color:" + getRed(useRed) + ">" + er + "</color>");
		}
		boolean first = true;
		for (String s : getSuggest()) {
			if (first) {
				htmlStrings.add(" <color:" + getSuggestColor(useRed) + "><i>" + s + "</i></color>");
			} else {
				htmlStrings.add("<color:" + getSuggestColor(useRed) + ">" + StringUtils.hideComparatorCharacters(s)
						+ "</color>");
			}
			first = false;
		}
		htmlStrings.addAll(this.debugLines);

		return htmlStrings;
	}

	public List<String> getSuggest() {
		boolean suggested = false;
		for (ErrorUml er : printedErrors) {
			if (er.hasSuggest()) {
				suggested = true;
			}
		}
		if (suggested == false) {
			return Collections.emptyList();
		}
		final List<String> result = new ArrayList<String>();
		result.add("Did you mean:");
		for (ErrorUml er : printedErrors) {
			if (er.hasSuggest()) {
				result.add(er.getSuggest().getSuggestedLine());
			}
		}
		return Collections.unmodifiableList(result);

	}

	private Collection<ErrorUml> getErrors(ErrorUmlType type, List<ErrorUml> all) {
		final Collection<ErrorUml> result = new LinkedHashSet<ErrorUml>();
		for (ErrorUml error : all) {
			if (error.getType() == type) {
				result.add(error);
			}
		}
		return result;
	}

	// private int getHigherErrorPosition(ErrorUmlType type, List<ErrorUml> all) {
	// int max = Integer.MIN_VALUE;
	// for (ErrorUml error : getErrors(type, all)) {
	// if (error.getPosition() > max) {
	// max = error.getPosition();
	// }
	// }
	// return max;
	// }

	private LineLocation getHigherErrorPosition2(ErrorUmlType type, List<ErrorUml> all) {
		LineLocation max = null;
		for (ErrorUml error : getErrors(type, all)) {
			if (max == null || error.getLineLocation().compareTo(max) > 0) {
				max = error.getLineLocation();
			}
		}
		return max;
	}

	// private List<ErrorUml> getErrorsAt(int position, ErrorUmlType type, List<ErrorUml> all) {
	// final List<ErrorUml> result = new ArrayList<ErrorUml>();
	// for (ErrorUml error : getErrors(type, all)) {
	// if (error.getPosition() == position && StringUtils.isNotEmpty(error.getError())) {
	// result.add(error);
	// }
	// }
	// return result;
	// }

	private List<ErrorUml> getErrorsAt2(LineLocation position, ErrorUmlType type, List<ErrorUml> all) {
		final List<ErrorUml> result = new ArrayList<ErrorUml>();
		for (ErrorUml error : getErrors(type, all)) {
			if (error.getLineLocation().compareTo(position) == 0 && StringUtils.isNotEmpty(error.getError())) {
				result.add(error);
			}
		}
		return result;
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Error)");
	}

	public final LineLocation getHigherErrorPosition2() {
		return higherErrorPosition;
	}

	public final Collection<ErrorUml> getErrorsUml() {
		return Collections.unmodifiableCollection(printedErrors);
	}

	@Override
	public String getWarningOrError() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getDescription());
		sb.append(BackSlash.CHAR_NEWLINE);
		for (CharSequence t : getTitle().getDisplay()) {
			sb.append(t);
			sb.append(BackSlash.CHAR_NEWLINE);
		}
		sb.append(BackSlash.CHAR_NEWLINE);
		for (String s : getSuggest()) {
			sb.append(s);
			sb.append(BackSlash.CHAR_NEWLINE);
		}
		return sb.toString();
	}

	public static PSystemError merge(Collection<PSystemError> ps) {
		UmlSource source = null;
		final List<ErrorUml> errors = new ArrayList<ErrorUml>();
		final List<String> debugs = new ArrayList<String>();
		for (PSystemError system : ps) {
			if (system == null) {
				continue;
			}
			if (system.getSource() != null && source == null) {
				source = system.getSource();
			}
			errors.addAll(system.getErrorsUml());
			debugs.addAll(system.debugLines);
			if (system.debugLines.size() > 0) {
				debugs.add("-");
			}
		}
		if (source == null) {
			throw new IllegalStateException();
		}
		return new PSystemError(source, errors, debugs);
	}

}
