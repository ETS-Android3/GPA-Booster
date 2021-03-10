package com.example.maimyou.Dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.maimyou.Activities.DashBoardActivity;
import com.example.maimyou.CarouselLayout.CoverFlowAdapter;
import com.example.maimyou.CarouselLayout.Tip;
import com.example.maimyou.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;

public class TipContainerDialog extends Dialog {
    //Vars
    public DashBoardActivity D;
    private FeatureCoverFlow coverFlow;
    private ArrayList<Tip> tips;
    int currentPage = 0;
    Dialog dialog = this;
    Boolean checked = false;
    String string;

    public void setCheckBox(boolean isChecked) {
        this.checked = isChecked;
    }

    //Views
    ImageButton closeTip;
    TextView pageNumber, Manually, pageDes;
    Button back, next;
    CardView cardView;
    LinearLayout Container;
    RelativeLayout backGround;
    CheckBox checkBox;

    public void setString(String string) {
        this.string = string;
    }

    public TipContainerDialog(DashBoardActivity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.D = a;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tip_container);

        View v = getWindow().getDecorView();

        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        v.setMinimumWidth((int)(displayRectangle.width() * 1f));
        v.setMinimumHeight((int)(displayRectangle.height() * 1f));

        v.setBackgroundResource(android.R.color.transparent);
        setCanceledOnTouchOutside(true);//See here is the code

        backGround = findViewById(R.id.backGround);
        Container = findViewById(R.id.Container);
        checkBox = findViewById(R.id.checkBox);
        closeTip = findViewById(R.id.closeTip);
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        pageNumber = findViewById(R.id.pageNumber);
        pageDes = findViewById(R.id.pageDes);
        cardView = findViewById(R.id.cardView);
        Manually = findViewById(R.id.Manually);
        coverFlow = findViewById(R.id.coverflow);
        checkBox.setChecked(checked);

        backGround.setOnTouchListener((v1, event) -> {
            dialog.dismiss();
            return false;
        });
        String str;
        View.OnClickListener listener;
        if (string.contains("man")) {
            str = "Set profile manually";
            listener = v12 -> {
                dialog.dismiss();
                D.setManually();
            };
            Manually.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
        } else {
            str = "Set from Camsys";
            listener = v12 -> {
                dialog.dismiss();
                D.setAuto();
            };
            Manually.setTextColor(getContext().getResources().getColor(R.color.address));
        }

        Manually.setText(Html.fromHtml("<u>" + str + "</u>"));
        Manually.setOnClickListener(listener);

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                FirebaseDatabase.getInstance().getReference().child("Member").child(D.loadData("Id")).child("DoNotShowUploadTip").setValue("1");
            } else {
                FirebaseDatabase.getInstance().getReference().child("Member").child(D.loadData("Id")).child("DoNotShowUploadTip").removeValue();
            }
        });

        back.setOnClickListener(view -> {
            currentPage--;
            if (currentPage < 0) {
                currentPage = tips.size() - 1;
            }
            pageNumber.setText((currentPage + 1) + "/" + tips.size());
            pageDes.setText(tips.get(currentPage).getDes());
            coverFlow.scrollToPosition(currentPage);
        });
        next.setOnClickListener(view -> {
            currentPage++;
            if (currentPage >= tips.size()) {
                currentPage = 0;
            }
            pageNumber.setText((currentPage + 1) + "/" + tips.size());
            pageDes.setText(tips.get(currentPage).getDes());
            coverFlow.scrollToPosition(currentPage);
        });
        closeTip.setOnClickListener(view -> dialog.dismiss());

        CoverFlowAdapter adapter = new CoverFlowAdapter(getContext(), tips);
        coverFlow.setAdapter(adapter);
        coverFlow.setOnScrollPositionListener(onScrollListener());
        pageNumber.setText((currentPage + 1) + "/" + tips.size());
        pageDes.setText(tips.get(currentPage).getDes());

        coverFlow.post(() -> {
            coverFlow.setCoverHeight((int) (((double) coverFlow.getHeight()) / 1.3d));
            coverFlow.setCoverWidth((int) (((double) coverFlow.getHeight()) / 2.6d));
            coverFlow.setMaxRotationAngle(10);
            coverFlow.setMaxScaleFactor(1.2f);
            coverFlow.setRotation(0.0f);
            coverFlow.setSpacing(0.5f);
            coverFlow.setVerticalPaddingTop(0);
            coverFlow.setReflectionOpacity(0);
            coverFlow.setAdapter(adapter);
            coverFlow.scrollToPosition(getInt(D.loadData("Tip")));
        });

//        coverFlow.setScalingThreshold();
//        coverFlow.setRotationTreshold();
    }

//    public int dpToPx(int dip) {
//        Resources r = D.getResources();
//        float px = TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP,
//                dip,
//                r.getDisplayMetrics()
//        );
//        return (int) px;
//    }

    public int getInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @SuppressLint("SetTextI18n")
    private FeatureCoverFlow.OnScrollPositionListener onScrollListener() {
        return new FeatureCoverFlow.OnScrollPositionListener() {
            @Override
            public void onScrolledToPosition(int position) {
                Log.v("MainActiivty", "position: " + position);
                D.saveData(Integer.toString(position), "Tip");
                currentPage = position;
                pageNumber.setText((currentPage + 1) + "/" + tips.size());
                pageDes.setText(tips.get(currentPage).getDes());
            }

            @Override
            public void onScrolling() {
                Log.i("MainActivity", "scrolling");
                pageNumber.setText("-/" + tips.size());
            }
        };
    }

    public void setTips(ArrayList<Tip> tips) {
        this.tips = tips;
    }
}