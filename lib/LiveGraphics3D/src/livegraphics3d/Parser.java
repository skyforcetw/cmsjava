package livegraphics3d;

// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 2009/3/2 ¤U¤È 11:23:49
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Live.java

import java.util.Vector;

import java.awt.Color;

class Parser {

  public Parser(String s) {
    scanned_identifier_chars = new char[max_length_identifier];
    scanned_AnimationDisplayTime = 0.050000000000000003D;
    scanned_AnimationDirection = 1;
    scanning_animation_option = false;
    scanned_numbers = new double[max_count_scanned_numbers];
    text = s;
  }

  Color copied_color(Color color) {
    if (color == null) {
      return null;
    }
    else {
      return new Color(color.getRed(), color.getGreen(), color.getBlue());
    }
  }

  char next_char() {
    boolean flag = false;
    last_char = current_char;
    last_text_index = text_index;
    text_index = text_index + 1;
    if (text_index < 0 || text_index >= text.length()) {
      current_char = '\f';
      return current_char;
    }
    while (!flag) {
      current_char = text.charAt(text_index);
      if (current_char == '\\' && text_index + 1 < text.length()) {
        text_index = text_index + 1;
        current_char = text.charAt(text_index);
        if (current_char == '[') {
          text_index = text_index + 1;
          int i = text_index;
          for (;
               text_index < text.length() &&
               (Character.isLetterOrDigit(text.charAt(text_index)) ||
                breaking_chars.indexOf(text.charAt(text_index)) >= 0);
               text_index = text_index + 1) {
            ;
          }
          if (text_index >= text.length() || text.charAt(text_index) != ']') {
            text_index = last_text_index + 1;
            current_char = '\\';
            flag = true;
          }
          else {
            String s = text.substring(i, text_index);
            int j = -1;
            int l = 0;
            while (j != s.length()) {
              j = s.length();
              int k;
              for (int j1 = 0; (k = standard_parts[j1].length()) > 0; j1++) {
                if (!s.regionMatches(l, standard_parts[j1], 0, k)) {
                  continue;
                }
                s = s.substring(0, l) + standard_short_parts[j1] +
                    s.substring(l + k);
                l++;
                break;
              }

            }
            int k1 = -1;
            if (s.equals("n")) {
              k1 = 172;
            }
            else
            if (s.length() >= 2) {
              int i1 = 0;
              String s1 = s.substring(0, 2);
              while (i1 >= 0) {
                i1 = coded_char_names.indexOf(s1, i1 + 1);
                if (i1 >= 0 && Character.isDigit(coded_char_names.charAt(i1 - 1))) {
                  int l1;
                  for (l1 = i1 + 2;
                       !Character.isDigit(coded_char_names.charAt(l1)); l1++) {
                    ;
                  }
                  int i2;
                  for (i2 = l1 + 1;
                       Character.isDigit(coded_char_names.charAt(i2)); i2++) {
                    ;
                  }
                  if (s.regionMatches(0, coded_char_names, i1, l1 - i1)) {
                    i1 = -1;
                    k1 = Integer.parseInt(coded_char_names.substring(l1, i2));
                  }
                }
              }
            }
            if (k1 >= 0) {
              byte abyte0[] = {
                  (byte) (k1 % 256)
              };
              current_char = (new String(abyte0, k1 / 256)).charAt(0);
            }
            else {
              current_char = '\277';
            }
            flag = true;
          }
        }
        else
        if (white_characters.indexOf(current_char) >= 0) {
          for (text_index = text_index + 1;
               text_index < text.length() &&
               white_characters.indexOf(text.charAt(text_index)) >= 0;
               text_index = text_index + 1) {
            ;
          }
          if (text_index >= text.length()) {
            text_index = text.length() - 1;
            current_char = ' ';
            flag = true;
          }
          else {
            flag = false;
          }
        }
        else {
          current_char = '\\';
          text_index = text_index - 1;
          flag = true;
        }
      }
      else
      if (single_quote_chars.indexOf(current_char) >= 0 &&
          text_index + 1 < text.length() &&
          single_quote_chars.indexOf(text.charAt(text_index + 1)) >= 0) {
        current_char = '"';
        text_index = text_index + 1;
        flag = true;
      }
      else
      if (current_char == '(' && text_index + 1 < text.length() &&
          text.charAt(text_index + 1) == '*') {
        text_index = text.indexOf(comment_end, text_index + 1);
        if (text_index == -1) {
          text_index = text.length() - 1;
        }
        else {
          text_index = text_index + 1;
        }
        current_char = ' ';
        flag = true;
      }
      else {
        flag = true;
      }
    }
    return current_char;
  }

  void unget_char() {
    current_char = last_char;
    text_index = last_text_index;
  }

  char first_non_white_char() {
    for (;
         text_index < text.length() && white_characters.indexOf(current_char) >= 0;
         next_char()) {
      ;
    }
    return current_char;
  }

  void scan_real() {
    int i = 1;
    long l = 0L;
    long l1 = 0L;
    int j = 0;
    is_scanned_real = false;
    if (current_char == '-') {
      i = -1;
      next_char();
    }
    else
    if (current_char == '+') {
      next_char();
    }
    int k = digit_characters.indexOf(current_char);
    if (k >= 0) {
      is_scanned_real = true;
    }
    for (; k >= 0; k = digit_characters.indexOf(next_char())) {
      l = 10L * l + (long) k;
    }

    if (current_char == '.') {
      int i1 = digit_characters.indexOf(next_char());
      if (i1 >= 0) {
        is_scanned_real = true;
      }
      for (; i1 >= 0; i1 = digit_characters.indexOf(next_char())) {
        l1 = 10L * l1 + (long) i1;
        j++;
      }

    }
    if (is_scanned_real) {
      if (j == 0) {
        scanned_real = (double) i * (double) l;
        return;
      }
      scanned_real = (double) i *
          ( (double) l + Math.pow(0.10000000000000001D, j) * (double) l1);
    }
  }

  void scan_token() {
    if (text_index < 0) {
      text_index = -1;
      next_char();
    }
    first_non_white_char();
    for (; text_index < text.length(); scan_salt()) {
      if (number_start.indexOf(current_char) >= 0) {
        double d = 0.0D;
        double d2 = 0.0D;
        double d3 = 1.0D;
        boolean flag = false;
        if (current_char == '-') {
          d3 = -1D;
          next_char();
          if (current_char == '>') {
            scanned_token_type = TOKEN_RIGHT_ARROW;
            next_char();
            return;
          }
          if (current_char == '(') {
            flag = true;
            next_char();
          }
        }
        scan_real();
        if (is_scanned_real) {
          double d1 = scanned_real;
          if (current_char == '`') {
            next_char();
            if (current_char == '`') {
              next_char();
            }
            scan_real();
          }
          boolean flag1 = false;
          boolean flag2 = false;
          if (current_char == '*') {
            flag1 = true;
            if (next_char() == '1') {
              if (next_char() == '0' && next_char() == '^') {
                flag2 = true;
              }
            }
            else
            if (current_char == '^') {
              flag2 = true;
            }
            if (flag2) {
              next_char();
              scan_real();
              if (is_scanned_real) {
                d2 = scanned_real;
              }
              else {
                flag2 = false;
              }
            }
          }
          if (!flag || current_char == ')') {
            if (flag) {
              next_char();
            }
            if (flag2 || !flag1) {
              scanned_number = d3 * d1 * Math.pow(10D, d2);
              scanned_token_type = TOKEN_NUMBER;
              return;
            }
          }
        }
      }
      else {
        if (Character.isLetterOrDigit(current_char) || current_char == '$') {
          scanned_identifier_chars[0] = current_char;
          int i = 1;
          while (Character.isLetterOrDigit(next_char()) || current_char == '$') {
            if (i < max_length_identifier) {
              scanned_identifier_chars[i] = current_char;
              i++;
            }
          }
          scanned_identifier = new String(scanned_identifier_chars, 0, i);
          scanned_token_type = TOKEN_IDENTIFIER;
          return;
        }
        if (structure_characters.indexOf(current_char) >= 0) {
          scanned_token_type = structure_characters.indexOf(current_char);
          next_char();
          return;
        }
        if (current_char == ':') {
          if (next_char() == '>') {
            scanned_token_type = TOKEN_RIGHT_ARROW;
            next_char();
            return;
          }
        }
        else
        if (current_char == '"') {
          StringBuffer stringbuffer = new StringBuffer(60);
          next_char();
          for (; text_index < text.length() && current_char != '"'; next_char()) {
            if (current_char == '\\') {
              next_char();
              if (current_char == 'n') {
                stringbuffer.append('\n');
              }
              else
              if (current_char == 'b') {
                stringbuffer.append('\b');
              }
              else
              if (current_char == 't') {
                stringbuffer.append('\t');
              }
              else
              if (current_char == 'r') {
                stringbuffer.append('\r');
              }
              else
              if (current_char == 'f') {
                stringbuffer.append('\f');
              }
              else
              if (single_quote_chars.indexOf(current_char) >= 0 ||
                  current_char == '"' || current_char == '\\') {
                stringbuffer.append(current_char);
                current_char = '\\';
              }
              else
              if (octal_digits.indexOf(current_char) >= 0 ||
                  current_char == '.' || current_char == ':') {
                byte byte0 = 3;
                int l = 0;
                if (current_char == '.') {
                  byte0 = 2;
                  next_char();
                }
                if (current_char == ':') {
                  byte0 = 4;
                  next_char();
                }
                int k;
                for (k = 0; k < byte0; k++) {
                  int j = hexadecimal_digits.indexOf(current_char);
                  if (j < 0) {
                    break;
                  }
                  if (j >= 16) {
                    j -= 6;
                  }
                  if (byte0 == 3) {
                    l = l * 8 + j;
                  }
                  else {
                    l = l * 16 + j;
                  }
                  if (k + 1 < byte0) {
                    next_char();
                  }
                }

                byte abyte0[] = {
                    (byte) (l % 256)
                };
                stringbuffer.append(new String(abyte0, l / 256));
                if (k < byte0) {
                  stringbuffer.append(current_char);
                }
              }
              else {
                stringbuffer.append('\\');
                stringbuffer.append(current_char);
              }
            }
            else
            if (current_char != '\n' && current_char != '\r' &&
                current_char != '\t' && current_char != '\f') {
              stringbuffer.append(current_char);
            }
          }

          if (text_index < text.length()) {
            scanned_token_type = TOKEN_STRING;
            scanned_string = new String(stringbuffer);
            next_char();
            return;
          }
        }
      }
    }

    scanned_token_type = TOKEN_NONE;
  }

