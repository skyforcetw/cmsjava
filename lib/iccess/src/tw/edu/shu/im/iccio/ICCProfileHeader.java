package tw.edu.shu.im.iccio;

import java.io.*;
import java.util.*;

import tw.edu.shu.im.iccio.datatype.*;

/**
 * ICCProfileHeader contains the ICC profile header which is a fixed 128-byte data block at the beginning of the file.
 *
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 *
 * The ICC profile header is 128-byte long, consisting of 18 fields as follows:
 * <code>
 *	Profile size, 4-byte int, header+tag_table+elements
 *	Preferrred CMM type, 4-byte, 0 if no preferred CMM
 *	Profile version number, 4-byte, currently, 4.2.0.0 (04200000h)
 *	Profile/Device Class, 4-byte, *
 *	Color space of data, 4-byte**
 *	Profile Connection Space, 4-byte, XYZData or labData, or any for DeviceLink class
 *	Date and time of profile created, 12
 *	'acsp'(61637370h) profile file signature, 4
 *	Primary platform signature, 4
 *	Profile flags to indicate various options, 4
 *	Device manufacturer of device, 4
 *	Device model of the device for profile, 4
 *	Device attributes unique to setup, 8
 *	Rendering intent, 4
 *	XYZ values of illuminant of PCS, D50 XYZNumber, 12
 *	Profile Creator signature, 4
 *	Profile ID, 16
 *	Reserved, 28, must be zeros
 * </code>
 */
public class ICCProfileHeader implements Saveable {
    public static final int ICC_PROFILE_HEADER_SIZE = 128;
    private static final int ICC_PROFILE_HEADER_RESERVED = 28;

    public static final int PROFILE_SIZE_OFFSET = 0;
    public static final int PREFERRED_CMM_TYPE_OFFSET = 4;
    public static final int VERSION_NUMBER_OFFSET = 8;
    public static final int DEVICE_CLASS_OFFSET = 12;
    public static final int COLOR_SPACE_OFFSET = 16;
    public static final int PCS_OFFSET = 20;
    public static final int DATE_TIME_OFFSET = 24;
    public static final int SIGNATURE_OFFSET = 36;
    public static final int PLATFORM_SIGNATURE_OFFSET = 40;
    public static final int FLAG_OFFSET = 44;
    public static final int MANUFACTURER_OFFSET = 48;
    public static final int DEVICE_MODEL_OFFSET = 52;
    public static final int DEVICE_ATTRIBUTE_OFFSET = 56;
    public static final int RENDER_INTENT_OFFSET = 64;
    public static final int ILLUMINANT_OFFSET = 68;
    public static final int CREATOR_SIGNATURE_OFFSET = 80;
    public static final int PROFILE_ID_OFFSET = 84;
    public static final int RESERVED_OFFSET = 100;

    private UInt32Number profile_size_; //4-byte at offset 0, including header size+tag table size + elements size
    private Signature preferred_cmm_type_; //0 if not used
    private UInt32Number profile_version_; //4-byte version number
    private ProfileClass device_class_; //4-byte tag for device class
    private ColorSpace color_space_; //4-byte tag for color space
    private ColorSpace pcs_; //profile connection space
    private DateTimeNumber date_time_; //12-byte
    private Signature signature_; //4-byte 'acsp' 0x61637370
    private Platform platform_signature_; //4-byte
    private ProfileFlag flags_; //profile flags
    private Signature manufacturer_;
    private Signature device_model_;
    private DeviceAttribute device_attribute_; //8-byte
    private RenderIntent render_intent_;
    private XYZNumber illuminant_; //12-byte XYZNumber correspond to D50
    private Signature creator_signature_;
    private byte[] profile_id_; //16 bytes for profile ID
    private byte[] reserved_; //28 bytes for future expansion, must be zero

