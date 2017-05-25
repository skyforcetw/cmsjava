package livegraphics3d;

// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 2009/3/2 ¤U¤È 11:23:49
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Live.java

import java.awt.Color;

class Primitive3D {

  public Primitive3D() {
    count_points = count_points;
    original_points = null;
    original_scaled_offsets = null;
    text = null;
    font_url = null;
    font = null;
    font_weight = -1;
    font_slant = -1;
    font_size = -1;
    standard_color = null;
    edge_color = null;
    front_face_color = null;
    back_face_color = null;
    front_diffuse_color = new Color(1.0F, 1.0F, 1.0F);
    back_diffuse_color = new Color(1.0F, 1.0F, 1.0F);
    front_specular_color = new Color(0.0F, 0.0F, 0.0F);
    back_specular_color = new Color(0.0F, 0.0F, 0.0F);
    front_specular_exponent = 1.0D;
    back_specular_exponent = 1.0D;
    original_point_size = 0.01D;
    is_absolute_point_size = false;
    original_thickness = 0.5D;
    is_absolute_thickness = true;
    original_edge_thickness = 0.5D;
    is_absolute_edge_thickness = true;
    is_filled = false;
    is_outlined = true;
    first_point = 0;
    second_point = 0;
    third_point = 0;
    fourth_point = 0;
    front_color = Color.black;
    back_color = Color.black;
    points = null;
    point_diameter = 0;
    last_z_coordinate = 0;
  }

  public Primitive3D(Primitive3D primitive3d) {
    count_points = primitive3d.count_points;
    original_points = primitive3d.original_points;
    original_scaled_offsets = primitive3d.original_scaled_offsets;
    text = primitive3d.text;
    font_url = primitive3d.font_url;
    font = primitive3d.font;
    font_weight = primitive3d.font_weight;
    font_slant = primitive3d.font_slant;
    font_size = primitive3d.font_size;
    standard_color = primitive3d.standard_color;
    edge_color = primitive3d.edge_color;
    front_face_color = primitive3d.front_face_color;
    back_face_color = primitive3d.back_face_color;
    front_diffuse_color = primitive3d.front_diffuse_color;
    back_diffuse_color = primitive3d.back_diffuse_color;
    front_specular_color = primitive3d.front_specular_color;
    back_specular_color = primitive3d.back_specular_color;
    front_specular_exponent = primitive3d.front_specular_exponent;
    back_specular_exponent = primitive3d.back_specular_exponent;
    original_point_size = primitive3d.original_point_size;
    is_absolute_point_size = primitive3d.is_absolute_point_size;
    original_thickness = primitive3d.original_thickness;
    is_absolute_thickness = primitive3d.is_absolute_thickness;
    original_edge_thickness = primitive3d.original_edge_thickness;
    is_absolute_edge_thickness = primitive3d.is_absolute_edge_thickness;
    is_filled = primitive3d.is_filled;
    is_outlined = primitive3d.is_outlined;
    first_point = primitive3d.first_point;
    second_point = primitive3d.second_point;
    third_point = primitive3d.third_point;
    fourth_point = primitive3d.fourth_point;
    front_color = primitive3d.front_color;
    back_color = primitive3d.back_color;
    points = primitive3d.points;
    point_diameter = primitive3d.point_diameter;
  }

  public int count_points;
  public double original_points[][];
  public double original_scaled_offsets[][];
  public String text;
  public String font_url;
  public Object font;
  public int font_weight;
  public int font_slant;
  public int font_size;
  public int font_y_offset;
  public Color standard_color;
  public Color edge_color;
  public Color front_face_color;
  public Color back_face_color;
  public Color front_diffuse_color;
  public Color back_diffuse_color;
  public Color front_specular_color;
  public Color back_specular_color;
  public double front_specular_exponent;
  public double back_specular_exponent;
  public int first_point;
  public int second_point;
  public int third_point;
  public int fourth_point;
  public double original_point_size;
  public boolean is_absolute_point_size;
  public double original_thickness;
  public boolean is_absolute_thickness;
  public double original_edge_thickness;
  public boolean is_absolute_edge_thickness;
  public boolean is_filled;
  public boolean is_outlined;
  public Color front_color;
  public Color back_color;
  public int points[];
  public int point_diameter;
  public int last_z_coordinate;
}