  void scan_salt() {
    int i = 0;
    for (; text_index < text.length(); next_char()) {
      if (current_char == '"') {
        next_char();
        for (; text_index < text.length() && current_char != '"'; next_char()) {
          if (current_char == '\\') {
            next_char();
          }
        }

      }
      else
      if (structure_characters.indexOf(current_char) >= 0) {
        if (current_char == '(' || current_char == '{' || current_char == '[') {
          i++;
        }
        else
        if (current_char == ')' || current_char == '}' || current_char == ']') {
          i--;
          if (i < 0) {
            return;
          }
        }
        else
        if (current_char == ',' && i == 0) {
          return;
        }
      }
    }

  }

  void scan_right_bracket() {
    scan_salt();
    for (; text_index < text.length() && current_char == ','; scan_salt()) {
      next_char();
    }

    scan_token();
    if (TOKEN_RIGHT_BRACKET != scanned_token_type &&
        TOKEN_RIGHT_PARENTHESIS != scanned_token_type &&
        TOKEN_RIGHT_BRACE != scanned_token_type) {
      scanned_token_type = TOKEN_NONE;
    }
  }

  public boolean scan_animation() {
    text_index = -1;
    scanned_AnimationDisplayTime = 0.050000000000000003D;
    scanned_AnimationDirection = 1;
    scanned_frames = new Vector();
    scanning_animation_option = false;
    scan_token();
    if (TOKEN_IDENTIFIER != scanned_token_type) {
      return false;
    }
    if (scanned_identifier.equals(name_Graphics3D)) {
      if (!scan_Graphics3D()) {
        return false;
      }
      else {
        scanned_frames.addElement(graphics);
        return true;
      }
    }
    if (scanned_identifier.equals(name_ShowAnimation)) {
      scan_token();
      if (TOKEN_LEFT_BRACKET != scanned_token_type) {
        return false;
      }
      scan_token();
      if (TOKEN_IDENTIFIER == scanned_token_type &&
          scanned_identifier.equals(name_Graphics3D)) {
        if (!scan_Graphics3D()) {
          return false;
        }
        scanned_frames.addElement(graphics);
      }
      else
      if (TOKEN_LEFT_BRACE == scanned_token_type) {
        for (scanned_token_type = TOKEN_COMMA;
             TOKEN_COMMA == scanned_token_type; ) {
          scan_token();
          if (!scan_Graphics3D()) {
            return false;
          }
          scanned_frames.addElement(graphics);
          scan_token();
        }

        if (TOKEN_RIGHT_BRACE != scanned_token_type) {
          return false;
        }
      }
      else {
        return false;
      }
      scan_token();
      scanning_animation_option = true;
      init_scan_Graphics3D();
      while (TOKEN_COMMA == scanned_token_type) {
        if (!scan_option()) {
          return false;
        }
        scan_token();
      }
      if (TOKEN_RIGHT_BRACKET != scanned_token_type) {
        return false;
      }
      for (int i = 0; i < scanned_frames.size(); i++) {
        Graphics3D graphics3d = (Graphics3D) scanned_frames.elementAt(i);
        if (is_scanned_AmbientLight) {
          graphics3d.option_AmbientLight = graphics.option_AmbientLight;
        }
        if (is_scanned_Axes) {
          graphics3d.option_Axes = graphics.option_Axes;
        }
        if (is_scanned_AxesLabel) {
          graphics3d.option_AxesLabel = graphics.option_AxesLabel;
        }
        if (is_scanned_AxesStyle) {
          graphics3d.option_AxesStyle = graphics.option_AxesStyle;
        }
        if (is_scanned_AxesEdge) {
          graphics3d.option_AxesEdge = graphics.option_AxesEdge;
        }
        if (is_scanned_Ticks) {
          graphics3d.option_Ticks = graphics.option_Ticks;
        }
        if (is_scanned_Background) {
          graphics3d.option_Background = graphics.option_Background;
        }
        if (is_scanned_DefaultColor) {
          graphics3d.option_DefaultColor = graphics.option_DefaultColor;
        }
        if (is_scanned_BoxStyle) {
          graphics3d.option_BoxStyle = graphics.option_BoxStyle;
        }
        if (is_scanned_Boxed) {
          graphics3d.option_Boxed = graphics.option_Boxed;
        }
        if (is_scanned_Lighting) {
          graphics3d.option_Lighting = graphics.option_Lighting;
        }
        if (is_scanned_BoxRatios) {
          graphics3d.option_BoxRatios = graphics.option_BoxRatios;
        }
        if (is_scanned_PlotRange) {
          graphics3d.option_PlotRange = graphics.option_PlotRange;
        }
        if (is_scanned_LightSources) {
          graphics3d.option_LightSources_vectors = graphics.
              option_LightSources_vectors;
          graphics3d.option_LightSources_colors = graphics.
              option_LightSources_colors;
        }
        if (is_scanned_ViewPoint) {
          graphics3d.option_ViewPoint = graphics.option_ViewPoint;
        }
        if (is_scanned_ViewVertical) {
          graphics3d.option_ViewVertical = graphics.option_ViewVertical;
        }
        if (is_scanned_TextStyle) {
          graphics3d.option_TextStyle_font_url = graphics.
              option_TextStyle_font_url;
          graphics3d.option_TextStyle_font_family = graphics.
              option_TextStyle_font_family;
          graphics3d.option_TextStyle_font_weight = graphics.
              option_TextStyle_font_weight;
          graphics3d.option_TextStyle_font_slant = graphics.
              option_TextStyle_font_slant;
          graphics3d.option_TextStyle_font_size = graphics.
              option_TextStyle_font_size;
          graphics3d.option_TextStyle_font_color = graphics.
              option_TextStyle_font_color;
          graphics3d.option_TextStyle_font_background = graphics.
              option_TextStyle_font_background;
        }
      }

      return true;
    }
    else {
      return false;
    }
  }

  public void init_scan_Graphics3D() {
    graphics = new Graphics3D();
    scanning_EdgeForm = false;
    scanning_FaceForm = false;
    scanning_SurfaceColor = false;
    is_scanned_AmbientLight = false;
    is_scanned_Axes = false;
    is_scanned_AxesLabel = false;
    is_scanned_AxesStyle = false;
    is_scanned_AxesEdge = false;
    is_scanned_Ticks = false;
    is_scanned_Background = false;
    is_scanned_DefaultColor = false;
    is_scanned_BoxStyle = false;
    is_scanned_Boxed = false;
    is_scanned_Lighting = false;
    is_scanned_BoxRatios = false;
    is_scanned_PlotRange = false;
    is_scanned_LightSources = false;
    is_scanned_ViewPoint = false;
    is_scanned_ViewVertical = false;
    is_scanned_TextStyle = false;
  }

  public boolean scan_Graphics3D() {
    init_scan_Graphics3D();
    Primitive3D primitive3d = new Primitive3D();
    if (TOKEN_IDENTIFIER != scanned_token_type ||
        !scanned_identifier.equals(name_Graphics3D)) {
      return false;
    }
    scan_token();
    if (TOKEN_LEFT_BRACKET != scanned_token_type) {
      return false;
    }
    if (!scan_primitive(primitive3d)) {
      return false;
    }
    scan_token();
    while (TOKEN_COMMA == scanned_token_type) {
      if (!scan_option()) {
        return false;
      }
      scan_token();
    }
    return TOKEN_RIGHT_BRACKET == scanned_token_type;
  }

