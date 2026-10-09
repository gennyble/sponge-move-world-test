package place.dreamy.worldmovetest;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.registry.Registry;
import org.spongepowered.api.world.WorldType;
import org.spongepowered.api.world.WorldTypes;
import org.spongepowered.api.world.generation.ChunkGenerator;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.WorldArchetype;
import org.spongepowered.api.world.server.WorldArchetypeType;
import org.spongepowered.api.world.server.storage.ServerWorldProperties;

import net.kyori.adventure.text.Component;

public class CreateOldWorld implements CommandExecutor {
	private final WorldMoveTest plugin;

	CreateOldWorld(WorldMoveTest plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult execute(CommandContext ctx) throws CommandException {
		ctx.sendMessage(Component.text("Attempting to create world " + WorldMoveTest.OLD_WORLD_KEY));
		createWorld().handle((worldOpt, e) -> {
			if (e != null) {
				this.plugin.getLogger().error("exception creating world {}", e.toString());
			}
			if (worldOpt != null) {
				return worldOpt;
			} else { return Optional.empty(); }
		}).thenAccept((worldOpt) -> {
			if (worldOpt.isEmpty()) {
				ctx.sendMessage(Component.text("Failed to create world " + WorldMoveTest.OLD_WORLD_KEY));
			} else {
				ctx.sendMessage(Component.text("Succeeded creating world " + WorldMoveTest.OLD_WORLD_KEY));
			}
		});

		return CommandResult.success();
	}

	private CompletableFuture<Optional<ServerWorld>> createWorld() {
		Registry<WorldType> worldTypeReg = WorldTypes.registry();
		WorldType worldType = worldTypeReg.findEntry(WorldMoveTest.OLD_WORLD_TYPE_KEY).get().value();
		WorldArchetype archetype = WorldArchetype.of(WorldArchetypeType.of(worldType, ChunkGenerator.theEnd()));
		ServerWorldProperties.LoadOptions loadOptions = ServerWorldProperties.LoadOptions.create(archetype);

		return plugin.getServer().worldManager().loadWorld(WorldMoveTest.OLD_WORLD_KEY, loadOptions);
	}
}
