package livegraphics3d;

// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 2009/3/2 ¤U¤È 11:23:49
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Live.java

import java.util.Vector;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

class Graphics3D {

  public void setGlobalParameters(int i, int j, Color color, Color color1) {
    point_edge_color = color1;
    if (option_Background == null) {
      option_Background = color;
    }
    full_pixel_width = i;
    full_pixel_height = j;
  }

  public Graphics3D() {
    option_AxesStyle = new Primitive3D[3];
    option_AxesLabel = new Primitive3D[3];
    option_Boxed = true;
    option_Lighting = true;
    option_TextStyle_font_weight = -1;
    option_TextStyle_font_slant = -1;
    option_TextStyle_font_size = -1;
    point_edge_color = null;
    option_Background = null;
    full_pixel_width = 0;
    full_pixel_height = 0;
    pixel_width = 0;
    pixel_height = 0;
    is_stereo = false;
    stereo_distance = 0.0D;
    primitives = new Vector();
    count_primitives = 0;
    min_primitive_index = 0;
    max_primitive_index = 0;
    rotation = new Quaternion(1.0D, 0.0D, 0.0D, 0.0D);
    width = 1000;
    height = 1000;
    eye_distance = 8000;
    length_view_point = 3.3837799999999998D;
    initial_length_view_point = 3.3837799999999998D;
    magnification = 1.0D;
    initial_magnification = 1.0D;
    original_sizes = new double[3];
    original_center = new double[3];
    old_rotation = new Quaternion(0.0D, 0.0D, 0.0D, 0.0D);
    double ad[][] = {
        {
        0.70710700000000004D, 0.0D, 0.70710700000000004D
    }, {
        0.57735000000000003D, 0.57735000000000003D, 0.57735000000000003D
    }, {
        0.0D, 0.70710700000000004D, 0.70710700000000004D
    }, {
        -0.408248D, -0.408248D, -0.81649700000000003D
    }
    };
    option_LightSources_vectors = new Vector();
    option_LightSources_vectors.addElement(ad[0]);
    option_LightSources_vectors.addElement(ad[1]);
    option_LightSources_vectors.addElement(ad[2]);
    option_LightSources_vectors.addElement(ad[3]);
    option_LightSources_colors = new Vector();
    option_LightSources_colors.addElement(new Color(1.0F, 0.0F, 0.0F));
    option_LightSources_colors.addElement(new Color(0.0F, 1.0F, 0.0F));
    option_LightSources_colors.addElement(new Color(0.0F, 0.0F, 1.0F));
    option_LightSources_colors.addElement(new Color(1.0F, 1.0F, 1.0F));
  }

  public Quaternion getQuaternion() {
    return rotation;
  }

  public void setQuaternion(Quaternion quaternion) {
    rotation = quaternion;
  }

  public void multiplyQuaternion(Quaternion quaternion) {
    rotation = quaternion.product(rotation);
    rotation.normalize();
  }

  public void projectPoints(boolean flag) {
    if (!flag && old_rotation.equals(rotation) &&
        old_eye_distance == eye_distance && old_pixel_width == pixel_width &&
        old_pixel_height == pixel_height && old_width == width &&
        old_height == height && old_is_stereo == is_stereo &&
        old_stereo_distance == stereo_distance) {
      return;
    }
    old_rotation.s = rotation.s;
    old_rotation.x = rotation.x;
    old_rotation.y = rotation.y;
    old_rotation.z = rotation.z;
    old_eye_distance = eye_distance;
    old_pixel_width = pixel_width;
    old_pixel_height = pixel_height;
    old_width = width;
    old_height = height;
    old_is_stereo = is_stereo;
    old_stereo_distance = stereo_distance;
    double d = rotation.s * rotation.x;
    double d1 = rotation.s * rotation.y;
    double d2 = rotation.s * rotation.z;
    double d3 = rotation.x * rotation.x;
    double d4 = rotation.x * rotation.y;
    double d5 = rotation.x * rotation.z;
    double d6 = rotation.y * rotation.y;
    double d7 = rotation.y * rotation.z;
    double d8 = rotation.z * rotation.z;
    double d9 = (double) (2 << count_accuracy_bits) * (double) pixel_height;
    double d10 = ( (double) (2 << count_accuracy_bits) * (double) height) /
        (double) eye_distance;
    int i = (int) (d9 * (0.5D - d6 - d8));
    int j = (int) (d9 * (d4 - d2));
    int k = (int) (d9 * (d5 + d1));
    int l = (int) (d9 * (d4 + d2));
    int i1 = (int) (d9 * (0.5D - d3 - d8));
    int j1 = (int) (d9 * (d7 - d));
    int k1 = (int) (d10 * (d5 - d1));
    int l1 = (int) (d10 * (d7 + d));
    int i2 = (int) (d10 * (0.5D - d3 - d6));
    int j2 = (int) ( ( (double) eye_distance * d10) / 2D);
    int i4 = pixel_width / 2;
    int j4 = pixel_height / 2;
    if (!is_stereo) {
      pixel_stereo_offset = 0;
      for (int k4 = 0; k4 < count_points; k4++) {
        int k2 = i * xs[k4] + j * ys[k4] + k * zs[k4];
        int i3 = l * xs[k4] + i1 * ys[k4] + j1 * zs[k4];
        int k3 = k1 * xs[k4] + l1 * ys[k4] + i2 * zs[k4];
        int i5 = j2 - k3;
        if (i5 <= 0) {
          point_scale[k4] = 0;
        }
        else {
          left_pixel_xs[k4] = i4 + k2 / i5;
          pixel_ys[k4] = j4 - i3 / i5;
          point_scale[k4] = i5;
        }
      }

    }
    else {
      int k5 = (int) ( (double) stereo_offset * d9);
      pixel_stereo_offset = k5 / j2;
      int l5 = i4 - pixel_stereo_offset;
      int k6 = i4 + pixel_stereo_offset;
      for (int l4 = 0; l4 < count_points; l4++) {
        int l2 = i * xs[l4] + j * ys[l4] + k * zs[l4];
        int j3 = l * xs[l4] + i1 * ys[l4] + j1 * zs[l4];
        int l3 = k1 * xs[l4] + l1 * ys[l4] + i2 * zs[l4];
        int j5 = j2 - l3;
        if (j5 <= 0) {
          point_scale[l4] = 0;
        }
        else {
          left_pixel_xs[l4] = l5 + (l2 + k5) / j5;
          right_pixel_xs[l4] = k6 + (l2 - k5) / j5;
          pixel_ys[l4] = j4 - j3 / j5;
          point_scale[l4] = j5;
        }
      }

    }
    if (count_points == 0) {
      return;
    }
    for (int i6 = 0; i6 < count_primitives; i6++) {
      Primitive3D primitive3d = (Primitive3D) primitives.elementAt(i6);
      int l6 = primitive3d.count_points;
      if (l6 >= 3) {
        if (l6 == 3) {
          int i7 = point_scale[primitive3d.first_point] +
              point_scale[primitive3d.second_point] +
              point_scale[primitive3d.fourth_point];
          rotated_center_zs[i6] = i7 + i7;
        }
        else {
          int j7 = point_scale[primitive3d.first_point] +
              point_scale[primitive3d.third_point];
          rotated_center_zs[i6] = j7 + j7 +
              point_scale[primitive3d.second_point] +
              point_scale[primitive3d.fourth_point];
        }
      }
      else
      if (l6 == 2) {
        int k7 = point_scale[primitive3d.first_point] +
            point_scale[primitive3d.third_point];
        rotated_center_zs[i6] = k7 + k7 + k7;
      }
      else {
        rotated_center_zs[i6] = 6 * point_scale[primitive3d.points[0]];
      }
    }

    for (int j8 = 0; j8 < count_primitives; j8++) {
      int j6 = order[j8];
      int i8 = rotated_center_zs[j6];
      int l7;
      for (l7 = j8 - 1; l7 >= 0 && rotated_center_zs[order[l7]] < i8; l7--) {
        order[l7 + 1] = order[l7];
      }

      order[l7 + 1] = j6;
    }

  }

