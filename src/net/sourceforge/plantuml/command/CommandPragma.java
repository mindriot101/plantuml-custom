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

import java.util.List;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UmlDiagram;

public class CommandPragma extends SingleLineCommand<UmlDiagram> {

	public CommandPragma() {
		super("(?i)^!pragma[%s]+([A-Za-z_][A-Za-z_0-9]*)(?:[%s]+(.*))?$");
	}

	@Override
	protected CommandExecutionResult executeArg(UmlDiagram system, List<String> arg) {
		final String name = StringUtils.goLowerCase(arg.get(0));
		final String value = arg.get(1);
		system.getPragma().define(name, value);
		if (name.equalsIgnoreCase("graphviz_dot") && value.equalsIgnoreCase("jdot")) {
			system.setUseJDot(true);
		} else if (name.equalsIgnoreCase("graphviz_dot")) {
			system.setDotExecutable(StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(value));
		}
		return CommandExecutionResult.ok();
	}

}
