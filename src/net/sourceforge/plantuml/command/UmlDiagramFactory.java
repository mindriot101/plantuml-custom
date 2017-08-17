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
package net.sourceforge.plantuml.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.CharSequence2;
import net.sourceforge.plantuml.ErrorUml;
import net.sourceforge.plantuml.ErrorUmlType;
import net.sourceforge.plantuml.NewpagedDiagram;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.PSystemError;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.classdiagram.command.CommandHideShowByGender;
import net.sourceforge.plantuml.classdiagram.command.CommandHideShowByVisibility;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.suggest.SuggestEngine;
import net.sourceforge.plantuml.suggest.SuggestEngineResult;
import net.sourceforge.plantuml.suggest.SuggestEngineStatus;
import net.sourceforge.plantuml.utils.StartUtils;
import net.sourceforge.plantuml.version.IteratorCounter2;

public abstract class UmlDiagramFactory extends PSystemAbstractFactory {

	private final List<Command> cmds;

	protected UmlDiagramFactory() {
		this(DiagramType.UML);
	}

	protected UmlDiagramFactory(DiagramType type) {
		super(type);
		cmds = createCommands();
	}

	final public Diagram createSystem(UmlSource source) {
		final IteratorCounter2 it = source.iterator2();
		final CharSequence2 startLine = it.next();
		if (StartUtils.isArobaseStartDiagram(startLine) == false) {
			throw new UnsupportedOperationException();
		}

		if (source.isEmpty()) {
			return buildEmptyError(source, startLine.getLocation());
		}
		AbstractPSystem sys = createEmptyDiagram();

		while (it.hasNext()) {
			if (StartUtils.isArobaseEndDiagram(it.peek())) {
				if (sys == null) {
					return null;
				}
				final String err = sys.checkFinalError();
				if (err != null) {
					return buildExecutionError(source, err, it.peek().getLocation());
				}
				if (source.getTotalLineCount() == 2) {
					return buildEmptyError(source, it.peek().getLocation());
				}
				sys.makeDiagramReady();
				if (sys.isOk() == false) {
					return null;
				}
				sys.setSource(source);
				return sys;
			}
			sys = executeOneLine(sys, source, it);
			if (sys instanceof PSystemError) {
				return sys;
			}
		}
		sys.setSource(source);
		return sys;

	}

	private AbstractPSystem executeOneLine(AbstractPSystem sys, UmlSource source, final IteratorCounter2 it) {
		final CommandControl commandControl = isValid2(it);
		if (commandControl == CommandControl.NOT_OK) {
			final ErrorUml err = new ErrorUml(ErrorUmlType.SYNTAX_ERROR, "Syntax Error?", /* it.currentNum(), */it.peek()
					.getLocation());
			if (OptionFlags.getInstance().isUseSuggestEngine()) {
				final SuggestEngine engine = new SuggestEngine(source, this);
				final SuggestEngineResult result = engine.tryToSuggest(sys);
				if (result.getStatus() == SuggestEngineStatus.ONE_SUGGESTION) {
					err.setSuggest(result);
				}
			}
			sys = new PSystemError(source, err, null);
		} else if (commandControl == CommandControl.OK_PARTIAL) {
			final IteratorCounter2 saved = it.cloneMe();
			final CommandExecutionResult result = manageMultiline2(it, sys);
			if (result.isOk() == false) {
				final ErrorUml err = new ErrorUml(ErrorUmlType.EXECUTION_ERROR, result.getError(),
				/* it.currentNum() - 1, */saved.next().getLocation());
				sys = new PSystemError(source, err, null);

			}
		} else if (commandControl == CommandControl.OK) {
			final CharSequence line = it.next();
			final BlocLines lines = BlocLines.single(line);
			Command cmd = getFirstCommandOkForLines(lines);
			final CommandExecutionResult result = sys.executeCommand(cmd, lines);
			if (result.isOk() == false) {
				final ErrorUml err = new ErrorUml(ErrorUmlType.EXECUTION_ERROR, result.getError(),
				/* it.currentNum() - 1, */((CharSequence2) line).getLocation());
				sys = new PSystemError(source, err,
						result.getDebugLines());
			}
			if (result.getNewDiagram() != null) {
				sys = result.getNewDiagram();
			}
		} else {
			assert false;
		}
		return sys;
	}

	public CommandControl isValid2(final IteratorCounter2 it) {
		final BlocLines lines = BlocLines.single(it.peek());
		for (Command cmd : cmds) {
			final CommandControl result = cmd.isValid(lines);
			if (result == CommandControl.OK) {
				return result;
			}
			if (result == CommandControl.OK_PARTIAL && isMultilineCommandOk(it.cloneMe(), cmd) != null) {
				return result;
			}
		}
		return CommandControl.NOT_OK;
	}

