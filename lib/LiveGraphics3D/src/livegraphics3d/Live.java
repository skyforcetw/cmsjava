package livegraphics3d;

// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 2009/3/2 ¤U¤È 11:23:49
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Live.java

import java.applet.Applet;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import java.awt.*;

import shu.ui.GUIUtils;

public class Live
    extends Applet implements Runnable {

  public void init() {
  }

  public void initialize() {
    if (!initialized) {
      System.out.println(
          "LiveGraphics3D -1.50 by Martin Kraus, fn141@fen.baynet.de");
      image_buffer = createImage(applet_width, applet_height);
      graphics_buffer = image_buffer.getGraphics();
      GUIUtils.setGraphics2DAntiAlias( (Graphics2D) graphics_buffer);
      String s = getParameter("BGCOLOR");
      if (s != null) {
        s = s.trim();
        if (s.charAt(0) == '#') {
          s = s.substring(1);
        }
        background_color = new Color(Integer.parseInt(s, 16));
        if (background_color.getRed() + background_color.getBlue() +
            background_color.getGreen() > 384) {
          foreground_color = Color.black;
        }
        else {
          foreground_color = Color.white;
        }
      }
      else {
        background_color = Color.white;
        foreground_color = Color.black;
      }
      String s1 = getParameter("POINT_EDGE_COLOR");
      if (s1 != null) {
        s1 = s1.trim();
        if (s1.charAt(0) == '#') {
          s1 = s1.substring(1);
        }
        point_edge_color = new Color(Integer.parseInt(s1, 16));
      }
      else {
        point_edge_color = null;
      }
      double d = 0.0D;
      double d1 = 0.0D;
      String s2 = getParameter("INITIAL_ROTATION");
      if (s2 != null && !Character.isDigit(s2.charAt(0))) {
        String s3 = getDocumentBase().getRef();
        s2 = s2.concat("=");
        int i;
        if (s3 == null || (i = s3.indexOf(s2)) < 0) {
          s2 = null;
        }
        else {
          s2 = s3.substring(i + s2.length());
        }
      }
      if (s2 != null) {
        try {
          d = Double.valueOf(s2).doubleValue();
          int j = s2.indexOf(",");
          if (j > 0 &&
              (s2.indexOf(equal_sign_string) < 0 ||
               j < s2.indexOf(equal_sign_string))) {
            d1 = Double.valueOf(s2.substring(j + 1)).doubleValue();
          }
        }
        catch (NumberFormatException _ex) {
          System.out.println(
              "LiveGraphics3D error: Could not parse INITIAL_ROTATION specification \"" +
              getParameter("INITIAL_ROTATION") + "\"");
          d = 0.0D;
          d1 = 0.0D;
        }
      }
      graphics_buffer.setColor(background_color);
      graphics_buffer.fillRect(0, 0, applet_width, applet_height);
      graphics_buffer.setColor(foreground_color);
      graphics_buffer.setFont(new Font("Dialog", 0, 10));
      print_message(graphics_buffer, "LiveGraphics3D -1.50", false);
      print_message(graphics_buffer, "Initializing. Please wait.", false);
      is_put_on_screen = false;
      repaint();
      String s4 = getParameter("INPUT");
      if (s4 == null) {
        String s5 = getParameter("INPUT_FILE");
        if (s5 != null) {
          try {
            URL url = new URL(getDocumentBase(), s5);
            InputStream inputstream = url.openStream();
            byte abyte0[] = new byte[20000];
            int k = 20000;
            StringBuffer stringbuffer = new StringBuffer();
            while (k > -1) {
              k = inputstream.read(abyte0);
              if (k > -1) {
                stringbuffer.append(new String(abyte0, 0, 0, k));
              }
            }
            abyte0 = null;
            s4 = stringbuffer.toString();
            stringbuffer = null;
          }
          catch (IOException _ex) {
            print_message(graphics_buffer, "LiveGraphics3D error:");
            print_message(graphics_buffer, "Can't read " + s5);
          }
        }
      }
      Parser parser1 = new Parser(s4);
      if (parser1.text != null && parser1.scan_animation() &&
          parser1.scanned_frames != null && parser1.scanned_frames.size() > 0) {
        frames = parser1.scanned_frames;
        animation_display_time = parser1.scanned_AnimationDisplayTime;
        animation_direction = parser1.scanned_AnimationDirection;
        parser1 = null;
        String s6 = getParameter("BACKGROUND");
        bg_is_fixed = false;
        bg_is_cylindrical = false;
        bg_is_spherical = false;
        if (s6 != null) {
          bg_is_fixed = true;
        }
        else {
          s6 = getParameter("CYLINDRICAL_BACKGROUND");
          if (s6 != null) {
            bg_is_cylindrical = true;
          }
          else {
            s6 = getParameter("SPHERICAL_BACKGROUND");
            if (s6 != null) {
              bg_is_spherical = true;
            }
          }
        }
        bg_image = null;
        bg_right_image = null;
        MediaTracker mediatracker = null;
        if (s6 != null) {
          mediatracker = new MediaTracker(this);
          try {
            bg_image = getImage(new URL(getDocumentBase(), s6));
            if (bg_image != null) {
              mediatracker.addImage(bg_image, 0);
            }
          }
          catch (MalformedURLException _ex) {
            print_message(graphics_buffer, "LiveGraphics3D error:");
            print_message(graphics_buffer, "bad URL " + s6);
            bg_image = null;
            mediatracker = null;
            bg_is_fixed = false;
            bg_is_cylindrical = false;
            bg_is_spherical = false;
          }
        }
        if (bg_image != null) {
          String s7 = getParameter("RIGHT_BACKGROUND");
          if (s7 != null) {
            try {
              bg_right_image = getImage(new URL(getDocumentBase(), s7));
              if (bg_right_image != null) {
                mediatracker.addImage(bg_right_image, 0);
              }
            }
            catch (MalformedURLException _ex) {}
          }
        }
        String s8 = getParameter("MAGNIFICATION");
        if (s8 != null) {
          magnification_factor = Math.abs(Double.valueOf(s8).doubleValue());
        }
        else {
          magnification_factor = 1.0D;
        }
        preceding_magnification_factor = magnification_factor;
        String s9 = getParameter("STEREO_DISTANCE");
        if (s9 != null) {
          is_stereo = true;
          stereo_distance = Double.valueOf(s9).doubleValue();
        }
        else {
          stereo_distance = 0.050000000000000003D;
          is_stereo = false;
        }
        preceding_stereo_distance = stereo_distance;
        for (int l = 0; l < frames.size(); l++) {
          ps3D = (Graphics3D) frames.elementAt(l);
          ps3D.setGlobalParameters(applet_width, applet_height,
                                   background_color, point_edge_color);
          ps3D.preparePrimitives(graphics_buffer);
        }

        current_frame_index = 0;
        if (bg_image != null) {
          if (mediatracker != null) {
            try {
              mediatracker.waitForAll();
            }
            catch (InterruptedException _ex) {}
          }
          bg_width = bg_image.getWidth(null);
          bg_height = bg_image.getHeight(null);
          if (bg_width < applet_width || bg_height < applet_height) {
            bg_is_fixed = true;
            bg_is_cylindrical = false;
            bg_is_spherical = false;
          }
          bg_x_offset = 0;
          bg_y_offset = 0;
          if (bg_is_spherical) {
            bg_x_offset = (int) ( ( -d * (double) bg_width) / 2D / 180D);
            bg_y_offset = (int) ( ( -d1 * (double) bg_height) / 180D);
            bg_rotated_image = createRotatedImage(bg_image);
            if (bg_right_image != null) {
              bg_rotated_right_image = createRotatedImage(bg_right_image);
            }
          }
          else
          if (bg_is_cylindrical) {
            bg_x_offset = (int) ( ( -d * (double) bg_width) / 2D / 180D);
            bg_y_offset = (int) ( ( -d1 * (double) bg_width) / 2D / 180D);
          }
          if (bg_is_cylindrical || bg_is_spherical) {
            rotation = adjust_bg_offsets();
          }
          if (bg_right_image == null) {
            bg_right_image = bg_image;
            bg_rotated_right_image = bg_rotated_image;
          }
        }
        if (!bg_is_cylindrical && !bg_is_spherical) {
          rotation = new Quaternion( (d1 * 3.1415926535897931D) / 180D, 1.0D,
                                    0.0D, 0.0D, false);
          rotation.multiply(new Quaternion( (d * 3.1415926535897931D) / 180D,
                                           0.0D, 1.0D, 0.0D, false));
        }
        preceding_rotation = rotation;
        ps3D = (Graphics3D) frames.elementAt(current_frame_index);
        ps3D.setPerspective(ps3D.length_view_point,
                            ps3D.initial_magnification * magnification_factor,
                            is_stereo, stereo_distance);
        ps3D.setQuaternion(rotation.product(ps3D.initial_rotation));
        ps3D.projectPoints(false);
        is_put_on_screen = false;
        paintGraphics3D();
      }
      else {
        if (parser1.text == null) {
          print_message(graphics_buffer,
                        "LiveGraphics3D error: applet parameter");
          print_message(graphics_buffer, "\"INPUT\" or \"INPUT_FILE\" missing.");
        }
        else {
          if (parser1.text_index + 1 >= parser1.text.length()) {
            parser1.text_index = parser1.text.length() - 1;
          }
          if (parser1.text_index > 30) {
            print_message(graphics_buffer, "LiveGraphics3D syntax error:");
            print_message(graphics_buffer,
                          "..." +
                          parser1.text.substring(parser1.text_index - 30,
                                                 parser1.text_index + 1));
          }
          else {
            print_message(graphics_buffer, "LiveGraphics3D syntax error:");
            print_message(graphics_buffer,
                          parser1.text.substring(0, parser1.text_index + 1));
          }
        }
        ps3D = null;
      }
      parser1 = null;
      System.gc();
    }
    if (ps3D == null) {
      print_message(graphics_buffer, "LiveGraphics3D aborted.", false);
      repaint();
    }
    initialized = true;
  }

  public Image createRotatedImage(Image image) {
    int i = image.getWidth(null);
    int j = image.getHeight(null);
    Image image1 = createImage(i + 1, j + 1);
    Graphics g = image1.getGraphics();
    g.drawImage(image, 0, 0, null);
    for (int k = 0; k < j / 2; k++) {
      g.copyArea(0, k, i, 1, 0, j - k - k);
      g.copyArea(0, j - 1 - k, i, 1, 0, k - (j - 1 - k));
    }

    g.copyArea(0, j - (j / 2 - 1), i, j / 2, 0, -1);
    for (int l = 0; l < i / 2; l++) {
      g.copyArea(l, 0, 1, j, i - l - l, 0);
      g.copyArea(i - 1 - l, 0, 1, j, l - (i - 1 - l), 0);
    }

    g.copyArea(i - (i / 2 - 1), 0, i / 2, j, -1, 0);
    g = null;
    return image1;
  }

  public void start() {
    applet_width = size().width;
    applet_height = size().height;
    if (applet_height < applet_width) {
      min_height_width = applet_height;
    }
    else {
      min_height_width = applet_width;
    }
    is_control_down = false;
    is_meta_down = false;
    is_shift_down = false;
    is_new_dragging = false;
    is_put_on_screen = true;
    is_dragging = false;
    is_animating = true;
    down_time = System.currentTimeMillis() - 10000L;
    painter = new Thread(this);
    painter.start();
  }

  public void print_message(Graphics g, String s) {
    print_message(g, s, true);
  }

  public void print_message(Graphics g, String s, boolean flag) {
    if (flag) {
      System.out.println(s);
    }
    text_y = text_y + g.getFontMetrics().getAscent() + 2;
    g.drawString(s, text_x, text_y);
  }

  public void stop() {
  }

  public void run() {
    initialize();
    while (ps3D != null) {
      if (is_new_dragging) {
        is_new_dragging = false;
        if (is_control_down) {
          length_view_point_factor = preceding_length_view_point_factor *
              Math.
              pow(4D,
                  (double) (down_mouse_y - last_y) / (double) min_height_width);
          stereo_distance = preceding_stereo_distance +
              (0.25D * (double) (last_x - down_mouse_x)) /
              (double) applet_width;
        }
        else
        if (is_shift_down) {
          magnification_factor = preceding_magnification_factor /
              Math.pow(4D,
                       (double) (down_mouse_y - last_y) /
                       (double) min_height_width);
          if (!bg_is_cylindrical && !bg_is_spherical) {
            rotation = (new Quaternion( (4.7123889803846897D *
                                         (double) (down_mouse_x - last_x)) /
                                       (double) min_height_width, 0.0D, 0.0D,
                                       1.0D, false)).product(preceding_rotation);
          }
        }
        else
        if (is_meta_down) {
          cut_primitives_count = preceding_cut_primitives_count -
              (down_mouse_y - last_y) / 5;
          if (cut_primitives_count < 0) {
            cut_primitives_count = 0;
          }
          int i = (last_x - down_mouse_x) / 5;
          if (animation_direction == -1) {
            i = -i;
          }
          current_frame_index = preceding_frame_index + i;
          int j = frames.size();
          if (animation_direction == 0) {
            j = 2 * frames.size() - 2;
          }
          if (current_frame_index >= 0) {
            current_frame_index = current_frame_index % j;
          }
          else {
            current_frame_index = (j - -current_frame_index % j) % j;
          }
          if (animation_direction == 0) {
            if (current_frame_index < frames.size()) {
              ps3D = (Graphics3D) frames.elementAt(current_frame_index);
            }
            else {
              ps3D = (Graphics3D) frames.elementAt(2 * frames.size() -
                  current_frame_index - 2);
            }
          }
          else {
            ps3D = (Graphics3D) frames.elementAt(current_frame_index);
          }
        }
        else
        if (is_dragging) {
          if (bg_is_cylindrical || bg_is_spherical) {
            bg_x_offset = preceding_bg_x_offset - (last_x - down_mouse_x);
            bg_y_offset = preceding_bg_y_offset - (last_y - down_mouse_y);
            rotation = adjust_bg_offsets();
          }
          else {
            double d = last_y - down_mouse_y;
            double d1 = last_x - down_mouse_x;
            double d2 = Math.sqrt(d * d + d1 * d1);
            if (d2 > 1.0D) {
              d /= d2;
              d1 /= d2;
              rotation = (new Quaternion( (4.7123889803846897D * d2) /
                                         (double) min_height_width, d, d1, 0.0D, false)).
                  product(preceding_rotation);
            }
            else {
              rotation = preceding_rotation;
            }
          }
        }
        ps3D.setPerspective(ps3D.initial_length_view_point *
                            length_view_point_factor,
                            ps3D.initial_magnification * magnification_factor,
                            is_stereo, stereo_distance);
        ps3D.setCutPrimitivesCount(cut_primitives_count);
        ps3D.setQuaternion(rotation.product(ps3D.initial_rotation));
        ps3D.projectPoints(false);
        while (!is_put_on_screen) {
          try {
            Thread.sleep(10L);
          }
          catch (InterruptedException _ex) {}
        }
        is_put_on_screen = false;
        paintGraphics3D();
      }
      else {
        try {
          if (is_animating && frames.size() > 1 &&
              (is_dragging || is_mouse_here) && !is_meta_down) {
            Thread.sleep(5L);
          }
          else {
            Thread.sleep(20L);
          }
        }
        catch (InterruptedException _ex) {}
      }
      if (is_animating && frames.size() > 1 && (is_dragging || is_mouse_here) &&
          !is_meta_down &&
          System.currentTimeMillis() - painted_time >=
          (long) (1000D * animation_display_time)) {
        is_new_dragging = true;
        painted_time = System.currentTimeMillis();
        if (animation_direction == 1) {
          current_frame_index = current_frame_index + 1;
          if (current_frame_index >= frames.size()) {
            current_frame_index = 0;
          }
          ps3D = (Graphics3D) frames.elementAt(current_frame_index);
        }
        else
        if (animation_direction == -1) {
          current_frame_index = current_frame_index - 1;
          if (current_frame_index < 0) {
            current_frame_index = frames.size() - 1;
          }
          ps3D = (Graphics3D) frames.elementAt(current_frame_index);
        }
        else
        if (animation_direction == 0) {
          current_frame_index = current_frame_index + 1;
          if (current_frame_index >= 2 * frames.size() - 2) {
            current_frame_index = 0;
          }
          if (current_frame_index < frames.size()) {
            ps3D = (Graphics3D) frames.elementAt(current_frame_index);
          }
          else {
            ps3D = (Graphics3D) frames.elementAt(2 * frames.size() -
                                                 current_frame_index - 2);
          }
        }
      }
    }
  }

  Quaternion adjust_bg_offsets() {
    for (; bg_x_offset > bg_width; bg_x_offset = bg_x_offset - bg_width) {
      ;
    }
    for (; bg_x_offset < 0; bg_x_offset = bg_x_offset + bg_width) {
      ;
    }
    double d = ( -6.2831853071795862D * (double) bg_x_offset) /
        (double) bg_width;
    double d1 = 0.0D;
    if (bg_is_cylindrical) {
      int i = (bg_height - applet_height) / 2;
      if (bg_y_offset > i) {
        bg_y_offset = i;
      }
      else
      if (bg_y_offset < -i) {
        bg_y_offset = -i;
      }
      d1 = ( -6.2831853071795862D * (double) bg_y_offset) / (double) bg_width;
    }
    else
    if (bg_is_spherical) {
      for (; bg_y_offset > bg_height + bg_height;
           bg_y_offset = bg_y_offset - bg_height - bg_height) {
        ;
      }
      for (; bg_y_offset < 0; bg_y_offset = bg_y_offset + bg_height + bg_height) {
        ;
      }
      d1 = ( -3.1415926535897931D * (double) bg_y_offset) / (double) bg_height;
    }
    rotation = new Quaternion(d1, 1.0D, 0.0D, 0.0D, false);
    rotation.multiply(new Quaternion(d, 0.0D, 1.0D, 0.0D, false));
    return rotation;
  }

  public boolean mouseEnter(Event event, int i, int j) {
    is_mouse_here = true;
    requestFocus();
    if (initialized) {
      if (ps3D == null) {
        getAppletContext().showStatus(syntax_error_string);
        return true;
      }
      getAppletContext().showStatus(enter_string);
    }
    return true;
  }

  public boolean mouseExit(Event event, int i, int j) {
    is_mouse_here = false;
    if (active_primitive != null) {
      drawTextRectangle(active_primitive);
      active_primitive = null;
      if (!is_dragging && !is_new_dragging && is_put_on_screen) {
        is_put_on_screen = false;
        repaint();
      }
    }
    return true;
  }

  public boolean mouseMove(Event event, int i, int j) {
    is_mouse_here = true;
    if (!initialized || painted_ps3D == null || i < 0 || i > applet_width ||
        j < 0 || j > applet_height || !is_put_on_screen) {
      return true;
    }
    else {
      mark_hyperlink(i, j);
      return true;
    }
  }

  public synchronized void mark_hyperlink(int i, int j) {
    Primitive3D primitive3d = active_primitive;
    active_primitive = null;
    int ai[] = painted_ps3D.left_pixel_xs;
    int ai1[] = painted_ps3D.pixel_ys;
    if (painted_ps3D.is_stereo && i > painted_ps3D.pixel_width) {
      ai = painted_ps3D.right_pixel_xs;
      i -= painted_ps3D.pixel_width;
    }
    for (int k = painted_ps3D.count_primitives - 1; k >= 0; k--) {
      int l = painted_ps3D.order[k];
      if (painted_ps3D.min_primitive_index > l ||
          l > painted_ps3D.max_primitive_index) {
        continue;
      }
      Primitive3D primitive3d1 = (Primitive3D) painted_ps3D.primitives.
          elementAt(l);
      if (primitive3d1.font_url == null) {
        continue;
      }
      int i1 = primitive3d1.points[0];
      if (painted_ps3D.point_scale[i1] <= 0 ||
          ai[i1] + primitive3d1.first_point > i ||
          ai1[i1] + primitive3d1.second_point > j ||
          ai[i1] + primitive3d1.first_point + primitive3d1.third_point < i ||
          ai1[i1] + primitive3d1.second_point + primitive3d1.fourth_point < j) {
        continue;
      }
      active_primitive = primitive3d1;
      break;
    }

    if (primitive3d != active_primitive &&
        (primitive3d != null || active_primitive != null)) {
      if (!is_put_on_screen) {
        active_primitive = null;
        return;
      }
      if (primitive3d != null) {
        drawTextRectangle(primitive3d);
        if (active_primitive == null) {
          getAppletContext().showStatus(empty_string);
        }
      }
      if (active_primitive != null) {
        drawTextRectangle(active_primitive);
        getAppletContext().showStatus(active_primitive.font_url);
      }
      is_put_on_screen = false;
      repaint();
    }
  }

  public synchronized void drawTextRectangle(Primitive3D primitive3d) {
    int i = primitive3d.points[0];
    if (painted_ps3D.point_scale[i] > 0) {
      graphics_buffer.setColor(Color.white);
      graphics_buffer.setXORMode(Color.black);
      graphics_buffer.drawRect(painted_ps3D.left_pixel_xs[i] +
                               primitive3d.first_point,
                               painted_ps3D.pixel_ys[i] +
                               primitive3d.second_point,
                               primitive3d.third_point - 1,
                               primitive3d.fourth_point - 1);
      if (painted_ps3D.is_stereo) {
        graphics_buffer.drawRect(painted_ps3D.pixel_width +
                                 painted_ps3D.right_pixel_xs[i] +
                                 primitive3d.first_point,
                                 painted_ps3D.pixel_ys[i] +
                                 primitive3d.second_point,
                                 primitive3d.third_point - 1,
                                 primitive3d.fourth_point - 1);
      }
      graphics_buffer.setPaintMode();
    }
  }

  public boolean mouseDown(Event event, int i, int j) {
    is_mouse_here = true;
    if (painted_ps3D == null) {
      return true;
    }
    if (active_primitive != null) {
      URL url;
      try {
        url = new URL(active_primitive.font_url);
      }
      catch (MalformedURLException malformedurlexception) {
        getAppletContext().showStatus(url_error_string +
                                      malformedurlexception.getMessage());
        url = null;
      }
      if (url != null) {
        getAppletContext().showDocument(url);
      }
    }
    else {
      long l = event.when;
      if (l > down_time && l - down_time < 500L) {
        is_animating = !is_animating;
        down_time = l - 1000L;
      }
      else {
        down_time = l;
      }
      is_dragging = true;
      preceding_rotation = rotation;
      preceding_length_view_point_factor = length_view_point_factor;
      preceding_magnification_factor = magnification_factor;
      preceding_cut_primitives_count = cut_primitives_count;
      preceding_bg_x_offset = bg_x_offset;
      preceding_bg_y_offset = bg_y_offset;
      preceding_stereo_distance = stereo_distance;
      preceding_frame_index = current_frame_index;
      down_mouse_x = i;
      down_mouse_y = j;
      last_x = down_mouse_x;
      last_y = down_mouse_y;
      is_shift_down = false;
      is_control_down = false;
      is_meta_down = false;
      if (event.shiftDown()) {
        is_shift_down = true;
      }
      else
      if (event.controlDown()) {
        is_control_down = true;
      }
      else
      if (event.metaDown()) {
        is_meta_down = true;
      }
    }
    return true;
  }

  public boolean mouseUp(Event event, int i, int j) {
    is_dragging = false;
    is_shift_down = false;
    is_meta_down = false;
    is_control_down = false;
    return true;
  }

  public boolean mouseDrag(Event event, int i, int j) {
    if (ps3D == null || active_primitive != null || !is_dragging) {
      return true;
    }
    if (preceding_rotation != null && (i != last_x || j != last_y)) {
      is_new_dragging = true;
      last_x = i;
      last_y = j;
    }
    return true;
  }

  public boolean keyDown(Event event, int i) {
    if (painted_ps3D == null) {
      return true;
    }
    if (i == 111) {
      double ad[] = {
          0.0D, 0.0D, 1.0D
      };
      double ad1[] = {
          0.0D, 1.0D, 0.0D
      };
      ad = painted_ps3D.getQuaternion().conjugated().rotated(ad);
      ad1 = painted_ps3D.getQuaternion().conjugated().rotated(ad1);
      String s =
          "LiveGraphics3D parameters:\n   <PARAM NAME=MAGNIFICATION VALUE=" +
          magnification_factor + ">\n";
      if (is_stereo) {
        s = s + "   <PARAM NAME=STEREO_DISTANCE VALUE=" + stereo_distance +
            ">\n";
      }
      s = s + "Graphics3D options:\n   ViewPoint->{" +
          ad[0] * painted_ps3D.length_view_point + ", " +
          ad[1] * painted_ps3D.length_view_point + ", " +
          ad[2] * painted_ps3D.length_view_point + "},\n   ViewVertical->{" +
          ad1[0] / painted_ps3D.option_BoxRatios[0] + ", " +
          ad1[1] / painted_ps3D.option_BoxRatios[1] + ", " +
          ad1[2] / painted_ps3D.option_BoxRatios[2] + "}";
      System.out.println(s);
    }
    else
    if (i == 115) {
      if (!is_stereo) {
        is_stereo = true;
      }
      else
      if (stereo_distance > 0.0D) {
        stereo_distance = -stereo_distance;
        if (stereo_distance > 0.0D) {
          stereo_distance = -0.050000000000000003D;
        }
      }
      else {
        stereo_distance = -stereo_distance;
        is_stereo = false;
      }
      is_new_dragging = true;
    }
    return true;
  }

  public void update(Graphics g) {
    paint(g);
  }

  public void paint(Graphics g) {
    if (image_buffer != null) {
      g.drawImage(image_buffer, 0, 0, this);
      is_put_on_screen = true;
    }
  }

  public void paintGraphics3D() {
    active_primitive = null;
    if (ps3D.is_stereo && second_graphics_buffer == null) {
      second_image_buffer = createImage( (applet_width + 1) / 2, applet_height);
      second_graphics_buffer = second_image_buffer.getGraphics();
      GUIUtils.setGraphics2DAntiAlias( (Graphics2D) second_graphics_buffer);
    }
    if (bg_image == null) {
      graphics_buffer.setColor(ps3D.option_Background);
      graphics_buffer.fillRect(0, 0, ps3D.pixel_width, ps3D.pixel_height);
      if (ps3D.is_stereo) {
        second_graphics_buffer.setColor(ps3D.option_Background);
        second_graphics_buffer.fillRect(0, 0, ps3D.pixel_width,
                                        ps3D.pixel_height);
      }
    }
    else {
      Image image = bg_image;
      Image image1 = bg_rotated_image;
      Image image2 = bg_right_image;
      Image image3 = bg_rotated_right_image;
      if (ps3D.is_stereo && ps3D.stereo_distance < 0.0D) {
        image = bg_right_image;
        image1 = bg_rotated_right_image;
        image2 = bg_image;
        image3 = bg_rotated_image;
      }
      if (bg_is_spherical) {
        int i = ( (bg_width / 2 + bg_width) - bg_x_offset) % bg_width;
        int k = ( (ps3D.pixel_height - bg_height) / 2 + bg_height + bg_y_offset) %
            (bg_height + bg_height) - bg_height;
        int i1 = 0;
        if (k > 0) {
          i1 = ( ( (ps3D.pixel_width + bg_width) / 2 + i) -
                ps3D.pixel_stereo_offset) % bg_width - bg_width;
          for (int j2 = i1; j2 < ps3D.pixel_width; j2 += bg_width) {
            graphics_buffer.drawImage(image1, j2, k - bg_height, null);
          }

          if (ps3D.is_stereo) {
            i1 = ( (ps3D.pixel_width + bg_width) / 2 + i +
                  ps3D.pixel_stereo_offset) % bg_width - bg_width;
            for (int j3 = i1; j3 < ps3D.pixel_width; j3 += bg_width) {
              second_graphics_buffer.drawImage(image3, j3, k - bg_height, null);
            }

          }
        }
        i1 = ( ( (ps3D.pixel_width + bg_width) / 2 + bg_x_offset) -
              ps3D.pixel_stereo_offset) % bg_width - bg_width;
        for (int k2 = i1; k2 < ps3D.pixel_width; k2 += bg_width) {
          graphics_buffer.drawImage(image, k2, k, null);
        }

        if (ps3D.is_stereo) {
          int j1 = ( (ps3D.pixel_width + bg_width) / 2 + bg_x_offset +
                    ps3D.pixel_stereo_offset) % bg_width - bg_width;
          for (int k3 = j1; k3 < ps3D.pixel_width; k3 += bg_width) {
            second_graphics_buffer.drawImage(image2, k3, k, null);
          }

        }
        if (k + bg_height < ps3D.pixel_height) {
          int k1 = ( ( (ps3D.pixel_width + bg_width) / 2 + i) -
                    ps3D.pixel_stereo_offset) % bg_width - bg_width;
          for (int l3 = k1; l3 < ps3D.pixel_width; l3 += bg_width) {
            graphics_buffer.drawImage(image1, l3, k + bg_height, null);
          }

          if (ps3D.is_stereo) {
            int l1 = ( (ps3D.pixel_width + bg_width) / 2 + i +
                      ps3D.pixel_stereo_offset) % bg_width - bg_width;
            for (int j4 = l1; j4 < ps3D.pixel_width; j4 += bg_width) {
              second_graphics_buffer.drawImage(image3, j4, k + bg_height, null);
            }

          }
        }
      }
      else {
        int j = ps3D.pixel_stereo_offset;
        if (bg_is_fixed) {
          j = 0;
        }
        int l = ( (ps3D.pixel_height + bg_height) / 2 + bg_y_offset) %
            bg_height - bg_height;
        for (int i2 = l; i2 < ps3D.pixel_height; i2 += bg_height) {
          int l2 = ( ( (ps3D.pixel_width + bg_width) / 2 + bg_x_offset) - j) %
              bg_width - bg_width;
          for (int i4 = l2; i4 < ps3D.pixel_width; i4 += bg_width) {
            graphics_buffer.drawImage(image, i4, i2, null);
          }

          if (ps3D.is_stereo) {
            int i3 = ( (ps3D.pixel_width + bg_width) / 2 + bg_x_offset + j) %
                bg_width - bg_width;
            for (int k4 = i3; k4 < ps3D.pixel_width; k4 += bg_width) {
              second_graphics_buffer.drawImage(image2, k4, i2, null);
            }

          }
        }

      }
    }
    ps3D.paint(graphics_buffer, second_graphics_buffer, second_image_buffer);
    painted_ps3D = ps3D;
    repaint();
  }

  public Live() {
    animation_display_time = 0.050000000000000003D;
    animation_direction = 1;
    is_animating = true;
    is_put_on_screen = true;
    min_height_width = 100;
    text_x = 10;
    applet_width = size().width;
    applet_height = size().height;
    is_dragging = false;
    down_mouse_x = 50;
    down_mouse_y = 50;
    last_x = 50;
    last_y = 50;
    is_control_down = false;
    is_meta_down = false;
    is_shift_down = false;
    is_mouse_here = false;
    length_view_point_factor = 1.0D;
    magnification_factor = 1.0D;
    rotation = new Quaternion(1.0D, 0.0D, 0.0D, 0.0D);
    stereo_distance = 0.050000000000000003D;
    preceding_length_view_point_factor = 1.0D;
    preceding_magnification_factor = 1.0D;
    preceding_rotation = rotation;
    preceding_stereo_distance = 0.050000000000000003D;
    is_new_dragging = false;
    initialized = false;
  }

  Parser parser;
  Graphics3D ps3D;
  Graphics3D painted_ps3D;
  Vector frames;
  int current_frame_index;
  double animation_display_time;
  int animation_direction;
  long painted_time;
  boolean is_animating;
  Thread painter;
  Image image_buffer;
  Graphics graphics_buffer;
  Image second_image_buffer;
  Graphics second_graphics_buffer;
  Image bg_image;
  Image bg_rotated_image;
  Image bg_right_image;
  Image bg_rotated_right_image;
  int bg_width;
  int bg_height;
  boolean bg_is_fixed;
  boolean bg_is_cylindrical;
  boolean bg_is_spherical;
  Color background_color;
  Color foreground_color;
  Color point_edge_color;
  boolean is_put_on_screen;
  int min_height_width;
  Primitive3D active_primitive;
  int text_x;
  int text_y;
  int applet_width;
  int applet_height;
  boolean is_dragging;
  static String empty_string = "";
  static String enter_string = "LiveGraphics3D -1.50: Please drag to rotate.";
  static String syntax_error_string =
      "LiveGraphics3D applet stopped because of a syntax error.";
  static String url_error_string = "Malformed URL: ";
  static String equal_sign_string = "=";
  int down_mouse_x;
  int down_mouse_y;
  long down_time;
  int last_x;
  int last_y;
  boolean is_control_down;
  boolean is_meta_down;
  boolean is_shift_down;
  boolean is_mouse_here;
  double length_view_point_factor;
  double magnification_factor;
  int cut_primitives_count;
  Quaternion rotation;
  int bg_x_offset;
  int bg_y_offset;
  boolean is_stereo;
  double stereo_distance;
  double preceding_length_view_point_factor;
  double preceding_magnification_factor;
  int preceding_cut_primitives_count;
  Quaternion preceding_rotation;
  int preceding_bg_x_offset;
  int preceding_bg_y_offset;
  double preceding_stereo_distance;
  int preceding_frame_index;
  boolean is_new_dragging;
  boolean initialized;

}
