package io.github.tiagofar78.grindstone.adapters;

public interface MinigamePlayerAdapter {

    public default void sendTitleMessage(String titleMessage, String subtitleMessage) {
        double defaultFadeIn = 1;
        double defaultStay = 3.5;
        double defaultFadeOut = 1;
        sendTitleMessage(titleMessage, subtitleMessage, defaultFadeIn, defaultStay, defaultFadeOut);
    }

    public void sendTitleMessage(
            String titleMessage,
            String subtitleMessage,
            double fadeIn,
            double stay,
            double fadeOut
    );

}
