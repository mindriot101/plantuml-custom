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
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.sequencediagram.InGroupable;
import net.sourceforge.plantuml.sequencediagram.MessageExo;
import net.sourceforge.plantuml.sequencediagram.MessageExoType;
import net.sourceforge.plantuml.sequencediagram.NoteOnMessage;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;

class Step1MessageExo extends Step1Abstract {

	private final MessageExoArrow messageArrow;

	Step1MessageExo(ParticipantRange range, StringBounder stringBounder, MessageExo message, DrawableSet drawingSet,
			Frontier freeY) {
		super(range, stringBounder, message, drawingSet, freeY);

		setConfig(getArrowType(message));

		this.messageArrow = new MessageExoArrow(freeY.getFreeY(range), drawingSet.getSkin(), drawingSet.getSkin()
				.createComponent(ComponentType.ARROW, getConfig(), drawingSet.getSkinParam(),
						message.getLabelNumbered()), getLivingParticipantBox(), message.getType(), message.getUrl(),
				message.isShortArrow(), message.getArrowConfiguration());

		final List<NoteOnMessage> noteOnMessages = message.getNoteOnMessages();
		for (NoteOnMessage noteOnMessage : noteOnMessages) {
			final ISkinParam skinParam = noteOnMessage.getSkinParamNoteBackcolored(drawingSet.getSkinParam());
			addNote(drawingSet.getSkin().createComponent(ComponentType.NOTE, null, skinParam,
					noteOnMessage.getDisplay()));
			// throw new UnsupportedOperationException();
		}

	}

	Frontier prepareMessage(ConstraintSet constraintSet, InGroupablesStack inGroupablesStack) {
		final Arrow graphic = createArrow();
		final double arrowYStartLevel = graphic.getArrowYStartLevel(getStringBounder());
		final double arrowYEndLevel = graphic.getArrowYEndLevel(getStringBounder());

		getMessage().setPosYstartLevel(arrowYStartLevel);

		final double length = graphic.getArrowOnlyWidth(getStringBounder());
		incFreeY(graphic.getPreferredHeight(getStringBounder()));
		double marginActivateAndDeactive = 0;
		if (getMessage().isActivateAndDeactive()) {
			marginActivateAndDeactive = 30;
			incFreeY(marginActivateAndDeactive);
		}
		getDrawingSet().addEvent(getMessage(), graphic);

		final LivingParticipantBox livingParticipantBox = getLivingParticipantBox();
		if (messageArrow.getType().isRightBorder()) {
			constraintSet.getConstraint(livingParticipantBox.getParticipantBox(), constraintSet.getLastborder())
					.ensureValue(length);
		} else {
			constraintSet.getConstraint(constraintSet.getFirstBorder(), livingParticipantBox.getParticipantBox())
					.ensureValue(length);
		}

		final double posYendLevel = arrowYEndLevel + marginActivateAndDeactive;
		getMessage().setPosYendLevel(posYendLevel);

		assert graphic instanceof InGroupable;
		if (graphic instanceof InGroupable) {
			inGroupablesStack.addElement((InGroupable) graphic);
			inGroupablesStack.addElement(livingParticipantBox);
		}

		return getFreeY();
	}

	private LivingParticipantBox getLivingParticipantBox() {
		return getDrawingSet().getLivingParticipantBox(((MessageExo) getMessage()).getParticipant());
	}

	private Arrow createArrow() {
		if (getMessage().getNoteOnMessages().size() == 0) {
			return messageArrow;
		}
		final List<NoteBox> noteBoxes = new ArrayList<NoteBox>();
		for (int i = 0; i < getNotes().size(); i++) {
			final Component note = getNotes().get(i);
			final NoteOnMessage noteOnMessage = getMessage().getNoteOnMessages().get(i);
			noteBoxes.add(createNoteBox(getStringBounder(), messageArrow, note, noteOnMessage));
		}
		return new ArrowAndNoteBox(getStringBounder(), messageArrow, noteBoxes);
	}

	private ArrowConfiguration getArrowType(MessageExo m) {
		final MessageExoType type = m.getType();
		ArrowConfiguration result = null;

		if (type.getDirection() == 1) {
			result = m.getArrowConfiguration();
		} else {
			result = m.getArrowConfiguration().reverse();
		}
		result = result.withDecoration1(m.getArrowConfiguration().getDecoration1());
		result = result.withDecoration2(m.getArrowConfiguration().getDecoration2());
		return result;
		// ArrowConfiguration result = null;
		// if (type.getDirection() == 1) {
		// result = ArrowConfiguration.withDirectionNormal();
		// } else {
		// result = ArrowConfiguration.withDirectionReverse();
		// }
		// if (m.getArrowConfiguration().isDotted()) {
		// result = result.withDotted();
		// }
		// if (m.getArrowConfiguration().isAsync()) {
		// result = result.withHead(ArrowHead.ASYNC);
		// }
		// result = result.withPart(m.getArrowConfiguration().getPart());
		// return result;
	}

}