  public void addPrimitive(Primitive3D primitive3d) {
    if (primitive3d == null || primitive3d.count_points <= 0 ||
        count_primitives != primitives.size()) {
      return;
    }
    primitives.addElement(primitive3d);
    for (int i = 0; i < primitive3d.count_points; i++) {
      if (primitive3d.original_points[i] != null) {
        if (max_original_coordinates != null) {
          for (int j = 0; j < 3; j++) {
            double d = primitive3d.original_points[i][j];
            if (d > max_original_coordinates[j]) {
              max_original_coordinates[j] = d;
            }
            if (d < min_original_coordinates[j]) {
              min_original_coordinates[j] = d;
            }
          }

        }
        else {
          max_original_coordinates = new double[3];
          min_original_coordinates = new double[3];
          for (int k = 0; k < 3; k++) {
            double d1 = primitive3d.original_points[i][k];
            max_original_coordinates[k] = d1;
            min_original_coordinates[k] = d1;
          }

        }
      }
    }

    if (primitive3d.count_points > max_primitive_count_points) {
      max_primitive_count_points = primitive3d.count_points;
    }
    max_count_points = max_count_points + primitive3d.count_points;
    count_primitives = count_primitives + 1;
    max_primitive_index = count_primitives - 1;
  }

  public boolean xor(boolean flag, boolean flag1) {
    return flag && !flag1 || !flag && flag1;
  }

