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
player animation integration, HUD, particles, and fog. The main mod still has
thousands of compiler errors; it has not reached a playable milestone.

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
the existing Shield rewrite. Placed portal frames use native `BlockEntityRenderer` states,
capturing connection visibility, facing, type, tint, and animation time. Portal also captures its selected type during extraction,
preserving the Undead Rift's blood portal selection. Electrocute copies its beam cache and clears captured
geometry when its owner disappears. Firebolt retains its particle-only appearance.
`verifySpellRenderSnapshots` (part of `check`, requires the full main compilation) checks
cache mutation isolation, state reuse, entity-free state fields, disconnected portal visibility,
balanced transforms in all four facings, and deferred portal tint/animation after state changes.
The standalone check initializes Minecraft's vanilla bootstrap. Focused compilation
of these renderers, Portal Frame, and Undead Rift against the 26.3 game
APIs passed, using the cached 1.21.1 mod binary only for unchanged entity type signatures.
This does not validate the current entity implementations or in-game rendering. Existing
GUI checks and the complete Iron's Lib build/checks also passed. A fresh full compiler run
on 2026-10-28 reported 3,178 errors before these renderer changes and 3,173 afterward,
with none in the changed renderers. The build remains blocked.

Open in the render phase: finish validating the GeckoLib 5 renderers described below,
remaining mob/humanoid and block-entity renderers, item render hooks, and render mixins.
Particle implementation migration is complete at the focused compilation/test level;
client/backend validation is still open. The heartstop vignette alpha-blends instead of adding until a GUI additive
pipeline exists.

## Particle milestone (2026-10-28)

All particle implementations, options, native providers, the registry, and particle collision
mixins compile together against 26.3. The focused check uses the cached 1.21.1 mod binary only
for unrelated, unchanged configuration/entity/utility signatures; it compiles the current
particle sources rather than substituting old particle implementations. The particle package
has no NeoForge integration imports or old OpenGL state mutations.

- All 33 particle types use direct vanilla registry values. IDs and limiter flags are preserved;
  all callers now use those values. Providers use Fabric's `ParticleProviderRegistry` and
  the current `RandomSource` signature. Non-sprite controllers register directly.
- Sprite particles use `SingleQuadParticle`, native layers, and current lighting. Ground
  pulses capture both faces, their original UV orientation, and their .08-block offset.
- Custom rings, fog, trails, slashes, and lightning capture primitive mesh vertices in a
  native particle group. Drawing consumes these snapshots through Minecraft's quad-particle
  renderer and its transparency pipelines. The additive lightning layer has an OIT pipeline.
  Large mesh effects are intentionally not culled by their origin, which could hide visible
  beams or ground effects; tighter mesh bounds remain a possible performance improvement.
- Falling blocks capture a fresh `BlockModelRenderState` and relative position per particle;
  the old global render queue and world-render callback are removed. Drawing reads no world
  or live particle. Particle collision tracking uses native Fabric entity/disconnect callbacks,
  and the shield-collision mixin targets the current `CollisionContext` signature.
- Short Soulfire beams now render without division by zero; zero-size shockwaves have a
  nonzero lifetime. Zap and Soulfire JSON destination codecs reject malformed coordinate
  arrays and preserve fractional positions to tenths. Zap retains three wire integers;
  Soulfire retains three wire doubles. Soulfire JSON decoding no longer scales positions tenfold.

`verifyPortParticles` is part of `check` and requires the full main compilation. Its standalone
focused run passed ground geometry/UV checks, source mutation and state reuse checks, short
beam checks, mixed mesh/native-quad submission, codec round trips and malformed inputs, and
falling-block list ownership. An isolated Fabric test mod initialized all 33 real particle
registrations and their codecs, reached dedicated-server startup, and stopped cleanly. The
temporary probe JAR was removed afterward. Dependency integrity, GUI snapshots, and Iron's
Lib build/checks also passed.

The full compiler report dropped from 3,173 to 1,750 errors with no diagnostics in the particle
package, particle registry, or particle mixins. This is not a playable build. Actual client
resource reload, particle/shield collisions, performance, and Vulkan/OpenGL appearance with
improved transparency on/off remain untested until the main mod builds.