	public CommandControl goForwardMultiline(final IteratorCounter2 it) {
		final BlocLines lines = BlocLines.single(it.peek());
		for (Command cmd : cmds) {
			final CommandControl result = cmd.isValid(lines);
			if (result == CommandControl.OK) {
				throw new IllegalStateException();
			}
			if (result == CommandControl.OK_PARTIAL && isMultilineCommandOk(it, cmd) != null) {
				return result;
			}
		}
		return CommandControl.NOT_OK;
		// throw new IllegalStateException();
	}

	private CommandExecutionResult manageMultiline2(IteratorCounter2 it, AbstractPSystem system) {
		for (Command cmd : cmds) {
			if (isMultilineCommandOk(it.cloneMe(), cmd) != null) {
				final BlocLines lines = isMultilineCommandOk(it, cmd);
				if (system instanceof NewpagedDiagram) {
					final NewpagedDiagram newpagedDiagram = (NewpagedDiagram) system;
					return cmd.execute(newpagedDiagram.getLastDiagram(), lines);

				}
				return cmd.execute(system, lines);
			}
		}
		return CommandExecutionResult.ok();
	}

	private BlocLines isMultilineCommandOk(IteratorCounter2 it, Command cmd) {
		BlocLines lines = new BlocLines();
		int nb = 0;
		while (it.hasNext()) {
			lines = addOneSingleLineManageEmbedded2(it, lines);
			final CommandControl result = cmd.isValid(lines);
			if (result == CommandControl.NOT_OK) {
				return null;
			}
			if (result == CommandControl.OK) {
				return lines;
			}
			nb++;
			if (cmd instanceof CommandDecoratorMultine && nb > ((CommandDecoratorMultine) cmd).getNbMaxLines()) {
				return null;
			}
		}
		return null;
	}

	private BlocLines addOneSingleLineManageEmbedded2(IteratorCounter2 it, BlocLines lines) {
		final CharSequence linetoBeAdded = it.next();
		lines = lines.add2(linetoBeAdded);
		if (StringUtils.trinNoTrace(linetoBeAdded).equals("{{")) {
			while (it.hasNext()) {
				final CharSequence s = it.next();
				lines = lines.add2(s);
				if (StringUtils.trinNoTrace(s).equals("}}")) {
					return lines;
				}
			}
		}
		return lines;
	}

	// -----------------------------------

	final public CommandControl isValid(BlocLines lines) {
		for (Command cmd : cmds) {
			final CommandControl result = cmd.isValid(lines);
			if (result == CommandControl.OK) {
				return result;
			}
			if (result == CommandControl.OK_PARTIAL) {
				return result;
			}
		}
		return CommandControl.NOT_OK;

	}

	private Command getFirstCommandOkForLines(BlocLines lines) {
		for (Command cmd : cmds) {
			final CommandControl result = cmd.isValid(lines);
			if (result == CommandControl.OK) {
				return cmd;
			}
		}
		throw new IllegalArgumentException();
	}

	protected abstract List<Command> createCommands();

	public abstract AbstractPSystem createEmptyDiagram();

	final protected void addCommonCommands(List<Command> cmds) {
		cmds.add(new CommandNope());
		cmds.add(new CommandComment());
		cmds.add(new CommandMultilinesComment());
		cmds.add(new CommandPragma());
		cmds.add(new CommandTitle());
		cmds.add(new CommandCaption());
		cmds.add(new CommandMultilinesTitle());
		cmds.add(new CommandMultilinesLegend());

		cmds.add(new CommandFooter());
		cmds.add(new CommandMultilinesFooter());

		cmds.add(new CommandHeader());
		cmds.add(new CommandMultilinesHeader());

		cmds.add(new CommandSkinParam());
		cmds.add(new CommandSkinParamMultilines());
		cmds.add(new CommandMinwidth());
		cmds.add(new CommandRotate());
		cmds.add(new CommandScale());
		cmds.add(new CommandScaleWidthAndHeight());
		cmds.add(new CommandScaleWidthOrHeight());
		cmds.add(new CommandScaleMaxWidth());
		cmds.add(new CommandScaleMaxHeight());
		cmds.add(new CommandScaleMaxWidthAndHeight());
		cmds.add(new CommandAffineTransform());
		cmds.add(new CommandAffineTransformMultiline());
		cmds.add(new CommandHideUnlinked());
		final FactorySpriteCommand factorySpriteCommand = new FactorySpriteCommand();
		cmds.add(factorySpriteCommand.createMultiLine(false));
		cmds.add(factorySpriteCommand.createSingleLine());
		cmds.add(new CommandSpriteFile());

		cmds.add(new CommandHideShowByVisibility());
		cmds.add(new CommandHideShowByGender());

	}

	final public List<String> getDescription() {
		final List<String> result = new ArrayList<String>();
		for (Command cmd : createCommands()) {
			result.addAll(Arrays.asList(cmd.getDescription()));
		}
		return Collections.unmodifiableList(result);

	}

}
