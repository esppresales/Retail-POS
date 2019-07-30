package com.printer.epos.rtpl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Abhishek Kotiyal on 10/4/15.
 */
public class MagStripeDataParser {

    private final String MAG_STRIPE_TRACK1_REGEX = "^%([aA-zZ])([0-9]{1,19})\\^([^\\^]{2,26})\\^([0-9]{4}|\\^)([0-9]{3}|\\^)([^\\?]+)\\?$";
    private Matcher matcher;
    private final int CARD_NUMBER_GROUP_POS = 2;
    private final int CARD_HOLDER_NAME_GROUP_POS = 3;
    private final int CARD_EXPIRY_DATE_GROUP_POS = 4;

    protected MagStripeDataParser(String data) {
        assert data != null;
        matcher = Pattern.compile(MAG_STRIPE_TRACK1_REGEX).matcher(data);
        if (!matcher.find())
            throw new IllegalStateException("Input String doesn't matched with pattern");

    }


    protected String getCardNumber() {

        return matcher.group(CARD_NUMBER_GROUP_POS);

    }

    protected String getCardHolderName() {
        StringBuffer name = new StringBuffer(matcher.group(CARD_HOLDER_NAME_GROUP_POS).toUpperCase());
        if (name.indexOf("/") != -1)
            name.deleteCharAt(name.indexOf("/"));
        return name.toString().trim();
    }

    protected String getCardExpiryDate() {
        String date = new StringBuffer(matcher.group(CARD_EXPIRY_DATE_GROUP_POS)).insert(2, "/").toString();
        return date;
    }
}
