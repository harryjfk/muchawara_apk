package cu.inmobile.wara.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cu.inmobile.wara.Adapters.PaymentRecyclerAdapter;
import cu.inmobile.wara.Models.PackageDetail;

import java.util.ArrayList;import cu.inmobile.wara.R;

/**
 * Created by Anurag on 4/7/2017.
 */

public class StripePaymentFragment extends PaymentFragment implements PaymentFragment.CreditDetailsFetchedListener{

    private View mainView;
    public StripePaymentFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.payment_fragment , container, false);
        GetCreditPackages(this);
        return mainView;
    }

    @Override
    public void onError(Exception e){

    }

    @Override
    public void onResultAvailable(ArrayList<String> packageList, ArrayList<PackageDetail> packageDetailList ){
        LinearLayout progressLayout = (LinearLayout) mainView.findViewById(R.id.loading_layout);
        RecyclerView paymentDetails = (RecyclerView) mainView.findViewById(R.id.payment_recyclerView);
        LinearLayoutManager mgr = new LinearLayoutManager(getActivity());
        paymentDetails.setLayoutManager(mgr);
        paymentDetails.setAdapter(new PaymentRecyclerAdapter(1, packageList, packageDetailList));
        progressLayout.setVisibility(View.GONE);
    }
}
