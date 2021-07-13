package Model;

public class EquipmentReturnedModel {
    private String id;
    private String borrowedDate;
    private String returnedDate;
    private float fine;

    public EquipmentReturnedModel(String id, String borrowedDate, String returnedDate, float fine) {
        this.id = id;
        this.borrowedDate = borrowedDate;
        this.returnedDate = returnedDate;
        this.fine = fine;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(String borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public String getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(String returnedDate) {
        this.returnedDate = returnedDate;
    }

    public float getFine() {
        return fine;
    }

    public void setFine(float fine) {
        this.fine = fine;
    }
}
