package sky4s.test.ui.table.hide;

import java.awt.*;
import javax.swing.*;

/**
 * @version 1.0 05/29/99
 */
public class ColumnButtonScrollPaneLayout
    extends ScrollPaneLayout {

  public ColumnButtonScrollPaneLayout() {
    super.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
  }

  public void setVerticalScrollBarPolicy(int x) {
// VERTICAL_SCROLLBAR_ALWAYS
    super.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
  }

  public void layoutContainer(Container parent) {
    super.layoutContainer(parent);

    if ( (colHead == null) || (!colHead.isVisible()) ||
        (upperRight == null) || (vsb == null)) {
      return;
    }

    Rectangle vsbR = new Rectangle(0, 0, 0, 0);
    vsbR = vsb.getBounds(vsbR);

    Rectangle colHeadR = new Rectangle(0, 0, 0, 0);
    colHeadR = colHead.getBounds(colHeadR);
    colHeadR.width -= vsbR.width;
    colHead.getBounds(colHeadR);

    Rectangle upperRightR = upperRight.getBounds();
    upperRightR.x -= vsbR.width;
    upperRightR.width += vsbR.width + 1;
    upperRight.setBounds(upperRightR);
  }
}
