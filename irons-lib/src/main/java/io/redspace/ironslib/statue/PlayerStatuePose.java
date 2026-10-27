package io.redspace.ironslib.statue;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum PlayerStatuePose implements StringRepresentable {
   NEUTRAL,
   ATTENTION,
   SQUAT,
   REACHING,
   POINTING_FORWARD,
   FALLEN,
   STATUE,
   MOURN,
   DRAMATIC,
   WAVING,
   MARIONETTE,
   DAVID,
   SITTING,
   APPLAUSE,
   ARCHERY,
   HEART,
   BALLET,
   ZOMBIE,
   WALKING,
   KNEELING,
   READY_TO_STRIKE,
   EXPLORING,
   AT_GUARD;

   public static final Codec<PlayerStatuePose> CODEC = StringRepresentable.fromEnum(PlayerStatuePose::values);
   private static final Map<String, PlayerStatuePose> BY_NAME = Arrays.stream(values())
      .collect(Collectors.toMap(PlayerStatuePose::getSerializedName, Function.identity()));
   private static final Map<PlayerStatuePose, String> DESC_ID = new HashMap<>();

   public static PlayerStatuePose defaultPose() {
      return NEUTRAL;
   }

   @NotNull
   public static PlayerStatuePose fromString(String pose) {
      return BY_NAME.getOrDefault(pose, defaultPose());
   }

   public String descriptionId() {
      return DESC_ID.computeIfAbsent(this, pose -> String.format("block.irons_lib.player_statue.pose.%s", this.getSerializedName()));
   }

   @NotNull
   public String getSerializedName() {
      return this.name().toLowerCase(Locale.ROOT);
   }
}
