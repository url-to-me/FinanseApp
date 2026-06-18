package com.example.finanseapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finanseapp.model.Operation;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Operation> operations = new ArrayList<>();

    public void setOperations(List<Operation> newOperations) {
        this.operations = newOperations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Operation op = operations.get(position);

        holder.tvTitle.setText(op.getTitle());

        // Логика отображения дат (группировка)
        String currentDate = extractDate(op.getDate());
        if (position == 0 || !currentDate.equals(extractDate(operations.get(position - 1).getDate()))) {
            holder.tvDateHeader.setVisibility(View.VISIBLE);
            holder.tvDateHeader.setText(currentDate);
        } else {
            holder.tvDateHeader.setVisibility(View.GONE);
        }

        if ("INCOME".equals(op.getType())) {
            holder.tvType.setText("Доход");
            holder.tvAmount.setText(String.format("+%.2f ₽", op.getAmount()));
            holder.tvAmount.setTextColor(Color.parseColor("#22C55E"));
            holder.tvIcon.setText("💰");
        } else {
            holder.tvType.setText("Расход");
            holder.tvAmount.setText(String.format("-%.2f ₽", op.getAmount()));
            holder.tvAmount.setTextColor(Color.parseColor("#EF4444"));
            holder.tvIcon.setText("🛒");
        }
    }

    @Override
    public int getItemCount() {
        return operations.size();
    }

    // Простая обрезка даты до "ГГГГ-ММ-ДД" (зависит от того, как шлет бэкенд)
    private String extractDate(String fullDate) {
        if (fullDate != null && fullDate.length() >= 10) {
            return fullDate.substring(0, 10);
        }
        return "Неизвестная дата";
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateHeader, tvIcon, tvTitle, tvType, tvAmount;

        ViewHolder(View itemView) {
            super(itemView);
            tvDateHeader = itemView.findViewById(R.id.tvDateHeader);
            tvIcon = itemView.findViewById(R.id.tvIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvType = itemView.findViewById(R.id.tvType);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}