  boolean scan_primitive(Primitive3D primitive3d) {
    scanned_nothing = false;
    scanned_unidentified = false;
    scan_token();
    if (TOKEN_LEFT_BRACE == scanned_token_type) {
      if (!scanning_EdgeForm && !scanning_FaceForm ||
          scanning_AxesStyles && recursion_depth == 1) {
        primitive3d = new Primitive3D(primitive3d);
      }
      recursion_depth = recursion_depth + 1;
      while (scan_primitive(primitive3d)) {
        scan_token();
        if (TOKEN_COMMA != scanned_token_type) {
          break;
        }
      }
      recursion_depth = recursion_depth - 1;
      if (TOKEN_RIGHT_BRACE != scanned_token_type) {
        return false;
      }
      if (scanning_AxesStyles && recursion_depth == 1) {
        scanned_AxesStyles[0] = scanned_AxesStyles[1];
        scanned_AxesStyles[1] = scanned_AxesStyles[2];
        scanned_AxesStyles[2] = primitive3d;
      }
      return true;
    }
    if (TOKEN_IDENTIFIER != scanned_token_type) {
      scanned_nothing = true;
      return false;
    }
    if (!scanning_EdgeForm && !scanning_FaceForm && !scanning_SurfaceColor &&
        scanned_identifier.equals(name_EdgeForm)) {
      scan_token();
      if (TOKEN_LEFT_BRACKET != scanned_token_type) {
        return false;
      }
      scanning_EdgeForm = true;
      primitive3d.is_outlined = false;
      if (scan_primitive(primitive3d)) {
        scanning_EdgeForm = false;
        scan_token();
        return TOKEN_RIGHT_BRACKET == scanned_token_type;
      }
      if (scanned_nothing && TOKEN_RIGHT_BRACKET == scanned_token_type) {
        scanning_EdgeForm = false;
        return true;
      }
    }
    else {
      if (!scanning_EdgeForm && !scanning_FaceForm && !scanning_SurfaceColor &&
          scanned_identifier.equals(name_FaceForm)) {
        scan_token();
        if (TOKEN_LEFT_BRACKET != scanned_token_type) {
          return false;
        }
        scanning_FaceForm = true;
        scanning_FaceForm_back = false;
        if (!scan_primitive(primitive3d)) {
          scanning_FaceForm = false;
          return false;
        }
        scan_token();
        if (TOKEN_COMMA == scanned_token_type) {
          scanning_FaceForm_back = true;
          if (!scan_primitive(primitive3d)) {
            scanning_FaceForm = false;
            scanning_FaceForm_back = false;
            return false;
          }
          scanning_FaceForm_back = false;
          scan_token();
        }
        scanning_FaceForm = false;
        return TOKEN_RIGHT_BRACKET == scanned_token_type;
      }
      if (!scanning_EdgeForm && !scanning_SurfaceColor &&
          scanned_identifier.equals(name_SurfaceColor)) {
        scan_token();
        if (TOKEN_LEFT_BRACKET != scanned_token_type) {
          return false;
        }
        scanning_SurfaceColor = true;
        scanning_SurfaceColor_specular = false;
        if (!scan_primitive(primitive3d)) {
          scanning_SurfaceColor = false;
          return false;
        }
        scan_token();
        if (TOKEN_COMMA == scanned_token_type) {
          scanning_SurfaceColor_specular = true;
          if (!scan_primitive(primitive3d)) {
            scanning_SurfaceColor = false;
            scanning_SurfaceColor_specular = false;
            return false;
          }
          scanning_SurfaceColor = false;
          scanning_SurfaceColor_specular = false;
          scan_token();
          if (TOKEN_COMMA == scanned_token_type) {
            scan_token();
            if (TOKEN_NUMBER != scanned_token_type) {
              return false;
            }
            if (!scanning_FaceForm_back) {
              primitive3d.front_specular_exponent = scanned_number;
            }
            primitive3d.back_specular_exponent = scanned_number;
            scan_token();
          }
        }
        scanning_SurfaceColor = false;
        return TOKEN_RIGHT_BRACKET == scanned_token_type;
      }
    }
    if (!scan_color()) {
      return false;
    }
    if (scanned_color != null) {
      if (scanning_SurfaceColor && !scanning_SurfaceColor_specular) {
        if (!scanning_FaceForm_back) {
          primitive3d.front_diffuse_color = scanned_color;
        }
        primitive3d.back_diffuse_color = scanned_color;
      }
      else
      if (scanning_SurfaceColor && scanning_SurfaceColor_specular) {
        if (!scanning_FaceForm_back) {
          primitive3d.front_specular_color = scanned_color;
        }
        primitive3d.back_specular_color = scanned_color;
      }
      else
      if (scanning_FaceForm) {
        if (!scanning_FaceForm_back) {
          primitive3d.front_face_color = scanned_color;
        }
        primitive3d.back_face_color = scanned_color;
      }
      else
      if (scanning_EdgeForm) {
        primitive3d.edge_color = scanned_color;
        primitive3d.is_outlined = true;
      }
      else {
        primitive3d.standard_color = scanned_color;
        primitive3d.front_face_color = scanned_color;
        primitive3d.back_face_color = scanned_color;
      }
      return true;
    }
    if (!scanning_SurfaceColor && !scanning_EdgeForm && !scanning_FaceForm) {
      if (scanned_identifier.equals(name_PointSize)) {
        if (!scan_numbers(false, false) || count_scanned_numbers != 1) {
          return false;
        }
        else {
          primitive3d.original_point_size = scanned_numbers[0];
          primitive3d.is_absolute_point_size = false;
          return true;
        }
      }
      if (scanned_identifier.equals(name_AbsolutePointSize)) {
        if (!scan_numbers(false, false) || count_scanned_numbers != 1) {
          return false;
        }
        else {
          primitive3d.original_point_size = scanned_numbers[0];
          primitive3d.is_absolute_point_size = true;
          return true;
        }
      }
    }
    if (!scanning_SurfaceColor && !scanning_FaceForm) {
      if (scanned_identifier.equals(name_Thickness)) {
        if (!scan_numbers(false, false) || count_scanned_numbers != 1) {
          return false;
        }
        if (scanning_EdgeForm) {
          primitive3d.is_outlined = true;
          primitive3d.original_edge_thickness = scanned_numbers[0];
          primitive3d.is_absolute_edge_thickness = false;
        }
        else {
          primitive3d.original_thickness = scanned_numbers[0];
          primitive3d.is_absolute_thickness = false;
        }
        return true;
      }
      if (scanned_identifier.equals(name_AbsoluteThickness)) {
        if (!scan_numbers(false, false) || count_scanned_numbers != 1) {
          return false;
        }
        if (scanning_EdgeForm) {
          primitive3d.is_outlined = true;
          primitive3d.original_edge_thickness = scanned_numbers[0];
          primitive3d.is_absolute_edge_thickness = true;
        }
        else {
          primitive3d.original_thickness = scanned_numbers[0];
          primitive3d.is_absolute_thickness = true;
        }
        return true;
      }
    }
    if (!scanning_EdgeForm && !scanning_FaceForm && !scanning_SurfaceColor) {
      if (scanned_identifier.equals(name_Point)) {
        scan_token();
        if (TOKEN_LEFT_BRACKET != scanned_token_type) {
          return false;
        }
        if (!scan_points() || count_scanned_points != 1) {
          return false;
        }
        scan_token();
        if (TOKEN_RIGHT_BRACKET != scanned_token_type) {
          return false;
        }
        else {
          Primitive3D primitive3d1 = new Primitive3D(primitive3d);
          primitive3d1.count_points = 1;
          primitive3d1.original_points = scanned_points;
          primitive3d1.original_scaled_offsets = scanned_scaled_offsets;
          primitive3d1.is_filled = false;
          primitive3d1.is_outlined = false;
          graphics.addPrimitive(primitive3d1);
          return true;
        }
      }
      if (scanned_identifier.equals(name_Line)) {
        scan_token();
        if (TOKEN_LEFT_BRACKET != scanned_token_type) {
          return false;
        }
        if (!scan_points()) {
          return false;
        }
        scan_token();
        if (TOKEN_RIGHT_BRACKET != scanned_token_type) {
          return false;
        }
        else {
          Primitive3D primitive3d2 = new Primitive3D(primitive3d);
          primitive3d2.count_points = count_scanned_points;
          primitive3d2.original_points = scanned_points;
          primitive3d2.original_scaled_offsets = scanned_scaled_offsets;
          primitive3d2.is_filled = false;
          primitive3d2.is_outlined = false;
          graphics.addPrimitive(primitive3d2);
          return true;
        }
      }
      if (scanned_identifier.equals(name_Polygon)) {
        scan_token();
        if (TOKEN_LEFT_BRACKET != scanned_token_type) {
          return false;
        }
        if (!scan_points()) {
          return false;
        }
        scan_right_bracket();
        if (TOKEN_RIGHT_BRACKET != scanned_token_type) {
          return false;
        }
        else {
          Primitive3D primitive3d3 = new Primitive3D(primitive3d);
          primitive3d3.count_points = count_scanned_points;
          primitive3d3.original_points = scanned_points;
          primitive3d3.original_scaled_offsets = scanned_scaled_offsets;
          primitive3d3.is_filled = true;
          graphics.addPrimitive(primitive3d3);
          return true;
        }
      }
      if (scanned_identifier.equals(name_Cuboid)) {
        int ai[][] = {
            {
            0, 1, 3, 2
        }, {
            0, 4, 5, 1
        }, {
            0, 2, 6, 4
        }, {
            7, 6, 4, 5
        }, {
            7, 3, 2, 6
        }, {
            7, 5, 1, 3
        }
        };
        scan_token();
        if (TOKEN_LEFT_BRACKET != scanned_token_type) {
          return false;
        }
        if (!scan_points() || count_scanned_points != 1) {
          return false;
        }
        double ad[] = scanned_points[0];
        double ad1[] = scanned_scaled_offsets[0];
        scan_token();
        double ad2[];
        double ad3[];
        if (TOKEN_RIGHT_BRACKET == scanned_token_type) {
          if (ad != null) {
            ad2 = new double[3];
            ad2[0] = ad[0] + 1.0D;
            ad2[1] = ad[1] + 1.0D;
            ad2[2] = ad[2] + 1.0D;
            ad3 = ad1;
          }
          else {
            ad2 = null;
            ad3 = null;
          }
        }
        else
        if (TOKEN_COMMA == scanned_token_type) {
          if (!scan_points() || count_scanned_points != 1) {
            return false;
          }
          scan_token();
          if (TOKEN_RIGHT_BRACKET != scanned_token_type) {
            return false;
          }
          ad2 = scanned_points[0];
          ad3 = scanned_scaled_offsets[0];
        }
        else {
          return false;
        }
        if (ad != null && ad2 != null || ad == null && ad2 == null && ad3 != null) {
          for (int i = 0; i < 6; i++) {
            Primitive3D primitive3d5 = new Primitive3D(primitive3d);
            primitive3d5.count_points = 4;
            primitive3d5.original_points = new double[4][3];
            primitive3d5.original_scaled_offsets = new double[4][3];
            primitive3d5.is_filled = true;
            for (int j = 0; j < 4; j++) {
              for (int k = 0; k < 3; k++) {
                if ( (ai[i][j] & 1 << k) != 0) {
                  if (ad2 != null) {
                    primitive3d5.original_points[j][k] = ad2[k];
                  }
                  else {
                    primitive3d5.original_points[j] = null;
                  }
                  primitive3d5.original_scaled_offsets[j][k] = ad3[k];
                }
                else {
                  if (ad != null) {
                    primitive3d5.original_points[j][k] = ad[k];
                  }
                  else {
                    primitive3d5.original_points[j] = null;
                  }
                  primitive3d5.original_scaled_offsets[j][k] = ad1[k];
                }
              }

            }

            graphics.addPrimitive(primitive3d5);
          }

        }
        return true;
      }
      if (scanned_identifier.equals(name_Text)) {
        Primitive3D primitive3d4 = new Primitive3D(primitive3d);
        scan_token();
        if (TOKEN_LEFT_BRACKET != scanned_token_type) {
          return false;
        }
        scan_token();
        if (!scan_text(primitive3d4, true)) {
          return false;
        }
        scan_token();
        if (TOKEN_COMMA != scanned_token_type) {
          return false;
        }
        if (!scan_points() || count_scanned_points != 1) {
          return false;
        }
        primitive3d4.count_points = 1;
        primitive3d4.original_points = scanned_points;
        primitive3d4.original_scaled_offsets = scanned_scaled_offsets;
        scan_token();
        double d = 0.0D;
        double d1 = 0.0D;
        if (TOKEN_COMMA == scanned_token_type) {
          if (!scan_numbers(false, true) || count_scanned_numbers != 2) {
            return false;
          }
          d = scanned_numbers[0];
          d1 = scanned_numbers[1];
          scan_right_bracket();
        }
        if (TOKEN_RIGHT_BRACKET != scanned_token_type) {
          return false;
        }
        else {
          primitive3d4.original_point_size = d;
          primitive3d4.original_thickness = d1;
          graphics.addPrimitive(primitive3d4);
          return true;
        }
      }
    }
    scan_salt();
    if (current_char == ',' || current_char == '}' || current_char == ']') {
      scanned_unidentified = true;
      return true;
    }
    else {
      return false;
    }
  }

