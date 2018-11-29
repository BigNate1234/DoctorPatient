/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.stream.XMLStreamException;

/**
 * @author nathan
 *
 */
public class VisitImpl<V, T> implements Serializable, Visit<V, T>{
	private static final long serialVersionUID = 1768797638105769146L;
	
	private V visitor;
	private T host;
	private Date vDate;
	private static int VID=100;
	private Integer visitID;

	public VisitImpl (V vst, T h, Date d) {
		visitor = vst;
		host = h;
		vDate = d;
		visitID = VID;
		VID++;
	}
	
	public VisitImpl (V vst, T h, Date d, Integer vid) {
		visitor = vst;
		host = h;
		vDate = d;
		visitID = vid;
	}
	
	public V getVisitor() {
		return visitor;
	}
	
	public T getHost() {
		return host;
	}
	
	public Date getDate() {
		return vDate;
	}
	
	public String getDateFormatted() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // ignore time zones for simplicity
		String dateStr = df.format(vDate.getTime());
		
		return dateStr;
	}
	
	public Integer getVisitID() {
		return visitID;
	}
	
	public void changeDate(Date d) {
		this.vDate = d;
	}
	
	public boolean equals(Object other) {
		if(this == other) return true; // only if passing same object as that object calling method is same

		if(other == null || (this.getClass() != other.getClass())){
			return false;
		}

		@SuppressWarnings("unchecked")
		Visit<V, T> v = (Visit<V, T>) other;
		return (this.visitor != null && this.visitor.equals(v.getVisitor())) && // using equals of visitor (PatientImpl)
				(this.visitID != null && this.visitID == v.getVisitID()) &&
				(this.host != null && this.host.equals(v.getHost())) && // using equals of host (DoctorImpl)
				(this.vDate != null && this.vDate.equals(v.getDate())); // using equals of date
	}
	
	
	public int hashCode() {
		int result = 0;
		result = 31*result + (visitor != null ? visitor.hashCode() : 0);
		result = 31*result + (host !=null ? host.hashCode() : 0);
		result = 31*result + (vDate  !=null ? vDate.hashCode() : 0);

		return result;
	}
	
	public String toString() {
		Integer temp = 0;
		// if doctorID and patientID
		if (this.visitor.getClass().equals(temp.getClass()) && this.host.getClass().equals(temp.getClass())) {
			SchedulerData data = null;
			try {
				data = SchedulerXMLReaderUtils.readSchedulingXML("resources/schedulerData.xml");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return Utilities.getDoctor((Integer)(this.host), data.getDocList()).toString() + " : " +
					Utilities.getPatient((Integer)(this.visitor), data.getPatList()).toString() + " : " +
					this.getDateFormatted();
		}
		return visitor.toString() + "//" + host.toString() + "//" + vDate.toString();
	}
}
