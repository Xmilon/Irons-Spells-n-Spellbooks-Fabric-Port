package io.redspace.ironslib.transmog.client;
import io.redspace.ironslib.internal.mixin.HumanoidRenderStateMixin;
public class CapeSnapshotCheck {
    public static void main(String[] args) {
        var entityCape = new CapeData();
        entityCape.capeLean = 12.5;
        entityCape.capeFlap = -3;
        entityCape.capeLean2 = 4.25;
        var state = new HumanoidRenderStateMixin();
        state.irons_lib$setCapeData(entityCape);
        var queuedCape = state.irons_lib$getCapeData();
        assert queuedCape != entityCape;
        entityCape.capeLean = 99;
        entityCape.capeFlap = 88;
        entityCape.capeLean2 = 77;
        assert queuedCape.capeLean == 12.5 && queuedCape.capeFlap == -3 && queuedCape.capeLean2 == 4.25;
        state.irons_lib$setCapeData(entityCape);
        assert state.irons_lib$getCapeData() != queuedCape;
        assert queuedCape.capeLean == 12.5;
        assert state.irons_lib$getCapeData().capeLean == 99;
        System.out.println("Cape state ownership and source-mutation isolation checks passed");
    }
}