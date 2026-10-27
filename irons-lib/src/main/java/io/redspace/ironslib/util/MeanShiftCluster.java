package io.redspace.ironslib.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.phys.Vec3;

public class MeanShiftCluster {
   public static Vec3 rgbIntToOKLab(int rgb) {
      double r = (rgb >> 16 & 0xFF) / 255.0;
      double g = (rgb >> 8 & 0xFF) / 255.0;
      double b = (rgb & 0xFF) / 255.0;
      r = Math.sqrt(r);
      g = Math.sqrt(g);
      b = Math.sqrt(b);
      r = r <= 0.04045 ? r / 12.92 : Math.pow((r + 0.055) / 1.055, 2.4);
      g = g <= 0.04045 ? g / 12.92 : Math.pow((g + 0.055) / 1.055, 2.4);
      b = b <= 0.04045 ? b / 12.92 : Math.pow((b + 0.055) / 1.055, 2.4);
      double l = 0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b;
      double m = 0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b;
      double s = 0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b;
      l = Math.cbrt(l);
      m = Math.cbrt(m);
      s = Math.cbrt(s);
      double L = 0.2104542553 * l + 0.793617785 * m - 0.0040720468 * s;
      double A = 1.9779984951 * l - 2.428592205 * m + 0.4505937099 * s;
      double B = 0.0259040371 * l + 0.7827717662 * m - 0.808675766 * s;
      return new Vec3(L, A, B);
   }

   public static int oklabToRgbInt(Vec3 lab) {
      double L = lab.x;
      double A = lab.y;
      double B = lab.z;
      double l = L + 0.3963377774 * A + 0.2158037573 * B;
      double m = L - 0.1055613458 * A - 0.0638541728 * B;
      double s = L - 0.0894841775 * A - 1.291485548 * B;
      l = l * l * l;
      m = m * m * m;
      s = s * s * s;
      double r = 4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s;
      double g = -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s;
      double b = -0.0041960863 * l - 0.7034186147 * m + 1.707614701 * s;
      r = r <= 0.0031308 ? 12.92 * r : 1.055 * Math.pow(r, 0.4166666666666667) - 0.055;
      g = g <= 0.0031308 ? 12.92 * g : 1.055 * Math.pow(g, 0.4166666666666667) - 0.055;
      b = b <= 0.0031308 ? 12.92 * b : 1.055 * Math.pow(b, 0.4166666666666667) - 0.055;
      r *= r;
      g *= g;
      b *= b;
      int ri = (int)Math.round(Math.min(1.0, Math.max(0.0, r)) * 255.0);
      int gi = (int)Math.round(Math.min(1.0, Math.max(0.0, g)) * 255.0);
      int bi = (int)Math.round(Math.min(1.0, Math.max(0.0, b)) * 255.0);
      return -16777216 + (ri << 16) + (gi << 8) + bi;
   }

   public static MeanShiftCluster.MeanShiftClusterResult meanShiftCluster(List<Integer> colorsI) {
      double bandwidth = 0.0392156862745098;
      double bandwidthSqr = bandwidth * bandwidth;
      double merge = 0.00392156862745098;
      double mergeSqr = merge * merge;
      double epsilon = 0.00196078431372549;
      int maxIterations = 50;
      List<Vec3> clusters = new ArrayList<>();
      List<Vec3> colors = new ArrayList<>();
      HashMap<Integer, Color> lookupTable = new HashMap<>();

      for (int i : colorsI) {
         colors.add(rgbIntToOKLab(i));
      }

      for (int i = 0; i < colorsI.size(); i++) {
         int colorI = colorsI.get(i);
         Vec3 currentColor = colors.get(i);

         for (int j = 0; j < maxIterations; j++) {
            int count = 0;
            Vec3 sum = Vec3.ZERO;

            for (Vec3 color : colors) {
               if (currentColor.distanceToSqr(color) < bandwidthSqr) {
                  sum = sum.add(color);
                  count++;
               }
            }

            if (count == 0) {
               break;
            }

            Vec3 average = sum.scale(1.0 / count);
            if (currentColor.distanceToSqr(average) < epsilon * epsilon) {
               break;
            }

            currentColor = average;
         }

         boolean unique = true;
         Vec3 assignment = currentColor;

         for (Vec3 existingCluster : clusters) {
            if (currentColor.distanceToSqr(existingCluster) < mergeSqr) {
               unique = false;
               assignment = existingCluster;
               break;
            }
         }

         if (unique) {
            clusters.add(currentColor);
         }

         lookupTable.put(colorI, new Color(oklabToRgbInt(assignment)));
      }

      List<Color> clustersI = new ArrayList<>(clusters.size());

      for (Vec3 oklab : clusters) {
         clustersI.add(new Color(oklabToRgbInt(oklab)));
      }

      return new MeanShiftCluster.MeanShiftClusterResult(clustersI, lookupTable);
   }

   public record MeanShiftClusterResult(List<Color> clusters, Map<Integer, Color> lookupTable) {
   }
}