    /**
     * Construct an empty ICCProfileHeader for building a new one.
     * Some default values are assigned, so the derived classes should call this.
     */
    public ICCProfileHeader() {
        profile_size_ = new UInt32Number(0);
        preferred_cmm_type_ = new Signature(Platform.APPLE);
        profile_version_ = new UInt32Number(0x04200000);
        //profile_version_ = new UInt32Number(0x02100000);	//TODO: this is temporary test
        device_class_ = new ProfileClass();
        color_space_ = new ColorSpace(ColorSpace.RGB_DATA); //default
        pcs_ = new ColorSpace(ColorSpace.LAB_DATA);
        date_time_ = new DateTimeNumber(); //set current system time
        platform_signature_ = new Platform(Platform.MICROSOFT);
        flags_ = new ProfileFlag();
        manufacturer_ = new Signature(0);
        device_model_ = new Signature(0);
        device_attribute_ = new DeviceAttribute();
        render_intent_ = new RenderIntent(RenderIntent.PERCEPTUAL);
        try {
            signature_ = new Signature("acsp"); //?why this value needed?
            illuminant_ = new XYZNumber(0.964202880859375, 1.0,
                                        0.8249053955078125);
        } catch (ICCProfileException e) {
            //this is unlikely to happen
            System.err.println(e.getMessage());
        }
        creator_signature_ = new Signature(0);
        profile_id_ = new byte[16];
        reserved_ = new byte[ICC_PROFILE_HEADER_RESERVED];
    }

    /**
     * Clone an ICCProfileHeader object from another.
     * @param copy - ICCProfileHeader object to deep copy into this one.
     */
    public ICCProfileHeader(ICCProfileHeader copy) throws ICCProfileException {
        assert (copy != null);
        this.profile_size_ = new UInt32Number(copy.profile_size_);
        this.preferred_cmm_type_ = new Signature(copy.preferred_cmm_type_);
        this.profile_version_ = new UInt32Number(copy.profile_version_);
        this.device_class_ = new ProfileClass(copy.device_class_);
        this.color_space_ = new ColorSpace(copy.color_space_);
        this.pcs_ = new ColorSpace(copy.pcs_);
        this.date_time_ = new DateTimeNumber(copy.date_time_);
        this.signature_ = new Signature(copy.signature_);
        this.platform_signature_ = new Platform(copy.platform_signature_);
        this.flags_ = new ProfileFlag(copy.flags_);
        this.manufacturer_ = new Signature(copy.manufacturer_);
        this.device_model_ = new Signature(copy.device_model_);
        this.device_attribute_ = new DeviceAttribute(copy.device_attribute_);
        this.render_intent_ = new RenderIntent(copy.render_intent_);
        this.illuminant_ = new XYZNumber(copy.illuminant_);
        this.creator_signature_ = new Signature(copy.creator_signature_);
        this.profile_id_ = new byte[16];
        for (int i = 0; i < this.profile_id_.length; i++) {
            this.profile_id_[i] = copy.profile_id_[i];
        }
        this.reserved_ = new byte[ICC_PROFILE_HEADER_RESERVED];
    }

    /**
     * Construct an ICCProfileHeader object.
     * @param byteArray - byte array containing the ICC profile header which is 128 bytes long.
     */
    public ICCProfileHeader(byte[] byteArray) throws ICCProfileException {
        fromByteArray(byteArray, 0, 0);
    }

