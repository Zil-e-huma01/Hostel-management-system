package hostel_management;

public class Resident {
	   private String cnic;
	   private String roomNumber;
	   private String residentId;
	   private String checkInDate;
	   private String rent;
	   private String name;
	   private String phone;
	   
	   public Resident() {
	   }

	   // Constructor
	   public Resident(String cnic, String roomNumber, String residentId, String checkInDate, String rent) {
	       this.cnic = cnic;
	       this.roomNumber = roomNumber;
	       this.residentId = residentId;
	       this.checkInDate = checkInDate;
	       this.rent = rent;
	   }

	   // Getters and setters
	   public String getCnic() {
	       return cnic;
	   }
	   public void setName(String name) {
		      this.name = name;
		  }

		  public void setPhone(String phone) {
		      this.phone = phone;
		  }

	   public void setCnic(String cnic) {
	       this.cnic = cnic;
	   }
	   
	   public String getName() {
	       return this.name;
	   }

	   public String getPhone() {
	       return this.phone;
	   }
	   // Other getters and setters...
	}