  boolean scan_numbers(boolean flag, boolean flag1) {
    int i = 0;
    scan_token();
    if (!flag1 && TOKEN_LEFT_BRACKET != scanned_token_type ||
        flag1 && TOKEN_LEFT_BRACE != scanned_token_type) {
      return false;
    }
    while (i < max_count_scanned_numbers) {
      scan_token();
      if (TOKEN_NUMBER != scanned_token_type) {
        break;
      }
      scanned_numbers[i] = scanned_number;
      if (flag) {
        if (scanned_number > 1.0D) {
          scanned_numbers[i] = 1.0D;
        }
        else
        if (scanned_number < 0.0D) {
          scanned_numbers[i] = 0.0D;
        }
      }
      i++;
      scan_token();
      if (TOKEN_COMMA != scanned_token_type) {
        break;
      }
    }
    count_scanned_numbers = i;
    if ( (!flag1 && TOKEN_RIGHT_BRACKET != scanned_token_type ||
          flag1 && TOKEN_RIGHT_BRACE != scanned_token_type) &&
        i >= max_count_scanned_numbers) {
      scan_right_bracket();
    }
    return (flag1 || TOKEN_RIGHT_BRACKET == scanned_token_type) &&
        (!flag1 || TOKEN_RIGHT_BRACE == scanned_token_type);
  }

  boolean scan_points() {
    boolean flag = false;
    int i = 0;
    double ad[] = {
        0.0D, 0.0D, 0.0D
    };
    double ad1[] = {
        0.0D, 0.0D, 0.0D
    };
    max_count_scanned_points = 2;
    scanned_points = new double[max_count_scanned_points][3];
    scanned_scaled_offsets = new double[max_count_scanned_points][3];
    for (; i == 0 || flag && TOKEN_COMMA == scanned_token_type; i++) {
      scan_token();
      if (TOKEN_LEFT_BRACE == scanned_token_type) {
        scan_token();
        if (i == 0) {
          if (TOKEN_LEFT_BRACE == scanned_token_type) {
            flag = true;
            scan_token();
          }
          else
          if (TOKEN_NUMBER != scanned_token_type) {
            flag = true;
          }
        }
      }
      if (TOKEN_NUMBER == scanned_token_type) {
        int j;
        for (j = 0; j < 3 && TOKEN_NUMBER == scanned_token_type; j++) {
          ad[j] = scanned_number;
          scan_token();
          if (TOKEN_COMMA != scanned_token_type) {
            break;
          }
          scan_token();
        }

        if (j < 2 || TOKEN_RIGHT_BRACE != scanned_token_type) {
          return false;
        }
        add_point(ad, ad1, i);
      }
      else
      if (TOKEN_IDENTIFIER == scanned_token_type &&
          scanned_identifier.equals(name_Scaled)) {
        scan_token();
        if (TOKEN_LEFT_BRACKET != scanned_token_type) {
          return false;
        }
        if (!scan_numbers(false, true) || count_scanned_numbers != 3) {
          return false;
        }
        ad1[0] = scanned_numbers[0];
        ad1[1] = scanned_numbers[1];
        ad1[2] = scanned_numbers[2];
        scan_token();
        if (TOKEN_COMMA == scanned_token_type) {
          if (!scan_numbers(false, true) || count_scanned_numbers != 3) {
            return false;
          }
          ad[0] = scanned_numbers[0];
          ad[1] = scanned_numbers[1];
          ad[2] = scanned_numbers[2];
          scan_token();
          if (TOKEN_RIGHT_BRACKET != scanned_token_type) {
            return false;
          }
          add_point(ad, ad1, i);
        }
        else {
          if (TOKEN_RIGHT_BRACKET != scanned_token_type) {
            return false;
          }
          add_point(null, ad1, i);
        }
      }
      else {
        return false;
      }
      if (flag) {
        scan_token();
      }
    }

    if (TOKEN_RIGHT_BRACE != scanned_token_type &&
        TOKEN_RIGHT_BRACKET != scanned_token_type) {
      return false;
    }
    else {
      count_scanned_points = i;
      return true;
    }
  }

  void add_point(double ad[], double ad1[], int i) {
    if (i >= max_count_scanned_points) {
      int k1 = 2 * max_count_scanned_points;
      double ad2[][] = new double[k1][3];
      double ad3[][] = new double[k1][3];
      for (int j1 = 0; j1 < max_count_scanned_points; j1++) {
        for (int j = 0; j < 3; j++) {
          ad3[j1][j] = scanned_scaled_offsets[j1][j];
        }

        if (scanned_points[j1] != null) {
          for (int k = 0; k < 3; k++) {
            ad2[j1][k] = scanned_points[j1][k];
          }

        }
        else {
          ad2[j1] = scanned_points[j1];
        }
      }

      scanned_points = ad2;
      scanned_scaled_offsets = ad3;
      max_count_scanned_points = k1;
    }
    for (int l = 0; l < 3; l++) {
      scanned_scaled_offsets[i][l] = ad1[l];
    }

    if (ad != null) {
      for (int i1 = 0; i1 < 3; i1++) {
        scanned_points[i][i1] = ad[i1];
      }

      return;
    }
    else {
      scanned_points[i] = ad;
      return;
    }
  }

  boolean scan_color() {
    scanned_color = null;
    if (scanned_identifier.equals(name_RGBColor)) {
      if (!scan_numbers(true, false) || count_scanned_numbers != 3) {
        return false;
      }
      scanned_color = new Color( (float) scanned_numbers[0],
                                (float) scanned_numbers[1],
                                (float) scanned_numbers[2]);
    }
    else
    if (scanned_identifier.equals(name_Hue)) {
      if (!scan_numbers(false, false)) {
        return false;
      }
      if (count_scanned_numbers == 1) {
        scanned_numbers[1] = 1.0D;
        count_scanned_numbers = 2;
      }
      if (count_scanned_numbers == 2) {
        scanned_numbers[2] = 1.0D;
        count_scanned_numbers = 3;
      }
      if (count_scanned_numbers != 3) {
        return false;
      }
      scanned_numbers[0] = scanned_numbers[0] - Math.floor(scanned_numbers[0]);
      for (int i = 0; i < 3; i++) {
        if (scanned_numbers[i] > 0.999D) {
          scanned_numbers[i] = 0.999D;
        }
        if (scanned_numbers[i] < 0.0D) {
          scanned_numbers[i] = 0.0D;
        }
      }

      scanned_color = Color.getHSBColor( (float) scanned_numbers[0],
                                        (float) scanned_numbers[1],
                                        (float) scanned_numbers[2]);
    }
    else
    if (scanned_identifier.equals(name_GrayLevel)) {
      if (!scan_numbers(true, false) || count_scanned_numbers != 1) {
        return false;
      }
      scanned_color = new Color( (float) scanned_numbers[0],
                                (float) scanned_numbers[0],
                                (float) scanned_numbers[0]);
    }
    else
    if (scanned_identifier.equals(name_CMYKColor)) {
      if (!scan_numbers(true, false) || count_scanned_numbers != 4) {
        return false;
      }
      scanned_color = new Color( (float) Math.max(0.0D,
                                                  1.0D - scanned_numbers[0] -
                                                  scanned_numbers[3]),
                                (float)
                                Math.max(0.0D,
                                         1.0D - scanned_numbers[1] - scanned_numbers[3]),
                                (float)
                                Math.max(0.0D,
                                         1.0D - scanned_numbers[2] - scanned_numbers[3]));
    }
    return true;
  }

  public boolean scan_text(Primitive3D primitive3d, boolean flag) {
    primitive3d.text = name_Questionmark;
    primitive3d.front_face_color = null;
    primitive3d.back_face_color = null;
    primitive3d.is_filled = false;
    if (TOKEN_NUMBER == scanned_token_type) {
      primitive3d.text = String.valueOf(scanned_number);
    }
    else
    if (TOKEN_STRING == scanned_token_type) {
      primitive3d.text = scanned_string;
    }
    else
    if (TOKEN_IDENTIFIER == scanned_token_type) {
      if (!scanned_identifier.equals(name_StyleForm)) {
        primitive3d.text = scanned_identifier;
      }
      else {
        scan_token();
        if (TOKEN_LEFT_BRACKET != scanned_token_type) {
          return false;
        }
        scan_token();
        if (TOKEN_NUMBER == scanned_token_type) {
          primitive3d.text = String.valueOf(scanned_number);
        }
        else
        if (TOKEN_STRING == scanned_token_type) {
          primitive3d.text = scanned_string;
        }
        else
        if (TOKEN_IDENTIFIER == scanned_token_type) {
          primitive3d.text = scanned_identifier;
        }
        scan_salt();
        scan_token();
        if (TOKEN_RIGHT_BRACKET == scanned_token_type) {
          primitive3d.font_url = null;
          primitive3d.font = null;
          primitive3d.font_weight = -1;
          primitive3d.font_slant = -1;
          primitive3d.font_size = -1;
          primitive3d.front_face_color = null;
          primitive3d.back_face_color = null;
        }
        else {
          if (TOKEN_COMMA != scanned_token_type) {
            return false;
          }
          if (!scan_font_options() || TOKEN_RIGHT_BRACKET != scanned_token_type) {
            return false;
          }
          primitive3d.font_url = scanned_font_url;
          primitive3d.font = scanned_font_family;
          primitive3d.font_weight = scanned_font_weight;
          primitive3d.font_slant = scanned_font_slant;
          primitive3d.font_size = scanned_font_size;
          primitive3d.front_face_color = scanned_font_color;
          primitive3d.back_face_color = scanned_font_background;
        }
      }
    }
    if (flag) {
      scan_salt();
    }
    return true;
  }