    /**
     * Parse a byte array and separate header elements into member variables.
     * @param byteArray - byte array to parse
     * @param index - from which byte to start to parse
     * @param size - number of bytes to use, not used in this method.
     */
    public void fromByteArray(byte[] byteArray, int index, int size) throws
            ICCProfileException {
        if (byteArray == null) {
            throw new ICCProfileException(
                    "ICCProfileHeader.fromByteArray():byteArray null",
                    ICCProfileException.NullPointerException);
        }
        if (index < 0 || index + 128 > byteArray.length) {
            throw new ICCProfileException(
                    "ICCProfileHeader.fromByteArray():index out of bounds",
                    ICCProfileException.IndexOutOfBoundsException);
        }
        if (size != 0 && size != ICC_PROFILE_HEADER_SIZE) {
            throw new ICCProfileException(
                    "ICCProfileHeader.fromByteArray():wrong size",
                    ICCProfileException.WrongSizeException);
        }

        profile_size_ = new UInt32Number(byteArray, index);
        preferred_cmm_type_ = new Signature(byteArray,
                                            PREFERRED_CMM_TYPE_OFFSET + index);
        profile_version_ = new UInt32Number(byteArray,
                                            VERSION_NUMBER_OFFSET + index); //assuming last two bytes are zero
        device_class_ = new ProfileClass(byteArray, DEVICE_CLASS_OFFSET + index);
        color_space_ = new ColorSpace(byteArray, COLOR_SPACE_OFFSET + index);
        pcs_ = new ColorSpace(byteArray, PCS_OFFSET + index);
        date_time_ = new DateTimeNumber(byteArray, DATE_TIME_OFFSET + index);
        signature_ = new Signature(byteArray, SIGNATURE_OFFSET + index);
        platform_signature_ = new Platform(byteArray,
                                           PLATFORM_SIGNATURE_OFFSET + index);
        flags_ = new ProfileFlag(byteArray, FLAG_OFFSET + index);
        manufacturer_ = new Signature(byteArray, MANUFACTURER_OFFSET + index);
        device_model_ = new Signature(byteArray, DEVICE_MODEL_OFFSET + index);
        device_attribute_ = new DeviceAttribute(byteArray,
                                                DEVICE_ATTRIBUTE_OFFSET + index);
        render_intent_ = new RenderIntent(byteArray,
                                          RENDER_INTENT_OFFSET + index);
        illuminant_ = new XYZNumber(byteArray, ILLUMINANT_OFFSET + index);
        creator_signature_ = new Signature(byteArray,
                                           CREATOR_SIGNATURE_OFFSET + index);
        profile_id_ = new byte[16];
        for (int i = 0; i < 16; i++) {
            profile_id_[i] = byteArray[i + PROFILE_ID_OFFSET];
        }
        reserved_ = new byte[28]; //cleared to zeror by default
        if (byteArray.length >= 128) {
            for (int i = 0; i < 28; i++) {
                reserved_[i] = byteArray[i + RESERVED_OFFSET];
            }
        }
    }

    /**
     * Write all header elements from member variables to a ICCFileOutput object.
     * @param dos - DataOutputStream to write the header data into.
     */
    public void save(ICCFileOutput out) throws ICCProfileException {
        byte[] ba = profile_size_.toByteArray(); //0: 4-byte int
        out.write(ba);
        ba = preferred_cmm_type_.toByteArray(); //4: 4 bytes
        out.write(ba);
        ba = profile_version_.toByteArray(); //8: 4 bytes
        out.write(ba);
        ba = device_class_.toByteArray(); //12: 4 bytes char
        out.write(ba);
        ba = color_space_.toByteArray(); //16: 4 bytes char
        out.write(ba);
        ba = pcs_.toByteArray(); //20: 4 bytes char
        out.write(ba);
        ba = date_time_.toByteArray(); //24: 12 bytes
        out.write(ba);
        ba = signature_.toByteArray(); //36: 4 bytes int
        out.write(ba);
        ba = platform_signature_.toByteArray(); //40: 4 bytes char
        out.write(ba);
        ba = flags_.toByteArray(); //44: 4 bytes char
        out.write(ba);
        ba = manufacturer_.toByteArray(); //48: 4 bytes int
        out.write(ba);
        ba = device_model_.toByteArray(); //52: 4 bytes int
        out.write(ba);
        ba = device_attribute_.toByteArray(); //56: 8 bytes long
        out.write(ba);
        ba = render_intent_.toByteArray(); //64: 4 bytes char
        out.write(ba);
        ba = illuminant_.toByteArray(); //68: 12 bytes XYZNumber
        out.write(ba);
        ba = creator_signature_.toByteArray(); //80: 4 bytes int
        out.write(ba);
        out.write(profile_id_); //84: 16 bytes
        out.write(reserved_); //100: 28 bytes
    }