  public void preparePrimitives(Graphics g) {
    if (max_original_coordinates == null) {
      max_original_coordinates = new double[3];
      min_original_coordinates = new double[3];
      for (int i = 0; i < 3; i++) {
        max_original_coordinates[i] = 0.0D;
        min_original_coordinates[i] = 0.0D;
      }

    }
    if (option_Background == null) {
      option_Background = Color.white;
    }
    if (option_DefaultColor == null) {
      option_DefaultColor = new Color(option_Background.getRed() < 128 ? 255 :
                                      0,
                                      option_Background.getGreen() < 128 ? 255 :
                                      0,
                                      option_Background.getBlue() < 128 ? 255 :
                                      0);
    }
    if (option_AmbientLight == null) {
      option_AmbientLight = Color.black;
    }
    for (int j = 0; j < 3; j++) {
      if (option_PlotRange != null && option_PlotRange[j][0] != ( -1.0D / 0.0D) &&
          option_PlotRange[j][1] != (1.0D / 0.0D)) {
        if (option_PlotRange[j][1] < option_PlotRange[j][0]) {
          double d = option_PlotRange[j][0];
          option_PlotRange[j][0] = option_PlotRange[j][1];
          option_PlotRange[j][1] = d;
        }
        original_sizes[j] = option_PlotRange[j][1] - option_PlotRange[j][0];
        original_center[j] = (option_PlotRange[j][1] + option_PlotRange[j][0]) *
            0.5D;
      }
      else {
        original_sizes[j] = max_original_coordinates[j] -
            min_original_coordinates[j];
        if (max_original_coordinates[j] == min_original_coordinates[j]) {
          original_sizes[j] = 2.1000000000000001D;
        }
        else {
          original_sizes[j] = original_sizes[j] * 1.05D;
        }
        original_center[j] = (max_original_coordinates[j] +
                              min_original_coordinates[j]) * 0.5D;
      }
    }

    if (option_BoxRatios == null) {
      option_BoxRatios = new double[3];
      for (int k = 0; k < 3; k++) {
        option_BoxRatios[k] = original_sizes[k];
      }

    }
    double d1 = option_BoxRatios[0];
    if (option_BoxRatios[1] > d1) {
      d1 = option_BoxRatios[1];
    }
    if (option_BoxRatios[2] > d1) {
      d1 = option_BoxRatios[2];
    }
    double ad[] = new double[3];
    double ad1[] = new double[3];
    for (int l = 0; l < 3; l++) {
      option_BoxRatios[l] = option_BoxRatios[l] / d1;
      ad[l] = (option_BoxRatios[l] / original_sizes[l]) *
          (double) max_coordinate;
      ad1[l] = option_BoxRatios[l] * (double) max_coordinate;
    }

    double d2 = Math.sqrt(option_BoxRatios[0] * option_BoxRatios[0] +
                          option_BoxRatios[1] * option_BoxRatios[1] +
                          option_BoxRatios[2] * option_BoxRatios[2]);
    if (option_Ticks == null) {
      option_Ticks = new Vector[3];
      for (int l1 = 0; l1 < 3; l1++) {
        option_Ticks[l1] = new Vector();
      }

    }
    if (option_Boxed) {
      if (option_BoxStyle == null) {
        option_BoxStyle = new Primitive3D();
      }
      if (option_BoxStyle.standard_color == null) {
        option_BoxStyle.standard_color = option_DefaultColor;
      }
    }
    double ad2[][] = {
        null, null
    };
    boolean flag = false;
    boolean flag1 = false;
    for (int i1 = 0; i1 < 3; i1++) {
      boolean flag2 = false;
      for (int i2 = 0; i2 < 4; i2++) {
        boolean flag3 = false;
        int k3 = i2;
        if (i1 == 1) {
          if (k3 == 1) {
            k3 = 2;
          }
          else
          if (k3 == 2) {
            k3 = 1;
          }
        }
        if (option_Axes[i1] && !flag2) {
          if (option_AxesEdge[i1] >= 0) {
            if (k3 == option_AxesEdge[i1]) {
              flag3 = true;
            }
          }
          else
          if (option_AxesEdge[i1] == -1) {
            if (i2 == 3) {
              flag3 = true;
            }
            else
            if (xor( (i2 & 1) == 1, option_ViewPoint[ (i1 + 1) % 3] >= 0.0D) &&
                xor( (i2 & 2) == 2, option_ViewPoint[ (i1 + 2) % 3] <= 0.0D) &&
                Math.abs(option_ViewPoint[ (i1 + 1) % 3]) <=
                Math.abs(option_ViewPoint[ (i1 + 2) % 3])) {
              flag3 = true;
            }
            else
            if (xor( (i2 & 1) == 1, option_ViewPoint[ (i1 + 1) % 3] <= 0.0D) &&
                xor( (i2 & 2) == 2, option_ViewPoint[ (i1 + 2) % 3] >= 0.0D) &&
                Math.abs(option_ViewPoint[ (i1 + 1) % 3]) >=
                Math.abs(option_ViewPoint[ (i1 + 2) % 3])) {
              flag3 = true;
            }
          }
        }
        if (flag3 || option_Boxed) {
          double ad3[][] = new double[2][3];
          Primitive3D primitive3d5;
          if (flag3) {
            flag2 = true;
            if (option_Ticks != null && option_Ticks[i1] != null) {
              flag1 = true;
              if (option_Ticks[i1].isEmpty()) {
                ticks_max_in_length[i1] = 0.01D;
                ticks_max_out_length[i1] = 0.0D;
                double ad6[] = {
                    0.25D, 0.20000000000000001D, 0.10000000000000001D,
                    0.050000000000000003D, 0.025000000000000001D, 0.02D, 0.01D
                };
                double d4 = Math.pow(10D,
                                     Math.ceil(Math.log(original_sizes[i1]) /
                                               Math.log(10D)));
                int l6;
                for (l6 = 1; l6 < 7 && original_sizes[i1] / d4 / ad6[l6] <= 5D;
                     l6++) {
                  ;
                }
                l6--;
                double d8 = ad6[l6] * d4;
                double d11 = original_center[i1] - original_sizes[i1] / 2D;
                double d14 = original_center[i1] + original_sizes[i1] / 2D;
                double d15 = Math.ceil(d11 / d8) * d8;
                for (int l9 = 0; l9 < 10 && d15 <= d14; l9++) {
                  if (Math.abs(d15) < Math.abs(d8 / 100D)) {
                    d15 = 0.0D;
                  }
                  Primitive3D primitive3d8 = new Primitive3D();
                  primitive3d8.front_specular_exponent = 0.01D;
                  primitive3d8.back_specular_exponent = 0.0D;
                  primitive3d8.original_point_size = d15;
                  primitive3d8.text = Float.toString( (float) d15);
                  option_Ticks[i1].addElement(primitive3d8);
                  d15 += d8;
                }

              }
              for (int l4 = 0; l4 < option_Ticks[i1].size(); l4++) {
                Primitive3D primitive3d6 = new Primitive3D( (Primitive3D)
                    option_Ticks[i1].elementAt(l4));
                Primitive3D primitive3d7 = (Primitive3D) option_Ticks[i1].
                    elementAt(l4);
                double d7 = primitive3d7.original_point_size;
                double d10 = primitive3d7.front_specular_exponent;
                double d13 = primitive3d7.back_specular_exponent;
                primitive3d7.text = null;
                primitive3d7.count_points = 2;
                primitive3d7.original_points = new double[2][3];
                primitive3d7.original_points[0][i1] = d7;
                primitive3d7.original_points[1][i1] = d7;
                primitive3d7.original_scaled_offsets = new double[2][3];
                primitive3d7.original_scaled_offsets[0][i1] = 0.0D;
                primitive3d7.original_scaled_offsets[1][i1] = 0.0D;
                primitive3d6.count_points = 1;
                primitive3d6.original_points = new double[1][3];
                primitive3d6.original_points[0][i1] = d7;
                primitive3d6.original_scaled_offsets = new double[1][3];
                primitive3d6.original_scaled_offsets[0][i1] = 0.0D;
                primitive3d6.original_point_size = 0.0D;
                primitive3d6.original_thickness = 0.0D;
                for (int l8 = 1; l8 < 3; l8++) {
                  int j9 = (i1 + l8) % 3;
                  double d16 = 1.0D;
                  if ( (i2 & l8) == 0) {
                    d16 = -1D;
                  }
                  double d19 = original_center[j9] - original_sizes[j9] / 2D;
                  primitive3d7.original_points[0][j9] = d19;
                  primitive3d7.original_points[1][j9] = d19;
                  primitive3d7.original_scaled_offsets[0][j9] = (d16 + 1.0D) /
                      2D -
                      (d16 * d10 * d2) / 1.4142136000000001D /
                      option_BoxRatios[j9];
                  primitive3d7.original_scaled_offsets[1][j9] = (d16 + 1.0D) /
                      2D +
                      (d16 * d13 * d2) / 1.4142136000000001D /
                      option_BoxRatios[j9];
                  primitive3d6.original_points[0][j9] = d19;
                  primitive3d6.original_scaled_offsets[0][j9] = (d16 + 1.0D) /
                      2D +
                      (d16 *
                       (0.050000000000000003D + ticks_max_in_length[i1] +
                        ticks_max_out_length[i1]) * d2) / 1.4142136000000001D /
                      option_BoxRatios[j9];
                }

                addPrimitive(primitive3d7);
                addPrimitive(primitive3d6);
              }

            }
            if (option_AxesLabel[i1] != null) {
              flag = true;
              Primitive3D primitive3d4 = option_AxesLabel[i1];
              primitive3d4.count_points = 1;
              primitive3d4.original_point_size = 0.0D;
              primitive3d4.original_thickness = 0.0D;
              primitive3d4.original_points = ad2;
              primitive3d4.original_scaled_offsets = new double[1][3];
              primitive3d4.original_scaled_offsets[0][i1] = 0.5D;
              for (int i5 = 1; i5 < 3; i5++) {
                double d5 = 1.0D;
                if ( (i2 & i5) == 0) {
                  d5 = -1D;
                }
                int i7 = (i1 + i5) % 3;
                primitive3d4.original_scaled_offsets[0][i7] = (d5 + 1.0D) / 2D +
                    (d5 *
                     (0.14999999999999999D + ticks_max_in_length[i1] +
                      ticks_max_out_length[i1]) * d2) / 1.4142136000000001D /
                    option_BoxRatios[i7];
              }

              addPrimitive(primitive3d4);
            }
            if (option_AxesStyle[i1] != null) {
              primitive3d5 = new Primitive3D(option_AxesStyle[i1]);
            }
            else {
              primitive3d5 = new Primitive3D();
            }
            if (primitive3d5.standard_color == null) {
              primitive3d5.standard_color = option_DefaultColor;
            }
          }
          else {
            primitive3d5 = new Primitive3D(option_BoxStyle);
          }
          primitive3d5.is_filled = false;
          primitive3d5.count_points = 2;
          primitive3d5.original_points = ad2;
          primitive3d5.original_scaled_offsets = ad3;
          primitive3d5.original_scaled_offsets[0][i1] = 0.0D;
          primitive3d5.original_scaled_offsets[0][ (i1 + 1) % 3] = i2 & 1;
          primitive3d5.original_scaled_offsets[0][ (i1 + 2) % 3] = (i2 & 2) / 2;
          primitive3d5.original_scaled_offsets[1][i1] = 1.0D;
          primitive3d5.original_scaled_offsets[1][ (i1 + 1) % 3] = i2 & 1;
          primitive3d5.original_scaled_offsets[1][ (i1 + 2) % 3] = (i2 & 2) / 2;
          addPrimitive(primitive3d5);
        }
      }

    }

    int ai[] = new int[3];
    xs = new int[max_count_points];
    ys = new int[max_count_points];
    zs = new int[max_count_points];
    count_points = 0;
    for (int j2 = 0; j2 < count_primitives; j2++) {
      Primitive3D primitive3d = (Primitive3D) primitives.elementAt(j2);
      if (primitive3d.points == null) {
        primitive3d.points = new int[primitive3d.count_points + 1];
      }
      int j4 = -1;
      int l3 = 0;
      for (int j5 = 0; j5 < primitive3d.count_points; j5++) {
        if (primitive3d.original_points[j5] != null) {
          for (int j1 = 0; j1 < 3; j1++) {
            ai[j1] = (int) (ad[j1] *
                            (primitive3d.original_points[j5][j1] -
                             original_center[j1])) +
                (int) (ad1[j1] * primitive3d.original_scaled_offsets[j5][j1]);
          }

        }
        else {
          for (int k1 = 0; k1 < 3; k1++) {
            ai[k1] = (int) (ad1[k1] *
                            (primitive3d.original_scaled_offsets[j5][k1] - 0.5D));
          }

        }
        int k4;
        for (k4 = 0;
             k4 < count_points &&
             (ai[0] != xs[k4] || ai[1] != ys[k4] || ai[2] != zs[k4]); k4++) {
          ;
        }
        if (k4 >= count_points) {
          xs[k4] = ai[0];
          ys[k4] = ai[1];
          zs[k4] = ai[2];
          count_points = k4 + 1;
        }
        if (j4 != k4) {
          primitive3d.points[l3] = k4;
          j4 = k4;
          l3++;
        }
      }

      if (primitive3d.is_filled && l3 >= 4 &&
          primitive3d.points[0] == primitive3d.points[l3 - 1]) {
        l3--;
      }
      if (primitive3d.count_points == 2) {
        if (l3 == 1) {
          primitive3d.points[1] = primitive3d.points[0];
        }
      }
      else
      if (primitive3d.count_points >= 3) {
        if (l3 == 1) {
          primitive3d.points[1] = primitive3d.points[0];
          l3 = 2;
        }
        if (l3 == 2) {
          primitive3d.points[2] = primitive3d.points[1];
          l3 = 3;
        }
        primitive3d.count_points = l3;
      }
      if (primitive3d.text == null) {
        if (!primitive3d.is_filled && primitive3d.count_points == 1) {
          if (primitive3d.is_absolute_point_size) {
            primitive3d.point_diameter = (int) primitive3d.original_point_size;
          }
          else {
            primitive3d.point_diameter = (int) (primitive3d.original_point_size *
                                                (double) full_pixel_height * d2 *
                                                (double) (max_coordinate <<
                count_accuracy_bits));
          }
        }
        else
        if (primitive3d.is_filled) {
          if (primitive3d.is_absolute_edge_thickness) {
            primitive3d.point_diameter = (int) primitive3d.
                original_edge_thickness;
          }
          else {
            primitive3d.point_diameter = (int) (primitive3d.
                                                original_edge_thickness *
                                                (double) full_pixel_height * d2 *
                                                (double) (max_coordinate <<
                count_accuracy_bits));
          }
        }
        else
        if (primitive3d.is_absolute_thickness) {
          primitive3d.point_diameter = (int) primitive3d.original_thickness;
        }
        else {
          primitive3d.point_diameter = (int) (primitive3d.original_thickness *
                                              (double) full_pixel_height * d2 *
                                              (double) (max_coordinate <<
              count_accuracy_bits));
        }
        if (primitive3d.count_points == 1) {
          primitive3d.first_point = primitive3d.points[0];
          primitive3d.second_point = primitive3d.points[0];
          primitive3d.third_point = primitive3d.points[0];
          primitive3d.fourth_point = primitive3d.points[0];
        }
        else
        if (primitive3d.count_points == 2) {
          primitive3d.first_point = primitive3d.points[0];
          primitive3d.second_point = primitive3d.points[0];
          primitive3d.third_point = primitive3d.points[1];
          primitive3d.fourth_point = primitive3d.points[1];
        }
        else
        if (primitive3d.count_points == 3) {
          primitive3d.first_point = primitive3d.points[0];
          primitive3d.second_point = primitive3d.points[1];
          primitive3d.third_point = primitive3d.points[1];
          primitive3d.fourth_point = primitive3d.points[2];
        }
        else
        if (primitive3d.count_points == 4) {
          int l5 = 0;
          int j6 = 0;
          boolean flag4 = false;
          for (int l7 = 0; l7 < 4; l7++) {
            int j7 = Math.abs(xs[primitive3d.points[ (l7 + 1) % 4]] -
                              xs[primitive3d.points[l7]]) +
                Math.abs(ys[primitive3d.points[ (l7 + 1) % 4]] -
                         ys[primitive3d.points[l7]]) +
                Math.abs(zs[primitive3d.points[ (l7 + 1) % 4]] -
                         zs[primitive3d.points[l7]]);
            if (l7 == 0 || j6 > j7) {
              l5 = l7;
              j6 = j7;
            }
          }

          if (l5 == 1 || l5 == 2) {
            primitive3d.first_point = primitive3d.points[0];
            primitive3d.second_point = primitive3d.points[1];
            primitive3d.third_point = primitive3d.points[2];
            primitive3d.fourth_point = primitive3d.points[3];
          }
          else {
            primitive3d.first_point = primitive3d.points[2];
            primitive3d.second_point = primitive3d.points[3];
            primitive3d.third_point = primitive3d.points[0];
            primitive3d.fourth_point = primitive3d.points[1];
          }
        }
        else
        if (primitive3d.count_points > 4) {
          primitive3d.first_point = primitive3d.points[0];
          primitive3d.second_point = primitive3d.points[primitive3d.
              count_points / 4];
          primitive3d.third_point = primitive3d.points[primitive3d.count_points /
              2];
          primitive3d.fourth_point = primitive3d.points[ (3 *
              primitive3d.count_points) / 4];
        }
      }
      primitive3d.original_points = null;
      primitive3d.original_scaled_offsets = null;
    }

    if (option_ViewPoint != null && option_ViewVertical != null) {
      double ad4[] = new double[3];
      double ad7[] = new double[3];
      double d9 = Math.sqrt(option_ViewPoint[0] * option_ViewPoint[0] +
                            option_ViewPoint[1] * option_ViewPoint[1] +
                            option_ViewPoint[2] * option_ViewPoint[2]);
      ad7[0] = option_ViewPoint[0] / d9;
      ad7[1] = option_ViewPoint[1] / d9;
      ad7[2] = option_ViewPoint[2] / d9;
      ad4[0] = ad7[1];
      ad4[1] = -ad7[0];
      ad4[2] = 0.0D;
      double d3 = Math.sqrt(ad4[0] * ad4[0] + ad4[1] * ad4[1] + ad4[2] * ad4[2]);
      if (d3 > 1.0D) {
        d3 = 1.0D;
      }
      double d6 = Math.asin(d3);
      if (d6 < 0.0001D) {
        if (option_ViewPoint[2] > 0.0D) {
          rotation = new Quaternion(0.0D, 1.0D, 0.0D, 0.0D, false);
        }
        else {
          rotation = new Quaternion(3.1415926535897931D, 1.0D, 0.0D, 0.0D, false);
        }
      }
      else {
        if (option_ViewPoint[2] < 0.0D) {
          d6 = 3.1415926535897931D - d6;
        }
        rotation = new Quaternion(d6, ad4[0], ad4[1], ad4[2], true);
      }
      double ad8[] = {
          option_ViewVertical[0] * option_BoxRatios[0],
          option_ViewVertical[1] * option_BoxRatios[1],
          option_ViewVertical[2] * option_BoxRatios[2]
      };
      ad8 = rotation.rotated(ad8);
      if (Math.abs(ad8[0]) < 9.9999999999999995E-008D) {
        if (ad8[1] > 0.0D) {
          d6 = 0.0D;
        }
        else {
          d6 = 3.1415926535897931D;
        }
      }
      else {
        d6 = Math.atan2(ad8[0], ad8[1]);
      }
      rotation = (new Quaternion(d6, 0.0D, 0.0D, 1.0D, false)).product(rotation);
      if (flag || flag1) {
        setPerspective(d9, 0.66000000000000003D, is_stereo, stereo_distance);
      }
      else {
        setPerspective(d9, 1.0D, is_stereo, stereo_distance);
      }
    }
    else
    if (flag || flag1) {
      setPerspective(3.3837799999999998D, 0.66000000000000003D, is_stereo,
                     stereo_distance);
    }
    else {
      setPerspective(3.3837799999999998D, 1.0D, is_stereo, stereo_distance);
    }
    initial_magnification = magnification;
    initial_length_view_point = length_view_point;
    initial_rotation = rotation;
    for (int k2 = 0; k2 < count_primitives; k2++) {
      Primitive3D primitive3d1 = (Primitive3D) primitives.elementAt(k2);
      if (primitive3d1.standard_color == null) {
        primitive3d1.standard_color = option_DefaultColor;
      }
      if (primitive3d1.text != null) {
        if (primitive3d1.front_face_color == null) {
          if (option_TextStyle_font_color != null) {
            primitive3d1.front_face_color = option_TextStyle_font_color;
          }
          else {
            primitive3d1.front_face_color = primitive3d1.standard_color;
          }
        }
        if (primitive3d1.back_face_color == null &&
            option_TextStyle_font_background != null) {
          primitive3d1.back_face_color = option_TextStyle_font_background;
        }
        primitive3d1.front_color = primitive3d1.front_face_color;
        primitive3d1.back_color = primitive3d1.back_face_color;
      }
      else {
        if (primitive3d1.edge_color == null) {
          primitive3d1.edge_color = option_DefaultColor;
        }
        if (primitive3d1.front_face_color == null) {
          primitive3d1.front_face_color = option_DefaultColor;
        }
        if (primitive3d1.back_face_color == null && primitive3d1.text == null) {
          primitive3d1.back_face_color = option_DefaultColor;
        }
        if (!option_Lighting) {
          primitive3d1.front_color = primitive3d1.front_face_color;
          primitive3d1.back_color = primitive3d1.back_face_color;
        }
      }
      if (primitive3d1.is_filled && option_Lighting &&
          primitive3d1.count_points > 2) {
        double ad5[] = new double[3];
        int ai2[][] = {
            {
            (primitive3d1.front_diffuse_color.getRed() *
             option_AmbientLight.getRed()) / 255,
            (primitive3d1.front_diffuse_color.getGreen() *
             option_AmbientLight.getGreen()) / 255,
            (primitive3d1.front_diffuse_color.getBlue() *
             option_AmbientLight.getBlue()) / 255
        }, {
            (primitive3d1.back_diffuse_color.getRed() *
             option_AmbientLight.getRed()) / 255,
            (primitive3d1.back_diffuse_color.getGreen() *
             option_AmbientLight.getGreen()) / 255,
            (primitive3d1.back_diffuse_color.getBlue() *
             option_AmbientLight.getBlue()) / 255
        }
        };
        int k5 = xs[primitive3d1.second_point] - xs[primitive3d1.first_point];
        int i6 = ys[primitive3d1.second_point] - ys[primitive3d1.first_point];
        int k6 = zs[primitive3d1.second_point] - zs[primitive3d1.first_point];
        int k7 = xs[primitive3d1.fourth_point] - xs[primitive3d1.second_point];
        int i8 = ys[primitive3d1.fourth_point] - ys[primitive3d1.second_point];
        int j8 = zs[primitive3d1.fourth_point] - zs[primitive3d1.second_point];
        ad5[0] = i6 * j8 - k6 * i8;
        ad5[1] = k6 * k7 - k5 * j8;
        ad5[2] = k5 * i8 - i6 * k7;
        double d12 = Math.sqrt(ad5[0] * ad5[0] + ad5[1] * ad5[1] +
                               ad5[2] * ad5[2]);
        ad5[0] = ad5[0] / d12;
        ad5[1] = ad5[1] / d12;
        ad5[2] = ad5[2] / d12;
        ad5 = rotation.rotated(ad5);
        for (int k8 = 0; k8 < option_LightSources_vectors.size(); k8++) {
          double ad9[] = (double[]) option_LightSources_vectors.elementAt(k8);
          Color color = (Color) option_LightSources_colors.elementAt(k8);
          double d17 = ad5[0] * ad9[0] + ad5[1] * ad9[1] + ad5[2] * ad9[2];
          int i10;
          Color color1;
          if (d17 > 0.0D) {
            i10 = 0;
            color1 = primitive3d1.front_diffuse_color;
          }
          else {
            d17 = -d17;
            i10 = 1;
            color1 = primitive3d1.back_diffuse_color;
          }
          if (color1 != null) {
            d17 /= 255D;
            ai2[i10][0] = ai2[i10][0] +
                (int) ( (double) (color1.getRed() * color.getRed()) * d17);
            ai2[i10][1] = ai2[i10][1] +
                (int) ( (double) (color1.getGreen() * color.getGreen()) * d17);
            ai2[i10][2] = ai2[i10][2] +
                (int) ( (double) (color1.getBlue() * color.getBlue()) * d17);
          }
          Color color2 = null;
          double d20 = 1.0D;
          d17 = ad5[0] * ad9[0] + ad5[1] * ad9[1] + ad5[2] * ad9[2];
          if (ad5[2] >= 0.0D) {
            if (d17 > 0.0D) {
              i10 = 0;
              color2 = primitive3d1.front_specular_color;
              d20 = primitive3d1.front_specular_exponent;
            }
          }
          else
          if (d17 < 0.0D) {
            i10 = 1;
            color2 = primitive3d1.back_specular_color;
            d20 = primitive3d1.back_specular_exponent;
          }
          if (color2 != null) {
            double ad10[] = {
                2D * ad5[0] * ad5[2], 2D * ad5[1] * ad5[2],
                2D * ad5[2] * ad5[2] - 1.0D
            };
            double d18 = ad10[0] * ad9[0] + ad10[1] * ad9[1] + ad10[2] * ad9[2];
            d18++;
            if (d18 < 0.0D) {
              d18 = 0.0D;
            }
            d18 = Math.pow(Math.abs(d18 / 2D), d20);
            d18 /= 255D;
            ai2[i10][0] = ai2[i10][0] +
                (int) ( (double) (color2.getRed() * color.getRed()) * d18);
            ai2[i10][1] = ai2[i10][1] +
                (int) ( (double) (color2.getGreen() * color.getGreen()) * d18);
            ai2[i10][2] = ai2[i10][2] +
                (int) ( (double) (color2.getBlue() * color.getBlue()) * d18);
          }
        }

        for (int i9 = 0; i9 < 3; i9++) {
          for (int k9 = 0; k9 < 2; k9++) {
            if (ai2[k9][i9] < 0) {
              ai2[k9][i9] = 0;
            }
            if (ai2[k9][i9] > 255) {
              ai2[k9][i9] = 255;
            }
          }

        }

        primitive3d1.front_color = new Color(ai2[0][0], ai2[0][1], ai2[0][2]);
        primitive3d1.back_color = new Color(ai2[1][0], ai2[1][1], ai2[1][2]);
      }
    }

    left_pixel_xs = new int[count_points];
    right_pixel_xs = new int[count_points];
    pixel_ys = new int[count_points];
    point_scale = new int[count_points];
    temp_xs = new int[max_primitive_count_points + 1];
    temp_ys = new int[max_primitive_count_points + 1];
    temp_line_xs = new int[6];
    temp_line_ys = new int[6];
    order = new int[count_primitives];
    for (int l2 = 0; l2 < count_primitives; l2++) {
      order[l2] = l2;
    }

    rotated_center_zs = new int[count_primitives];
    int ai1[] = new int[count_primitives];
    int ai3[] = new int[count_primitives];
    int ai4[] = new int[count_primitives];
    for (int i3 = 0; i3 < count_primitives; i3++) {
      Primitive3D primitive3d2 = (Primitive3D) primitives.elementAt(i3);
      for (int i4 = 0; i4 < primitive3d2.count_points; i4++) {
        ai1[i3] = ai1[i3] + xs[primitive3d2.points[i4]];
        ai3[i3] = ai3[i3] + ys[primitive3d2.points[i4]];
        ai4[i3] = ai4[i3] + zs[primitive3d2.points[i4]];
      }

      ai1[i3] = ai1[i3] / primitive3d2.count_points;
      ai3[i3] = ai3[i3] / primitive3d2.count_points;
      ai4[i3] = ai4[i3] / primitive3d2.count_points;
    }

    if (option_TextStyle_font_family == null) {
      option_TextStyle_font_family = "Courier";
    }
    if (option_TextStyle_font_weight == -1) {
      option_TextStyle_font_weight = 0;
    }
    if (option_TextStyle_font_slant == -1) {
      option_TextStyle_font_slant = 0;
    }
    if (option_TextStyle_font_size == -1) {
      option_TextStyle_font_size = 10;
    }
    for (int j3 = 0; j3 < count_primitives; j3++) {
      Primitive3D primitive3d3 = (Primitive3D) primitives.elementAt(j3);
      if (primitive3d3.text != null) {
        if (primitive3d3.font_url == null) {
          primitive3d3.font_url = option_TextStyle_font_url;
        }
        if (primitive3d3.font == null) {
          primitive3d3.font = option_TextStyle_font_family;
        }
        if (primitive3d3.font_size == -1) {
          primitive3d3.font_size = option_TextStyle_font_size;
        }
        if (primitive3d3.font_weight == -1) {
          primitive3d3.font_weight = option_TextStyle_font_weight;
        }
        if (primitive3d3.font_slant == -1) {
          primitive3d3.font_slant = option_TextStyle_font_slant;
        }
        primitive3d3.font = new Font( (String) primitive3d3.font,
                                     primitive3d3.font_weight |
                                     primitive3d3.font_slant,
                                     primitive3d3.font_size);
        g.setFont( (Font) primitive3d3.font);
        FontMetrics fontmetrics = g.getFontMetrics();
        primitive3d3.font_size = fontmetrics.getMaxAscent() +
            fontmetrics.getMaxDescent();
        primitive3d3.font_weight = fontmetrics.stringWidth(primitive3d3.text);
        primitive3d3.font_slant = - (int) ( ( (double) primitive3d3.font_weight *
                                             (primitive3d3.original_point_size +
                                              1.0D)) / 2D);
        primitive3d3.font_y_offset = (int) ( ( (double) primitive3d3.font_size *
                                              (primitive3d3.original_thickness +
                                               1.0D)) / 2D) -
            fontmetrics.getMaxDescent();
        primitive3d3.first_point = primitive3d3.font_slant - 2;
        primitive3d3.second_point = primitive3d3.font_y_offset -
            fontmetrics.getMaxAscent() - 2;
        primitive3d3.third_point = primitive3d3.font_weight + 4;
        primitive3d3.fourth_point = primitive3d3.font_size + 4;
      }
    }

  }

