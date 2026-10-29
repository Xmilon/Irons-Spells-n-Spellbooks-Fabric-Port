package io.redspace.ironsspellbooks.render;

import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.JsonOps;
import io.redspace.ironsspellbooks.item.weapons.pyrium_staff.PyriumStaffHeadModel;
import io.redspace.ironsspellbooks.item.weapons.pyrium_staff.PyriumStaffOrbModel;
import io.redspace.ironsspellbooks.item.weapons.pyrium_staff.PyriumStaffRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class ItemModelPortCheck {
    public static void main(String[] args) throws Exception {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        ItemModels.bootstrap();
        net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties.bootstrap();
        net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties.bootstrap();
        net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties.bootstrap();
        net.minecraft.client.color.item.ItemTintSources.bootstrap();
        net.minecraft.client.renderer.special.SpecialModelRenderers.bootstrap();
        ItemModelRegistration.register();
        checkDefinitions();
        checkEmptySchoolFallback();
        checkStaff();
        System.out.println("Native item definition codecs/dependencies, empty school fallback, and captured staff rendering passed");
    }

    private static void checkDefinitions() throws Exception {
        Path assets = Path.of("src/main/resources/assets/irons_spellbooks");
        int count = 0;
        try (var paths = Files.list(assets.resolve("items"))) {
            for (var path : paths.filter(p -> p.toString().endsWith(".json")).toList()) {
                var json = JsonParser.parseString(Files.readString(path)).getAsJsonObject().get("model");
                if (path.getFileName().toString().equals("wizard_helmet.json")) {
                    // The full mod must register its clothing component before this native property can decode.
                    assert json.getAsJsonObject().get("component").getAsString().equals("irons_spellbooks:clothing_variant");
                    assert json.getAsJsonObject().getAsJsonArray("cases").get(0).getAsJsonObject().get("when").getAsString().equals("hat");
                    continue;
                }
                ItemModel.Unbaked definition = ItemModels.CODEC.parse(JsonOps.INSTANCE, json)
                        .getOrThrow(error -> new AssertionError(path + ": " + error));
                var dependencies = new HashSet<Identifier>();
                if (definition instanceof SchoolItemModel.Unbaked school) {
                    school.fallback().resolveDependencies(dependencies::add);
                } else {
                    definition.resolveDependencies(dependencies::add);
                }
                for (var dependency : dependencies) {
                    if (dependency.getNamespace().equals("irons_spellbooks")) {
                        assert Files.exists(assets.resolve("models/" + dependency.getPath() + ".json"))
                                : "Missing item geometry " + dependency + " in " + path;
                    }
                }
                count++;
            }
        }
        assert count >= 200 : "Item definitions were not generated";
        var book = JsonParser.parseString(Files.readString(assets.resolve("items/iron_spell_book.json"))).getAsJsonObject().getAsJsonObject("model");
        assert book.get("property").getAsString().equals("minecraft:display_context");
        assert book.getAsJsonArray("cases").size() == 2 : "Book lost GUI/head variants";
        var staff = JsonParser.parseString(Files.readString(assets.resolve("items/pyrium_staff.json"))).getAsJsonObject().getAsJsonObject("model");
        assert staff.getAsJsonArray("models").size() == 2 : "Staff lost haft or animated head";
        var compass = JsonParser.parseString(Files.readString(assets.resolve("items/wayward_compass.json"))).getAsJsonObject().getAsJsonObject("model");
        assert compass.get("target").getAsString().equals("lodestone");
        assert compass.getAsJsonArray("entries").size() == 33 : "Compass frames changed";
        System.out.println("Decoded " + count + " native item definitions; clothing selector requires full component bootstrap");
    }

    private static void checkEmptySchoolFallback() {
        for (var kind : SchoolItemModel.Kind.values()) {
            var calls = new AtomicInteger();
            ItemModel fallback = (state, item, resolver, context, level, owner, seed) -> calls.incrementAndGet();
            var children = new HashMap<Identifier, ItemModel>();
            children.put(Identifier.parse("addon:school"), fallback);
            var model = new SchoolItemModel(kind, children, fallback);
            children.clear();
            assert model.schools().size() == 1 : "Baked selection retained mutable children";
            model.update(new ItemStackRenderState(), ItemStack.EMPTY, null, ItemDisplayContext.GUI, null, null, 0);
            assert calls.get() == 1 : "Empty item did not fall back safely";
        }
        assert SchoolItemModel.Kind.SCROLL.modelLocation(Identifier.parse("addon:school"))
                .equals(Identifier.parse("addon:item/scroll_school")) : "Addon namespace lost";
    }

    private record Draw(Matrix4f pose, RenderType type, int light) { }

    private static void checkStaff() {
        var head = new PyriumStaffHeadModel(PyriumStaffHeadModel.createBodyLayer().bakeRoot());
        var orb = new PyriumStaffOrbModel(PyriumStaffOrbModel.createBodyLayer().bakeRoot());
        var renderer = new PyriumStaffRenderer(head, orb);
        var draws = new ArrayList<Draw>();
        SubmitNodeCollector collector = (SubmitNodeCollector) Proxy.newProxyInstance(SubmitNodeCollector.class.getClassLoader(),
                new Class<?>[]{SubmitNodeCollector.class}, (proxy, method, args) -> {
                    if (method.getName().equals("submitModel")) {
                        draws.add(new Draw(new Matrix4f(((PoseStack) args[2]).last().pose()), (RenderType) args[3], (int) args[4]));
                    }
                    return null;
                });
        var poses = new PoseStack();
        renderer.submit(new PyriumStaffRenderer.AnimationState(0), poses, collector, 123, 0, false, 0);
        assert poses.last().pose().equals(new Matrix4f()) : "Staff leaked its transform";
        renderer.submit(new PyriumStaffRenderer.AnimationState(20), poses, collector, 123, 0, true, 0);
        assert draws.size() == 4 : "Staff head/orb submission changed";
        assert draws.get(0).pose.equals(draws.get(2).pose) : "Animation moved the haft attachment";
        assert !draws.get(1).pose.equals(draws.get(3).pose) : "Orb animation was lost";
        assert draws.get(0).light == 123 && draws.get(1).light == LightCoordsUtil.FULL_BRIGHT;
        assert draws.get(0).type != draws.get(2).type : "Enchanted staff lost its glint material";
        var extents = new ArrayList<org.joml.Vector3f>();
        renderer.getExtents(point -> extents.add(new org.joml.Vector3f(point)));
        assert !extents.isEmpty() && extents.stream().allMatch(p -> Float.isFinite(p.x) && Float.isFinite(p.y) && Float.isFinite(p.z));
    }
}