GeckoLib 5 status: animation API, model resources, procedural bone animation (`TransformStack` over
`BoneSnapshots`), `IronsGeoEntityRenderer`, the Geo render layers, `HumanoidRenderer`, the Dead King,
Keeper and Tyros renderers, and the armor renderers are ported. Marked `unverified` in code: bone position
listeners (Tyros particles), per-bone layer poses (eyes, charge, flame), and armor async-bone conventions.
Known gaps: re-draw layers (swirl, ghost, soul) cannot tint per pass; the hard-coded mob cape layer and the
Summoned Skeleton/Zombie vanilla-fallback renderers are not ported; player render layers (`*Layer.Vanilla`)
remain open. GeckoLib armor item providers and the wizard clothing variant now use native item/state APIs.

Item/registry/save status: armor items use `ArmorMaterial`/`ArmorType` with `Properties.humanoidArmor`; weapon tiers build
a `ToolMaterial`. Items and blocks get their registry key through `ItemIdContext` (set by the `DeferredRegister` shim)
and `ItemMixin`/`BlockBehaviourMixin` (`Properties.setId`); mixin application is untested. Entity saves use
`ValueInput`/`ValueOutput`; legacy `CompoundTag` helpers are bridged by `ValueIO`, which nests their data under a key
(not compatible with 1.21 saves). `CompoundTag` getters were moved to the `*Or` variants.
Still needed: tag data for `irons_spellbooks:armor_repair/*` and `irons_spellbooks:weapon_repair`, equipment asset JSONs under
`assets/irons_spellbooks/equipment/`, `SummonManager` entity save/load, remaining `Item` signatures (`useOn`,
`releaseUsing`, `addCooldown`, `inventoryTick`), and particle runtime validation.

## Native equipment, item models and GeckoLib resources (2026-10-28)

Equipment now uses the current armor/tool components and per-material repair tags. Native
armor attributes are extended once; legacy player/default-attribute mixins and reflective
fallback registration were removed. Fabric's default-attribute callback remains the single
registration path and preserves values supplied by custom entities. Pumpkin armor uses the
vanilla gaze-disguise tag; the developer crown remains indestructible.

An isolated Fabric server probe (`EquipmentPortProbe`) passed all 15 weapon tiers' damage,
speed, durability and repair checks; armor durability and modifier checks; pumpkin disguise;
compass codec/target conversion; scroll school data; and preservation of a custom entity's
mana default. It uses current production equipment/attribute sources with fixture item IDs.
This does not validate the full main-mod registry or accessory equipment behavior. The probe
stopped cleanly and its temporary JAR was removed from the library run directory.

231 native item definitions replace old baked-model swaps, item properties and client
extensions. They cover GUI/head variants, wizard clothing, scroll/ring schools, crossbow
charge/pull stages, throwing state, compass frames and the Pyrium staff. The compass keeps
its saved catacomb coordinate and mirrors it into the native lodestone component, targeting
the Overworld without requiring a lodestone block. The staff captures animation time before
submission and uses native special-model rendering for its head and full-bright orb.
The removed vanilla spawn-egg template is preserved with a mod-owned layered template and
unchanged mask PNGs copied from the cached Minecraft 1.21.1 client; original egg colors remain.

`verifyPortItemModels` checks native codecs/dependencies, school fallbacks, and captured staff
submission. A focused run decoded 230 definitions; the wizard component selector still needs
the full component bootstrap. A separate dependency scan followed 370 models without a missing
parent/reference. `verifyPortArmorVariants` passed immutable variant mapping, addon namespaces,
unknown/default selection and clearing reused state; actual equipped-stack extraction and
rendered armor remain untested. Focused runs compile the current changed sources against 26.3,
using the cached old mod binary only for unrelated unchanged signatures. These two normal
Gradle checks depend on full main compilation, which is still blocked.

GeckoLib 5 discovers `geckolib/models` and `geckolib/animations`, with bare cache identifiers.
Moved all 41 main-mod models and 11 unique animation files into those folders and updated
callers. Geometry/animation bytes are unchanged. The redundant `spectral_hammer.json` matched
`spectral_hammer.animation.json` byte-for-byte and was removed to avoid a duplicate cache key.
PAL's `player_animations` folder is unchanged. Fixed two remaining old library animation IDs.
Wizard armor captures its clothing variant during extraction; drawing reads only that snapshot.

