package nemesiss.scheduler.change.chargescheduler.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import com.amap.api.services.help.Tip;
import nemesiss.scheduler.change.chargescheduler.Application.ChargerApplication;
import nemesiss.scheduler.change.chargescheduler.R;
import nemesiss.scheduler.change.chargescheduler.SearchActivity;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>
{
    private List<Tip> _searchList = null;
    private SearchView searchView = null;
    private SearchActivity searchActivity;
    public SearchResultAdapter(List<Tip> searchList, final SearchView sv, final SearchActivity activity){
        searchView = sv;
        searchActivity = activity;
        RefreshSearchList(searchList);
        notifyDataSetChanged();
    }

    public void RefreshSearchList(List<Tip> NewSearchList)
    {
        _searchList = NewSearchList;
    }
    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_search_result_layout,parent,false);
        SearchResultViewHolder vh = new SearchResultViewHolder(view);
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int pos = vh.getAdapterPosition();
                Tip currTip = _searchList.get(pos);
                Context ctx = ChargerApplication.getContext();

                Intent it = new Intent();
                it.putExtra(ctx.getResources().getString(R.string.SearchLocationBackAddressName),currTip);
                searchActivity.setResult(Activity.RESULT_OK,it);
                searchActivity.finish();
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position)
    {
//        String str = _searchList.get(position);
//        holder.NameTextView.setText(str);
        Tip CurrentTip = _searchList.get(position);
        holder.NameTextView.setText(CurrentTip.getName());
        holder.AddressTextView.setText(new StringBuilder(CurrentTip.getDistrict()).append(CurrentTip.getAddress()).toString());
    }

    @Override
    public int getItemCount()
    {
        return _searchList.size();
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder{

        private TextView NameTextView;
        private TextView AddressTextView;
        private ImageView autoCompleteArrow;
        public SearchResultViewHolder(View itemView)
        {
            super(itemView);
            NameTextView = itemView.findViewById(R.id.SingleResultTextView);
            AddressTextView = itemView.findViewById(R.id.SingleAddressTextView);
            autoCompleteArrow = itemView.findViewById(R.id.AutoCompleteArrow);
            autoCompleteArrow.setOnClickListener(view ->
            {
                searchView.setQuery(NameTextView.getText().toString(), false);
            });
        }
    }
}
