package io.redspace.ironsspellbooks.render;

import com.geckolib.cache.animation.BakedAnimations;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.loading.loader.GeckoLibGsonLoader;
import com.geckolib.loading.math.MathParser;
import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Exercises GeckoLib's real CPU loaders without requiring a window or GPU. */
public final class GeckoResourcePortCheck {
    public static void main(String[] args) throws IOException {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        var loader = new GeckoLibGsonLoader();
        var parser = MathParser.createWithDeduplication();
        var models = new HashMap<Identifier, BakedGeoModel>();
        var animations = new HashMap<Identifier, BakedAnimations>();
        var failures = new ArrayList<String>();
        checkPack(Path.of("src/main/resources/assets/irons_spellbooks"), "irons_spellbooks", 41, 11,
                loader, parser, models, animations, failures);
        checkPack(Path.of("irons-lib/src/main/resources/assets/irons_lib"), "irons_lib", 20, 2,
                loader, parser, models, animations, failures);
        if (!failures.isEmpty()) throw new AssertionError(String.join("\n", failures));

        for (String id : List.of("irons_spellbooks:wizard_armor", "irons_spellbooks:wizard_armor_hat",
                "irons_spellbooks:statue/tyros_statue", "irons_lib:transmog/wizard_armor",
                "irons_lib:transmog/wizard_armor_hat", "irons_lib:statue/player")) {
            assert models.containsKey(Identifier.parse(id)) : "Missing bare model ID: " + id;
        }
        for (String id : List.of("irons_spellbooks:casting_animations", "irons_spellbooks:spectral_hammer",
                "irons_spellbooks:wizard_armor_animation", "irons_lib:statue_poses",
                "irons_lib:wizard_armor_animation")) {
            assert animations.containsKey(Identifier.parse(id)) : "Missing bare animation ID: " + id;
        }
        System.out.println("GeckoLib CPU bake passed: " + models.size() + " models, " + animations.size()
                + " animation files; unique bare cache keys and critical resource references verified");
    }

    private static void checkPack(Path root, String namespace, int expectedModels, int expectedAnimations,
                                  GeckoLibGsonLoader loader, MathParser parser,
                                  Map<Identifier, BakedGeoModel> models, Map<Identifier, BakedAnimations> animations,
                                  List<String> failures) throws IOException {
        for (String kind : List.of("models", "animations")) {
            var folder = root.resolve("geckolib").resolve(kind);
            if (!Files.isDirectory(folder)) {
                failures.add("Missing GeckoLib resource folder: " + folder);
                continue;
            }
            List<Path> files;
            try (var paths = Files.walk(folder)) {
                files = paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".json"))
                        .sorted().toList();
            }
            int expected = kind.equals("models") ? expectedModels : expectedAnimations;
            if (files.size() != expected) {
                failures.add(folder + ": expected " + expected + " JSON resources, found " + files.size());
            }
            for (var file : files) {
                var resource = Identifier.fromNamespaceAndPath(namespace, root.relativize(file).toString().replace('\\', '/'));
                // Match the loader's discovered-folder and suffix rules without initializing its global reload cache.
                var key = Identifier.fromNamespaceAndPath(namespace, folder.relativize(file).toString().replace('\\', '/')
                        .replaceFirst("((\\.geo)|((\\.animation)s?))?(\\.json)$", ""));
                try (var reader = Files.newBufferedReader(file)) {
                    var json = JsonParser.parseReader(reader).getAsJsonObject();
                    if (kind.equals("models")) {
                        var model = loader.bakeGeckoLibModelFile(resource, json);
                        if (models.putIfAbsent(key, model) != null) failures.add("Duplicate GeckoLib model ID: " + key);
                        if (model.topLevelBones().length == 0) {
                            failures.add(file + ": model baked without geometry");
                        }
                    } else {
                        var baked = loader.bakeGeckoLibAnimationsFile(resource, json, parser);
                        if (animations.putIfAbsent(key, baked) != null) failures.add("Duplicate GeckoLib animation ID: " + key);
                        if (baked.animations().size() != json.getAsJsonObject("animations").size()) {
                            failures.add(file + ": animations were lost during baking");
                        }
                    }
                } catch (RuntimeException exception) {
                    failures.add(file + ": " + exception);
                }
            }
        }
    }
}
