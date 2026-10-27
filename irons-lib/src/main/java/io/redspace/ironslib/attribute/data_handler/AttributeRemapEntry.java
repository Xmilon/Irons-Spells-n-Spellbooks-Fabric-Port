package io.redspace.ironslib.attribute.data_handler;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public record AttributeRemapEntry(Identifier from, Identifier to) {
   public static final Codec<AttributeRemapEntry> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            Identifier.CODEC.fieldOf("from").forGetter(AttributeRemapEntry::from), Identifier.CODEC.fieldOf("to").forGetter(AttributeRemapEntry::to)
         )
         .apply(builder, AttributeRemapEntry::new)
   );
}
