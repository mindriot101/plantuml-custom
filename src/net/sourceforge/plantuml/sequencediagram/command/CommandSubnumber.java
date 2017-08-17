package net.sourceforge.plantuml.sequencediagram.command;

import java.util.List;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;

public class CommandSubnumber extends SingleLineCommand<SequenceDiagram> {
	public CommandSubnumber() {
		super("(?i)^subnumber[%s]*(\\d+)?[%s]*$");
	}

	@Override
	protected CommandExecutionResult executeArg(SequenceDiagram sequenceDiagram, List<String> arg) {
		int topNumber = 1;
		if (arg.get(0) != null) {
			topNumber = Integer.parseInt(arg.get(0));
		}

		sequenceDiagram.subnumberGo(topNumber);
		return CommandExecutionResult.ok();
	}
}
