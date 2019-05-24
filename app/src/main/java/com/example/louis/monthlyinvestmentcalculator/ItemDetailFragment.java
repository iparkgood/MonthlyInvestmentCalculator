package com.example.louis.monthlyinvestmentcalculator;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.louis.monthlyinvestmentcalculator.dummy.DummyContent;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
        }
    }
    int intGoal;
    float floatRate;
    static float floatDefaultRate = 2.45f;
    boolean yesDefault;
    float monthlyDeposit;
    float period;
    EditText name;
    static String[] names = new String[100];
    static float[] deposits = new float[100];
    static int size = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem.id.equals("1")){
            rootView = inflater.inflate(R.layout.calculator, container, false);
            final EditText goal = (EditText)rootView.findViewById(R.id.txtGoal);
            name = (EditText)rootView.findViewById(R.id.txtName);

            final EditText interest = (EditText)rootView.findViewById(R.id.txtInterest);
            final TextView showDefault = (TextView)rootView.findViewById(R.id.txtShowDefault);

            final Calendar today = Calendar.getInstance();
            final Calendar c = Calendar.getInstance();
            final DateFormat fmtDate = DateFormat.getDateInstance();
            final DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.MONTH, monthOfYear);
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    period = (c.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) + 1 )/30;
                    Toast.makeText(getContext(), "Your last day for deposit is " + fmtDate.format(c.getTime()), Toast.LENGTH_LONG).show();
                }
            };

            Button date = (Button)rootView.findViewById(R.id.btnDate);
            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatePickerDialog(getContext(), d, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            final RadioGroup radioGroup = (RadioGroup)rootView.findViewById(R.id.radGroup);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (i == R.id.radYes){
                        interest.setVisibility(View.VISIBLE);
                        showDefault.setVisibility(View.GONE);
                        yesDefault = false;
                    }
                    if (i == R.id.radNo){
                        interest.setVisibility(View.GONE);
                        showDefault.setVisibility(View.VISIBLE);
                        showDefault.setText("Your Default Interest Rate is " + floatDefaultRate);
                        Toast.makeText(getContext(), "Default Interest Rate will be applied.", Toast.LENGTH_LONG).show();
                        yesDefault = true;
                    }
                }
            });

            Button see = (Button)rootView.findViewById(R.id.btnSee);
            final WebView webView = (WebView)rootView.findViewById(R.id.goWeb);
            see.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl("http://www.bankrate.com/banking/savings/rates");
                }
            });

            final TextView result = (TextView)rootView.findViewById(R.id.txtResult);
            Button button = (Button)rootView.findViewById(R.id.btnCalc);

            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    intGoal = Integer.parseInt(goal.getText().toString());

                    if (!yesDefault) {
                        floatRate = Float.parseFloat(interest.getText().toString());
                        monthlyDeposit = (intGoal * (1 + (floatRate * period / 12))) / period;
                    }
                    if (yesDefault){
                        monthlyDeposit = (intGoal * (1 + (floatDefaultRate * period / 12))) / period;
                    }
                    DecimalFormat currency = new DecimalFormat("$###,###.##");
                    result.setText("Your Monthly Deposit is " + currency.format(monthlyDeposit));

                    deposits[size] = monthlyDeposit;
                    names[size] = name.getText().toString();
                    size++;

                }
            });

        }
        if (mItem.id.equals("2")){
            rootView = inflater.inflate(R.layout.piechart, container, false);
            PieChart pieChart = (PieChart)rootView.findViewById(R.id.chart);
            pieChart.setUsePercentValues(true);
            pieChart.setHoleRadius(25f);
            pieChart.setTransparentCircleRadius(25f);

            ArrayList<PieEntry> values = new ArrayList<>();

            for (int i = 0 ; i < size ; i++) {
               PieEntry pieEntry = new PieEntry(deposits[i], names[i]);
               values.add(pieEntry);
            }

            PieDataSet pieDataSet = new PieDataSet(values, " - Names/Purposes");
            PieData pieData = new PieData(pieDataSet);
            pieChart.setData(pieData);
            pieData.setValueTextSize(40f);

            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

            pieChart.getDescription().setEnabled(false);
            pieChart.animateXY(1400, 1400);


        }
        if (mItem.id.equals("3")){
            rootView = inflater.inflate(R.layout.defaultrate, container, false);

            final EditText defaultRate = (EditText)rootView.findViewById(R.id.txtDefault);
            Button change = (Button)rootView.findViewById(R.id.btnDefault);

            change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    floatDefaultRate = Float.parseFloat(defaultRate.getText().toString());
                    Toast.makeText(getContext(), "You Changed Default Interest Rate to " + floatDefaultRate, Toast.LENGTH_LONG).show();
                }
            });
        }
        return rootView;
    }
}