  public void setPerspective(double d, double d1, boolean flag, double d2) {
    length_view_point = d;
    magnification = d1;
    is_stereo = flag;
    stereo_distance = d2;
    if (!flag) {
      pixel_width = full_pixel_width;
      pixel_height = full_pixel_height;
    }
    else {
      pixel_width = (full_pixel_width + 1) / 2;
      pixel_height = full_pixel_height;
    }
    double d3 = 0.5D * (double) max_coordinate *
        Math.sqrt(option_BoxRatios[0] * option_BoxRatios[0] +
                  option_BoxRatios[1] * option_BoxRatios[1] +
                  option_BoxRatios[2] * option_BoxRatios[2]);
    d = (double) max_coordinate * d;
    if (d < 1.01D * d3) {
      d = 1.01D * d3;
      length_view_point = d / (double) max_coordinate;
    }
    eye_distance = (int) d;
    width = 2 * (int) Math.sqrt( (d * d * d3 * d3) / (d * d - d3 * d3));
    height = width;
    if (pixel_width < pixel_height) {
      height = (int) ( ( (double) height * (double) pixel_height) /
                      (double) pixel_width);
    }
    else {
      width = (int) ( ( (double) width * (double) pixel_width) /
                     (double) pixel_height);
    }
    eye_distance = (int) ( (double) eye_distance / d1);
    width = (int) ( (double) width / d1);
    height = (int) ( (double) height / d1);
    stereo_offset = (int) ( (d2 / 2D) * d3 * 2D);
  }

