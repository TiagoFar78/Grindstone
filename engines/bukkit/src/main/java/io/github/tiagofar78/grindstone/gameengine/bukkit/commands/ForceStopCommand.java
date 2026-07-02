package io.github.tiagofar78.grindstone.gameengine.bukkit.commands;

public class ForceStopCommand { /* implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Locale locale = sender instanceof Player ? ((Player) sender).locale() : Locale.ENGLISH;
        if (!sender.hasPermission(Grindstone.ADMIN_PERMISSION)) {
            BukkitPlayer.sendMessage(sender, locale, "grindstone.forcestop.not_allowed");
            return true;
        }

        String name = sender.getName();
        if (GamesManager.isInGame(name)) {
            GamesManager.getGame(name).forceStop();
            BukkitPlayer.sendMessage(sender, locale, "grindstone.forcestop.success");
            return true;
        }

        if (args.length == 0) {
            Set<Game> games = GamesManager.getUniqueGames();
            if (games.size() == 0) {
                BukkitPlayer.sendMessage(sender, locale, "grindstone.forcestop.no_games_running");
                return true;
            }

            BukkitPlayer.sendMessage(sender, locale, "grindstone.forcestop.no_games_running");
            sender.sendMessage(getGamesMessage(games));

            return true;
        }

        String targetName = args[0];
        if (GamesManager.isInGame(targetName)) {
            GamesManager.getGame(targetName).forceStop();
            BukkitPlayer.sendMessage(sender, locale, "grindstone.forcestop.success");
            return true;
        }

        BukkitPlayer.sendMessage(sender, locale, "grindstone.forcestop.no_game_for_target", targetName);
        return true;
    }

    private String[] getGamesMessage(Set<Game> games) {
        List<String> message = new ArrayList<>();

        String messageSeparator = "=".repeat(15);
        String gameSeparator = "-".repeat(15);

        message.add("");
        message.add(messageSeparator);

        for (Game game : games) {
            for (GameTeam<? extends GamePlayer> team : game.getTeams()) {
                String teamLine = "- " + team.getChatColor() + team.getName() + ": ";
                for (GamePlayer player : team.getMembers()) {
                    teamLine += player + ", ";
                }
                teamLine = teamLine.substring(0, teamLine.length() - 2);

                message.add(teamLine);
            }

            message.add(gameSeparator);
        }

        message.remove(message.size() - 1);
        message.add(messageSeparator);
        message.add("");

        return message.toArray(new String[0]);
    }
    
    */

}
