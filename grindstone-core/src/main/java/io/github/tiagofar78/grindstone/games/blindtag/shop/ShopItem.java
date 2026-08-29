package io.github.tiagofar78.grindstone.games.blindtag.shop;

import java.util.function.Supplier;

import io.github.tiagofar78.grindstone.games.blindtag.items.Clone;
import io.github.tiagofar78.grindstone.games.blindtag.items.Item;
import io.github.tiagofar78.grindstone.games.blindtag.items.Trap;

public enum ShopItem {

    CLONE(Clone::new),
    TRAP(Trap::new);

    private final Supplier<? extends Item> factory;

    ShopItem(Supplier<? extends Item> factory) {
        this.factory = factory;
    }

    public Item create() {
        return factory.get();
    }
    
}