  boolean scan_font_options() {
    scanned_font_url = null;
    scanned_font_family = null;
    scanned_font_weight = -1;
    scanned_font_slant = -1;
    scanned_font_size = -1;
    scanned_font_color = null;
    scanned_font_background = null;
    scan_token();
    while (TOKEN_IDENTIFIER == scanned_token_type) {
      if (scanned_identifier.equals(name_URL)) {
        scan_token();
        if (TOKEN_RIGHT_ARROW != scanned_token_type) {
          return false;
        }
        scan_token();
        if (TOKEN_STRING != scanned_token_type) {
          return false;
        }
        scanned_font_url = scanned_string;
        scan_token();
      }
      else
      if (scanned_identifier.equals(name_FontFamily)) {
        scan_token();
        if (TOKEN_RIGHT_ARROW != scanned_token_type) {
          return false;
        }
        scan_token();
        if (TOKEN_STRING != scanned_token_type) {
          return false;
        }
        scanned_font_family = scanned_string;
        scan_token();
      }
      else
      if (scanned_identifier.equals(name_FontWeight)) {
        scan_token();
        if (TOKEN_RIGHT_ARROW != scanned_token_type) {
          return false;
        }
        scan_token();
        if (TOKEN_STRING != scanned_token_type) {
          return false;
        }
        if (scanned_string.equals(name_Bold)) {
          scanned_font_weight = 1;
        }
        else {
          scanned_font_weight = 0;
        }
        scan_token();
      }
      else
      if (scanned_identifier.equals(name_FontSlant)) {
        scan_token();
        if (TOKEN_RIGHT_ARROW != scanned_token_type) {
          return false;
        }
        scan_token();
        if (TOKEN_STRING != scanned_token_type) {
          return false;
        }
        if (scanned_string.equals(name_Italic)) {
          scanned_font_slant = 2;
        }
        else {
          scanned_font_slant = 0;
        }
        scan_token();
      }
      else
      if (scanned_identifier.equals(name_FontSize)) {
        scan_token();
        if (TOKEN_RIGHT_ARROW != scanned_token_type) {
          return false;
        }
        scan_token();
        if (TOKEN_NUMBER != scanned_token_type) {
          return false;
        }
        scanned_font_size = (int) scanned_number;
        scan_token();
      }
      else
      if (scanned_identifier.equals(name_FontColor)) {
        scan_token();
        if (TOKEN_RIGHT_ARROW != scanned_token_type) {
          return false;
        }
        scan_token();
        if (TOKEN_IDENTIFIER != scanned_token_type) {
          return false;
        }
        if (!scan_color()) {
          return false;
        }
        scanned_font_color = scanned_color;
        scan_token();
      }
      else
      if (scanned_identifier.equals(name_Background)) {
        scan_token();
        if (TOKEN_RIGHT_ARROW != scanned_token_type) {
          return false;
        }
        scan_token();
        if (TOKEN_IDENTIFIER != scanned_token_type) {
          return false;
        }
        if (!scan_color()) {
          return false;
        }
        scanned_font_background = scanned_color;
        scan_token();
      }
      else {
        scan_salt();
        scan_token();
      }
      if (TOKEN_COMMA != scanned_token_type) {
        break;
      }
      scan_token();
    }
    return true;
  }

