package io.github.tiagofar78.grindstone.gameengine.bukkit.commands;

public class PasteMapsCommand { /* implements CommandExecutor {

    public static final String COMMAND_LABEL = "pastemaps";

    private File pluginFolder;
    private List<MapFactory> maps;
    private String worldName;

    public PasteMapsCommand(File pluginFolder, String gamemodeName, List<MapFactory> maps, String worldName) {
        this.pluginFolder = pluginFolder;
        this.maps = maps;
        this.worldName = worldName;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Locale locale = sender instanceof Player ? ((Player) sender).locale() : Locale.ENGLISH;
        if (!sender.hasPermission(Grindstone.ADMIN_PERMISSION)) {
            BukkitPlayer.sendMessage(sender, locale, "grindstone.pastemaps.not_allowed");
            return true;
        }

        if (Bukkit.getWorld(worldName) == null) {
            BukkitPlayer.sendMessage(sender, locale, "grindstone.pastemaps.world_not_found");
            return true;
        }

        BukkitPlayer.sendMessage(sender, locale, "grindstone.pastemaps.pasting");
        pasteMaps();
        BukkitPlayer.sendMessage(sender, locale, "grindstone.pastemaps.completed");

        return false;
    }

    private void pasteMaps() {
        World world = BukkitAdapter.adapt(Bukkit.getWorld(worldName));
        for (MapFactory map : maps) {
            int x = map.indexInWorld();
            for (int z = 0; z < GamesManager.SIMULTANEOUS_GAMES; z++) {
                pasteMap(world, x * GamesManager.MAPS_DISTANCE, z * GamesManager.MAPS_DISTANCE, map.getName());
            }
        }
    }

    private void pasteMap(World world, int x, int z, String mapName) {
        File file = new File(pluginFolder + File.separator + "maps", mapName + ".schem");
        if (!file.exists()) {
            throw new IllegalArgumentException("There is no map named " + mapName);
        }

        ClipboardFormat format = ClipboardFormats.findByPath(file.toPath());
        try {

            ClipboardReader reader = format.getReader(new FileInputStream(file));
            Clipboard clipboard = reader.read();

            EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();
            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(
                    BlockVector3.at(x, GamesManager.MAPS_Y_CORD, z)
            ).ignoreAirBlocks(true).copyBiomes(true).build();
            Operations.complete(operation);
            editSession.commit();
            editSession.close();
        } catch (IOException | WorldEditException e) {
            e.printStackTrace();
        }
    }
    */

}