`verifyPortGeckoResources` runs independently of main compilation and uses GeckoLib's actual
geometry baker and animation/Molang parser. All 61 models and 13 animation files across both
projects passed, including unique cache keys and critical identifiers. Library build/checks,
GUI snapshots and bundled dependency verification also passed. These checks do not load client
textures, reload resources in-game, or validate Vulkan/OpenGL output.

The latest full compiler report is 1,503 errors (down from 1,750 after particles). The main
mod is not playable yet. Next: remaining block/entity renderers, native gameplay API changes,
startup mixins and complete registry validation. FireBoss's dedicated dagger model shortcut,
remaining live-entity render states, actual equipment/casting visuals and backend testing remain open.

## Pedestal and scroll-forge rendering (2026-10-29)

Both renderers now extract native item render states, lighting, facing and animation values.
Submission reads no live block entity, world or item stack. Tool/sword pedestal tilt uses
native item tags; the existing item scale, offsets, rotations and paper UV winding are kept.
The scroll-forge paper callback captures lighting before deferred drawing.

`verifyBlockItemSnapshots` is wired into `check` and awaits full main compilation. Focused
compilation against 26.3 and snapshot checks passed: empty/reused item states, tool tilt,
all four forge facings, paper UV/light capture, and balanced transforms. The fixture supplies
baked item layers through a test resolver; actual world extraction/resource reload and
in-game block appearance remain untested. Cached old mod types supply only unrelated tile
signatures during focused compilation, not renderer implementations.

## Acid orb, magma ball and snowball snapshots (2026-10-29)

These three renderers now extend vanilla `EntityRenderer` directly. Extraction captures
motion-derived orientation, bounding-box offset and texture selection; submission uses
those values and native animation age. No entity or world is retained in their custom state.
Existing geometry, animation rates, scales and light behavior are preserved, including the
magma core's full brightness and separately lit swirl. Shared model parts are not mutated.

`verifyOrbSnapshots` joins `check` when full compilation is available. Focused compilation
and real collector/model snapshot checks passed for animation, texture layers, light,
balanced transforms, independent submitted poses and unchanged shared geometry. Entity
implementations still need porting; focused compilation uses the old mod binary only for
those unrelated signatures. In-game extraction and appearance remain untested.

## Particle payload codecs (2026-10-29)

All 11 particle payload classes now use vanilla `Vec3.STREAM_CODEC`; its three-double wire
layout matches the removed buffer convenience methods exactly. Shockwave lookup now parses
the already namespaced particle ID, using the native registry value API. Previously it tried
to prepend `irons_spellbooks` to values such as `minecraft:flame` and swallowed the failure.
Unknown/malformed particle identifiers are ignored without hiding unrelated exceptions.

`verifyParticlePacketCodecs` is part of `check` after full main compilation. Focused checks
passed for all 11 actual payload codecs: byte-for-byte legacy layout, fractional/large/negative
coordinates, field order, payload IDs, round trips, and truncated vector rejection. Vanilla,
unknown and malformed shockwave IDs were checked. The slice uses old unrelated client helper
signatures; live packet delivery, client particle effects and the existing receiver adapter
still need complete networking/client validation.

## Native structure processors (2026-10-29)

All six custom processors implement vanilla's current interface and return their own map
codec. Registration uses the native structure-processor codec registry; the deferred type
adapter and old generic types are removed. Existing IDs and serialized configuration fields
are preserved. Copper weathering now overrides the actual vanilla callback and uses the
position-seeded structure RNG. Vault cleanup copies NBT before clearing its two runtime keys,
preserving unrelated data without changing the shared template. Foundation placement uses
native chunk positions, inclusive maximum Y and chunk update flags; existing lower-limit and
neighbor-chunk guards remain. Stair-to-slab lookup uses a native optional registry value.