  boolean scan_option() {
    scanned_nothing = false;
    scanning_FaceForm_back = false;
    scan_token();
    if (TOKEN_LEFT_BRACE == scanned_token_type) {
      while (scan_option()) {
        scan_token();
        if (TOKEN_COMMA != scanned_token_type) {
          break;
        }
      }
      return TOKEN_RIGHT_BRACE == scanned_token_type;
    }
    if (TOKEN_IDENTIFIER != scanned_token_type) {
      scanned_nothing = true;
      return false;
    }
    if (!is_scanned_AmbientLight && scanned_identifier.equals(name_AmbientLight)) {
      is_scanned_AmbientLight = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      Primitive3D primitive3d = new Primitive3D();
      scanning_FaceForm = true;
      if (!scan_primitive(primitive3d)) {
        scanning_FaceForm = false;
        return false;
      }
      else {
        graphics.option_AmbientLight = primitive3d.front_face_color;
        scanning_FaceForm = false;
        return true;
      }
    }
    if (!is_scanned_Axes && scanned_identifier.equals(name_Axes)) {
      is_scanned_Axes = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      scan_token();
      if (TOKEN_IDENTIFIER == scanned_token_type) {
        if (scanned_identifier.equals(name_True)) {
          graphics.option_Axes[0] = true;
          graphics.option_Axes[1] = true;
          graphics.option_Axes[2] = true;
        }
        return true;
      }
      if (TOKEN_LEFT_BRACE == scanned_token_type) {
        int i = 0;
        for (scanned_token_type = TOKEN_COMMA;
             i < 3 && TOKEN_COMMA == scanned_token_type; ) {
          scan_token();
          if (TOKEN_IDENTIFIER == scanned_token_type) {
            if (scanned_identifier.equals(name_True)) {
              graphics.option_Axes[i] = true;
            }
            i++;
            scan_token();
          }
        }

        if (TOKEN_RIGHT_BRACE == scanned_token_type) {
          return true;
        }
      }
      return false;
    }
    if (!is_scanned_AxesLabel && scanned_identifier.equals(name_AxesLabel)) {
      is_scanned_AxesLabel = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      scan_token();
      int j = 2;
      boolean flag = false;
      if (TOKEN_LEFT_BRACE == scanned_token_type) {
        j = 0;
        flag = true;
        scan_token();
      }
      else
      if (TOKEN_IDENTIFIER == scanned_token_type &&
          scanned_identifier.equals(name_None)) {
        return true;
      }
      do {
        if (j > 0 && flag) {
          scan_token();
        }
        if (j > 2) {
          return false;
        }
        if (TOKEN_IDENTIFIER != scanned_token_type ||
            !scanned_identifier.equals(name_None)) {
          Primitive3D primitive3d6 = new Primitive3D();
          graphics.option_AxesLabel[j] = primitive3d6;
          if (!scan_text(primitive3d6, false)) {
            return false;
          }
        }
        if (flag) {
          j++;
          scan_token();
        }
      }
      while (flag && TOKEN_COMMA == scanned_token_type);
      return!flag || TOKEN_RIGHT_BRACE == scanned_token_type;
    }
    if (!is_scanned_AxesStyle && scanned_identifier.equals(name_AxesStyle)) {
      is_scanned_AxesStyle = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      Primitive3D primitive3d1 = new Primitive3D();
      scanning_EdgeForm = true;
      scanning_AxesStyles = true;
      scanned_AxesStyles[0] = null;
      scanned_AxesStyles[1] = null;
      scanned_AxesStyles[2] = null;
      if (!scan_primitive(primitive3d1)) {
        scanning_EdgeForm = false;
        scanning_AxesStyles = false;
        return false;
      }
      scanning_EdgeForm = false;
      scanning_AxesStyles = false;
      if (scanned_AxesStyles[2] == null) {
        scanned_AxesStyles[0] = primitive3d1;
        scanned_AxesStyles[1] = primitive3d1;
        scanned_AxesStyles[2] = primitive3d1;
      }
      for (int j1 = 0; j1 < 3; j1++) {
        Primitive3D primitive3d2 = scanned_AxesStyles[j1];
        if (primitive3d2 != null) {
          primitive3d2.original_thickness = primitive3d2.
              original_edge_thickness;
          primitive3d2.is_absolute_thickness = primitive3d2.
              is_absolute_edge_thickness;
          primitive3d2.standard_color = primitive3d2.edge_color;
        }
        graphics.option_AxesStyle[j1] = primitive3d2;
      }

      return true;
    }
    if (!is_scanned_AxesEdge && scanned_identifier.equals(name_AxesEdge)) {
      is_scanned_AxesEdge = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      scan_token();
      if (TOKEN_IDENTIFIER == scanned_token_type) {
        if (!scanned_identifier.equals(name_Automatic)) {
          graphics.option_AxesEdge[0] = -2;
          graphics.option_AxesEdge[1] = -2;
          graphics.option_AxesEdge[2] = -2;
        }
        return true;
      }
      if (TOKEN_LEFT_BRACE != scanned_token_type) {
        return false;
      }
      int k = 0;
      do {
        scan_token();
        if (TOKEN_IDENTIFIER == scanned_token_type) {
          if (!scanned_identifier.equals(name_Automatic)) {
            graphics.option_AxesEdge[k] = -2;
          }
        }
        else
        if (TOKEN_LEFT_BRACE == scanned_token_type) {
          scan_token();
          if (TOKEN_NUMBER != scanned_token_type) {
            return false;
          }
          int k1 = 0;
          if (scanned_number > 0.0D) {
            k1 = 1;
          }
          scan_token();
          if (TOKEN_COMMA != scanned_token_type) {
            return false;
          }
          scan_token();
          if (TOKEN_NUMBER != scanned_token_type) {
            return false;
          }
          if (scanned_number > 0.0D) {
            k1 += 2;
          }
          scan_token();
          if (TOKEN_RIGHT_BRACE != scanned_token_type) {
            return false;
          }
          graphics.option_AxesEdge[k] = k1;
        }
        else {
          return false;
        }
        k++;
        scan_token();
      }
      while (k < 3 && TOKEN_COMMA == scanned_token_type);
      return TOKEN_RIGHT_BRACE == scanned_token_type;
    }
    if (!is_scanned_Ticks && scanned_identifier.equals(name_Ticks)) {
      is_scanned_Ticks = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      scan_token();
      if (TOKEN_IDENTIFIER == scanned_token_type) {
        if (scanned_identifier.equals(name_None)) {
          graphics.option_Ticks = new Vector[3];
          return true;
        }
        if (scanned_identifier.equals(name_Automatic)) {
          graphics.option_Ticks = new Vector[3];
          for (int l = 0; l < 3; l++) {
            graphics.option_Ticks[l] = new Vector();
          }

          return true;
        }
        else {
          return false;
        }
      }
      if (TOKEN_LEFT_BRACE == scanned_token_type) {
        graphics.option_Ticks = new Vector[3];
        for (int i1 = 0; i1 < 3; i1++) {
          graphics.option_Ticks[i1] = null;
        }

        int l1 = 0;
        for (scanned_token_type = TOKEN_COMMA;
             TOKEN_COMMA == scanned_token_type; ) {
          scan_token();
          if (TOKEN_IDENTIFIER == scanned_token_type) {
            if (scanned_identifier.equals(name_Automatic)) {
              graphics.option_Ticks[l1] = new Vector();
            }
            else
            if (!scanned_identifier.equals(name_None)) {
              return false;
            }
          }
          else {
            if (TOKEN_LEFT_BRACE != scanned_token_type) {
              return false;
            }
            graphics.option_Ticks[l1] = new Vector();
            for (scanned_token_type = TOKEN_COMMA;
                 TOKEN_COMMA == scanned_token_type; ) {
              Primitive3D primitive3d7 = new Primitive3D();
              primitive3d7.front_specular_exponent = 0.01D;
              primitive3d7.back_specular_exponent = 0.0D;
              scan_token();
              if (TOKEN_NUMBER == scanned_token_type) {
                primitive3d7.original_point_size = scanned_number;
                primitive3d7.text = Double.toString(scanned_number);
              }
              else
              if (TOKEN_LEFT_BRACE == scanned_token_type) {
                scan_token();
                if (TOKEN_NUMBER != scanned_token_type) {
                  return false;
                }
                double d1 = scanned_number;
                scan_token();
                if (TOKEN_COMMA == scanned_token_type) {
                  scan_token();
                  if (!scan_text(primitive3d7, false)) {
                    return false;
                  }
                  scan_token();
                  if (TOKEN_COMMA == scanned_token_type) {
                    scan_token();
                    if (TOKEN_NUMBER == scanned_token_type) {
                      primitive3d7.front_specular_exponent = scanned_number /
                          2D;
                      primitive3d7.back_specular_exponent = scanned_number / 2D;
                    }
                    else
                    if (TOKEN_LEFT_BRACE == scanned_token_type) {
                      scan_token();
                      if (TOKEN_NUMBER != scanned_token_type) {
                        return false;
                      }
                      primitive3d7.front_specular_exponent = scanned_number;
                      scan_token();
                      if (TOKEN_COMMA != scanned_token_type) {
                        return false;
                      }
                      scan_token();
                      if (TOKEN_NUMBER != scanned_token_type) {
                        return false;
                      }
                      primitive3d7.back_specular_exponent = scanned_number;
                      scan_token();
                      if (TOKEN_RIGHT_BRACE != scanned_token_type) {
                        return false;
                      }
                    }
                    else {
                      return false;
                    }
                    scan_token();
                    if (TOKEN_COMMA == scanned_token_type) {
                      scanning_EdgeForm = true;
                      if (!scan_primitive(primitive3d7)) {
                        scanning_EdgeForm = false;
                        return false;
                      }
                      primitive3d7.original_thickness = primitive3d7.
                          original_edge_thickness;
                      primitive3d7.is_absolute_thickness = primitive3d7.
                          is_absolute_edge_thickness;
                      primitive3d7.standard_color = primitive3d7.edge_color;
                      scanning_EdgeForm = false;
                      scan_token();
                    }
                  }
                }
                else {
                  primitive3d7.text = Double.toString(d1);
                }
                primitive3d7.original_point_size = d1;
                if (TOKEN_RIGHT_BRACE != scanned_token_type) {
                  return false;
                }
              }
              else {
                return false;
              }
              if (graphics.ticks_max_in_length[l1] <
                  primitive3d7.front_specular_exponent) {
                graphics.ticks_max_in_length[l1] = primitive3d7.
                    front_specular_exponent;
              }
              if (graphics.ticks_max_out_length[l1] <
                  primitive3d7.back_specular_exponent) {
                graphics.ticks_max_out_length[l1] = primitive3d7.
                    back_specular_exponent;
              }
              graphics.option_Ticks[l1].addElement(primitive3d7);
              scan_token();
            }

            if (TOKEN_RIGHT_BRACE != scanned_token_type) {
              return false;
            }
          }
          l1++;
          scan_token();
        }

        return TOKEN_RIGHT_BRACE == scanned_token_type;
      }
      else {
        return false;
      }
    }
    if (!is_scanned_Background && scanned_identifier.equals(name_Background)) {
      is_scanned_Background = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      Primitive3D primitive3d3 = new Primitive3D();
      scanning_FaceForm = true;
      if (!scan_primitive(primitive3d3)) {
        scanning_FaceForm = false;
        return false;
      }
      else {
        graphics.option_Background = primitive3d3.front_face_color;
        scanning_FaceForm = false;
        return true;
      }
    }
    if (!is_scanned_DefaultColor && scanned_identifier.equals(name_DefaultColor)) {
      is_scanned_DefaultColor = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      Primitive3D primitive3d4 = new Primitive3D();
      scanning_FaceForm = true;
      if (!scan_primitive(primitive3d4)) {
        scanning_FaceForm = false;
        return false;
      }
      else {
        graphics.option_DefaultColor = primitive3d4.front_face_color;
        scanning_FaceForm = false;
        return true;
      }
    }
    if (!is_scanned_BoxStyle && scanned_identifier.equals(name_BoxStyle)) {
      is_scanned_BoxStyle = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      Primitive3D primitive3d5 = new Primitive3D();
      scanning_EdgeForm = true;
      if (!scan_primitive(primitive3d5)) {
        scanning_EdgeForm = false;
        return false;
      }
      else {
        primitive3d5.original_thickness = primitive3d5.original_edge_thickness;
        primitive3d5.is_absolute_thickness = primitive3d5.
            is_absolute_edge_thickness;
        primitive3d5.standard_color = primitive3d5.edge_color;
        graphics.option_BoxStyle = primitive3d5;
        scanning_EdgeForm = false;
        return true;
      }
    }
    if (!is_scanned_Boxed && scanned_identifier.equals(name_Boxed)) {
      is_scanned_Boxed = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      scan_token();
      if (TOKEN_IDENTIFIER == scanned_token_type) {
        if (scanned_identifier.equals(name_False)) {
          graphics.option_Boxed = false;
        }
        return true;
      }
      else {
        return false;
      }
    }
    if (!is_scanned_Lighting && scanned_identifier.equals(name_Lighting)) {
      is_scanned_Lighting = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      scan_token();
      if (TOKEN_IDENTIFIER == scanned_token_type) {
        if (scanned_identifier.equals(name_False)) {
          graphics.option_Lighting = false;
        }
        return true;
      }
      else {
        return false;
      }
    }
    if (!is_scanned_BoxRatios && scanned_identifier.equals(name_BoxRatios)) {
      is_scanned_BoxRatios = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      if (scan_numbers(false, true) && count_scanned_numbers == 3) {
        graphics.option_BoxRatios = new double[3];
        graphics.option_BoxRatios[0] = scanned_numbers[0];
        graphics.option_BoxRatios[1] = scanned_numbers[1];
        graphics.option_BoxRatios[2] = scanned_numbers[2];
        return true;
      }
      else {
        scan_salt();
        return true;
      }
    }
    if (!is_scanned_PlotRange && scanned_identifier.equals(name_PlotRange)) {
      is_scanned_PlotRange = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      scan_token();
      if (TOKEN_LEFT_BRACE != scanned_token_type) {
        scan_salt();
        return true;
      }
      double ad[][] = {
          {
          ( -1.0D / 0.0D), (1.0D / 0.0D)
      }, {
          ( -1.0D / 0.0D), (1.0D / 0.0D)
      }, {
          ( -1.0D / 0.0D), (1.0D / 0.0D)
      }
      };
      int i2 = 0;
      boolean flag1 = false;
      scan_token();
      if (TOKEN_NUMBER == scanned_token_type) {
        i2 = 2;
        flag1 = true;
      }
      else
      if (TOKEN_LEFT_BRACE == scanned_token_type) {
        scan_token();
      }
      else
      if (TOKEN_IDENTIFIER != scanned_token_type) {
        return false;
      }
      for (; i2 < 3; i2++) {
        if (TOKEN_IDENTIFIER != scanned_token_type) {
          for (int j2 = 0; j2 < 2; j2++) {
            if (TOKEN_NUMBER != scanned_token_type) {
              return false;
            }
            ad[i2][j2] = scanned_number;
            scan_token();
            if (j2 == 0) {
              scan_token();
            }
          }

          if (TOKEN_RIGHT_BRACE != scanned_token_type) {
            return false;
          }
        }
        if (!flag1) {
          scan_token();
          if (i2 < 2) {
            scan_token();
            if (TOKEN_LEFT_BRACE == scanned_token_type) {
              scan_token();
            }
          }
        }
      }

      if (TOKEN_RIGHT_BRACE != scanned_token_type) {
        return false;
      }
      else {
        graphics.option_PlotRange = ad;
        return true;
      }
    }
    if (!is_scanned_LightSources && scanned_identifier.equals(name_LightSources)) {
      is_scanned_LightSources = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      scan_token();
      if (TOKEN_LEFT_BRACE != scanned_token_type) {
        scan_salt();
        return true;
      }
      graphics.option_LightSources_vectors = new Vector();
      graphics.option_LightSources_colors = new Vector();
      for (scanned_token_type = TOKEN_COMMA; TOKEN_COMMA == scanned_token_type; ) {
        double ad1[] = new double[3];
        scan_token();
        if (TOKEN_LEFT_BRACE != scanned_token_type) {
          return false;
        }
        if (!scan_numbers(false, true) || count_scanned_numbers != 3) {
          return false;
        }
        double d = Math.sqrt(scanned_numbers[0] * scanned_numbers[0] +
                             scanned_numbers[1] * scanned_numbers[1] +
                             scanned_numbers[2] * scanned_numbers[2]);
        ad1[0] = scanned_numbers[0] / d;
        ad1[1] = scanned_numbers[1] / d;
        ad1[2] = scanned_numbers[2] / d;
        scan_token();
        if (TOKEN_COMMA != scanned_token_type) {
          return false;
        }
        Primitive3D primitive3d8 = new Primitive3D();
        scanning_FaceForm = true;
        if (!scan_primitive(primitive3d8) || primitive3d8.front_face_color == null) {
          scanning_FaceForm = false;
          return false;
        }
        scanning_FaceForm = false;
        graphics.option_LightSources_vectors.addElement(ad1);
        graphics.option_LightSources_colors.addElement(primitive3d8.
            front_face_color);
        scan_token();
        if (TOKEN_RIGHT_BRACE != scanned_token_type) {
          return false;
        }
        scan_token();
      }

      return TOKEN_RIGHT_BRACE == scanned_token_type;
    }
    if (!is_scanned_ViewPoint && scanned_identifier.equals(name_ViewPoint)) {
      is_scanned_ViewPoint = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      if (!scan_numbers(false, true) || count_scanned_numbers != 3) {
        return false;
      }
      else {
        graphics.option_ViewPoint = new double[3];
        graphics.option_ViewPoint[0] = scanned_numbers[0];
        graphics.option_ViewPoint[1] = scanned_numbers[1];
        graphics.option_ViewPoint[2] = scanned_numbers[2];
        return true;
      }
    }
    if (!is_scanned_ViewVertical && scanned_identifier.equals(name_ViewVertical)) {
      is_scanned_ViewVertical = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      if (!scan_numbers(false, true) || count_scanned_numbers != 3) {
        return false;
      }
      else {
        graphics.option_ViewVertical = new double[3];
        graphics.option_ViewVertical[0] = scanned_numbers[0];
        graphics.option_ViewVertical[1] = scanned_numbers[1];
        graphics.option_ViewVertical[2] = scanned_numbers[2];
        return true;
      }
    }
    if (!is_scanned_TextStyle && scanned_identifier.equals(name_TextStyle)) {
      is_scanned_TextStyle = true;
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      scan_token();
      if (TOKEN_IDENTIFIER == scanned_token_type) {
        return true;
      }
      if (TOKEN_LEFT_BRACE != scanned_token_type) {
        return false;
      }
      if (!scan_font_options() || TOKEN_RIGHT_BRACE != scanned_token_type) {
        return false;
      }
      else {
        graphics.option_TextStyle_font_url = scanned_font_url;
        graphics.option_TextStyle_font_family = scanned_font_family;
        graphics.option_TextStyle_font_weight = scanned_font_weight;
        graphics.option_TextStyle_font_slant = scanned_font_slant;
        graphics.option_TextStyle_font_size = scanned_font_size;
        graphics.option_TextStyle_font_color = scanned_font_color;
        graphics.option_TextStyle_font_background = scanned_font_background;
        return true;
      }
    }
    if (scanning_animation_option &&
        scanned_identifier.equals(name_AnimationDisplayTime)) {
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      scan_token();
      if (TOKEN_NUMBER != scanned_token_type) {
        return false;
      }
      else {
        scanned_AnimationDisplayTime = scanned_number;
        return true;
      }
    }
    if (scanning_animation_option &&
        scanned_identifier.equals(name_AnimationDirection)) {
      scan_token();
      if (TOKEN_RIGHT_ARROW != scanned_token_type) {
        return false;
      }
      scan_token();
      if (TOKEN_IDENTIFIER != scanned_token_type) {
        return false;
      }
      if (scanned_identifier.equals(name_Forward)) {
        scanned_AnimationDirection = 1;
      }
      else
      if (scanned_identifier.equals(name_Backward)) {
        scanned_AnimationDirection = -1;
      }
      else
      if (scanned_identifier.equals(name_ForwardBackward)) {
        scanned_AnimationDirection = 0;
      }
      return true;
    }
    scan_salt();
    return current_char == ',' || current_char == '}' || current_char == ']';
  }

