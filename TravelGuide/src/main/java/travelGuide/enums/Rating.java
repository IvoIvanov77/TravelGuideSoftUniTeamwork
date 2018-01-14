package travelGuide.enums;

public enum Rating {
    Sucks (1),
    Bad (2),
    Meh(3),
    Good(4),
    Rocks(5);

    private Integer value;

    Rating(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
