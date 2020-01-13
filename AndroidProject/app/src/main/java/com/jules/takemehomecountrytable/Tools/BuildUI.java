package com.jules.takemehomecountrytable.Tools;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;

public class BuildUI {

    /**
     * Simplifies LinearLayout creation
     *
     * @param context      AppCompatActivity required
     * @param layoutWidth  LinearLayout.LayoutParams.MATCH_PARENT or LinearLayout.LayoutParams.WRAP_CONTENT
     * @param layoutHeight LinearLayout.LayoutParams.MATCH_PARENT or LinearLayout.LayoutParams.WRAP_CONTENT
     * @param orientation  LinearLayout.VERTICAL or LinearLayout.HORIZONTAL
     * @return LinearLayout Object
     */
    public static LinearLayout buildLinearLayout(Context context, int layoutWidth, int layoutHeight, int orientation) {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(layoutWidth, layoutHeight));
        layout.setOrientation(orientation);

        return layout;
    }

    /**
     * Simplifies TextView creation
     *
     * @param context      AppCompatActivity required
     * @param layoutWidth  LinearLayout.LayoutParams.MATCH_PARENT or LinearLayout.LayoutParams.WRAP_CONTENT
     * @param layoutHeight LinearLayout.LayoutParams.MATCH_PARENT or LinearLayout.LayoutParams.WRAP_CONTENT
     * @param text         String displayed text
     * @param size         int size in sp
     * @param margins      int array [left, top, right, bottom]
     * @return
     */
    public static TextView buildTextView(Context context, int layoutWidth, int layoutHeight, String text, int size, int[] margins, int color) {
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(layoutWidth, layoutHeight);
        textParams.setMargins(Tools.getDp(context, margins[0]),
                Tools.getDp(context, margins[1]),
                Tools.getDp(context, margins[2]),
                Tools.getDp(context, margins[3])); // left, top, right, bottom
        textView.setLayoutParams(textParams);
        textView.setText(text);
        textView.setTextSize(size);

        return textView;
    }

    /**
     * Simplifies simple divider creation
     *
     * @param context AppCompatActivity required
     * @param margins int array [left, top, right, bottom]
     * @return
     */
    public static View buildDivider(Context context, int[] margins) {
        View divider = new View(context);
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Tools.getDp(context, 1));
        dividerParams.gravity = Gravity.CENTER_VERTICAL;
        dividerParams.setMargins(Tools.getDp(context, margins[0]),
                Tools.getDp(context, margins[1]),
                Tools.getDp(context, margins[2]),
                Tools.getDp(context, margins[3]));
        divider.setLayoutParams(dividerParams);
        divider.setBackgroundColor(Color.GRAY);

        return divider;
    }

    /**
     * Simplifies MaterialCardView creation
     *
     * @param context      AppCompatActivity required
     * @param layoutWidth  LinearLayout.LayoutParams.MATCH_PARENT or LinearLayout.LayoutParams.WRAP_CONTENT
     * @param layoutHeight LinearLayout.LayoutParams.MATCH_PARENT or LinearLayout.LayoutParams.WRAP_CONTENT
     * @param radius       int radius in dp
     * @param margins      int array [left, top, right, bottom]
     * @param color        color id (R.color)
     * @return
     */
    public static MaterialCardView buildMaterialCardView(Context context, int layoutWidth, int layoutHeight, int radius, int[] margins, int color) {
        MaterialCardView cardView = new MaterialCardView(context);
        LinearLayout.LayoutParams classParams = new LinearLayout.LayoutParams(layoutWidth, layoutHeight);
        classParams.setMargins(Tools.getDp(context, margins[0]),
                Tools.getDp(context, margins[1]),
                Tools.getDp(context, margins[2]),
                Tools.getDp(context, margins[3]));
        cardView.setLayoutParams(classParams);
        if (color != Color.TRANSPARENT) {
//            cardView.setCardBackgroundColor(ContextCompat.getColor(context, color));
            String backColor = "#33" + context.getResources().getString(color).replace("#", "").substring(2);

            cardView.setCardBackgroundColor(ContextCompat.getColor(context, color));
        } else {
            cardView.setCardBackgroundColor(Color.TRANSPARENT);
            cardView.setElevation(0);
        }
        cardView.setRadius(Tools.getDp(context, radius));

        return cardView;
    }
}
