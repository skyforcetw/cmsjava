package sky4s.test.wb;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2001</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 * @deprecated
 */
public interface CATConst {
  public interface VonKries {
    double[] M = new double[] {
        0.3897, 0.6890, -0.0787,
        -0.2298, 1.1834, 0.0464,
        0, 0, 1
    };
  }

  public interface BFD {
    double[] M = new double[] {
        0.8951, 0.2664, 0.1614,
        -0.7502, 1.7135, 0.0367,
        0.0389, 0.0685, 1.0296
    };
  }

  public interface Sharp {
    double[] M = new double[] {
        1.2694, -0.0988, -0.1706,
        -0.8364, 1.8006, 0.0357,
        0.0297, -0.0315, 1.0018
    };
  }

  public interface CAT02 {
    double[] M = new double[] {
        0.4002, 0.7076, -0.0808,
        -0.2263, 1.1653, 0.0457,
        0, 0, 0.9182
    };

  }

}
