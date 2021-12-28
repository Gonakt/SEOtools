public class CSSValidationIssue {

    String row;
    String type;
    String description;
    String address;
    String element;

    CSSValidationIssue(String row, String type, String description, String address, String element) {

        this.row = row;
        this.type = type;
        this.description = description;
        this.address = address;
        this.element = element;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }
}