  public String text;
  public int text_index;
  char current_char;
  int last_text_index;
  char last_char;
  static String single_quote_chars = "'\264`'";
  static String breaking_chars = " \r\n\t\\";
  static String white_characters = " \t\r\n\f";
  static String comment_end = "*)";
  static String standard_parts[] = {
      "Capital", "Script", "Gothic", "Not", "DoubleStruck", "Doubled", "Double",
      "Filled", "Left", "Right",
      ""
  };
  static String standard_short_parts[] = {
      "c", "s", "g", "n", "t", "v", "w", "f", "l", "r"
  };
  static String coded_char_names = "0AA225AB257AC259AD228AE230AG224AH226Ak63272Ale8501AliasD63332AliasI63336Alig63328Alp945Alt63441Andy63273And8743Angl8736Angs8491AR229As8944AT227AutoL62376AutoO62382AutoP62372AutoR62377AutoS62381Ba8726Bec8757Beta946Bet8502Br774Bu8226CAc263Cap8994cAA193cAB256cAC258cAD196cAE198cAG192cAH194cAl913cAR197cAT195cB914cCA262cCC199cCH268cCh935cDe916cDif63307cDig988cEA201cEB274cEC276cED203cEG200cEH202cEp917cEta919cEth208cG915cIA205cIC300cID207cIG204cIH206cIo921cKa922cKo990cLa923cLS321cM924cNT209cNu925cOA211cODoubleA336cODoubleD214cOG210cOH212cOme937cOmi927cOS216cOT213cPh934cPi928cPs936cR929cSa992cSH352cSi931cSt986cTa932cThe920cTho222cUA218cUDoubleA368cUDoubleD220cUG217cUH219cUp933cX926cY221cZ918CC231Ced807CenterD183CenterE8943Cent162CH269Ch967CircleD8857CircleM8854CircleP8853CircleT8855Cloc8754CloseCurlyD8221CloseCurlyQ8217Clov8984Clu9827Col8758Com63338Cong8801Conti62385Conto8750Contr63331Copr8720Copy169Cou8755Cr62624CupC8781Cup8995CurlyCapitalU978CurlyE603CurlyK1008CurlyPh981CurlyPi982CurlyR1009CurlyT977Curr164Dag8224Dal8504Das8211Deg176Dele63440Delt948Del8711Des8945Diame62468Diamond62753DiamondS9826Dif63308Dig63249Div247DotE8784DotlessI305DotlessJ63232Dott63313wC8751wDa8225vG63306wDot776wDow8659vP63305wlA8656wlr8660wlT62467wLongLeftA62720wLongLeftR62722wLongRightA62721wP8243wrA8658wrT8872tA63206tB63207tC63208tcA63396tcB63397tcC63398tcD63399tcE63400tcF63401tcG63402tcH63403tcI63404tcJ63405tcK63406tcL63407tcM63408tcN63409tcO63410tcP63411tcQ63412tcR63413tcS63414tcT63415tcU63416tcV63417tcW63418tcX63419tcY63420tcZ63421tD63209tE63210tF63211tG63212tH63213tI63214tJ63215tK63216tL63217tM63218tN63219tO63220tP63221tQ63222tR63223tS63224tT63225tU63226tV63227tW63228tX63229tY63230tZ63231wUpA8657wUpD8661wV8741DownArrowB62724DownArrowU62726DownArrow8595DownB785DownE161DownLeftR62731DownLeftT62734DownLeftVectorB62732DownLeftVector8637DownQ191DownRightT62735DownRightVectorB62733DownRightVector8641DownTeeA8615DownTee8868EA233EB275EC277ED235EG232EH234Ele8712Ell8230EmptyC9675EmptyDi9671EmptyDo9661EmptyR9647EmptySe8709EmptySmallC9702EmptySmallS62759EmptySq9635EmptyU9651EmptyV62768En63444Ep949EqualT8770Equal62513Equi8652Er63335Es63337Eta951Eth240Exi8707Exp63309Fe63274Fi63233fC9679fDi9670fDo9660fR9646fSmallC63312fSmallS62760fS9634fU9650fV62761Fin962Fiv9733Fla9837FlL63234Flo402Fo8704Fr63265Ga947Gi8503gA63180gB63181gC63182gcA63370gcB63371gcC63372gcD63373gcE63374gcF63375gcG63376gcH63377gcI63378gcJ63379gcK63380gcL63381gcM63382gcN63383gcO63384gcP63385gcQ63386gcR63387gcS63388gcT63389gcU63390gcV63391gcW63392gcX63393gcY63394gcZ63395gD63183gE63184gF63185gG63186gH63187gI63188gJ63189gK63190gL63191gM63192gN63193gO63194gP63195gQ63196gR63197gS63198gT63199gU63200gV63201gW63202gX63203gY63204gZ63205GrayC63315GrayS63314GreaterEqualL8923GreaterEqual8805GreaterF8807GreaterG8811GreaterL8823GreaterS62502GreaterT8819Hac780Hap9786HB8463He9825Ho62977HumpD8782HumpE8783Hy173IA237IC301ID239IG236IH238ImaginaryI63310ImaginaryJ63311Imp62755Ind62371Inf8734Integ8747Inter8898InvisibleC63333InvisiblePo62388InvisiblePr62387InvisibleS62304Io953Ka954Ker63318Key63443Ko63250La955lAn9001lArrowB8676lArrowR8646lArrow8592lB62979lC8968lwBracketi62981lwBracket12314lDownT62745lDownVectorB62743lDownVector8643lF8970lG171lM63339lrA8596lrV62725lS63329lTeeA8612lTeeV62729lTee8867lTriangleB62480lTriangleE8884lTriangle8882lUpD62741lUpT62744lUpVectorB62742lUpVector8639lVectorB62727lVector8636LessEqualG8922LessEqual8804LessF8806LessG8822LessL8810LessS62496LessT8818Li63267LongD8212LongLeftA62748LongLeftR62750LongR62749LowerL8601LowerR8600LS322Ma63319Mea8737Med62308Mh8487Mic181Min8723Mo63446Mu956Na9838NegativeM62339NegativeThic62340NegativeThin62338NegativeV62336NestedG62501NestedL62497Neu63266NoB62370Non160nCo8802nCu8813nw8742nEl8713nEqualT62464nEqual8800nEx8708nGreaterE8817nGreaterF8809nGreaterG62503nGreaterL8825nGreaterS62505nGreaterT8821nGreater8815nHumpD62466nHumpE62465nlTriangleB62482nlTriangleE8940nlTriangle8938nLessE8816nLessF8808nLessG8824nLessL62498nLessS62500nLessT8820nLess8814nNestedG62504nNestedL62499nPrecedesE8928nPrecedesS62507nPrecedesT8936nPrecedes8832nRe8716nrTriangleB62483nrTriangleE8941nrTriangle8939nSquareSubsetE8930nSquareSubset62510nSquareSupersetE8931nSquareSuperset62511nSubsetE8840nSubset8836nSucceedsE8929nSucceedsS62509nSucceedsT8937nSucceeds8833nSupersetE8841nSuperset8837nTildeE8772nTildeF8775nTildeT8777nTilde8769nV8740NT241Nul62368Num63268Nu957OA243ODoubleA337ODoubleD246OG242OH244Ome969Omi959OpenCurlyD8220OpenCurlyQ8216Opt63442Or8744OS248OT245OverBrace62994OverBrack62996OverP62992Para182Part8706Ph966Pi960Pla9633Plu177PrecedesE8828PrecedesS62506PrecedesT8830Precedes8826Pri8242Prod8719Proportiona8733Proportion8759Ps968Reg174ReturnI8629ReturnK63334ReverseD8245ReverseEl8715ReverseEq8651ReverseP8244ReverseU62747Rh961rAngleB9002rAngle8735rArrowB8677rArrowL8644rArrow8594rB62980rC8969rwBracketi62982rwBracket12315rDownT62740rDownVectorB62738rDownVector8642rF8971rG187rM63340rS63330rTeeA8614rTeeV62730rTee8866rTriangleB62481rTriangleE8885rTriangle8883rUpD62736rUpT62739rUpVectorB62737rUpVector8638rVectorB62728rVector8640RoundI62756RoundS62386RuleD62751Rule62754Sad9785Sam63251sA63154sB63155sC63156scA63344scB63345scC63346scD63347scE63348scF63349scG63350scH63351scI63352scJ63353scK63354scL63355scM63356scN63357scO63358scP63359scQ63360scR63361scS63362scT63363scU63364scV63365scW63366scX63367scY63368scZ63369sDotlessI63280sDotlessJ63281sD63157sE63158sF63159sG63160sH63161sI63162sJ63163sK63164sL63165sM63166sN63167sO63168sP63169sQ63170sR63171sS63172sT63173sU63174sV63175sW63176sX63177sY63178sZ63179Sec167Sel9632SH353Sha9839Shi63445ShortD62763ShortL62758ShortR62757ShortU62762Sig963Six63317Sk8259Sm8728SpaceI9251SpaceK63423Spa9824Sp8738Sqr8730SquareI8851SquareSubsetE8849SquareSubset8847SquareSupersetE8850SquareSuperset8848SquareU8852Square62752Sta8902Ste163Sti63248SubsetE8838Subset8834SucceedsE8829SucceedsS62508SucceedsT8831Succeeds8827Such8717Sum8721SupersetE8839Superset8835SZ223Tab63422Tau964Ther8756Thet952Thick62309Thin62307Tho254TildeE8771TildeF8773TildeT8776Tilde8764Tim215Tra63270Tri8411UA250UDoubleA369UDoubleD252UG249UH251UnderBrace62995UnderBrack62997UnderP62993UnionP8846Union8899Unk65533UpArrowB62723UpArrowD8645UpArrow8593UpD8597UpE62746UpperL8598UpperR8599Ups965UpTeeA8613UpTee8869Vee8897VerticalB8739VerticalE8942VerticalL62978VerticalS62514VerticalT8768Very62305Vi63271War63269Wat63316Wed8896Wei8472Wo63264Xi958YA253YD255Ye165Ze950   "

