/*
 * This file is part of the AusStage Utilities Package
 *
 * The AusStage Utilities Package is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Utilities Package is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Utilities Package.  
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * @resource: http://www.java2s.com/Code/Java/2D-Graphics-GUI/Aclasstorepresentalatitudeandlongitude.htm
*/

package model;

//import additional libraries
import java.text.DecimalFormat;

/**
 * A class to represent a latitude and longitude
 */
public class coordinate implements Comparable<coordinate>{

  // declare private class level variables
  private float latitude;
  private float longitude;
  private DecimalFormat format;
  
  /**
   * Added by Masoud Gholami
   * Default constructor for this class
   *
   * @author Masoud Gholami
   */
  public coordinate() {  
    this.format = new DecimalFormat("##.######");
  }
  
  /**
   * Constructor for this class
   *
   * @param latitude a latitude coordinate in decimal notation
   * @param longitude a longitude coordinate in decimal notation
   */
  public coordinate(float latitude, float longitude) {
  
    if(CoordinateManager.isValidLatitude(latitude) == true && CoordinateManager.isValidLongitude(longitude) == true) {
      this.latitude = latitude;
      this.longitude = longitude;
    } else {
      throw new IllegalArgumentException("The parameters did not pass validation as defined by the CoordinateManager class");
    }
    
    this.format = new DecimalFormat("##.######");
  }
  
  /*
   * get and set methods
   */
  public float getLatitude() {
    return latitude;
  }
  
  public float getLongitude() {
    return longitude;
  }
  
  public void setLatitude(float latitude) {
    if(CoordinateManager.isValidLatitude(latitude) == true) {
      this.latitude = latitude;
    } else {
      throw new IllegalArgumentException("The parameter did not pass validation as defined by the CoordinateManager class");
    }
  }
  
  public void setLongitude(float longitude) {
    if(CoordinateManager.isValidLongitude(longitude) == true) {  
      this.longitude = longitude;
    } else {
      throw new IllegalArgumentException("The parameter did not pass validation as defined by the CoordinateManager class");
    }
  }
  
  public String getLatitudeAsString() {
  
    return format.format(latitude);
  }
  
  public String getLongitudeAsString() {
    return format.format(longitude);
  }
  
  public String toString() {
    return format.format(latitude) + "; " + format.format(longitude);
  }
  
  /**
   * Parses a valid coordination string
   * @param str	coordination string
   * @see	coordinate
   * @author Masoud Gholami
   */
  public static coordinate Parse(String str){
	  String[] parts = str.split(";");
	  parts[0] = parts[0].replace(",", ".");
	  parts[1] = parts[1].replace(",", ".");
	  float latitude = Float.parseFloat(parts[0].trim());
	  float longitude = Float.parseFloat(parts[1].trim());
	  return new coordinate(latitude, longitude);
  }
  
  /*
   * methods required for ordering in collections
   * http://java.sun.com/docs/books/tutorial/collections/interfaces/order.html
   */

  /**
   * A method to determine if one event is the same as another
   *
   * @param o the object to compare this one to
   *
   * @return  true if they are equal, false if they are not
   */
  public boolean equals(Object o) {
    // check to make sure the object is an event
    if ((o instanceof coordinate) == false) {
      // o is not an event object
       return false;
    }
    
    // compare these two events
    coordinate c = (coordinate)o;
    
    // build items for comparison
    String me  = this.getLatitudeAsString() + this.getLongitudeAsString();
    String you = c.getLatitudeAsString() + c.getLongitudeAsString();
    
    return me.equals(you);
    
  } // end equals method
  
  /**
   * Overide the default hashcode method
   * 
   * @return a hashcode for this object
   */
  public int hashCode() {
  
    String me = this.getLatitudeAsString() + this.getLongitudeAsString();
    return 31*me.hashCode();
  }
    
    /**
     * The compareTo method compares the receiving object with the specified object and returns a 
     * negative integer, 0, or a positive integer depending on whether the receiving object is 
     * less than, equal to, or greater than the specified object.
     *
     * @param c the event to compare this one to
     *
     * @return  an integer indicating comparison result
     */    
  public int compareTo(coordinate c) {
  
    String me  = this.getLatitudeAsString() + this.getLongitudeAsString();
    String you = c.getLatitudeAsString() + c.getLongitudeAsString();
    
    Double meDbl  = Double.valueOf(me);
    Double youDbl = Double.valueOf(you);
    
    if(meDbl == youDbl) {
      return 0;
    } else {
      Double tmp = Math.floor(meDbl - youDbl);
      return tmp.intValue();
    }
    
  } // end compareTo method
  
  /**
   * added by Masoud Gholami
   * 
   * Computes the distance between this coordinate and
   * another coordinate in kilometers
   * 
   * @param	c	the other coordinate
   * @return	distance in kilometers
   * @see		coordinate
   * @author	Masoud Gholami	
   */
  public double getDistanceTo(coordinate co){
	  double delta_long = Math.toRadians(this.getLongitude() - co.getLongitude());
	  double delta_lat = Math.toRadians(this.getLatitude() - co.getLatitude());
	  
	  double a = Math.pow(Math.sin(delta_lat / 2), 2)
			  + Math.cos(Math.toRadians(co.getLatitude())) * 
			  Math.cos(Math.toRadians(this.getLatitude()))
			  * Math.pow(Math.sin(delta_long / 2), 2);
	  
	  double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));	  
	  double d = CoordinateManager.EARTH_DIAMETER * c / 2;
	  
	  return d;
  }
}
