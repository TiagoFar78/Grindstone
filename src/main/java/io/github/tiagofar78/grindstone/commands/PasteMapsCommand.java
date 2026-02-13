package io.github.tiagofar78.grindstone.commands;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import io.github.tiagofar78.grindstone.Grindstone;
import io.github.tiagofar78.grindstone.bukkit.BukkitPlayer;
import io.github.tiagofar78.grindstone.game.GamesManager;
import io.github.tiagofar78.grindstone.game.MapFactory;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PasteMapsCommand implements CommandExecutor {

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
            BukkitPlayer.sendMessage(sender, locale, "pastemaps.not_allowed");
            return true;
        }

        if (Bukkit.getWorld(worldName) == null) {
            BukkitPlayer.sendMessage(sender, locale, "pastemaps.world_not_found");
            return true;
        }

        BukkitPlayer.sendMessage(sender, locale, "pastemaps.pasting");
        pasteMaps();
        BukkitPlayer.sendMessage(sender, locale, "pastemaps.completed");

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

}