      ;
  double scanned_real;
  boolean is_scanned_real;
  public static int TOKEN_COMMA;
  public static int TOKEN_LEFT_PARENTHESIS = 1;
  public static int TOKEN_RIGHT_PARENTHESIS = 2;
  public static int TOKEN_LEFT_BRACKET = 3;
  public static int TOKEN_RIGHT_BRACKET = 4;
  public static int TOKEN_LEFT_BRACE = 5;
  public static int TOKEN_RIGHT_BRACE = 6;
  public static int TOKEN_IDENTIFIER = 7;
  public static int TOKEN_NUMBER = 8;
  public static int TOKEN_RIGHT_ARROW = 9;
  public static int TOKEN_STRING = 10;
  public static int TOKEN_NONE = 11;
  public static int TOKEN_UNIDENTIFIED = 12;
  static String token_symbols = ",()[]{}a1>_?";
  static String structure_characters = ",()[]{}";
  static String number_start = "+-.0123456789";
  static String digit_characters = "0123456789";
  static String octal_digits = "01234567";
  static String hexadecimal_digits = "0123456789abcdefABCDEF";
  int scanned_token_type;
  double scanned_number;
  String scanned_identifier;
  String scanned_string;
  static int max_length_identifier = 100;
  char scanned_identifier_chars[];
  public static int CONSTRUCT_NONE;
  public static int CONSTRUCT_FUNCTION = 1;
  public static int CONSTRUCT_SYMBOL = 2;
  public static int CONSTRUCT_LIST = 3;
  public static int CONSTRUCT_RULE = 4;
  public static int CONSTRUCT_UNIDENTIFIED = 5;
  static String name_ShowAnimation = "ShowAnimation";
  static String name_AnimationDisplayTime = "AnimationDisplayTime";
  static String name_AnimationDirection = "AnimationDirection";
  static String name_Forward = "Forward";
  static String name_Backward = "Backward";
  static String name_ForwardBackward = "ForwardBackward";
  static String name_Graphics3D = "Graphics3D";
  static String name_Scaled = "Scaled";
  static String name_Cuboid = "Cuboid";
  static String name_Line = "Line";
  static String name_Point = "Point";
  static String name_Polygon = "Polygon";
  static String name_Text = "Text";
  static String name_StyleForm = "StyleForm";
  static String name_Questionmark = "?";
  static String name_TextStyle = "TextStyle";
  static String name_FontWeight = "FontWeight";
  static String name_Bold = "Bold";
  static String name_FontSize = "FontSize";
  static String name_FontSlant = "FontSlant";
  static String name_Italic = "Italic";
  static String name_FontFamily = "FontFamily";
  static String name_FontColor = "FontColor";
  static String name_URL = "URL";
  static String name_AbsolutePointSize = "AbsolutePointSize";
  static String name_AbsoluteThickness = "AbsoluteThickness";
  static String name_CMYKColor = "CMYKColor";
  static String name_EdgeForm = "EdgeForm";
  static String name_FaceForm = "FaceForm";
  static String name_GrayLevel = "GrayLevel";
  static String name_Hue = "Hue";
  static String name_PointSize = "PointSize";
  static String name_RGBColor = "RGBColor";
  static String name_SurfaceColor = "SurfaceColor";
  static String name_Thickness = "Thickness";
  static String name_AmbientLight = "AmbientLight";
  static String name_Axes = "Axes";
  static String name_AxesLabel = "AxesLabel";
  static String name_AxesEdge = "AxesEdge";
  static String name_AxesStyle = "AxesStyle";
  static String name_Background = "Background";
  static String name_Boxed = "Boxed";
  static String name_BoxRatios = "BoxRatios";
  static String name_BoxStyle = "BoxStyle";
  static String name_DefaultColor = "DefaultColor";
  static String name_Lighting = "Lighting";
  static String name_LightSources = "LightSources";
  static String name_PlotRange = "PlotRange";
  static String name_Ticks = "Ticks";
  static String name_ViewPoint = "ViewPoint";
  static String name_ViewVertical = "ViewVertical";
  static String name_True = "True";
  static String name_False = "False";
  static String name_Automatic = "Automatic";
  static String name_All = "All";
  static String name_None = "None";
  boolean scanning_EdgeForm;
  boolean scanning_FaceForm;
  boolean scanning_FaceForm_back;
  boolean scanning_SurfaceColor;
  boolean scanning_SurfaceColor_specular;
  boolean scanning_AxesStyles;
  double scanned_AnimationDisplayTime;
  int scanned_AnimationDirection;
  Vector scanned_frames;
  boolean scanning_animation_option;
  Graphics3D graphics;
  boolean is_scanned_AmbientLight;
  boolean is_scanned_Axes;
  boolean is_scanned_AxesLabel;
  boolean is_scanned_AxesStyle;
  boolean is_scanned_AxesEdge;
  boolean is_scanned_Ticks;
  boolean is_scanned_Background;
  boolean is_scanned_DefaultColor;
  boolean is_scanned_BoxStyle;
  boolean is_scanned_Boxed;
  boolean is_scanned_Lighting;
  boolean is_scanned_BoxRatios;
  boolean is_scanned_PlotRange;
  boolean is_scanned_LightSources;
  boolean is_scanned_ViewPoint;
  boolean is_scanned_ViewVertical;
  boolean is_scanned_TextStyle;
  boolean scanned_nothing;
  boolean scanned_unidentified;
  int recursion_depth;
  Primitive3D scanned_AxesStyles[] = {
      null, null, null
  };
  static int max_count_scanned_numbers = 4;
  double scanned_numbers[];
  int count_scanned_numbers;
  int max_count_scanned_points;
  int count_scanned_points;
  double scanned_points[][];
  double scanned_scaled_offsets[][];
  Color scanned_color;
  String scanned_font_url;
  String scanned_font_family;
  int scanned_font_weight;
  int scanned_font_slant;
  int scanned_font_size;
  Color scanned_font_color;
  Color scanned_font_background;

}
