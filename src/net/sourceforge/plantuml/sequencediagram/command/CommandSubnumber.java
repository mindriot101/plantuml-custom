package net.sourceforge.plantuml.sequencediagram.command;

import java.util.List;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;

public class CommandSubnumber extends SingleLineCommand<SequenceDiagram> {
	public CommandSubnumber() {
		super("(?i)^subnumber.*$");
	}

	@Override
	protected CommandExecutionResult executeArg(SequenceDiagram sequenceDiagram, List<String> arg) {
		sequenceDiagram.subnumberGo();
		return CommandExecutionResult.ok();
	}
}
