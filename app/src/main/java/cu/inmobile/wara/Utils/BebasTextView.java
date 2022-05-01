package cu.inmobile.wara.Utils;

/**
 * Created by In.Mobile on 11/07/2018.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatTextView;

public class BebasTextView extends AppCompatTextView {

    public BebasTextView(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public BebasTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public BebasTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("BebasNeue Bold.otf", context);
        setTypeface(customFont);
    }
}
