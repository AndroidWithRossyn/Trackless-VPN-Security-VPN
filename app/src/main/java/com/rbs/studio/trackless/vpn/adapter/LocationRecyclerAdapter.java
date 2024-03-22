package com.rbs.studio.trackless.vpn.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

//import com.anchorfree.partner.api.data.Country;
import com.rbs.studio.trackless.vpn.BuildConfig;
import com.rbs.studio.trackless.vpn.R;
import com.rbs.studio.trackless.vpn.databinding.RowLocationsBinding;
import com.rbs.studio.trackless.vpn.dialog.CountryData;
import com.rbs.studio.trackless.vpn.utils.Preference;
import com.rbs.studio.trackless.vpn.utils.BillConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import unified.vpn.sdk.Country;

public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.ViewHolder> {
    Context context;
    private Preference preference;
    private List<CountryData> regions;
    private RegionListAdapterInterface listAdapterInterface;

    public LocationRecyclerAdapter(Context context, RegionListAdapterInterface listAdapterInterface) {
        this.context = context;
        this.listAdapterInterface = listAdapterInterface;
        preference = new Preference(this.context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_locations, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CountryData datanew = this.regions.get(holder.getAdapterPosition());
        final Country data = datanew.getCountryvalue();
        Locale locale = new Locale("", data.getCountry());

        if (position == 0) {
            holder.binding.flag.setImageResource(context.getResources().getIdentifier("drawable/earthspeed", null, context.getPackageName()));
            holder.binding.countryTitle.setText(R.string.best_performance_server);
//            holder.limit.setVisibility(View.GONE);
        } else {
            ImageView imageView = holder.binding.flag;
            Resources resources = context.getResources();
            String sb = "drawable/" + data.getCountry().toLowerCase();
            imageView.setImageResource(resources.getIdentifier(sb, null, context.getPackageName()));
            holder.binding.countryTitle.setText(locale.getDisplayCountry());
            Log.d("ADAPTER_LOCATION", "onBindViewHolder:    " + locale.getDisplayCountry());
//            holder.limit.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listAdapterInterface.onCountrySelected(regions.get(holder.getAdapterPosition()));
                holder.binding.locationLayout.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.yellow_strock));
                holder.binding.locationRadio.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.radio_box_filed));
            }
        });

    }

    @Override
    public int getItemCount() {
        return regions != null ? regions.size() : 0;
    }

    public void setRegions(List<Country> list) {
        regions = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CountryData newData = new CountryData();
            newData.setCountryvalue(list.get(i));

            if (i % 2 == 0) {
                newData.setPro(false);
                regions.add(newData);
            } else {
                if (list.get(i).getServers() > 0) {
//                    if (BuildConfig.USE_IN_APP_PURCHASE) {
//                        if (preference.isBooleenPreference(BillConfig.PRIMIUM_STATE)) {
//                            newData.setPro(false);
//                        } else {
//                            newData.setPro(false);
//                        }
//                    } else {
//                        newData.setPro(false);
//                    }

                    newData.setPro(false);
                    regions.add(newData);
                } else {
                    newData.setPro(false);
                    regions.add(newData);
                }
            }
        }
        notifyDataSetChanged();
    }

    public interface RegionListAdapterInterface {
        void onCountrySelected(CountryData item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RowLocationsBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowLocationsBinding.bind(itemView);
        }
    }


}
