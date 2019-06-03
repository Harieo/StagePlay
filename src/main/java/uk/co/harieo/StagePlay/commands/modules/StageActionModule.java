package uk.co.harieo.StagePlay.commands.modules;

import app.ashcon.intake.argument.ArgumentException;
import app.ashcon.intake.argument.ArgumentParseException;
import app.ashcon.intake.argument.CommandArgs;
import app.ashcon.intake.argument.Namespace;
import app.ashcon.intake.parametric.AbstractModule;
import app.ashcon.intake.parametric.Provider;
import app.ashcon.intake.parametric.ProvisionException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import uk.co.harieo.StagePlay.scripts.StageAction;

public class StageActionModule extends AbstractModule {

	@Override
	public void configure() {
		bind(StageAction.class).toProvider(new ActionProvider());
	}

	private static class ActionProvider implements Provider<StageAction> {

		@Override
		public String getName() {
			return "action";
		}

		@Nullable
		@Override
		public StageAction get(CommandArgs args, List<? extends Annotation> mods)
				throws ArgumentException, ProvisionException {
			String rawActionType = args.next();

			StageAction action = null;
			for (StageAction stageAction : StageAction.values()) {
				if (stageAction.name().equalsIgnoreCase(rawActionType.toLowerCase())) {
					action = stageAction;
				}
			}

			if (action == null) {
				throw new ArgumentParseException("The action " + rawActionType + " does not appear to be applicable");
			}

			return action;
		}

		@Override
		public List<String> getSuggestions(String prefix, Namespace namespace, List<? extends Annotation> modifiers) {
			List<String> typeNames = new ArrayList<>();
			for (StageAction action : StageAction.values()) {
				if (action.name().startsWith(prefix)) {
					typeNames.add(action.name());
				}
			}
			return typeNames;
		}

	}

}
