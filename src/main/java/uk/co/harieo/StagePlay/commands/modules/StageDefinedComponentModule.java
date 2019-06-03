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
import uk.co.harieo.StagePlay.components.DefinedComponent;

public class StageDefinedComponentModule extends AbstractModule {

	@Override
	public void configure() {
		bind(DefinedComponent.class).toProvider(new DefinedComponentProvider());
	}

	private static class DefinedComponentProvider implements Provider<DefinedComponent> {

		@Override
		public String getName() {
			return "defined-component";
		}

		@Nullable
		@Override
		public DefinedComponent get(CommandArgs args, List<? extends Annotation> mods)
				throws ArgumentException, ProvisionException {
			String rawComponent = args.next();

			DefinedComponent component = null;
			for (DefinedComponent definedComponent : DefinedComponent.values()) {
				if (definedComponent.name().equalsIgnoreCase(rawComponent.toLowerCase())) {
					component = definedComponent;
				}
			}

			if (component == null) {
				throw new ArgumentParseException("The component " + rawComponent + " does not appear to exist");
			}

			return component;
		}

		@Override
		public List<String> getSuggestions(String prefix, Namespace namespace, List<? extends Annotation> modifiers) {
			List<String> typeNames = new ArrayList<>();
			for (DefinedComponent component : DefinedComponent.values()) {
				if (component.name().startsWith(prefix)) {
					typeNames.add(component.name());
				}
			}
			return typeNames;
		}

	}

}
