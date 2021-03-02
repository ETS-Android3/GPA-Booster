package com.example.maimyou.Dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.maimyou.Fragments.FragmentEdit;
import com.example.maimyou.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.MODE_PRIVATE;
import static com.example.maimyou.Activities.RegisterActivity.SHARED_PREFS;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    FragmentEdit fragmentEdit;
    TextView Title;
    EditText val;
    String dataName = "", dataName2 = "", title = "", Content = "", id = "", MaxCGPA = "", MinCGPA = "", curCGPA = "";
    Button save, cancel;
    ImageButton Visible;
    SeekBar sb_quota;
    TextView tv_quota;

    boolean visible = false;
    int Prog = 50;

    public void setMaxMin(String MaxCGPA, String MinCGPA, String curCGPA) {
        this.MaxCGPA = MaxCGPA;
        this.MinCGPA = MinCGPA;
        this.curCGPA = curCGPA;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValString(String Content) {
        this.Content = Content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public void setDataName2(String dataName) {
        this.dataName2 = dataName;
    }

    public void setFragmentEdit(FragmentEdit fragmentEdit) {
        this.fragmentEdit = fragmentEdit;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        if (!dataName.isEmpty()) {
            return inflater.inflate(R.layout.bottom_sheet, container, false);
        } else {
            return inflater.inflate(R.layout.bottom_sheet_cgpa, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) {
            Title = getView().findViewById(R.id.Title);
            Title.setText(title);

            if (!dataName.isEmpty()) {
                val = getView().findViewById(R.id.Val);
                if (dataName2.toLowerCase().contains("camsysid")) {
                    val.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                val.setText(Content);
                val.setSelectAllOnFocus(true);
                val.selectAll();
                val.requestFocus();

                if (dataName.toLowerCase().contains("camsyspassword")) {
                    val.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    val.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    Visible = getView().findViewById(R.id.visible);
                    Visible.setVisibility(View.VISIBLE);
                    Visible.setOnClickListener(v -> {
                        if (visible) {
                            visible = false;
                            Visible.setImageResource(R.drawable.ic_baseline_visibility_24);
                            val.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            val.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        } else {
                            visible = true;
                            Visible.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                            val.setInputType(InputType.TYPE_CLASS_TEXT);
                            val.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        }
                    });
                }

                InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            } else {
                if (MaxCGPA.isEmpty()) {
                    MaxCGPA = Content;
                }
                if (MinCGPA.isEmpty()) {
                    MinCGPA = Content;
                }
                Prog = (int) (((getDouble(curCGPA) - getDouble(MinCGPA)) / (getDouble(MaxCGPA) - getDouble(MinCGPA))) * 100d);
                tv_quota = (TextView) getView().findViewById(R.id.tv_quota);
                sb_quota = (SeekBar) getView().findViewById(R.id.sb_quota);
                TextView min = getView().findViewById(R.id.min);
                TextView max = getView().findViewById(R.id.max);
                min.setText("Min: " + MinCGPA);
                max.setText("Max: " + MaxCGPA);
                sb_quota.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        setProg(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                sb_quota.setProgress(Prog);
                tv_quota.post(() -> setProg(Prog));
            }
            save = getView().findViewById(R.id.save);
            save.setOnClickListener(v -> {
                if (dataName.isEmpty()) {
                    fragmentEdit.setRange(Prog);
                    fragmentEdit.cancel();
                } else if (dataName2.isEmpty()) {
                    if (!id.isEmpty() && !val.getText().toString().isEmpty()) {
                        FirebaseDatabase.getInstance().getReference().child("Member").child(id).child(dataName).setValue(val.getText().toString());
                        if (dataName.toLowerCase().contains("camsyspassword")) {
                            saveData(val.getText().toString(), "camsysPassword");
                        }
                        closeKeyBoard();
                        fragmentEdit.cancel();
                    }
                } else {
                    if (!id.isEmpty() && !val.getText().toString().isEmpty()) {
                        FirebaseDatabase.getInstance().getReference().child("Member").child(id).child(dataName).setValue(val.getText().toString());
                        FirebaseDatabase.getInstance().getReference().child("Member").child(id).child(dataName2).setValue(val.getText().toString());
                        if (dataName2.toLowerCase().contains("camsysid")) {
                            saveData(val.getText().toString(), "camsysId");
                        }
                        closeKeyBoard();
                        fragmentEdit.cancel();
                    }
                }
            });

            cancel = getView().findViewById(R.id.cancel);
            cancel.setOnClickListener(v -> {
                closeKeyBoard();
                fragmentEdit.cancel();
            });
        }
    }

    public void setProg(int progress) {
        Prog = progress;
        double CGPA = (((((double) progress) / 100d) * (getDouble(MaxCGPA) - getDouble(MinCGPA))) + getDouble(MinCGPA));
        tv_quota.setText("CGPA: " + round(CGPA));
//                        quota = progress;
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        tv_quota.measure(spec, spec);
        int quotaWidth = tv_quota.getMeasuredWidth();

        int spec2 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        tv_quota.measure(spec2, spec2);
        int sbWidth = sb_quota.getMeasuredWidth();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tv_quota.getLayoutParams();
        params.leftMargin = (int) (((double) progress / sb_quota.getMax()) * sbWidth - (double) quotaWidth * progress / sb_quota.getMax());
        tv_quota.setLayoutParams(params);
    }

    public double round(double a) {
        return Math.round(a * 100.0) / 100.0;
    }


    public double getDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception ignore) {
            return 0;
        }
    }

    public void saveData(String data, String name) {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, data);
        editor.apply();
    }

    private void closeKeyBoard() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
}