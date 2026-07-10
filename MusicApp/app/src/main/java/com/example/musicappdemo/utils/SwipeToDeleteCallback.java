package com.example.musicappdemo.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicappdemo.R;

public abstract class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private final Context context;
    private final int iconMargin;
    private final Paint paint = new Paint();
    private final Paint textPaint = new Paint();

    public SwipeToDeleteCallback(Context context) {
        super(0, ItemTouchHelper.LEFT);
        this.context = context;
        this.iconMargin = (int) (16 * context.getResources().getDisplayMetrics().density);
        
        paint.setColor(Color.parseColor("#B0273F")); // Màu đỏ xóa
        
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(12 * context.getResources().getDisplayMetrics().density);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.5f; // Vuốt quá 50% mới kích hoạt onSwiped
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return defaultValue * 10f; // Khó vuốt bay mất hơn
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return defaultValue * 0.1f;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        float itemWidth = (float) itemView.getWidth();
        
        // Giới hạn dX chỉ kéo được tối đa 50% màn hình
        float limitedDX = Math.max(dX, -itemWidth / 2);

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && limitedDX < 0) {
            // Draw red background
            RectF background = new RectF((float) itemView.getRight() + limitedDX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
            c.drawRect(background, paint);

            // Draw icon
            Drawable icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete);
            if (icon != null) {
                int iconSize = (int) (24 * context.getResources().getDisplayMetrics().density);
                float height = (float) itemView.getBottom() - (float) itemView.getTop();
                int iconTop = itemView.getTop() + ((int) height - iconSize) / 2 - 10;
                
                // Căn giữa icon trong phần đỏ đã kéo ra
                float redAreaWidth = Math.abs(limitedDX);
                int iconRight = (int) (itemView.getRight() - (redAreaWidth / 2) + (iconSize / 2));
                int iconLeft = iconRight - iconSize;
                int iconBottom = iconTop + iconSize;

                icon.setTint(Color.WHITE);
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                icon.draw(c);
                
                // Draw "Xóa" text dưới icon
                float textX = (iconLeft + iconRight) / 2f;
                float textY = iconBottom + (12 * context.getResources().getDisplayMetrics().density);
                c.drawText("Xóa", textX, textY, textPaint);
            }
            
            // Dịch chuyển itemView nhưng giới hạn lại
            super.onChildDraw(c, recyclerView, viewHolder, limitedDX, dY, actionState, isCurrentlyActive);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
