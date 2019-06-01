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
import uk.co.harieo.StagePlay.entities.StageableEntities;

public class StageEntityModule extends AbstractModule {

	@Override
	public void configure() {
		bind(StageableEntities.class).toProvider(new EntityProvider());
	}

	private static class EntityProvider implements Provider<StageableEntities> {

		@Override
		public String getName() {
			return "entity";
		}

		@Nullable
		@Override
		public StageableEntities get(CommandArgs args, List<? extends Annotation> mods)
				throws ArgumentException, ProvisionException {
			String rawEntityType = args.next();

			StageableEntities entity = null;
			for (StageableEntities entityType : StageableEntities.values()) {
				if (entityType.name().equalsIgnoreCase(rawEntityType.toLowerCase())) {
					entity = entityType;
				}
			}

			if (entity == null) {
				throw new ArgumentParseException("The entity type " + rawEntityType + " does not appear to be applicable");
			}

			return entity;
		}

		@Override
		public List<String> getSuggestions(String prefix, Namespace namespace, List<? extends Annotation> modifiers) {
			List<String> typeNames = new ArrayList<>();
			for (StageableEntities entityType : StageableEntities.values()) {
				if (entityType.name().startsWith(prefix)) {
					typeNames.add(entityType.name());
				}
			}
			return typeNames;
		}

	}

}
