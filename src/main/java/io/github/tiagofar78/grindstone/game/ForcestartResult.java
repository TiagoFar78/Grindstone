package io.github.tiagofar78.grindstone.game;

public enum ForcestartResult {

    SUCCESS("grindstone.forcestart.success"),
    TOO_MANY_TEAMS("grindstone.forcestart.too_many_teams"),
    TOO_FEW_TEAMS("grindstone.forcestart.too_few_teams"),
    TOO_MANY_PLAYERS("grindstone.forcestart.too_many_players"),
    TOO_FEW_PLAYERS("grindstone.forcestart.too_few_players"),
    TEAM_CAPACITY_EXCEEDED("grindstone.forcestart.team_capacity_exceeded"),
    INVALID_TEAMS_DISTRIBUTION("grindstone.forcestart.invalid_team_distribution");

    private String messageKey;

    ForcestartResult(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return this.messageKey;
    }

}
