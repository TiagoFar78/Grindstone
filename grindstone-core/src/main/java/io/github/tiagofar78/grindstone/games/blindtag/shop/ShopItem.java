package io.github.tiagofar78.grindstone.games.blindtag.shop;

import io.github.tiagofar78.grindstone.games.blindtag.items.Clone;
import io.github.tiagofar78.grindstone.games.blindtag.items.Compass;
import io.github.tiagofar78.grindstone.games.blindtag.items.HawkEye;
import io.github.tiagofar78.grindstone.games.blindtag.items.Horse;
import io.github.tiagofar78.grindstone.games.blindtag.items.Item;
import io.github.tiagofar78.grindstone.games.blindtag.items.Needy;
import io.github.tiagofar78.grindstone.games.blindtag.items.SixthSense;
import io.github.tiagofar78.grindstone.games.blindtag.items.Trap;
import io.github.tiagofar78.grindstone.games.blindtag.items.Witch;

import java.util.function.Supplier;

public enum ShopItem {

    COMPASS(Compass::new),
    SIXTH_SENSE(SixthSense::new),
    HAWK_EYE(HawkEye::new),
    CLONE(Clone::new),
    WITCH(Witch::new),
    NEEDY(Needy::new),
    HORSE(Horse::new),
    TRAP(Trap::new);

    private final Supplier<? extends Item> factory;

    ShopItem(Supplier<? extends Item> factory) {
        this.factory = factory;
    }

    public Item buy() {
        return factory.get();
    }
}
