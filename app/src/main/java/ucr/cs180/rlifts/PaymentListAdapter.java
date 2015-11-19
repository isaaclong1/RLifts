package ucr.cs180.rlifts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by isaaclong on 11/18/15.
 */
public class PaymentListAdapter extends BaseAdapter {
    Context context;
    List<PaymentOptionList.PaymentOption> paymentOptionList;

    PaymentListAdapter(Context context) {
        this.context = context;
        paymentOptionList = PaymentOptionList.ITEMS;
    }

    public List<PaymentOptionList.PaymentOption> getPaymentOptionList() {
        return paymentOptionList;
    }

    @Override
    public int getCount() {
        return paymentOptionList.size();
    }

    @Override
    public Object getItem(int position) {
        return paymentOptionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // return same as passed?
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.payment_list_item, null);
        }

        TextView cost = (TextView)convertView.findViewById(R.id.cost);
        TextView numTokens = (TextView)convertView.findViewById(R.id.numTokens);

        PaymentOptionList.PaymentOption option = paymentOptionList.get(position);

        cost.setText("$" + option.cost.toString());
        numTokens.setText(option.numTokens.toString());

        return convertView;
    }
}
