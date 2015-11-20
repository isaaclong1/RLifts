package ucr.cs180.rlifts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by isaaclong on 11/18/15.
 */
public class PaymentOptionList {
    public static List<PaymentOption> ITEMS = new ArrayList<>();

    // add our 3 initial payment options
    static {
        addOption(new PaymentOption("1",10,10));
        addOption(new PaymentOption("2",50,50));
        addOption(new PaymentOption("3",100,100));
    }

    public static void addOption (PaymentOption option) {
        ITEMS.add(option);
    }

    public static class PaymentOption {
        public String id;
        public Integer numTokens;
        public Integer cost;

        public PaymentOption(String id, Integer numTokens, Integer cost) {
            this.id = id;
            this.numTokens = numTokens;
            this.cost = cost;
        }
    }

}