Ten processor-list files now use native block-state `id`/`properties` fields. The actual
block-state codec decoded 62 outputs. One `wisewood_bookshelf` output requires the main mod's
block registry and remains explicitly untested; complete processor lists and structures still
need a full datapack load. Structure NBT/save migration is not claimed by this JSON change.

`verifyStructureProcessors` joins `check` once main compilation succeeds. Focused checks pass
six codec round trips, malformed copper fields, copied vault data, deterministic copper aging,
per-block light overrides and foundation bounds. An isolated Fabric server loaded all six
real registrations, round-tripped their native dispatch codecs, checked stair/slab behavior
against loaded vanilla tags and stopped cleanly. It used the existing library test world;
this is not a fresh-world/main-mod worldgen test. Its temporary probe JAR was removed. Actual
foundation writes, portal-frame cleanup and complete structure placement remain untested.

## Remaining-error assessment and entity tags (2026-10-29)

The latest full compile reports 1,399 errors, down from 1,477 before the packet/processor
milestones. There are no compiler diagnostics in the changed particle payload or structure
processor implementations. Sixteen entity-type tag queries across twelve files now use
`Entity.typeHolder().is(tag)`, preserving existing tag IDs and predicates. These affect ally
selection, healing, parrying, portal filtering, blood collection, rooting and ricochets.
No new compatibility wrapper was introduced. Full compilation type-checks these calls;
in-game behavior is still blocked by the wider migration.

Next shared causes, ranked by impact:

1. Client renderers/mixins still reference removed immediate drawing APIs and live-entity states.
2. Mob targeting now requires server-aware selectors; combat/damage overrides and several
   entity fields have changed. Preserve server validation rather than replacing boolean damage
   checks with unconditional success.
3. Loot functions/registries now expose codecs directly, while old global loot integration
   needs a native Fabric replacement. Recipe interfaces also need a coordinated migration.
4. Block neighbor updates, interactions and block-entity lifecycle hooks have changed.
5. Entity save/load, teleport transitions and startup registry/mixin validation remain open.

Compiler counts describe migration work, not a percentage of playable feature completion.
The main mod still does not build; Vulkan/OpenGL, multiplayer, actual equipment and spell
behavior, and a complete fresh-world datapack/structure load remain unvalidated.

## Server AI and native targeting goals (2026-10-29)

All twelve custom server AI hooks now receive/pass the native `ServerLevel` argument. Nearest
attack selectors use the current two-argument selector without changing their hostility
predicates. Priest/alchemist combat checks pass the server context explicitly. Neutral-mob
anger persistence and damage overrides still require their own coordinated migration.

The support goal now extends native `TargetGoal` directly. It keeps non-combat conditions,
search cadence, follow-distance limits and support/combat separation without assigning the
now-final nearest-attack conditions. Stop clears its own support state while preserving a
combat target acquired by another goal. Nearby-target and village-defence queries use the
server API. Village defence clears its previous candidate before each search, preventing a
stale hostile target from surviving when witnesses disappear. Revenge uses the native universal
anger game rule and retains attacker exclusions.

`compilePortTargeting` independently compiles the four shared goals, support interface and
real-server probe against 26.3 without the old mod binary. The isolated Fabric probe passed
nearest injured support selection, selector server context, recovery checks, stop/combat
isolation, stale village target cleanup and revenge exclusions. Fixture entities were discarded,
the existing library world saved cleanly, and the temporary probe JAR was removed. The concrete
wizard/spider/summon implementations are not playable yet; their broader AI behavior remains
untested. The latest full compile reports 1,368 errors, with no diagnostics in those four goals,
server AI hook signatures or nearest-attack selector calls.

## Entity synchronization, timers and built-in types (2026-10-29)

Moved 21 built-in entity references to native `EntityTypes` fields. Lightning factories pass
an explicit triggered-spawn reason. Existing mod registry IDs and cast-data formats remain
unchanged. Old entity `hurtMarked` writes now use `syncVelocity`; impulse writes use `needsSync`,
matching the current engine's `markHurt` and `push` implementations. The serializable
`ImpulseCastData.hasImpulse` field is preserved.

