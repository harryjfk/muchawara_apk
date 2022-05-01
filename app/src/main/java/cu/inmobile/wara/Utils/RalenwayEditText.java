package cu.inmobile.wara.Utils;

/**
 * Created by In.Mobile on 11/07/2018.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

public class RalenwayEditText extends AppCompatEditText {

    public RalenwayEditText(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public RalenwayEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public RalenwayEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("Raleway-Regular.ttf", context);
        setTypeface(customFont);
    }
}
