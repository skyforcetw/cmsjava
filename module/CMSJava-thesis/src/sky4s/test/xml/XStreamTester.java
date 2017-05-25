package sky4s.test.xml;

import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.io.xml.*;
import shu.util.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class XStreamTester {

  public static void main(String[] args) {
//    XStream xstream = new XStream();
    XStream xstream = new XStream(new DomDriver()); // does not require XPP3 library

//    xstream.alias("person", Person.class);
//    xstream.alias("phonenumber", PhoneNumber.class);

    Person joe = new Person("Joe", "Walnes");
    joe.setPhone(new PhoneNumber(123, "1234-456"));
    joe.setFax(new PhoneNumber(123, "9999-999"));

    Persistence.writeObjectAsXML(joe, "joe.xml");
    joe = (Person) Persistence.readObjectAsXML("joe.xml");
    System.out.println(joe.firstname + " " + joe.lastname);
  }
}

class Person {
  public Person(String firstname, String lastname) {
    this.firstname = firstname;
    this.lastname = lastname;
  }

  public String firstname;
  public String lastname;
  public PhoneNumber phone;
  public PhoneNumber fax;

  public void setPhone(PhoneNumber phone) {
    this.phone = phone;
  }

  public void setFax(PhoneNumber fax) {
    this.fax = fax;
  }

  // ... constructors and methods
}

class PhoneNumber {
  public PhoneNumber(int code, String number) {
    this.code = code;
    this.number = number;
  }

  public int code;
  public String number;
  // ... constructors and methods
}
