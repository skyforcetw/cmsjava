/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.bigatti.it/projects/jacobgen)
 */
package auo.cms.measure.cp.i2c;

import com.jacob.com.*;

public class _I2C extends Dispatch {

	public static final String componentName = "AUOI2C._I2C";

	public _I2C() {
		super(componentName);
	}

	/**
	* This constructor is used instead of a case operation to
	* turn a Dispatch object into a wider object - it must exist
	* in every wrapper class whose instances may be returned from
	* method calls wrapped in VT_DISPATCH Variants.
	*/
	public _I2C(Dispatch d) {
		// take over the IDispatch pointer
		m_pDispatch = d.m_pDispatch;
		// null out the input's pointer
		d.m_pDispatch = 0;
	}

	public _I2C(String compName) {
		super(compName);
	}

	public void frc_dg_en(String tcon, boolean frc_en, boolean lastParam) {
		Dispatch.call(this, "frc_dg_en", tcon, new Variant(frc_en), new Variant(lastParam));
	}

	public boolean write_lut(String tcon, String lastParam) {
		return Dispatch.call(this, "write_lut", tcon, lastParam).toBoolean();
	}

}
