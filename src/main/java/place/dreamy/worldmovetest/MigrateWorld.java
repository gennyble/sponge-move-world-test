package place.dreamy.worldmovetest;

import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.Logger;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.world.server.WorldManager;

import net.kyori.adventure.text.Component;

public class MigrateWorld implements CommandExecutor {
	private final Logger logger;
	private final WorldMoveTest plugin;

	MigrateWorld(WorldMoveTest plugin) {
		this.plugin = plugin;
		this.logger = plugin.getLogger();
	}

	@Override
	public CommandResult execute(CommandContext ctx) throws CommandException {
		ctx.sendMessage(Component.text("Attempting to move world " + WorldMoveTest.OLD_WORLD_KEY));
		moveWorld(WorldMoveTest.OLD_WORLD_KEY, WorldMoveTest.WORLD_KEY);

		return CommandResult.success();
	}

	private CompletableFuture<Void> moveWorld(ResourceKey oldWorld, ResourceKey newWorld) {
		WorldManager manager = plugin.getServer().worldManager();

		return manager.moveWorld(oldWorld, newWorld).thenAccept((moved) -> {
			if (!moved) {
				logger.warn("Move failed! Tried to move {} to {}", oldWorld, newWorld);
				return;
			}
			logger.info("Moved world {} to {}", oldWorld, newWorld);

			/*ServerWorldProperties.LoadOptions getOrLoad = ServerWorldProperties.LoadOptions.getOrLoad();
			manager.loadProperties(newWorld, getOrLoad).thenAccept((propsOpt) -> {
				if (propsOpt.isEmpty()) {
					logger.warn("Failed to load world properties for {}", newWorld);
					return;
				}
				logger.info("Loaded world properties for {}", newWorld);

				// Get the new world type
				Registry<WorldType> worldTypeReg = WorldTypes.registry();
				WorldType worldType = worldTypeReg.findEntry(WorldMoveTest.WORLD_TYPE_KEY).get().value();

				ServerWorldProperties props = propsOpt.get();
				props.setWorldType(worldType);

				logger.info("Set world type to {} for world {}", WorldMoveTest.WORLD_TYPE_KEY, newWorld);
			});*/
		});
	}
}