    /**
     * Dump the ICC profile header information into the console.
     */
    public void dump() {
        System.out.println("---- ICC Profile ----");
        System.out.println("Profile Size         " + getProfileSize());
        System.out.println("Preferred CMM Type   " + getPreferredCmmType());
        System.out.println("Profile Version      " + getProfileVersionMajor() +
                           "." +
                           getProfileVersionMinor() + "." +
                           getProfileVersionBuild());
        System.out.println("Device Class         " + getDeviceClass());
        System.out.println("Color Space          " + getColorSpace());
        System.out.println("PCS                  " + getPcs());
        System.out.println("Date Time            " + getDateTime());
        System.out.println("Signature            " + getSignature());
        System.out.println("Platform Signature   " + getPlatformSignature());
        System.out.println("Flags                " + getFlags());
        System.out.println("Manufacturer         " + getManufacturer());
        System.out.println("Device Model         " + getDeviceModel());
        System.out.println("Device Attributes    " + getDeviceAttribute());
        System.out.println("Render Intent        " + getRenderIntent());
        System.out.println("Illuminant WP        " + getIlluminant().toString());
        if (profile_id_[0] != 0) {
            try {
                String s = new String(profile_id_, "ISO-8859-1");
                System.out.println("Profile ID           " + s);
            } catch (UnsupportedEncodingException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Return the ICC profile header size, should be 128.
     */
    public int size() {
        return ICC_PROFILE_HEADER_SIZE;
    }

    /**
     * Return the total file size of this profile.
     * It will return a valid value only when the header is loaded
     * from a valid ICC profile disk file.  If created from scratch,
     * it is necessary to set this value before saving to disk file.
     */
    public int getProfileSize() {
        return this.profile_size_.intValue();
    }

    /**
     * Set the total file size in bytes for the profile.
     * It is necessary to assign this value before saving the whole
     * profile to a disk file.
     * @param size - number of bytes as a long int.
     */
    public void setProfileSize(long size) throws ICCProfileException {
        this.profile_size_ = new UInt32Number(size);
    }

    public String getPreferredCmmType() {
        try {
            return this.preferred_cmm_type_.getSignature();
        } catch (ICCProfileException e) {
            System.err.println("getPreferredCmmType() exception:" +
                               e.getMessage());
        }
        return "";
    }

    /**
     * Set the preferred CMM type.
     * @param cmmtype - a string which should be less than 5 characters long.
     */
    public void setPreferredCmmType(String cmmtype) throws ICCProfileException {
        this.preferred_cmm_type_ = new Signature(cmmtype);
    }

    public int getProfileVersion() {
        return this.profile_version_.intValue();
    }

    public void setProfileVersion(short major, short minor, short build) {
        this.profile_version_ = new UInt32Number((major & 0xff) << 24 |
                                                 (minor & 0xff) << 20 |
                                                 (build & 0xff) << 16);
    }

    public int getProfileVersionMajor() {
        try {
            byte[] b = this.profile_version_.toByteArray();
            return b[0];
        } catch (ICCProfileException e) {
            System.err.println("getProfileVersionMajor() exception:" +
                               e.getMessage());
        }
        return 0;
    }

    public int getProfileVersionMinor() {
        try {
            byte[] b = this.profile_version_.toByteArray();
            return (b[1] & 0xff) >>> 4;
        } catch (ICCProfileException e) {
            System.err.println("getProfileVersionMinor() exception:" +
                               e.getMessage());
        }
        return 0;
    }

    public int getProfileVersionBuild() {
        try {
            byte[] b = this.profile_version_.toByteArray();
            return b[1] & 0xF;
        } catch (ICCProfileException e) {
            System.err.println("getProfileVersionBuild() exception:" +
                               e.getMessage());
        }
        return 0;
    }

    public String getDeviceClass() {
        return this.device_class_.toString();
    }

    public ProfileClass getDeviceClass_() {
        return this.device_class_;
    }

    public void setDeviceClass(int cls) {
        this.device_class_ = new ProfileClass(cls);
    }

    public String getColorSpace() {
        return this.color_space_.toString();
    }

    public ColorSpace getColorSpace_() {
        return this.color_space_;
    }

    public void setColorSpace(int cs) {
        this.color_space_ = new ColorSpace(cs);
    }

    public String getPcs() {
        return this.pcs_.toString();
    }

    public ColorSpace getPcs_() {
        return this.pcs_;
    }

    public void setPcs(int cs) {
        this.pcs_ = new ColorSpace(cs);
    }

    public String getDateTime() {
        return this.date_time_.toString();
    }

    public void setDateTime(Calendar d) {
        this.date_time_ = new DateTimeNumber(d);
    }

    public String getSignature() {
        try {
            return this.signature_.getSignature();
        } catch (ICCProfileException e) {
            System.err.println("getSignature() exception:" + e.getMessage());
        }
        return "";
    }

    public void setSignature(String sig) throws ICCProfileException {
        this.signature_ = new Signature(sig);
    }

    public String getPlatformSignature() {
        return this.platform_signature_.toString();
    }

    public void setPlatformSignature(int code) {
        this.platform_signature_ = new Platform(code);
    }

    public String getFlags() {
        return this.flags_.toString();
    }

    public void setFlags(int flags) {
        this.flags_ = new ProfileFlag(flags);
    }

    public String getManufacturer() {
        try {
            return this.manufacturer_.getSignature();
        } catch (ICCProfileException e) {
            System.err.println("getManufacturer() exception:" + e.getMessage());
        }
        return "";
    }

    public void setManufacturer(int sig) throws ICCProfileException {
        this.manufacturer_ = new Signature(sig);
    }

    public String getDeviceModel() {
        try {
            return this.device_model_.getSignature();
        } catch (ICCProfileException e) {
            System.err.println("getDeviceModel() exception:" + e.getMessage());
        }
        return "";
    }

    public void setDeviceModel(String model) throws ICCProfileException {
        this.device_model_ = new Signature(model);
    }

    public String getDeviceAttribute() {
        return this.device_attribute_.toString();
    }

    public void setDeviceAttribute(long attrib) {
        this.device_attribute_ = new DeviceAttribute(attrib);
    }

    public String getRenderIntent() {
        return this.render_intent_.toString();
    }

    public void setRenderIntent(int intent) {
        this.render_intent_ = new RenderIntent(intent);
    }

    public XYZNumber getIlluminant() {
        return this.illuminant_;
    }

    public void setIlluminant(double x, double y, double z) throws
            ICCProfileException {
        this.illuminant_ = new XYZNumber(x, y, z);
    }

    public String getCreatorSignature() {
        try {
            return this.creator_signature_.getSignature();
        } catch (ICCProfileException e) {
            System.err.println("getCreatorSignature() exception:" +
                               e.getMessage());
        }
        return "";
    }

    public void setCreatorSignature(String sig) throws ICCProfileException {
        this.creator_signature_ = new Signature(sig);
    }

    public byte[] getProfileId() {
        return this.profile_id_;
    }

    public String getProfileIdString() {
        try {
            String s = new String(profile_id_, "ISO-8859-1");
            return s.trim();
        } catch (UnsupportedEncodingException e) {
            System.err.println(e.getMessage());
        }
        return "";
    }

    /**
     * Set the profile ID byte string.
     * According to the specification, the profile id, if necessary, should
     * be a MD5 hashed code string of exactly 16 bytes long.
     * @param id - profile ID as a byte array
     */
    public void setProfileId(byte[] id) {
        this.profile_id_ = new byte[16];
        int n = id.length;
        if (n > 16) {
            n = 16;
        }
        for (int i = 0; i < n; i++) {
            this.profile_id_[i] = id[i];
        }
    }

    /**
     * Make an XML fragment of this ICC profile header.
     * @param name - attribute name of the header element.
     * @return XML fragment as a String.
     */
    public String toXmlString(String name) {
        StringBuffer sb = new StringBuffer();
        sb.append("<header");
        if (name != null && name.length() > 0) {
            sb.append(" name=\"" + name + "\">");
        } else {
            sb.append(">");
        }
        sb.append(profile_size_.toXmlString("ProfileSize"));
        sb.append(preferred_cmm_type_.toXmlString("Preferred_CMM_Type"));
        sb.append("<version major=\"" + getProfileVersionMajor() +
                  "\" minor=\"" +
                  getProfileVersionMinor() + "\" build=\"" +
                  getProfileVersionBuild() +
                  "\"/>");
        sb.append(device_class_.toXmlString("ProfileClass"));
        sb.append(color_space_.toXmlString("ColorSpace"));
        sb.append(pcs_.toXmlString("PCS"));
        sb.append(date_time_.toXmlString());
        sb.append(signature_.toXmlString("Signature"));
        sb.append(platform_signature_.toXmlString("Platform_Signature"));
        sb.append(flags_.toXmlString("Flags"));
        sb.append(manufacturer_.toXmlString("Manufacture"));
        sb.append(device_model_.toXmlString("DeviceModel"));
        sb.append(device_attribute_.toXmlString("Attributes"));
        sb.append(render_intent_.toXmlString("Render_Intent"));
        sb.append(illuminant_.toXmlString("Illuminant"));
        sb.append(creator_signature_.toXmlString("Creator Signature"));
        sb.append("<profileID>" + getProfileIdString() + "</profileID>");
        sb.append("</header>");
        return sb.toString();
    }

    public String toXmlString() {
        return toXmlString(null);
    }

}
