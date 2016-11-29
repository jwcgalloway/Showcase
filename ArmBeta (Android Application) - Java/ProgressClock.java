package qut.wearable_remake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

class ProgressClock extends View {
    private Bitmap info;
    private Paint paint;
    private RectF oval;
    private Paint textPaint;
    private Context context;

    /**
     * Simple constructor(s) to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public ProgressClock(Context context) {
        super(context);
        init(context);
    }
    public ProgressClock(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    /**
     * Initialises the class variables.
     */
    @SuppressWarnings("deprecation")
    private void init(Context c) {
        context = c;
        info = drawableToBitmap(getResources().getDrawable(R.drawable.clock_text));
        Bitmap ring = drawableToBitmap(getResources().getDrawable(R.drawable.clock_progress));

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new BitmapShader(ring, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        oval = new RectF(0, 0, info.getWidth(), info.getHeight());

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(120);
        textPaint.setTextAlign(Paint.Align.CENTER);
    } // end init()

    /**
     * Draws the progress clock.
     *
     * @param canvas The canvas on which to draw.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        int moveCount = 54;
        int moveGoal = 80;

        if (!isInEditMode()) {
            moveCount = ((WearableApplication) context.getApplicationContext()).getTotalMovesToday();
            moveGoal = ((WearableApplication) context.getApplicationContext()).getMoveGoal();
        }

        canvas.translate((getWidth() - info.getWidth()) / 2, (getHeight() - info.getHeight()) / 2);
        canvas.drawBitmap(info, 0, 0, null);

        float angle = 0;
        if (moveGoal > 0 && moveCount > 0) {
            angle = ((float) moveCount / (float) moveGoal) * 360;
        }

        canvas.drawArc(oval, -90, angle, true, paint);
        canvas.drawText(Integer.toString(moveCount),
                info.getWidth() / 2,
                (info.getHeight() - textPaint.ascent()) / 2,
                textPaint);
    } // end onDraw()

    /**
     * Converts a drawable object to a bitmap.
     *
     * @param drawable The drawable to be converted.
     * @return The bitmap conversion of the drawable.
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    } // end drawableToBitmap()
}
