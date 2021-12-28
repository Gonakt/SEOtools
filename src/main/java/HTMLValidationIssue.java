public class HTMLValidationIssue {

    String row;
    String type;
    String description;

    HTMLValidationIssue(String row, String type, String description) {

        this.row = row;
        this.type = type;
        this.description = description;
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
}
