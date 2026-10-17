package net.neoforged.neoforge.client.event;

import net.minecraft.client.Camera;
import net.neoforged.bus.api.Event;

public class ViewportEvent extends Event {
    public static class ComputeCameraAngles extends ViewportEvent {
        private float yaw;
        private float pitch;
        private float roll;
        private final float partialTick;
        private final Camera camera;

        public ComputeCameraAngles(Camera camera, float partialTick, float yaw, float pitch, float roll) {
            this.camera = camera;
            this.partialTick = partialTick;
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
        }

        public float getYaw(){return yaw;} public void setYaw(float v){yaw=v;}
        public float getPitch(){return pitch;} public void setPitch(float v){pitch=v;}
        public float getRoll(){return roll;} public void setRoll(float v){roll=v;}
        public float getPartialTick(){return partialTick;}
        public Camera getCamera(){return camera;}
    }

    public static class ComputeFogColor extends ViewportEvent {
        private float red, green, blue;
        private final float partialTick;

        public ComputeFogColor(float partialTick, float red, float green, float blue) {
            this.partialTick = partialTick;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public float getRed(){return red;} public void setRed(float v){red=v;}
        public float getGreen(){return green;} public void setGreen(float v){green=v;}
        public float getBlue(){return blue;} public void setBlue(float v){blue=v;}
        public float getPartialTick(){return partialTick;}
    }
}
