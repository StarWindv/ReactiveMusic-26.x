package circuitlord.reactivemusic.neoforge;

import circuitlord.reactivemusic.ReactiveMusic;
import circuitlord.reactivemusic.SongPicker;
import circuitlord.reactivemusic.config.ModConfig;
//? if >=26 {
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
//?} else {
/*import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
*///?}
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

//? if >=26 {
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
//?} else {
/*import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
*///?}
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

@Mod("reactivemusic")
public class ReactiveMusicNeoForge {

    public ReactiveMusicNeoForge(IEventBus modEventBus) {
        ReactiveMusic.init();
        modEventBus.addListener(this::onClientSetup);
        NeoForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (modContainer, parent) -> ModConfig.createScreen(parent)
        );
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ReactiveMusic::initClient);
    }

    @SubscribeEvent
    public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(literal("reactivemusic")
                .executes(context -> {
                    //? if >=26 {
                    Minecraft mc = Minecraft.getInstance();
                    Screen screen = ModConfig.createScreen(mc.gui.screen());
                    mc.execute(() -> mc.gui.setScreen(screen));
                    //?} else {
                    /*MinecraftClient mc = MinecraftClient.getInstance();
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
                        .then(argument("text", greedyString())
                                .executes(context -> {
                                    ReactiveMusic.LOGGER.info("[ReactiveMusic] MARKER: " + getString(context, "text")
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
                            Minecraft mc = Minecraft.getInstance();
                            String key = mc.level.dimension().identifier().toString();

                            if (ReactiveMusic.config.blacklistedDimensions.contains(key)) {
                                mc.player.sendSystemMessage(Component.literal("[ReactiveMusic]: " + key + " was already in blacklist."));
                                return 1;
                            }

                            mc.player.sendSystemMessage(Component.literal("[ReactiveMusic]: Added " + key + " to blacklist."));
                            //?} else {
                            /*MinecraftClient mc = MinecraftClient.getInstance();
                            String key = mc.world.getRegistryKey().getValue().toString();

                            if (ReactiveMusic.config.blacklistedDimensions.contains(key)) {
                                mc.player.sendMessage(Text.literal("[ReactiveMusic]: " + key + " was already in blacklist."), false);
                                return 1;
                            }

                            mc.player.sendMessage(Text.literal("[ReactiveMusic]: Added " + key + " to blacklist."), false);
                            *///?}
                            ReactiveMusic.config.blacklistedDimensions.add(key);
                            ModConfig.saveConfig();
                            return 1;
                        })
                )

                .then(literal("unblacklistDimension")
                        .executes(context -> {
                            //? if >=26 {
                            Minecraft mc = Minecraft.getInstance();
                            String key = mc.level.dimension().identifier().toString();

                            if (!ReactiveMusic.config.blacklistedDimensions.contains(key)) {
                                mc.player.sendSystemMessage(Component.literal("[ReactiveMusic]: " + key + " was not in blacklist."));
                                return 1;
                            }

                            mc.player.sendSystemMessage(Component.literal("[ReactiveMusic]: Removed " + key + " from blacklist."));
                            //?} else {
                            /*MinecraftClient mc = MinecraftClient.getInstance();
                            String key = mc.world.getRegistryKey().getValue().toString();

                            if (!ReactiveMusic.config.blacklistedDimensions.contains(key)) {
                                mc.player.sendMessage(Text.literal("[ReactiveMusic]: " + key + " was not in blacklist."), false);
                                return 1;
                            }

                            mc.player.sendMessage(Text.literal("[ReactiveMusic]: Removed " + key + " from blacklist."), false);
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
                            Minecraft.getInstance().player.sendSystemMessage(
                                    Component.literal("[ReactiveMusic]: Logging enabled: " + ReactiveMusic.chatLoggingEnabled));
                            //?} else {
                            /*MinecraftClient.getInstance().player.sendMessage(
                                    Text.literal("[ReactiveMusic]: Logging enabled: " + ReactiveMusic.chatLoggingEnabled), false);
                            *///?}
                            return 1;
                        })
                )
        );
    }
}
