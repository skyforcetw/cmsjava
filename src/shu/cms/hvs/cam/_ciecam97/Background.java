package shu.cms.hvs.cam._ciecam97;

public class Background {

  public double k; //
  public double Fl; //
  public double n; //
  public double Nbb; //
  public double Ncb; //
  public double z; //

  public Background() {
    k = 0;
    Fl = 0;
    n = 0;
    Nbb = 0;
    Ncb = 0;
    z = 0;
  }

  public void getBackground(double La, double Yb, double Yw, double Fll) {

    k = 1.0 / (5 * La + 1.0);
    Fl = 0.2 * Math.pow(k, 4.0) * 5.0 * La +
        0.1 * Math.pow( (1.0 - Math.pow(k, 4.0)), 2.0) *
        Math.pow( (5.0 * La), (1.0 / 3.0));
    n = Yb / Yw;
    Nbb = 0.725 * Math.pow(1.0 / n, 0.2);
    Ncb = Nbb;
    z = 1 + Fll * Math.pow(n, 0.5);
  }
}
