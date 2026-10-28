# Fabric 26.3 port

This branch is work in progress. The full mod does not compile or run yet. The
1.21 branch remains the working baseline. Fresh worlds are the initial target.

## Implementation direction

Rewrite loader integration with native Fabric APIs and narrow Minecraft mixins.
Do not add replacement NeoForge classes, no-op events, or compatibility wrappers.
Preserve gameplay calculations, registry IDs, saved data, and the addon entrypoint.
Existing compatibility classes will be removed as their callers are rewritten.

Iron's Lib is a source subproject with its own `irons_lib` mod ID. Its source was
recovered with Vineflower 1.12.0 from the supplied 26.1.2 JAR; this recovered the
statue renderers, menus, records, and other classes missing from the supplied
JD-Core ZIP. Recovered code still requires native Fabric and 26.3 adaptation.

## Toolchain and dependencies

Use Java 25 for both Gradle and the game. The build pins Gradle 9.6.1, Loom
1.17.21, Loader 0.19.5, Fabric API 0.162.0+26.3, GeckoLib 5.5.7 (Modrinth version
`kSxHvs99`), and Player Animation Library 1.2.8+mc.26.3 (`xJ30QmpE`, beta).

Accessories and owo-lib are packaged as nested, unrelocated mods, preserving
their identities and licenses. They are dependencies, not copied source projects.
Future official builds must satisfy the dependency ranges and pass integration
tests before replacing them. No automatic compatibility with future APIs is promised.

