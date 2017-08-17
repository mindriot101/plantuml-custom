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
package net.sourceforge.plantuml.project3;

import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;

public class NaturalCommand extends SingleLineCommand2<GanttDiagram> {

	private final SubjectPattern subjectPattern;
	private final VerbPattern verbPattern;
	private final ComplementPattern complementPattern;

	private NaturalCommand(RegexConcat pattern, SubjectPattern subject, VerbPattern verb, ComplementPattern complement) {
		super(pattern);
		this.subjectPattern = subject;
		this.verbPattern = verb;
		this.complementPattern = complement;
	}

	@Override
	public String toString() {
		return subjectPattern.toString() + " " + verbPattern.toString() + " " + complementPattern.toString();
	}

	@Override
	protected CommandExecutionResult executeArg(GanttDiagram system, RegexResult arg) {
		final Subject subject = subjectPattern.getSubject(system, arg);
		final Verb verb = verbPattern.getVerb(system, arg);
		final Failable<Complement> complement = complementPattern.getComplement(system, arg, "0");
		if (complement.isFail()) {
			return CommandExecutionResult.error(complement.getError());
		}
		return verb.execute(subject, complement.get());
	}

	public static Command create(SubjectPattern subject, VerbPattern verb, ComplementPattern complement) {
		return new NaturalCommand(new RegexConcat(//
				new RegexLeaf("^"), //
				subject.toRegex(), //
				new RegexLeaf("[%s]+"), //
				verb.toRegex(), //
				new RegexLeaf("[%s]+"), //
				complement.toRegex("0"), //
				new RegexLeaf("$")), subject, verb, complement);
	}
}
