package io.github.tiagofar78.grindstone.gameengine.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class Grindstone extends JavaPlugin {
    
    /*

    public static final String ADMIN_PERMISSION = "Grindstone.Admin";

    private static final String LEAVE_QUEUE_COMMAND_LABEL = "dequeue";
    private static final String FORCE_STOP_COMMAND_LABEL = "forcestop";

    private static final Map<JavaPlugin, Map<String, CommandExecutor>> pluginCommands = new HashMap<>();

    private static Grindstone instance;

    public static Grindstone getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }

        MessagesRepo.getTranslations().register(this.getClassLoader(), "/lang");

        instance = (Grindstone) Bukkit.getServer().getPluginManager().getPlugin("TF_Grindstone");

        registerCommand(this, LEAVE_QUEUE_COMMAND_LABEL, new LeaveQueueCommand());
        registerCommand(this, FORCE_STOP_COMMAND_LABEL, new ForceStopCommand());

        GrindstoneConfig.load();

        registerListener(new PlayerConnectionListener());
        PartyService.registerFallbackProviderListener();
    }

    public static void registerListener(Listener listener) {
        instance.getServer().getPluginManager().registerEvents(listener, instance);
    }

    public static void registerCommands(JavaPlugin plugin) {
        plugin.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS,
                event -> {

                    for (Map.Entry<String, CommandExecutor> entry : pluginCommands.get(plugin).entrySet()) {

                        String label = entry.getKey();
                        CommandExecutor executor = entry.getValue();

                        event.registrar().register(
                                Commands.literal(label).executes(ctx -> {

                                    CommandSender sender = ctx.getSource().getSender();

                                    String input = ctx.getInput();
                                    String[] split = input.split(" ");
                                    String[] args = Arrays.copyOfRange(split, 1, split.length);

                                    executor.onCommand(sender, null, label, args);
                                    return 1;
                                }).build()
                        );
                    }
                }
        );
    }

    public static void registerCommand(JavaPlugin plugin, String label, CommandExecutor executor) {
        if (!pluginCommands.containsKey(plugin)) {
            pluginCommands.put(plugin, new HashMap<>());
        }

        pluginCommands.get(plugin).put(label, executor);
    }
    
    */

}