  public void setCutPrimitivesCount(int i) {
    min_primitive_index = 0;
    max_primitive_index = count_primitives - i;
  }

  public void paint(Graphics g, Graphics g1, Image image) {
    if (!is_stereo) {
      paintPrimitives(g, left_pixel_xs);
      return;
    }
    else {
      paintPrimitives(g, left_pixel_xs);
      paintPrimitives(g1, right_pixel_xs);
      g.drawImage(image, pixel_width, 0, null);
      return;
    }
  }

  public void paintPrimitives(Graphics g, int ai[]) {
    for (int i = 0; i < count_primitives; i++) {
      int j = order[i];
      if (min_primitive_index <= j && j <= max_primitive_index) {
        Primitive3D primitive3d = (Primitive3D) primitives.elementAt(j);
        int l2 = primitive3d.count_points;
        if (primitive3d.text != null) {
          int k = primitive3d.points[0];
          if (point_scale[k] != 0) {
            if (primitive3d.back_color != null) {
              g.setColor(primitive3d.back_color);
              g.fillRect(ai[k] + primitive3d.first_point,
                         pixel_ys[k] + primitive3d.second_point,
                         primitive3d.third_point, primitive3d.fourth_point);
            }
            g.setColor(primitive3d.front_color);
            g.setFont( (Font) primitive3d.font);
            g.drawString(primitive3d.text, ai[k] + primitive3d.font_slant,
                         pixel_ys[k] + primitive3d.font_y_offset);
          }
        }
        else
        if (l2 == 1) {
          int k1 = primitive3d.points[0];
          if (point_scale[k1] != 0) {
            g.setColor(primitive3d.standard_color);
            int i2;
            if (primitive3d.is_absolute_point_size) {
              i2 = primitive3d.point_diameter;
            }
            else {
              i2 = primitive3d.point_diameter / point_scale[k1];
            }
            if (i2 > 1) {
              int k2 = i2 / 2;
              g.fillOval(ai[k1] - k2, pixel_ys[k1] - k2, i2, i2);
              if (point_edge_color != null) {
                g.setColor(point_edge_color);
                g.drawOval(ai[k1] - k2, pixel_ys[k1] - k2, i2, i2);
              }
            }
            else {
              if (point_edge_color != null) {
                g.setColor(point_edge_color);
              }
              g.drawLine(ai[k1], pixel_ys[k1], ai[k1], pixel_ys[k1]);
            }
          }
        }
        else
        if (l2 > 1) {
          int l;
          for (l = 0; l < l2; l++) {
            int l1 = primitive3d.points[l];
            if (point_scale[l1] == 0) {
              break;
            }
            temp_xs[l] = ai[l1];
            temp_ys[l] = pixel_ys[l1];
          }

          if (l >= l2) {
            boolean flag = true;
            if (primitive3d.is_filled && l2 > 2) {
              int i3 = (ai[primitive3d.second_point] -
                        ai[primitive3d.first_point]) *
                  (pixel_ys[primitive3d.fourth_point] -
                   pixel_ys[primitive3d.second_point]) -
                  (ai[primitive3d.fourth_point] - ai[primitive3d.second_point]) *
                  (pixel_ys[primitive3d.second_point] -
                   pixel_ys[primitive3d.first_point]);
              if (i3 == 0) {
                i3 = primitive3d.last_z_coordinate;
              }
              else {
                primitive3d.last_z_coordinate = i3;
              }
              if (i3 <= 0) {
                g.setColor(primitive3d.front_color);
              }
              else
              if (i3 > 0) {
                g.setColor(primitive3d.back_color);
              }
              g.fillPolygon(temp_xs, temp_ys, l2);
              if (primitive3d.is_outlined) {
                temp_xs[l2] = temp_xs[0];
                temp_ys[l2] = temp_ys[0];
                primitive3d.points[l2] = primitive3d.points[0];
                l2++;
                g.setColor(primitive3d.edge_color);
                flag = primitive3d.is_absolute_edge_thickness;
              }
            }
            else
            if (!primitive3d.is_filled) {
              g.setColor(primitive3d.standard_color);
              flag = primitive3d.is_absolute_thickness;
            }
            if (!primitive3d.is_filled || primitive3d.is_outlined && l2 > 3) {
              if (flag && primitive3d.point_diameter < 2) {
                if (primitive3d.is_filled || l2 <= 2) {
                  g.drawPolygon(temp_xs, temp_ys, l2);
                }
                else {
                  int j3 = temp_xs[0];
                  int l3 = temp_ys[0];
                  for (int i1 = 1; i1 < l2; i1++) {
                    int j4 = temp_xs[i1];
                    int l4 = temp_ys[i1];
                    g.drawLine(j3, l3, j4, l4);
                    j3 = j4;
                    l3 = l4;
                  }

                }
              }
              else {
                int j2 = primitive3d.point_diameter;
                int i4;
                if (!flag) {
                  i4 = j2 / point_scale[primitive3d.points[0]];
                }
                else {
                  i4 = j2;
                }
                i4--;
                int i5 = i4 / 2;
                int l5 = temp_xs[0] - i5;
                int i6 = temp_ys[0] - i5;
                for (int j1 = 1; j1 < l2; j1++) {
                  int k3 = 0;
                  int k4;
                  if (!flag) {
                    k4 = j2 / point_scale[primitive3d.points[j1]];
                  }
                  else {
                    k4 = j2;
                  }
                  k4--;
                  int k5 = k4 / 2;
                  int j6 = temp_xs[j1] - k5;
                  int k6 = temp_ys[j1] - k5;
                  k3 = 0;
                  g.drawLine(l5, i6, j6, k6);
                  if (i4 > 0 || k4 > 0) {
                    if (i4 <= 0) {
                      temp_line_xs[0] = l5;
                      temp_line_ys[0] = i6;
                      k3 = 1;
                    }
                    if (i6 < k6) {
                      if (l5 < j6) {
                        if (i4 > 0) {
                          temp_line_xs[0] = l5;
                          temp_line_ys[0] = i6 + i4;
                          temp_line_xs[1] = l5;
                          temp_line_ys[1] = i6;
                          temp_line_xs[2] = l5 + i4;
                          temp_line_ys[2] = i6;
                          k3 = 3;
                        }
                        if (k4 > 0) {
                          temp_line_xs[k3] = j6 + k4;
                          temp_line_ys[k3] = k6;
                          temp_line_xs[k3 + 1] = j6 + k4;
                          temp_line_ys[k3 + 1] = k6 + k4;
                          temp_line_xs[k3 + 2] = j6;
                          temp_line_ys[k3 + 2] = k6 + k4;
                          k3 += 3;
                        }
                      }
                      else {
                        if (i4 > 0) {
                          temp_line_xs[0] = l5;
                          temp_line_ys[0] = i6;
                          temp_line_xs[1] = l5 + i4;
                          temp_line_ys[1] = i6;
                          temp_line_xs[2] = l5 + i4;
                          temp_line_ys[2] = i6 + i4;
                          k3 = 3;
                        }
                        if (k4 > 0) {
                          temp_line_xs[k3] = j6 + k4;
                          temp_line_ys[k3] = k6 + k4;
                          temp_line_xs[k3 + 1] = j6;
                          temp_line_ys[k3 + 1] = k6 + k4;
                          temp_line_xs[k3 + 2] = j6;
                          temp_line_ys[k3 + 2] = k6;
                          k3 += 3;
                        }
                      }
                    }
                    else
                    if (l5 > j6) {
                      if (i4 > 0) {
                        temp_line_xs[0] = l5 + i4;
                        temp_line_ys[0] = i6;
                        temp_line_xs[1] = l5 + i4;
                        temp_line_ys[1] = i6 + i4;
                        temp_line_xs[2] = l5;
                        temp_line_ys[2] = i6 + i4;
                        k3 = 3;
                      }
                      if (k4 > 0) {
                        temp_line_xs[k3] = j6;
                        temp_line_ys[k3] = k6 + k4;
                        temp_line_xs[k3 + 1] = j6;
                        temp_line_ys[k3 + 1] = k6;
                        temp_line_xs[k3 + 2] = j6 + k4;
                        temp_line_ys[k3 + 2] = k6;
                        k3 += 3;
                      }
                    }
                    else {
                      if (i4 > 0) {
                        temp_line_xs[0] = l5 + i4;
                        temp_line_ys[0] = i6 + i4;
                        temp_line_xs[1] = l5;
                        temp_line_ys[1] = i6 + i4;
                        temp_line_xs[2] = l5;
                        temp_line_ys[2] = i6;
                        k3 = 3;
                      }
                      if (k4 > 0) {
                        temp_line_xs[k3] = j6;
                        temp_line_ys[k3] = k6;
                        temp_line_xs[k3 + 1] = j6 + k4;
                        temp_line_ys[k3 + 1] = k6;
                        temp_line_xs[k3 + 2] = j6 + k4;
                        temp_line_ys[k3 + 2] = k6 + k4;
                        k3 += 3;
                      }
                    }
                    if (k4 <= 0) {
                      temp_line_xs[k3] = j6;
                      temp_line_ys[k3] = k6;
                      k3++;
                    }
                    g.fillPolygon(temp_line_xs, temp_line_ys, k3);
                  }
                  i4 = k4;
                  int j5 = k5;
                  l5 = j6;
                  i6 = k6;
                }

              }
            }
          }
        }
      }
    }

  }