Nonliving invulnerability accesses use native getters/setters with the same durations. Ender Chain's
manual decrement is removed: 26.3 already decrements this timer in `Entity.commonTick`, so
retaining both would halve its immunity window. The actual server probe confirmed that a
native ten-tick timer becomes nine after one common tick and that `push` requests native
synchronization. Targeting regressions still passed and the temporary probe was removed.
Ender Chain's complete damage/multipart logic and affected spells remain untested in-game;
this native timer proof does not establish full spell/entity compatibility.

Correction from the subsequent damage tests: living entities use `damageCooldownTime` for
damage i-frames, distinct from `Entity` general invulnerability. Dash, strike, echo, dagger,
scapegoat, frozen-humanoid and boss melee writes now use that living timer. Mixed targets retain
the general timer only for nonliving entities. The earlier common-tick test proved only the
general timer, not living repeated-hit behavior.

The full compile is down from 1,399 to 1,313 errors across this pass. No removed motion-field
or private invulnerability-field access remains in the changed sources. Next combat work:
native neutral-mob anger references/end times and coordinated `hurtServer` overrides, preserving
conditional effects and server-side damage results. Rendering, loot/recipes, teleport/save
APIs and startup validation remain substantial outstanding groups.

## Native wizard anger state (2026-10-29)

Neutral wizards and cursed armor stands implement current `NeutralMob` end-time and
`EntityReference<LivingEntity>` contracts. Timers use native game time rather than a second
manual countdown. Both readers retain the old `AngryAt` UUID fallback; vanilla's reader
already accepts `AngerTime`. These narrow readers do not establish complete old-world migration.
Wizard hostility, cultist overrides, healing and taunting pass the native server context.
When loading, per-player levels are restored before anger references; a saved player target
below that wizard's hostility threshold is cleared rather than being promoted into combat
by vanilla's default reader.

`WizardAngerData` owns the real per-player state and writes the same `angerLevels` list of
`player` UUID / `anger` integer compounds through native `ValueInput`/`ValueOutput`. Reload
clears stale rows, malformed UUID/nonpositive entries are skipped, and values cap at ten
without integer overflow. Decay keeps the original strict greater-than-400-tick interval.
The deprecated bulk-increase overload now honors its amount and cap instead of always adding
one; nonpositive increases no longer create permanently negative anger entries.

`verifyWizardAngerData` runs independently of the incomplete main compilation and passed
production data checks for increments/caps, overflow, decay boundaries, legacy save shape,
round trips, reused-state clearing, malformed-row isolation and forgiveness. The actual
wizard/armor-stand entity implementations remain blocked by other migration work, so their
spawn/reload behavior and guarded-block event callbacks still require runtime validation.

## Native server damage dispatch (2026-10-29)

All reviewed two-argument entity damage overrides now implement `hurtServer(ServerLevel,
DamageSource, float)`. Super calls and multipart forwarding preserve accepted/rejected results,
including summon immunity, boss reductions/parries, cursed-stand wake-up, tomb/chain health,
scapegoat hit effects and frozen-entity shattering. The spider's part-aware overload is now
`hurtPart` and forwards its server context. Non-damageable visuals/controllers explicitly
return false, preserving their previous inherited `Entity.hurt` behavior; shield damage still
runs through its existing part/takeDamage boundary.

The shared damage dispatcher rejects client execution and returns native server damage results.
Native damage-type lookup and knockback timing use current registry/server APIs. Friendly-fire
rejection happens before marking a no-knockback spell hit. Earthquake, fire eruption, fire field
and thrown-spear conditional hit effects run only after accepted native server damage.
This is not a complete damage-event integration: legacy pre/post-hit callbacks, knockback
cancellation, other effect listeners and their mixins still require a coordinated native
Fabric rewrite. Their lifesteal/freeze/fire behavior is not claimed as working by this milestone.

The expanded isolated server probe passed native anger reference/end-time save round trips,
restored hostility, and actual accepted/rejected vanilla damage. It waits for a ticking loaded
fixture chunk before querying mobs, discards fixtures and restores its temporary forced-chunk
change. Those SDK contract checks do not instantiate the unfinished custom mobs. Custom damage,
multipart collision, AoE effects, multiplayer and old-world migration remain untested in-game.
The probe JAR was removed after shutdown. Anger data checks and targeting compilation also pass.
The full main compile reports 1,195 errors, down from 1,313 before this pass; no diagnostics
remain in the changed anger data, shared damage dispatcher or native damage-hook signatures.

