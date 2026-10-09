package place.dreamy.worldmovetest;

import org.apache.logging.log4j.Logger;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.RegisterRegistryValueEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.WorldType;
import org.spongepowered.api.world.WorldTypes;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

// Imports for logger
import com.google.inject.Inject;

import net.kyori.adventure.text.Component;

// Have a look here: https://docs.spongepowered.org/stable/en/plugin/plugin-class.html
@Plugin("worldmovefirst")
public class WorldMoveTest {
	public static final String NAMESPACE = "worldmovenew";
	public static final ResourceKey WORLD_TYPE_KEY = ResourceKey.of(NAMESPACE, "world_type");
	public static final ResourceKey WORLD_KEY = ResourceKey.of(NAMESPACE, "test_world");

	public static final String OLD_NAMESPACE = "worldmoveold";
	public static final ResourceKey OLD_WORLD_TYPE_KEY = ResourceKey.of(OLD_NAMESPACE, "world_type");
	public static final ResourceKey OLD_WORLD_KEY = ResourceKey.of(OLD_NAMESPACE, "test_world");

	private Server server;

	@Inject
	private PluginContainer container;

	@Inject
	private Logger logger;

	@Listener
	public void onRegistryGameScoped(final RegisterRegistryValueEvent event) {
		registerWorldType(event, WORLD_TYPE_KEY);
		registerWorldType(event, OLD_WORLD_TYPE_KEY);
	}

	@Listener
	public void onRegisterCommands(final RegisterCommandEvent<Command.Parameterized> event) {
		Parameter.Value<ResourceKey> keyParameter = Parameter.resourceKey().key("world_key").build();
		Command.Parameterized cmd =  Command
			.builder()
			.executor((CommandContext ctx) -> {
				ResourceKey worldKey = ctx.requireOne(keyParameter);

				server.worldManager().loadWorld(worldKey).thenAccept((loaded) -> {
					if (loaded.isPresent()) {
						ctx.sendMessage(Component.text("World load success!"));
					} else {
						ctx.sendMessage(Component.text("World load failure!"));
					}
				});

				return CommandResult.success();
			}).addParameter(keyParameter).build();

		event.register(this.container, cmd, "load_world");

		Command.Parameterized listWorldKeys =  Command
			.builder()
			.executor((CommandContext ctx) -> {
				server.worldManager().worldKeys().stream().forEach((key) -> {
					ctx.sendMessage(Component.text("World key " + key));
				});
				return CommandResult.success();
			}).build();
		event.register(this.container, listWorldKeys, "list_world_keys");

		Command.Parameterized oldWorld = Command.builder().executor(new CreateOldWorld(this)).build();
		event.register(this.container, oldWorld, "create_old_world");

		Command.Parameterized migrateWorld = Command.builder().executor(new MigrateWorld(this)).build();
		event.register(this.container, migrateWorld, "migrate_world");
	}

	private void registerWorldType(RegisterRegistryValueEvent event, ResourceKey key) {
		event.registry(RegistryTypes.WORLD_TYPE, (step) -> {
			logger.info("Registering {}", key);

			WorldType worldType = WorldType.builder()
				.from(WorldTypes.THE_END.get())
				.add(Keys.CREATE_DRAGON_FIGHT, true)
				.build();

			step.register(key, worldType);
		});
	}

	@Listener
	public void onServerStart(final StartedEngineEvent<Server> event) {
		this.server = event.engine();
	}

	public Server getServer() {
		return this.server;
	}

	public Logger getLogger() {
		return this.logger;
	}
}
