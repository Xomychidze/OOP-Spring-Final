package university.enums;

public enum School {
    FIT("School of Information Technology"),
    BS("Business School"),
    KMA("Kazakhstan Management Academy"),
    ISE("School of Industrial Engineering");

    private final String fullName;

    School(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return name() + " (" + fullName + ")";
    }
}
