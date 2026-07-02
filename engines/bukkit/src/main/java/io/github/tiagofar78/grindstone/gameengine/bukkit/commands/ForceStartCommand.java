package io.github.tiagofar78.grindstone.gameengine.bukkit.commands;

public class ForceStartCommand { /* implements CommandExecutor {
    
    public static final String COMMAND_LABEL = "forcestart";

    private GameFactory factory;
    private GameSettings settings;
    private List<MapFactory> availableMaps;

    public ForceStartCommand(GameFactory factory, GameSettings settings, MapFactory map) {
        this(factory, settings, singleMapList(map));
    }

    public ForceStartCommand(GameFactory factory, GameSettings settings, List<MapFactory> maps) {
        this.factory = factory;
        this.settings = settings;
        this.availableMaps = maps;
    }

    private static List<MapFactory> singleMapList(MapFactory map) {
        List<MapFactory> maps = new ArrayList<>();
        maps.add(map);
        return maps;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Locale locale = sender instanceof Player ? ((Player) sender).locale() : Locale.ENGLISH;
        if (!sender.hasPermission(Grindstone.ADMIN_PERMISSION)) {
            BukkitPlayer.sendMessage(sender, locale, "grindstone.forcestart.not_allowed");
            return true;
        }

        List<Collection<String>> teams = new ArrayList<>();
        for (String team : args) {
            List<String> members = new ArrayList<>();
            for (String member : team.split(",")) {
                if (BukkitPlayer.isOnline(member)) {
                    BukkitPlayer.sendMessage(sender, locale, "grindstone.forcestart.player_not_online", member);
                    return true;
                }

                members.add(member);
            }
            teams.add(members);
        }

        int mapIndex = new Random().nextInt(availableMaps.size());
        ForcestartResult result = GamesManager.forceStart(factory, settings, availableMaps.get(mapIndex), teams);
        BukkitPlayer.sendMessage(sender, locale, result.getMessageKey());

        return true;
    }
    */

}
