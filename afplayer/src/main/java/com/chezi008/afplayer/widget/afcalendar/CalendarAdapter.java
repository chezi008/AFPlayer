package com.chezi008.afplayer.widget.afcalendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chezi008.afplayer.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private List<Date> dates;
    private int selection = 0;//默认值
    private OnItemListener onItemListener;
    public CalendarAdapter(List<Date> dates) {
        this.dates = dates;
    }

    public void setOnItemListener(OnItemListener onItemListener)
    {
        this.onItemListener = onItemListener;
    }
    //设置点击事件
    public interface OnItemListener
    {
        void onClick(View v, int pos);
    }
    //获取点击的位置
    public void setDefSelect(int position)
    {
        this.selection = position;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Date date = dates.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        holder.bindData(position==0?"今日":day+"");
        holder.textView.setTextSize(position==0?14:18);

        holder.textView.setBackgroundResource(position==selection?R.drawable.bg_round:0);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (onItemListener != null) {
//                        onItemListener.onClick(v, getLayoutPosition());
//                    }
                    setDefSelect(getLayoutPosition());
                }
            });

        }

        public void bindData(String date) {
            textView.setText(date);
        }
    }
}