| Artifact | Source revision | SHA-256 |
| --- | --- | --- |
| [Accessories 1.4.3-beta, Fabric 26.3](https://github.com/roggy666/accessories-26.3/releases/tag/Fabric-26.3) | `90c6095347ac78374aefa89a84f23cb00e5bf092` | `3bc31f765f1d145c36fdf2780d654efc17eee15e2129a31a8754c4c8d3c83234` |
| [owo-lib 0.13.1+26.3](https://github.com/roggy666/owo-lib-26.3/releases/tag/Fabric-26.3) | `04cade0038d146349617b6ffc803a5e77410c14d` | `879e155e9bdde306e9c8a6e4120abf52ccc05d22743a4246348ea87b7a78ba53` |
| Supplied Iron's Lib 26.1.2-2.3.0 binary | User-supplied original JAR | `d9e7827800cca96ab6a3a3c829d25f22dd857ddd0d7c5a6d14936afcd792fc31` |
| Supplied Iron's Lib 1.21.1-2.3.0 sources | Comparison reference | `6b62f1ec135496cb037d4223411c061da7938edec143eea37029f99b6603645d` |

owo-lib includes Endec, Gson/Jankson/Netty adapters, Jankson, and KDL4J internally.
The old 1.21.1 loose helper JARs are no longer included in the development classpath.

## Checks and remaining work

`gradlew verifyBundledDependencies verifyPortRendering processResources :irons-lib:build`
checks pinned artifact integrity, nested helper presence, compilation of the native
rendering helpers, deferred GUI/cape/soul-fire state ownership, clipping, bounds,
statue/remap codec round trips, resource processing, and the complete library build.
These focused checks do not imply the full mod builds.

Library milestone verified on a dedicated 26.3 development server: fresh-world
startup, statue and transmog-table placement, custom attributes on a pig,
guaranteed dodge cancelling damage without reducing health, save/stop/restart,
persisted attribute values and both blocks, and datapack reload. A second run
included compatible external copies of Accessories and owo-lib; each mod ID
loaded once. This tests library behavior, not the Spellbooks mod or its mana.

Remaining: validate the library client visually; replace main-mod compatibility
classes and removed game APIs; migrate the remaining renderers, item models,
player animation integration, HUD, particles, and fog. The main mod currently
reaches the compiler's 2,000-error limit; it has not reached a playable milestone.

Before calling the port playable, build both projects and test dedicated server
startup, multiplayer/reconnect, resource reload, equipment modifiers exactly once,
mana save/sync, statues/transmog, and Firebolt/Icicle/Electrocute/Shield/Portal on
Vulkan and OpenGL with improved transparency enabled and disabled.

The dark portal pipeline preserves its original destination-color blend factors.
Correct ordering with improved transparency needs a custom accumulation shader;
the current classic blend path is an explicitly documented intermediate limit.
Client rendering, multiplayer, equipment bonuses, hostile statue-packet cases,
and old-save migration remain unchecked. The community dependencies log nonfatal
empty-refmap/client-mixin warnings on a dedicated server; keep these attributable
to their original artifacts rather than silently modifying the pinned JARs.

## Main-mod migration progress

Done: HUD overlays (Fabric `HudElementRegistry`, `GuiGraphicsExtractor`), container screens,
boss-bar customization (`BossHealthOverlayMixin` + `BossbarManager`), and initial submission
API changes in the non-GeckoLib spell-entity renderers. Many still extend
`render/EntityBackedRenderer`, which keeps a live entity in the render state. These need
native extraction rewrites to satisfy the rendering milestone; staying on the client thread
does not guarantee that deferred drawing sees the extracted values. Shared `ModelPart`s must not be mutated per entity:
submission is deferred, so offset through the `PoseStack` or pose inside `submitCustomGeometry`.

Firebolt, Icicle, Electrocute, and Portal now use native `EntityRenderer` states, alongside
the existing Shield rewrite. Portal also captures its selected type during extraction,
preserving the Undead Rift's blood portal selection. Electrocute copies its beam cache and clears captured
geometry when its owner disappears. Firebolt retains its particle-only appearance.
`verifySpellRenderSnapshots` (part of `check`, requires the full main compilation) checks
cache mutation isolation, state reuse, and entity-free state fields. Focused compilation
of these renderers and Undead Rift against the 26.3 game
APIs passed, using the cached 1.21.1 mod binary only for unchanged entity type signatures.
This does not validate the current entity implementations or in-game rendering. Existing
GUI checks and the complete Iron's Lib build/checks also passed. A fresh full compiler run
on 2026-10-28 reported 3,178 errors before this renderer change; the build remains blocked.

Open in the render phase: every GeckoLib 5 renderer, model and layer (`GeoEntityRenderer` is now
`<T, R extends EntityRenderState>` with `RenderPassInfo`), mob/humanoid renderers, block-entity
renderers, particles (`MultiBufferSource`/`LightTexture` users), item render hooks and the
render mixins. The heartstop vignette alpha-blends instead of adding until a GUI additive
pipeline exists.

GeckoLib 5 status: animation API, model resources, procedural bone animation (`TransformStack` over
`BoneSnapshots`), `IronsGeoEntityRenderer`, the Geo render layers, `HumanoidRenderer`, the Dead King,
Keeper and Tyros renderers, and the armor renderers are ported. Marked `unverified` in code: bone position
listeners (Tyros particles), per-bone layer poses (eyes, charge, flame), and armor async-bone conventions.
Known gaps: re-draw layers (swirl, ghost, soul) cannot tint per pass; the hard-coded mob cape layer and the
Summoned Skeleton/Zombie vanilla-fallback renderers are not ported; player render layers (`*Layer.Vanilla`)
and the GeckoLib armor item registration (`ExtendedArmorItem`) wait for the item/API phase.

Item/registry/save status: armor items use `ArmorMaterial`/`ArmorType` with `Properties.humanoidArmor`; weapon tiers build
a `ToolMaterial`. Items and blocks get their registry key through `ItemIdContext` (set by the `DeferredRegister` shim)
and `ItemMixin`/`BlockBehaviourMixin` (`Properties.setId`); mixin application is untested. Entity saves use
`ValueInput`/`ValueOutput`; legacy `CompoundTag` helpers are bridged by `ValueIO`, which nests their data under a key
(not compatible with 1.21 saves). `CompoundTag` getters were moved to the `*Or` variants.
Still needed: tag data for `irons_spellbooks:armor_repair/*` and `irons_spellbooks:weapon_repair`, equipment asset JSONs under
`assets/irons_spellbooks/equipment/`, `SummonManager` entity save/load, remaining `Item` signatures (`useOn`,
`releaseUsing`, `addCooldown`, `inventoryTick`), and particles.
