package io.github.tiagofar78.grindstone.game;

public enum ForcestartResult {

    SUCCESS("forcestart.success"),
    TOO_MANY_TEAMS("forcestart.too_many_teams"),
    TOO_FEW_TEAMS("forcestart.too_few_teams"),
    TOO_MANY_PLAYERS("forcestart.too_many_players"),
    TOO_FEW_PLAYERS("forcestart.too_few_players"),
    TEAM_CAPACITY_EXCEEDED("forcestart.team_capacity_exceeded"),
    INVALID_TEAMS_DISTRIBUTION("forcestart.invalid_team_distribution");

    private String messageKey;

    ForcestartResult(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return this.messageKey;
    }

}
