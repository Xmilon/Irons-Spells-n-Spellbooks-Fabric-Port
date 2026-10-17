package io.redspace.ironsspellbooks.api;

/**
 * Entrypoint for mods that extend Iron's Spells 'n Spellbooks.
 * <p>
 * Declare it in the addon's fabric.mod.json:
 * <pre>
 * "entrypoints": { "irons_spellbooks:addon": [ "com.example.MyAddon" ] }
 * </pre>
 * It is invoked once, after every Iron's Spells 'n Spellbooks registry (spells, schools, items, attributes...)
 * has been created and bootstrapped, so the addon can register into them safely. Event handler classes can be
 * hooked up with {@code NeoForge.EVENT_BUS.register(MyHandlers.class)}.
 */
public interface IronsSpellbooksAddon {
    void onInitialize();
}
