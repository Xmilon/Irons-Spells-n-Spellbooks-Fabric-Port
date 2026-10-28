package io.redspace.ironsspellbooks.setup;

import io.redspace.ironsspellbooks.api.util.FogManager;
import io.redspace.ironsspellbooks.api.util.MusicManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

/**
 * Client counterpart of {@link FabricEventBridge}. ClientPlayerEvents and ClientInputEvents are
 * called directly from IronsSpellbooksClient and the client mixins, so they are not registered here.
 */
public final class ClientEventBridge {
    private ClientEventBridge() {
    }

    public static void init() {
        for (Class<?> handlers : new Class<?>[]{FogManager.class, MusicManager.class}) {
            NeoForge.EVENT_BUS.register(handlers);
        }
        ClientTickEvents.START_CLIENT_TICK.register(client -> NeoForge.EVENT_BUS.post(new ClientTickEvent.Pre()));
        ClientTickEvents.END_CLIENT_TICK.register(client -> NeoForge.EVENT_BUS.post(new ClientTickEvent.Post()));
        ClientEntityEvents.ENTITY_LOAD.register((entity, level) -> NeoForge.EVENT_BUS.post(new EntityJoinLevelEvent(entity, level)));
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, level) -> NeoForge.EVENT_BUS.post(new EntityLeaveLevelEvent(entity)));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (client.player != null) {
                NeoForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggingOut(client.player));
            }
        });
    }
}
