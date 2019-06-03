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
import uk.co.harieo.StagePlay.entities.StageableEntity;

public class StageEntityModule extends AbstractModule {

	@Override
	public void configure() {
		bind(StageableEntity.class).toProvider(new EntityProvider());
	}

	private static class EntityProvider implements Provider<StageableEntity> {

		@Override
		public String getName() {
			return "entity";
		}

		@Nullable
		@Override
		public StageableEntity get(CommandArgs args, List<? extends Annotation> mods)
				throws ArgumentException, ProvisionException {
			String rawEntityType = args.next();

			StageableEntity entity = null;
			for (StageableEntity entityType : StageableEntity.values()) {
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
			for (StageableEntity entityType : StageableEntity.values()) {
				if (entityType.name().startsWith(prefix)) {
					typeNames.add(entityType.name());
				}
			}
			return typeNames;
		}

	}

}
