package io.redspace.ironsspellbooks.api.util;

import com.geckolib.animation.object.LoopType;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.resources.Identifier;
import com.geckolib.cache.animation.Animation;
import com.geckolib.animation.RawAnimation;

import java.util.Optional;

public class AnimationHolder {
    private final RawAnimation geckoAnimation;
    private final Identifier playerAnimation;
    public final boolean isPass;
    public final boolean animatesLegs;

    /**
     * Use resource-location sensitive {@link AnimationHolder#AnimationHolder(Identifier, boolean, boolean)}
     */
    @Deprecated(forRemoval = true)
    public AnimationHolder(String path, boolean playOnce, boolean animatesLegs) {
        this(path.contains(":") ? Identifier.parse(path) : IronsSpellbooks.id(path), playOnce, animatesLegs);
    }

    public AnimationHolder(Identifier animation, boolean playOnce, boolean animatesLegs) {
        this.playerAnimation = animation;
        this.geckoAnimation = RawAnimation.begin().then(playerAnimation.getPath(), playOnce ? LoopType.PLAY_ONCE : LoopType.HOLD_ON_LAST_FRAME);
        this.isPass = false;
        this.animatesLegs = animatesLegs;
    }
    /**
     * Use resource-location sensitive {@link AnimationHolder#AnimationHolder(Identifier, boolean, boolean)}
     */
    @Deprecated(forRemoval = true)
    public AnimationHolder(String path, boolean playOnce) {
        this(path, playOnce, false);
    }

    public AnimationHolder(Identifier path, boolean playOnce) {
        this(path, playOnce, false);
    }

    private AnimationHolder(boolean isPass) {
        this.playerAnimation = null;
        this.geckoAnimation = null;
        this.isPass = isPass;
        this.animatesLegs = false;
    }

    private static final AnimationHolder empty = new AnimationHolder(false);
    private static final AnimationHolder pass = new AnimationHolder(true);

    /**
     * Represents an empty animation, making the player immediately stop animating at the end of a cast
     */
    public static AnimationHolder none() {
        return empty;
    }

    /**
     * Represents the lack of an animation, letting the previous animation (the cast start animation) continue to play after the spell ends, so long as the spell wasn't cancelled
     */
    public static AnimationHolder pass() {
        return pass;
    }

    public Optional<RawAnimation> getForMob() {
        return geckoAnimation == null ? Optional.empty() : Optional.of(geckoAnimation);
    }

    public Optional<Identifier> getForPlayer() {
        return playerAnimation == null ? Optional.empty() : Optional.of(playerAnimation);

    }
}
