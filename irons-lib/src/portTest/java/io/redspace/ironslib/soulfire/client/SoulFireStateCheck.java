package io.redspace.ironslib.soulfire.client;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
public class SoulFireStateCheck {
    static class CapturedState extends EntityRenderState {
        Boolean value;
        @Override public <T> T getData(RenderStateDataKey<T> key) { return (T) value; }
        @Override public <T> void setData(RenderStateDataKey<T> key, T value) { this.value = (Boolean)value; }
    }
    public static void main(String[] args) {
        var state = new CapturedState();
        assert !SoulFireClientHandler.isSoulFire(state);
        state.value = true;
        assert SoulFireClientHandler.isSoulFire(state);
        state.displayFireAnimation = false;
        SoulFireClientHandler.captureRenderState(null, state);
        assert !SoulFireClientHandler.isSoulFire(state) : "Reused state retained soul-fire flag";
        System.out.println("Soul-fire state default, draw lookup and stale-flag clearing checks passed");
    }
}