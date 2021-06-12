package com.danielamaral.gft.demogcpbt.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Asset {


    public final static String BT_COL_NAME="name";
    public final static String BT_COL_DATE="date";
    public final static String BT_COL_CLOSE_PRICE="closePrice";
    public final static String BT_COL_OPEN_PRICE="openPrice";
    public final static String BT_COL_VARIATION="variation";

    private String key;
    private String name;
    private LocalDate date;
    private double closePrice;
    private double openPrice;
    private double variation;

    public double getVariation() {
        return (closePrice / openPrice-1)*100;
    }

}
