package com.fredwang.demo.practiceview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.fredwang.demo.practiceview.Utils;

import java.text.DecimalFormat;

/**
 * @Package: com.fredwang.demo.practiceview.view
 * @Author: FredWang
 * @DateTime: 2019-10-26 09:00
 * @Description: 仪表盘自定义 View
 **/
public class DashboardView extends View {
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Path path = new Path();
    Path dashPath = new Path();
    PathMeasure pathMeasure = null;
    RectF rectArc = new RectF();        // 刻度弧框
    RectF rectEnergy = new RectF();     // 能量弧框
    RectF rectText = new RectF();       // 文字框
    Shader shader = null;
    Matrix matrix = new Matrix();

    float mark = 0;
    float progress = 0;

    final float DASHBOARD_RADIUS = Utils.dpToPixel(150);    // 圆弧半径
    final float POINTER_LENGTH = Utils.dpToPixel(100);      // 指针长度
    final float ENERGY_CIRCLE_RADIUS = Utils.dpToPixel(130);// 能量环半径
    final float TEXT_BG_WIDTH = Utils.dpToPixel(100);       // 文字框背景长度
    final float TEXT_BG_HEIGHT = Utils.dpToPixel(40);       // 文字框背景宽度
    final int DASHBOARD_ANGLE = 120;     // 仪表盘底部缺失角度
    final int START_ANGLE = 90 + DASHBOARD_ANGLE / 2;       // 起始角度
    final int SWEEP_ANGLE = 360 - DASHBOARD_ANGLE;      // 旋转角度

    public DashboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 这段代码会在构造函数 super() 之后执行
     */
    {

//        paint.setColor(Color.BLACK);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(Utils.dpToPixel(2));

    }

    public float getMark() {
        return mark;
    }

    public void setMark(float mark) {
        this.mark = mark;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rectArc.set(getWidth() / 2 - DASHBOARD_RADIUS, getHeight() / 2 - DASHBOARD_RADIUS,
                getWidth() / 2 + DASHBOARD_RADIUS, getHeight() / 2 + DASHBOARD_RADIUS);

        rectEnergy.set(getWidth() / 2 - ENERGY_CIRCLE_RADIUS, getHeight() / 2 - ENERGY_CIRCLE_RADIUS,
                getWidth() / 2 + ENERGY_CIRCLE_RADIUS, getHeight() / 2 + ENERGY_CIRCLE_RADIUS);

        // 画刻度
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Utils.dpToPixel(2));
        path.addArc(rectArc, 90 + DASHBOARD_ANGLE / 2, 360 - DASHBOARD_ANGLE);
        // PathMeasure 用来测量 Path 的长度，位置等信息
        pathMeasure = new PathMeasure(path, false);
        // dashPath 用来画仪表盘的刻度，并通过 PathDashPathEffect 设置为 paint 的效果
        dashPath.addRect(new RectF(0, 0, Utils.dpToPixel(2), Utils.dpToPixel(8)), Path.Direction.CCW);
        paint.setPathEffect(new PathDashPathEffect(dashPath, (pathMeasure.getLength() -  Utils.dpToPixel(2))/ 20, 0, PathDashPathEffect.Style.ROTATE));
        canvas.drawPath(path, paint);

        // 画弧
        paint.setPathEffect(null);
        paint.setStrokeCap(Paint.Cap.BUTT);
        path.addArc(rectArc, 90 + DASHBOARD_ANGLE / 2, 360 - DASHBOARD_ANGLE);
        canvas.drawPath(path, paint);

        // 画中心黄色圆
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#F4B350"));
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, Utils.dpToPixel(10), paint);

        // 画中心红色圆
        paint.setColor(Color.parseColor("#CF000F"));
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, Utils.dpToPixel(6), paint);

        // 画指针
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(Utils.dpToPixel(4));
        canvas.drawLine(getWidth() / 2,getHeight() / 2,
                (float) (getWidth() / 2 + Math.cos(Math.toRadians(getAngleFromMark(mark))) * POINTER_LENGTH),
                (float) (getHeight() / 2 + Math.sin(Math.toRadians(getAngleFromMark(mark))) * POINTER_LENGTH), paint);

        // 画灰色能量环
        paint.setStrokeWidth(Utils.dpToPixel(10));
        paint.setColor(Color.parseColor("#BDC3C7"));
        canvas.drawArc(rectEnergy, START_ANGLE, SWEEP_ANGLE, false, paint);

        // 画红色能量环
        /**
         * 渐变色画圆弧 注意事项：
         * 1. 选择 SweepGradient
         * 2. 通过 Matrix 将渐变色起始点旋转至 圆弧起始角度START_ANGLE 位置
         * 3. 将 Matrix 设置到 Shader 上
         */
        matrix.setRotate(START_ANGLE, getWidth() / 2, getHeight() / 2);
        shader = new SweepGradient(getWidth() / 2, getHeight() / 2, new int[]{Color.parseColor("#F7CA18"), Color.parseColor("#CF000F"), Color.parseColor("#F7CA18")}, new float[]{0, 2f/3f, 1});
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        paint.setStrokeWidth(Utils.dpToPixel(10));
//        paint.setColor(Color.parseColor("#CF000F"));
        canvas.drawArc(rectEnergy, START_ANGLE, getSweepAngleFromMark(mark), false, paint);
        // 取消 shader 效果，避免在执行动画时，重复执行 onDraw() 造成效果错乱
        paint.setShader(null);

        // 画文字灰色背景框
        rectText.set(getWidth() / 2 - TEXT_BG_WIDTH / 2, getHeight() / 2 + DASHBOARD_RADIUS - TEXT_BG_HEIGHT, getWidth() / 2 + TEXT_BG_WIDTH / 2, getHeight() / 2 + DASHBOARD_RADIUS);
        paint.setColor(Color.parseColor("#BDC3C7"));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rectText, Utils.dpToPixel(10), Utils.dpToPixel(10), paint);

        // 画白色文字
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(Utils.dpToPixel(24));
        paint.setTextSkewX(-0.5f);
        paint.setFakeBoldText(true);
//        paint.getTextBounds(text, 0, text.length(), bounds);
        progress = 10 * mark;
        // 格式化后，初始值为 .00 ？？？
//        canvas.drawText("" + new DecimalFormat(".00").format(progress), getWidth() / 2 - Utils.dpToPixel(4), getHeight() / 2 + DASHBOARD_RADIUS - Utils.dpToPixel(20), paint);
        canvas.drawText((int) progress + " Km", getWidth() / 2 - Utils.dpToPixel(4), getHeight() / 2 + DASHBOARD_RADIUS - Utils.dpToPixel(10), paint);

    }

    // 通过刻度获取角度
    private float getAngleFromMark(float mark) {
        // 起始角度 + 第几个刻度 * 每个刻度对应的角度值
        return (90 + DASHBOARD_ANGLE / 2) + (360 - DASHBOARD_ANGLE) / 20 * mark;
    }

    // 通过刻度获取旋转角度
    private float getSweepAngleFromMark(float mark) {
        return SWEEP_ANGLE / 20 * mark;
    }
}
