package io.redspace.ironslib;

import com.mojang.serialization.JsonOps;
import io.redspace.ironslib.attribute.data_handler.AttributeRemapEntry;
import io.redspace.ironslib.internal.client.gui_block_entity_renderer.PipSnapshotCheck;
import io.redspace.ironslib.soulfire.client.SoulFireStateCheck;
import io.redspace.ironslib.statue.PlayerStatuePose;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.transmog.client.CapeSnapshotCheck;
import net.minecraft.resources.Identifier;
import java.util.UUID;

public final class LibraryPortChecks {
    public static void main(String[] args) {
        PipSnapshotCheck.main(args);
        CapeSnapshotCheck.main(args);
        SoulFireStateCheck.main(args);

        var remap = new AttributeRemapEntry(Identifier.parse("irons_lib:armor_pierce"), Identifier.parse("example:armor_pierce"));
        var remapJson = AttributeRemapEntry.CODEC.encodeStart(JsonOps.INSTANCE, remap).getOrThrow();
        assert AttributeRemapEntry.CODEC.parse(JsonOps.INSTANCE, remapJson).getOrThrow().equals(remap);
        assert remapJson.getAsJsonObject().get("from").getAsString().equals("irons_lib:armor_pierce");

        var statue = new StatueData(UUID.fromString("43fa7031-dd31-4e1f-9850-78c07df1314a"), PlayerStatuePose.defaultPose(), true);
        var statueJson = StatueData.CODEC.encodeStart(JsonOps.INSTANCE, statue).getOrThrow();
        assert StatueData.CODEC.parse(JsonOps.INSTANCE, statueJson).getOrThrow().equals(statue);
        System.out.println("Attribute-remap and statue codec round trips passed");
    }
}
