package com.lesbougs.androidprojectm1.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.lesbougs.androidprojectm1.R;
import com.lesbougs.androidprojectm1.model.Widget;

import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anupamchugh on 09/02/16.
 */
public class AdminResultAdapter extends RecyclerView.Adapter {

    ArrayList<Widget> dataSet;
    Context mContext;

    public static class Type0 extends RecyclerView.ViewHolder {

        TextView textField;
        TextView questionTextField;

        public Type0(View itemView) {
            super(itemView);
            this.textField = itemView.findViewById(R.id.textViewResult);
            this.questionTextField = itemView.findViewById(R.id.questionTextView);
        }
    }


    public static class Type1 extends RecyclerView.ViewHolder {

        TextView textField;
        TextView titleTextField;
        BarChart barChart;

        public Type1(View itemView) {
            super(itemView);
            this.barChart = itemView.findViewById(R.id.barChartResult);
            this.textField = itemView.findViewById(R.id.averageResult);
            this.titleTextField = itemView.findViewById(R.id.titleTextView);
        }
    }

    public AdminResultAdapter(ArrayList<Widget> data, Context context) {
        this.dataSet = data;
        this.mContext = context;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result_widget_text, parent, false);
                return new Type0(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result_widget_number, parent, false);
                return new Type1(view);
        }
        return null;
    }

    public int getItemViewType(int position) {
        return dataSet.get(position).type;
    }


    @Override
    public void onBindViewHolder(@NotNull final RecyclerView.ViewHolder holder, final int listPosition) {

        Widget object = dataSet.get(listPosition);

        switch (object.type) {
            case 0:

                String allResult = "";

                if (object.getTextFieldResult().size() == 0) {
                    allResult = "Aucun résultat";
                } else {
                    for (String element : object.getTextFieldResult()) {
                        allResult = allResult.concat(element + "\n");
                    }
                }

                ((Type0) holder).questionTextField.setText(object.getQuestion());

                ((Type0) holder).textField.setText(allResult);
                ((Type0) holder).textField.setOnClickListener(v -> {
                    ViewGroup.LayoutParams params = v.getLayoutParams();

                    if (params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                    else {
                        final float scale = mContext.getResources().getDisplayMetrics().density;
                        params.height = (int) (45 * scale + 0.5f);
                    }
                    v.requestLayout();
                });
                break;
            case 1:
                ((Type1) holder).titleTextField.setText(object.getQuestion());

                ArrayList<BarEntry> entries = new ArrayList<>();

                Map<Integer, Integer> countMap = new HashMap<>();
                for (int i = 0; i < object.getResultPoint().size(); i++) {
                    int key = object.getResultPoint().get(i);
                    if (countMap.containsKey(key)) {
                        int count = countMap.get(key);
                        count++;
                        countMap.put(key, count);
                    } else {
                        countMap.put(key, 1);
                    }
                }


                double average = 0.0;
                int totalValue = 0;

                for (int i = object.getMinPoint(); i < object.getMaxPoint(); i++) {
                    if (countMap.containsKey(i)) {
                        entries.add(new BarEntry(i, countMap.get(i)));
                        average += i * countMap.get(i);
                        totalValue += countMap.get(i);
                    }
                }

                average /= totalValue;

                BarChart chart = ((Type1) holder).barChart;
                TextView averageText = ((Type1) holder).textField;

                DecimalFormat df = new DecimalFormat("##.##");
                df.setRoundingMode(RoundingMode.CEILING);

                if(totalValue == 0) averageText.setText("Mean : No result");
                else {
                    String str = "Mean : " + df.format((double) average);
                    averageText.setText(str);
                }

                YAxis leftAxis = chart.getAxis(YAxis.AxisDependency.LEFT);
                YAxis rightAxis = chart.getAxisRight();
                XAxis xAxis = chart.getXAxis();


                leftAxis.setDrawGridLines(false);
                leftAxis.setAxisLineColor(mContext.getResources().getColor(R.color.colorPrimary));

                //leftAxis.setLabelCount(3);
                xAxis.setAxisMinimum(object.getMinPoint());
                xAxis.setAxisMaximum(object.getMaxPoint());
                xAxis.setTextSize(10f);
                xAxis.setDrawAxisLine(true);
                xAxis.setDrawGridLines(false);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setAxisLineColor(mContext.getColor(R.color.colorPrimary));
                //xAxis.setEnabled(false);


                rightAxis.setDrawAxisLine(false);
                rightAxis.setDrawGridLines(false);
                rightAxis.setDrawLabels(false);


                BarDataSet set = new BarDataSet(entries, "");
                set.setColor(mContext.getColor(R.color.colorPrimary));
                set.setValueTextColor(mContext.getColor(R.color.colorPrimary));
                set.setDrawValues(false);

                BarData data = new BarData(set);
                chart.setData(data);
                chart.setFitBars(true); // make the x-axis fit exactly all bars
                chart.invalidate(); // refresh
                chart.setScaleEnabled(false);
                chart.setDoubleTapToZoomEnabled(false);
                chart.setBackgroundColor(Color.rgb(255, 255, 255));
                chart.animateXY(2000, 2000);
                chart.setDrawBorders(false);
                chart.setDrawValueAboveBar(true);
                chart.getDescription().setEnabled(false);
                chart.setDrawGridBackground(true);
                chart.setGridBackgroundColor(Color.rgb(230, 230, 230));
                chart.getLegend().setEnabled(false);

                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}