  static int count_accuracy_bits = 9;
  static int max_coordinate = 4096;
  public Color point_edge_color;
  public int full_pixel_width;
  public int full_pixel_height;
  public int pixel_width;
  public int pixel_height;
  public boolean is_stereo;
  public double stereo_distance;
  public int stereo_offset;
  public int pixel_stereo_offset;
  public double length_view_point;
  public double initial_length_view_point;
  public double magnification;
  public double initial_magnification;
  public int width;
  public int height;
  public int eye_distance;
  public Quaternion rotation;
  public Quaternion initial_rotation;
  public double original_sizes[];
  public double original_center[];
  public int old_pixel_width;
  public int old_pixel_height;
  public int old_width;
  public int old_height;
  public boolean old_is_stereo;
  public double old_stereo_distance;
  public int old_eye_distance;
  public Quaternion old_rotation;
  public Color option_AmbientLight;
  public boolean option_Axes[] = {
      false, false, false};
  public Primitive3D option_AxesStyle[];
  public Primitive3D option_AxesLabel[];
  public int option_AxesEdge[] = {
      -1, -1, -1
  };
  public Vector option_Ticks[];
  public double ticks_max_in_length[] = {
      0.0D, 0.0D, 0.0D
  };
  public double ticks_max_out_length[] = {
      0.0D, 0.0D, 0.0D
  };
  public Color option_Background;
  public boolean option_Boxed;
  public double option_BoxRatios[];
  public Primitive3D option_BoxStyle;
  public Color option_DefaultColor;
  public boolean option_Lighting;
  public Vector option_LightSources_vectors;
  public Vector option_LightSources_colors;
  public double option_PlotRange[][];
  public double option_ViewPoint[] = {
      1.3D, -2.3999999999999999D, 2D
  };
  public double option_ViewVertical[] = {
      0.0D, 0.0D, 1.0D
  };
  public String option_TextStyle_font_url;
  public String option_TextStyle_font_family;
  public int option_TextStyle_font_weight;
  public int option_TextStyle_font_slant;
  public int option_TextStyle_font_size;
  public Color option_TextStyle_font_color;
  public Color option_TextStyle_font_background;
  int max_count_points;
  int count_points;
  int xs[];
  int ys[];
  int zs[];
  public int left_pixel_xs[];
  public int right_pixel_xs[];
  public int pixel_ys[];
  int point_scale[];
  int temp_xs[];
  int temp_ys[];
  int temp_line_xs[];
  int temp_line_ys[];
  int count_primitives;
  int min_primitive_index;
  int max_primitive_index;
  Vector primitives;
  int max_primitive_count_points;
  double max_original_coordinates[];
  double min_original_coordinates[];
  int order[];
  int rotated_center_zs[];

}
