package Model;

public class EquipmentModel {
    private String id;
    private String description;
    private String branding;
    private String status;

    public EquipmentModel(String id, String description, String branding, String status) {
        this.id = id;
        this.description = description;
        this.branding = branding;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBranding() {
        return branding;
    }

    public void setBranding(String branding) {
        this.branding = branding;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "EquipmentModel{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", branding='" + branding + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