## Native living damage pipeline (2026-10-29)

Replaced the legacy damage/healing/item-completion event objects with native Fabric callbacks
and current server mixin hooks. Removed five unused NeoForge compatibility event classes.
Incoming cancellation/modifiers reuse vanilla's immunity query; health-stage modifiers run
after armor and absorption. Accepted-hit callbacks restore custom damage cooldowns. Migrated
summon immunity/power, casting interruption, evasion, fire ward, rings, blight, oakskin, spider
aspect, invisibility removal, heartstop, immolate, echo, soul burn and gluttony registrations.
The custom spell-damage addon event now exposes a Fabric event; addons require recompilation.

Spell hit effects use actual health lost for lifesteal, including lethal hits, avoiding healing
from absorption or overkill. Fire/freezing still apply on accepted fully absorbed hits. Native
26.3 ignition clears freezing; the tests preserve that engine behavior.

`damagePipelineProbeJar` builds a standalone server test mod from the production callback API,
mixin and spell-hit helper, without an old mod binary. It passed single dispatch, unchanged
vanilla immunity-query counts, cancellation, modifiers, absorption, heartstop's health stage,
fire/freezing, lethal-hit lifesteal, knockback cancellation, healing, item snapshots, invalid
numbers and repeated-hit cooldown checks on the library server. The server saved/shut down and
the temporary probe was removed. `compilePortDamagePipeline` is also part of `check`.

The concrete full-mod ring/summon/casting policies and client visuals still require runtime
validation. Other legacy events remain pending migration. The latest main compilation reports
1,190 errors; this milestone does not make the complete mod playable.

## Native effect ticks and lifecycle (2026-10-29)

Migrated all remaining effect tick overrides to native `applyEffectTick(ServerLevel, ... )`:
airborne, ascension, burning dash, chilled, fall immunity, immolate, instant mana, thunderstorm
and volt strike. Removed redundant base-effect overrides so vanilla owns its default lifecycle.
Instant Mana implements the correctly named `isInstantaneous`/`applyInstantaneousEffect`, and
its potion/fluid callers use the same native names. Mana sync and immolate burst packets use
Fabric networking directly. Airborne, immolate and Heartstop damage use explicit server damage.
Chilled supplies native riding arguments and discards its tomb if mounting fails.

Heartstop heartbeat and Planar Sight particles use the existing client effect tick interface,
called directly from the Fabric client callback. Heartbeat cadence reads each effect instance's
duration rather than mutable singleton state. Immolate also reads its active instance duration;
its complete chain-reaction behavior remains untested.

Extracted the production effect lifecycle hooks from the large living-entity mixin. The native
`onEffectsRemoved(Collection)` callback cleans up every removed effect and sends removal packets
to tracking players. Add/update sync uses the native tracking-player packet API. Poison's mixin
receives `ServerLevel`; invisibility follows the renamed native visibility-update method.
This does not establish that all other injections in the old living-entity mixin are valid.

`effectTickProbeJar` builds an isolated test mod using the actual production effect base,
Airborne, fall immunity and lifecycle mixin, with no old main-mod binary. The 26.3 server passed
native tick dispatch, fall-distance reset/expiry, airborne no-collision/slow-collision cases,
impact damage and effect termination, expiry cleanup, updated amplifier preservation, explicit
removal, duplicate-removal rejection and cleanup of multiple effects in one batch. The fixture
was discarded and the server saved/shut down. Temporary probe JARs are removed after validation.
`compilePortEffectTicks` participates in `check` and can run independently of main compilation.

The main compile reports 1,176 errors (previously 1,190), with no diagnostics in these migrated
tick implementations or the new lifecycle mixin. Evasion teleport APIs and Guiding Bolt save
data remain separate effect-related blockers. Full Heartstop/Immolate/Instant Mana gameplay,
tracking-player packet delivery, client sounds/particles, Vulkan/OpenGL and multiplayer are
not runtime-validated by this isolated probe. Main-mod startup is still blocked.
