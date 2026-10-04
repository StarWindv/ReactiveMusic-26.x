package circuitlord.reactivemusic.fabric;

import circuitlord.reactivemusic.ReactiveMusic;
import circuitlord.reactivemusic.SongPicker;
import circuitlord.reactivemusic.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
//? if >=26 {
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
//?} else {
/*import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
*///?}
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
//? if >=26 {
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
//?} else {
/*import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
*///?}

public class ReactiveMusicFabric implements ClientModInitializer {

    // Fabric API renamed ClientCommandManager to ClientCommands in the 26.x line
    //? if >=26 {
    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource> literal(String name) {
        return ClientCommands.literal(name);
    }

    private static <T> com.mojang.brigadier.builder.RequiredArgumentBuilder<net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource, T> argument(String name, com.mojang.brigadier.arguments.ArgumentType<T> type) {
        return ClientCommands.argument(name, type);
    }
    //?} else {
    /*private static com.mojang.brigadier.builder.LiteralArgumentBuilder<net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource> literal(String name) {
        return ClientCommandManager.literal(name);
    }

    private static <T> com.mojang.brigadier.builder.RequiredArgumentBuilder<net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource, T> argument(String name, com.mojang.brigadier.arguments.ArgumentType<T> type) {
        return ClientCommandManager.argument(name, type);
    }
    *///?}

    @Override
    public void onInitializeClient() {
        ReactiveMusic.init();
        ReactiveMusic.initClient();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("reactivemusic")
                .executes(context -> {
                    //? if >=26 {
                    Minecraft mc = context.getSource().getClient();
                    Screen screen = ModConfig.createScreen(mc.gui.screen());
                    mc.execute(() -> mc.gui.setScreen(screen));
                    //?} else {
                    /*MinecraftClient mc = context.getSource().getClient();
                    Screen screen = ModConfig.createScreen(mc.currentScreen);
                    mc.send(() -> mc.setScreen(screen));
                    *///?}
                    return 1;
                })

                .then(literal("logBlockCounter")
                        .executes(context -> {
                            SongPicker.queuedToPrintBlockCounter = true;
                            return 1;
                        })
                )

                // Writes a marker straight to the log, so a run can be annotated without
                // opening the chat. Reports the underground state at that moment too, so a
                // marker is enough to record what was happening without waiting for a flip.
                // Usage: /reactivemusic marker <text>
                .then(literal("marker")
                        .then(argument("text", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                                .executes(context -> {
                                    ReactiveMusic.LOGGER.info("[ReactiveMusic] MARKER: "
                                            + com.mojang.brigadier.arguments.StringArgumentType.getString(context, "text")
                                            + " | " + SongPicker.describeUndergroundState());
                                    return 1;
                                })
                        )
                )

                .then(literal("toggleSoundEventLogging")
                        .executes(context -> {
                            ReactiveMusic.printSoundEvents = !ReactiveMusic.printSoundEvents;
                            return 1;
                        })
                )

                .then(literal("blacklistDimension")
                        .executes(context -> {
                            //? if >=26 {
                            String key = context.getSource().getClient().level.dimension().identifier().toString();
                            //?} else {
                            /*String key = context.getSource().getClient().world.getRegistryKey().getValue().toString();
                            *///?}

                            //? if >=26 {
                            if (ReactiveMusic.config.blacklistedDimensions.contains(key)) {
                                context.getSource().sendFeedback(Component.literal("[ReactiveMusic]: " + key + " was already in blacklist."));
                                return 1;
                            }

                            context.getSource().sendFeedback(Component.literal("[ReactiveMusic]: Added " + key + " to blacklist."));
                            //?} else {
                            /*if (ReactiveMusic.config.blacklistedDimensions.contains(key)) {
                                context.getSource().sendFeedback(Text.literal("[ReactiveMusic]: " + key + " was already in blacklist."));
                                return 1;
                            }

                            context.getSource().sendFeedback(Text.literal("[ReactiveMusic]: Added " + key + " to blacklist."));
                            *///?}
                            ReactiveMusic.config.blacklistedDimensions.add(key);
                            ModConfig.saveConfig();
                            return 1;
                        })
                )

                .then(literal("unblacklistDimension")
                        .executes(context -> {
                            //? if >=26 {
                            String key = context.getSource().getClient().level.dimension().identifier().toString();
                            //?} else {
                            /*String key = context.getSource().getClient().world.getRegistryKey().getValue().toString();
                            *///?}

                            //? if >=26 {
                            if (!ReactiveMusic.config.blacklistedDimensions.contains(key)) {
                                context.getSource().sendFeedback(Component.literal("[ReactiveMusic]: " + key + " was not in blacklist."));
                                return 1;
                            }

                            context.getSource().sendFeedback(Component.literal("[ReactiveMusic]: Removed " + key + " from blacklist."));
                            //?} else {
                            /*if (!ReactiveMusic.config.blacklistedDimensions.contains(key)) {
                                context.getSource().sendFeedback(Text.literal("[ReactiveMusic]: " + key + " was not in blacklist."));
                                return 1;
                            }

                            context.getSource().sendFeedback(Text.literal("[ReactiveMusic]: Removed " + key + " from blacklist."));
                            *///?}
                            ReactiveMusic.config.blacklistedDimensions.remove(key);
                            ModConfig.saveConfig();
                            return 1;
                        })
                )

                .then(literal("toggleLogging")
                        .executes(context -> {
                            ReactiveMusic.chatLoggingEnabled = !ReactiveMusic.chatLoggingEnabled;
                            //? if >=26 {
                            context.getSource().sendFeedback(Component.literal("[ReactiveMusic]: Logging enabled: " + ReactiveMusic.chatLoggingEnabled));
                            //?} else {
                            /*context.getSource().sendFeedback(Text.literal("[ReactiveMusic]: Logging enabled: " + ReactiveMusic.chatLoggingEnabled));
                            *///?}
                            return 1;
                        })
                )
            )
        );
    }
}
