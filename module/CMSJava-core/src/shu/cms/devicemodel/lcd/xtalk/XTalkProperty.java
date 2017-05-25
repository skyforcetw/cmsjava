package shu.cms.devicemodel.lcd.xtalk;

import shu.cms.colorspace.depend.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * xtalk影響效應, 具有 "左方xtalk效應" 及 "右方xtalk效應" 兩種.
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public abstract class XTalkProperty {

  public static void main(String[] args) {
    System.out.println(rightXTalkProperty.getNextAdjacentChannel(RGBBase.
        Channel.B));
  }

  static XTalkProperty rightXTalkProperty = new RightXTalkProperty();
  static XTalkProperty leftXTalkProperty = new LeftXTalkProperty();

  public final static XTalkProperty getRightXTalkProperty() {
    return rightXTalkProperty;
  }

  public final static XTalkProperty getLeftXTalkProperty() {
    return leftXTalkProperty;
  }

  public final static XTalkProperty getOppositeProperty(XTalkProperty property) {
    if (property instanceof LeftXTalkProperty) {
      return rightXTalkProperty;
    }
    else if (property instanceof RightXTalkProperty) {
      return leftXTalkProperty;
    }
    else {
      throw new IllegalArgumentException("");
    }
  }

  protected final static class RightXTalkProperty
      extends XTalkProperty {
    public RGBBase.Channel getNextAdjacentChannel(RGBBase.Channel ch) {
      return getAdjacentChannel(getAdjacentChannel(ch));
    }

    public RGBBase.Channel getAdjacentChannel(RGBBase.Channel ch) {
      switch (ch) {
        case R:
        case Y:
          return RGBBase.Channel.G;
        case C:
        case G:
          return RGBBase.Channel.B;
        case M:
        case B:
          return RGBBase.Channel.R;

        default:
          return null;
      }
    }

    public RGBBase.Channel getSelfChannel(RGBBase.Channel ch) {
      switch (ch) {
        case Y:
          return RGBBase.Channel.R;
        case M:
          return RGBBase.Channel.B;
        case C:
          return RGBBase.Channel.G;
        default:
          return null;
      }
    }

  }

  protected final static class LeftXTalkProperty
      extends XTalkProperty {
    public RGBBase.Channel getNextAdjacentChannel(RGBBase.Channel ch) {
      return getAdjacentChannel(getAdjacentChannel(ch));
    }

    public RGBBase.Channel getAdjacentChannel(RGBBase.Channel ch) {
      switch (ch) {
        case G:
        case Y:
          return RGBBase.Channel.R;
        case R:
        case M:
          return RGBBase.Channel.B;
        case B:
        case C:
          return RGBBase.Channel.G;
        default:
          return null;
      }
    }

    public RGBBase.Channel getSelfChannel(RGBBase.Channel ch) {
      switch (ch) {
        case Y:
          return RGBBase.Channel.G;
        case M:
          return RGBBase.Channel.R;
        case C:
          return RGBBase.Channel.B;
        default:
          return null;
      }
    }

  }

  public abstract RGBBase.Channel getAdjacentChannel(RGBBase.Channel ch);

  public abstract RGBBase.Channel getNextAdjacentChannel(RGBBase.Channel ch);

  public abstract RGBBase.Channel getSelfChannel(RGBBase.Channel ch);
}
