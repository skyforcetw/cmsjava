package auo.simplex;

import java.io.*;

public class Objfunc implements Objfun
{
	public double evalObjfun(double x[]){
		return (100*(x[1]-x[0]*x[0])*(x[1]-x[0]*x[0])+(1.0-x[0])*(1.0-x[0]));
	}
}