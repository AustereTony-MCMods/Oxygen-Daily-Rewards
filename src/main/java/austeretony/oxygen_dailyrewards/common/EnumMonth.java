package austeretony.oxygen_dailyrewards.common;

public enum EnumMonth {

    JANUARY("january", 31),
    FEBRARY("febrary", 29),
    MARCH("march", 31),
    APRIL("april", 30),
    MAY("may", 31),
    JUNE("june", 30),
    JULY("july", 31),
    AUGUST("august", 31),
    SEPTEMBER("september", 30),
    OCTOBER("october", 31),
    NOVEMBER("november", 30),
    DECEMBER("december", 31);

    private final String name;

    private final int length;

    EnumMonth(String name, int length) {
        this.name = name;
        this.length = length;
    }

    public String getName() {
        return this.name;
    }

    public int getLength() {
        return this.length;
    }
}
