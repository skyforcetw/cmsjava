package livegraphics3d;

// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 2009/3/2 ¤U¤È 11:23:49
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Live.java


class Quaternion {

  public Quaternion(double d, double d1, double d2, double d3) {
    s = d;
    x = d1;
    y = d2;
    z = d3;
  }

  public Quaternion(double d, double d1, double d2, double d3, boolean flag) {
    if (flag) {
      double d6 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
      double d4 = Math.sin(d / 2D) / d6;
      s = Math.cos(d / 2D);
      x = d4 * d1;
      y = d4 * d2;
      z = d4 * d3;
      return;
    }
    else {
      double d5 = Math.sin(d / 2D);
      s = Math.cos(d / 2D);
      x = d5 * d1;
      y = d5 * d2;
      z = d5 * d3;
      return;
    }
  }

  public void add(Quaternion quaternion) {
    s = s + quaternion.s;
    x = x + quaternion.x;
    y = y + quaternion.y;
    z = z + quaternion.z;
  }

  public Quaternion sum(Quaternion quaternion) {
    return new Quaternion(s + quaternion.s, x + quaternion.x, y + quaternion.y,
                          z + quaternion.z);
  }

  public void conjugate() {
    x = -x;
    y = -y;
    z = -z;
  }

  public Quaternion conjugated() {
    return new Quaternion(s, -x, -y, -z);
  }

  public void multiply(Quaternion quaternion) {
    double d = s * quaternion.s - x * quaternion.x - y * quaternion.y -
        z * quaternion.z;
    double d1 = (s * quaternion.x + x * quaternion.s + y * quaternion.z) -
        z * quaternion.y;
    double d2 = (s * quaternion.y - x * quaternion.z) + y * quaternion.s +
        z * quaternion.x;
    double d3 = ( (s * quaternion.z + x * quaternion.y) - y * quaternion.x) +
        z * quaternion.s;
    s = d;
    x = d1;
    y = d2;
    z = d3;
  }

  public Quaternion product(Quaternion quaternion) {
    double d = s * quaternion.s - x * quaternion.x - y * quaternion.y -
        z * quaternion.z;
    double d1 = (s * quaternion.x + x * quaternion.s + y * quaternion.z) -
        z * quaternion.y;
    double d2 = (s * quaternion.y - x * quaternion.z) + y * quaternion.s +
        z * quaternion.x;
    double d3 = ( (s * quaternion.z + x * quaternion.y) - y * quaternion.x) +
        z * quaternion.s;
    return new Quaternion(d, d1, d2, d3);
  }

  public void normalize() {
    double d = Math.sqrt(s * s + x * x + y * y + z * z);
    s = s / d;
    x = x / d;
    y = y / d;
    z = z / d;
  }

  public Quaternion normalized() {
    double d = Math.sqrt(s * s + x * x + y * y + z * z);
    return new Quaternion(s / d, x / d, y / d, z / d);
  }

  public boolean equals(Quaternion quaternion) {
    return s == quaternion.s && x == quaternion.x && y == quaternion.y &&
        z == quaternion.z;
  }

  public double[] rotated(double ad[]) {
    double d = s * x;
    double d1 = s * y;
    double d2 = s * z;
    double d3 = x * x;
    double d4 = x * y;
    double d5 = x * z;
    double d6 = y * y;
    double d7 = y * z;
    double d8 = z * z;
    double ad1[][] = {
        {
        2D * (0.5D - d6 - d8), 2D * (d4 - d2), 2D * (d5 + d1)
    }, {
        2D * (d4 + d2), 2D * (0.5D - d3 - d8), 2D * (d7 - d)
    }, {
        2D * (d5 - d1), 2D * (d7 + d), 2D * (0.5D - d3 - d6)
    }
    };
    double ad2[] = new double[3];
    for (int i = 0; i < 3; i++) {
      ad2[i] = ad1[i][0] * ad[0] + ad1[i][1] * ad[1] + ad1[i][2] * ad[2];
    }

    return ad2;
  }

  public double s;
  public double x;
  public double y;
  public double z;
}
