package cu.inmobile.wara.Utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.otaliastudios.autocomplete.AutocompletePresenter;
import com.otaliastudios.autocomplete.RecyclerViewPresenter;

import java.util.ArrayList;
import java.util.List;

import cu.inmobile.wara.Pojo.CityApi;
import cu.inmobile.wara.R;
import cu.inmobile.wara.RoomModels.City;


public class CitiesPresenter extends RecyclerViewPresenter <String>{

    protected Adapter adapter;
    private List<City> cities;

    public CitiesPresenter(Context context,List<City>cities) {
        super(context);
        this.cities = cities;

        Log.d("-- CitiesPresenter" , "Contructor: cities: " + cities.size());
    }

    @Override
    protected RecyclerView.Adapter instantiateAdapter() {
        adapter = new Adapter();
        return adapter;
    }

    @Override
    protected void onQuery(@Nullable CharSequence query) {

        if (TextUtils.isEmpty(query)) {
            adapter.setData(cities);
        } else {
            query = query.toString().toLowerCase();
            ArrayList<City> list = new ArrayList<>();
            for (City city : cities) {
                if (city.getName().toLowerCase().contains(query)) {
                    list.add(city);
                }
            }
            adapter.setData(list);
            Log.e("Cities Presenter", "found "+list.size()+" Cities for query "+query);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected PopupDimensions getPopupDimensions() {
        PopupDimensions dims = new PopupDimensions();
        dims.width = 600;
        dims.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        return dims;
    }




    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private List<City> data;

        public class Holder extends RecyclerView.ViewHolder {
            private View root;
            private TextView tvCityName;
            private TextView username;
            public Holder(View itemView) {
                super(itemView);
                root = itemView;
                tvCityName = ((TextView) itemView.findViewById(R.id.tv_city));
                //username = ((TextView) itemView.findViewById(R.id.username));
            }
        }

        public void setData(List<City> data) {
            this.data = data;
        }

        @Override
        public int getItemCount() {
            return (isEmpty()) ? 0 : data.size();
            //return data.size();
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(getContext()).inflate(R.layout.layout_city_item, parent, false));
        }

        private boolean isEmpty() {
            return data == null || data.isEmpty();
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            if (isEmpty()) {
                //holder.tvCityName.setText("No user here!");
                //holder.username.setText("Sorry!");
                holder.root.setOnClickListener(null);
                return;
            }
            final String cityName = data.get(position).getName();
            holder.tvCityName.setText(cityName);
            //holder.username.setText("@" + user.getUsername());
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchClick(cityName);
                }
            });
        }
    }
}
