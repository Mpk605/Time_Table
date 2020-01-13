package com.jules.takemehomecountrytable.Fragments.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.jules.takemehomecountrytable.Fragments.Map.Room.OfficeRoom;
import com.jules.takemehomecountrytable.Fragments.Map.Room.Room;
import com.jules.takemehomecountrytable.Fragments.Map.Room.Stair;
import com.jules.takemehomecountrytable.Fragments.Map.Room.TdRoom;
import com.jules.takemehomecountrytable.Fragments.Map.Room.TpRoom;
import com.jules.takemehomecountrytable.Fragments.Map.Room.WC;
import com.jules.takemehomecountrytable.R;
import com.jules.takemehomecountrytable.Tools.Tools;
import com.jules.takemehomecountrytable.Tools.XMLReader;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class DrawRoomMap extends View {

    private Paint paint;

    private int rect_posX;
    private int rect_posY;
    private int rect_height;
    private int rect_width;
    private int text_posX;
    private int text_posY;
    private int text_size;
    ArrayList<ArrayList<Room>> test;

    public DrawRoomMap(Context context) throws FileNotFoundException {
        super(context);
        test = XMLReader.CreateXMLData(context);

        this.paint = new Paint();
        this.text_size = 17;
        this.text_posX = 15;
        this.text_posY = 45;

        this.rect_posX = 0;
        this.rect_posY = 0;
        this.rect_height = 150;
        this.rect_width = 100;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < this.test.get(0).size(); i++) {
            //ROOM 1
            paint.setColor(ContextCompat.getColor(getContext(), this.test.get(0).get(i).getColor()));
            canvas.drawRect(Tools.getDp(getContext(), this.rect_width), Tools.getDp(getContext(), this.rect_height), Tools.getDp(getContext(), this.rect_posX), this.rect_posY, paint);

            paint.setColor(ContextCompat.getColor(getContext(), R.color.darkGray));
            paint.setTextSize(Tools.getDp(getContext(), this.text_size));

            //NAME
            if (this.test.get(0).get(i).getClass() == TdRoom.class) {
                canvas.drawText("SALLE TD", Tools.getDp(getContext(), this.text_posX), Tools.getDp(getContext(), this.text_posY), paint);
            }
            if (this.test.get(0).get(i).getClass() == OfficeRoom.class) {
                for (int y = 0; y < this.test.get(0).get(i).getNumberOfProf(); y++) {
                    canvas.drawText(this.test.get(0).get(i).getProf(y), Tools.getDp(getContext(), this.text_posX), Tools.getDp(getContext(), this.text_posY), paint);
                    this.text_posY += 20;
                }
                this.text_posY = 45;
            }
            if (this.test.get(0).get(i).getClass() == TpRoom.class) {
                canvas.drawText(this.test.get(0).get(i).getName(), Tools.getDp(getContext(), this.text_posX), Tools.getDp(getContext(), this.text_posY), paint);
            }
            if (this.test.get(0).get(i).getClass() == WC.class) {
                paint.setColor(ContextCompat.getColor(getContext(), R.color.white));
                canvas.drawText("WC", Tools.getDp(getContext(), this.text_posX), Tools.getDp(getContext(), this.text_posY), paint);
            }

            if (this.test.get(0).get(i).getClass() == Stair.class) {

            }

            if (this.test.get(0).get(i).getNum() == null) {
            } else {
                canvas.drawText(this.test.get(0).get(i).getNum(), Tools.getDp(getContext(), this.text_posX), Tools.getDp(getContext(), this.text_posY + 20), paint);
            }

            //SEPARATION
            paint.setColor(ContextCompat.getColor(getContext(), R.color.darkGray));
            canvas.drawRect(Tools.getDp(getContext(), this.rect_width + 10), Tools.getDp(getContext(), this.rect_height), Tools.getDp(getContext(), this.rect_width), Tools.getDp(getContext(), this.rect_posY), paint);

            //COULOIR
            paint.setColor(ContextCompat.getColor(getContext(), R.color.white));
            canvas.drawRect(Tools.getDp(getContext(), this.rect_width + 10), Tools.getDp(getContext(), this.rect_height + 75), Tools.getDp(getContext(), this.rect_posX), Tools.getDp(getContext(), this.rect_posY + 150), paint);
            if (this.test.get(0).get(i).getPos() == 1) {


                //BAS
                paint.setColor(ContextCompat.getColor(getContext(), this.test.get(0).get(i).getColor()));
                canvas.drawRect(Tools.getDp(getContext(), this.rect_width), Tools.getDp(getContext(), this.rect_height + 225), Tools.getDp(getContext(), this.rect_posX), Tools.getDp(getContext(), this.rect_posY + 225), paint);

                paint.setColor(ContextCompat.getColor(getContext(), R.color.darkGray));
                canvas.drawText("SALLE TD", Tools.getDp(getContext(), this.text_posX), Tools.getDp(getContext(), this.text_posY + 225), paint);
            }
                if (this.test.get(1).get(i).getClass() == OfficeRoom.class) {
                for (int y = 0; y < this.test.get(1).get(i).getNumberOfProf(); y++) {
                    canvas.drawText(this.test.get(1).get(i).getProf(y), Tools.getDp(getContext(), this.text_posX), Tools.getDp(getContext(), this.text_posY + 225), paint);
                    this.text_posY += 20;
                }
                this.text_posY = 45;
            }
            if (this.test.get(1).get(i).getClass() == TpRoom.class) {
                canvas.drawText(this.test.get(1).get(i).getName(), Tools.getDp(getContext(), this.text_posX), Tools.getDp(getContext(), this.text_posY + 225), paint);

            }

            if (this.test.get(1).get(i).getClass() == WC.class) {
                paint.setColor(ContextCompat.getColor(getContext(), R.color.white));
                canvas.drawText("WC", Tools.getDp(getContext(), this.text_posX), Tools.getDp(getContext(), this.text_posY + 225), paint);

            }

            if (this.test.get(1).get(i).getNum() == null) {
            } else {
                canvas.drawText(this.test.get(1).get(i).getNum(), Tools.getDp(getContext(), this.text_posX), Tools.getDp(getContext(), this.text_posY + 20 + 225), paint);
            }

            paint.setColor(ContextCompat.getColor(getContext(), R.color.gold));
            canvas.drawRect(Tools.getDp(getContext(), this.rect_width), Tools.getDp(getContext(), this.rect_height + 225), Tools.getDp(getContext(), this.rect_posX), Tools.getDp(getContext(), this.rect_posY + 225), paint);

            //SEPARATION
            paint.setColor(ContextCompat.getColor(getContext(), R.color.darkGray));
            canvas.drawRect(Tools.getDp(getContext(), this.rect_width + 10), Tools.getDp(getContext(), rect_height + 225), Tools.getDp(getContext(), this.rect_width), Tools.getDp(getContext(), rect_posY + 225), paint);

            this.rect_width += 110;
            this.rect_posX += 110;
            this.text_posX += 110;
        }

        this.rect_width = 100;
        this.rect_posX = 0;
        this.text_posX = 15;
    }
}
