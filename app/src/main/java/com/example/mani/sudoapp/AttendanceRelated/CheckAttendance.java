package com.example.mani.sudoapp.AttendanceRelated;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mani.sudoapp.NewsRelaled.MySingleton;
import com.example.mani.sudoapp.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.mani.sudoapp.CommonVariablesAndFunctions.BASE_URL_ATTENDANCE;
import static com.example.mani.sudoapp.CommonVariablesAndFunctions.maxNoOfTries;
import static com.example.mani.sudoapp.CommonVariablesAndFunctions.retrySeconds;

public class CheckAttendance extends AppCompatActivity {

    Integer mCollege_id;
    Integer mBranch_id;
    Integer mSem_id;
    Integer mStudent_id;

    ArrayList<AttendanceReport> mAttandanceList;

    String FETCH_ATTENDANCE_URL = BASE_URL_ATTENDANCE+"fetch_attendance_fron_database.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);

        mAttandanceList = new ArrayList<>();

        mCollege_id = getIntent().getIntExtra("college_id",-1);
        mBranch_id = getIntent().getIntExtra("branch_id",-1);
        mSem_id = getIntent().getIntExtra("sem_id",-1);
        mStudent_id = getIntent().getIntExtra("student_id",-1);

       /* Toast.makeText(CheckAttendance.this,"college_id "+mCollege_id +
                " branch_id "+mBranch_id +
                " sem_id "+mSem_id +
                " student_id "+mStudent_id,
                Toast.LENGTH_SHORT).show();*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            loadAttendance();
        }
        else {
            Toast.makeText(CheckAttendance.this,"Not compatible",Toast.LENGTH_SHORT).show();
        }


    }

    private void loadAttendance() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_ATTENDANCE_URL,
                new Response.Listener<String>() {

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray allAttendance = new JSONArray(response);

                            if(allAttendance.length() == 0) {
                                Toast.makeText(CheckAttendance.this,"EmptyList",Toast.LENGTH_SHORT).show();
                                return;
                            }

                            for(int i = 0;i<allAttendance.length();i++){

                                JSONObject attendanceJSONObject = allAttendance.getJSONObject(i);

                                String sub_name = attendanceJSONObject.getString("subject_name");
                                int total_hrs   = attendanceJSONObject.getInt("total_hrs");
                                int present_hrs = attendanceJSONObject.getInt("present_hrs");

                                mAttandanceList.add(new AttendanceReport(sub_name,total_hrs,present_hrs));
                            }

                            PieChart pieChart = findViewById(R.id.total_attendance_piechart);
                            setTotalAttendance(pieChart);

                            RecyclerView recyclerView = findViewById(R.id.recycler_view_show_attandance);

                            AttendanceReportAdapter adapter = new AttendanceReportAdapter(CheckAttendance.this,mAttandanceList);
                            recyclerView.setLayoutManager(new GridLayoutManager(CheckAttendance.this,2));
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();

                params.put("college_id",String.valueOf(mCollege_id));
                params.put("branch_id",String.valueOf(mBranch_id));
                params.put("sem_id",String.valueOf(mSem_id));
                params.put("student_id",String.valueOf(mStudent_id));
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy( (retrySeconds*1000),maxNoOfTries,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(CheckAttendance.this).addToRequestQueue(stringRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTotalAttendance(PieChart pieChart) {

        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDragDecelerationFrictionCoef(0.99f);
        pieChart.setExtraOffsets(5,0,5,0);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(60f);
        pieChart.animateY(2000, Easing.EasingOption.EaseInOutCubic);
        pieChart.setHoleRadius(43);

        //To disable the legend
        pieChart.getLegend().setEnabled(false);

        ArrayList<PieEntry> yValues = new ArrayList<>();

        int totalHrsPresent = 0;
        int totalHrsAbsent = 0;

        for(int i=0;i<mAttandanceList.size();i++){
            totalHrsPresent += mAttandanceList.get(i).getPresentHrs();
            totalHrsAbsent += mAttandanceList.get(i).getAbsentHrs();
        }

        int totalHrs = totalHrsPresent + totalHrsAbsent;

        double percentage = ((double) totalHrsPresent/totalHrs) * 100;
        double roundoff = Math.round(percentage*100)/100.0;

        pieChart.setCenterText(String.valueOf(roundoff)+" %");
        pieChart.setCenterTextSize(21f);

        TextView tv_overall = findViewById(R.id.tv_overall_attendance);

        if(percentage < 75.00){
            pieChart.setCenterTextColor(Color.RED);
            tv_overall.setTextColor(Color.RED);
            tv_overall.setText("Overall Attendence is Low !");
        }
        else{
            pieChart.setCenterTextColor(Color.CYAN);
            tv_overall.setTextColor(Color.BLUE);
            tv_overall.setText("Good, maintain same attendance");

        }


        yValues.add(new PieEntry(totalHrsPresent,"Present"));
        yValues.add(new PieEntry(totalHrsAbsent,"Absent"));

        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(7f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.YELLOW);
        pieChart.setData(data);

    }
}
