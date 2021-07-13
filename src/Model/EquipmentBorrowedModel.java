package Model;

public class EquipmentBorrowedModel {
    private String brwId;
    private String date;
    private String memberId;
    private String eqpId;

    public EquipmentBorrowedModel(String brwId, String date, String memberId, String eqpId) {
        this.brwId = brwId;
        this.date = date;
        this.memberId = memberId;
        this.eqpId = eqpId;
    }

    public String getBrwId() {
        return brwId;
    }

    public void setBrwId(String brwId) {
        this.brwId = brwId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getEqpId() {
        return eqpId;
    }

    public void setEqpId(String eqpId) {
        this.eqpId = eqpId;
    }
}
