package io.redspace.ironslib.transmog.client;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class CapeData {
   public double xCloakO;
   public double yCloakO;
   public double zCloakO;
   public double xCloak;
   public double yCloak;
   public double zCloak;
   public double capeFlap;
   public double capeLean;
   public double capeLean2;
   public float bob;
   public float oBob;
   public int lastTick = -1;

   public CapeData snapshot() {
      CapeData captured = new CapeData();
      captured.capeLean = this.capeLean;
      captured.capeFlap = this.capeFlap;
      captured.capeLean2 = this.capeLean2;
      return captured;
   }

   public void extractState(@NotNull LivingEntity entity, float partialTicks, HumanoidRenderState renderState) {
      if (this.lastTick != entity.tickCount) {
         if (this.lastTick == -1) {
            for (int i = 0; i < 20; i++) {
               this.moveCloak(entity);
            }
         }

         this.moveCloak(entity);
         this.lastTick = entity.tickCount;
      }

      double deltaX = Mth.lerp(partialTicks, this.xCloakO, this.xCloak) - Mth.lerp(partialTicks, entity.xo, entity.getX());
      double deltaY = Mth.lerp(partialTicks, this.yCloakO, this.yCloak) - Mth.lerp(partialTicks, entity.yo, entity.getY());
      double deltaZ = Mth.lerp(partialTicks, this.zCloakO, this.zCloak) - Mth.lerp(partialTicks, entity.zo, entity.getZ());
      float yBodyRot = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
      double forwardX = Mth.sin(yBodyRot * (float) (Math.PI / 180.0));
      double forwardZ = -Mth.cos(yBodyRot * (float) (Math.PI / 180.0));
      this.capeFlap = (float)deltaY * 10.0F;
      this.capeFlap = Mth.clamp(this.capeFlap, -6.0, 32.0);
      this.capeLean = (float)(deltaX * forwardX + deltaZ * forwardZ) * 100.0F;
      this.capeLean = Mth.clamp(this.capeLean, 0.0, 150.0);
      this.capeLean2 = (float)(deltaX * forwardZ - deltaZ * forwardX) * 100.0F;
      this.capeLean2 = Mth.clamp(this.capeLean2, -20.0, 20.0);
      ((ICapeDataProvider)renderState).irons_lib$setCapeData(this);
   }

   public Vec3 calculateRotationsDegrees(float partialTicks) {
      double d0 = Mth.lerp(partialTicks, this.xCloakO, this.xCloak);
      double d1 = Mth.lerp(partialTicks, this.yCloakO, this.yCloak);
      double d2 = Mth.lerp(partialTicks, this.zCloakO, this.zCloak);
      float f = 0.0F;
      double d3 = Mth.sin(f * (float) (Math.PI / 180.0));
      double d4 = -Mth.cos(f * (float) (Math.PI / 180.0));
      float f1 = (float)d1 * 10.0F;
      f1 = Mth.clamp(f1, -6.0F, 32.0F);
      float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
      f2 = Mth.clamp(f2, 0.0F, 150.0F);
      float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
      f3 = Mth.clamp(f3, -20.0F, 20.0F);
      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      float f4 = Mth.lerp(partialTicks, this.oBob, this.bob);
      return new Vec3(f2 / 2.0F + f1, f3 / 2.0F, -f3 / 2.0F);
   }

   private void moveCloak(Entity livingEntity) {
      this.oBob = this.bob;
      float f;
      if (livingEntity.onGround()) {
         f = (float)Math.min(0.1, livingEntity.getDeltaMovement().horizontalDistance());
      } else {
         f = 0.0F;
      }

      this.bob = this.bob + (f - this.bob) * 0.4F;
      this.xCloakO = this.xCloak;
      this.yCloakO = this.yCloak;
      this.zCloakO = this.zCloak;
      double d0 = livingEntity.getX() - this.xCloak;
      double d1 = livingEntity.getY() - this.yCloak;
      double d2 = livingEntity.getZ() - this.zCloak;
      double d3 = 10.0;
      if (d0 > 10.0) {
         this.xCloak = livingEntity.getX();
         this.xCloakO = this.xCloak;
      }

      if (d2 > 10.0) {
         this.zCloak = livingEntity.getZ();
         this.zCloakO = this.zCloak;
      }

      if (d1 > 10.0) {
         this.yCloak = livingEntity.getY();
         this.yCloakO = this.yCloak;
      }

      if (d0 < -10.0) {
         this.xCloak = livingEntity.getX();
         this.xCloakO = this.xCloak;
      }

      if (d2 < -10.0) {
         this.zCloak = livingEntity.getZ();
         this.zCloakO = this.zCloak;
      }

      if (d1 < -10.0) {
         this.yCloak = livingEntity.getY();
         this.yCloakO = this.yCloak;
      }

      this.xCloak += d0 * 0.25;
      this.zCloak += d2 * 0.25;
      this.yCloak += d1 * 0.25;
   }
}
