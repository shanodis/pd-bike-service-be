package me.project.enums;

public enum JwtExpire {

    ACCESS_TOKEN(4 * 60 * 1000),
    REFRESH_TOKEN(12 * 60 * 60 * 1000);

    private final Integer amount;

    JwtExpire(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }
}
