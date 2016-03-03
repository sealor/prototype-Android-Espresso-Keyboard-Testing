package io.github.sealor.prototype.android.espresso.keyboard.testing;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;

public class KeyboardSwitchHelper {

    public static final int POLLING_PERIOD_IN_MILLIS = 1000;

    private final Activity activity;
    private final String packageName;
    private final ContentResolver contentResolver;
    private final InputMethodManager inputMethodManager;

    public KeyboardSwitchHelper(Activity activity) {
        this.activity = activity;
        this.packageName = activity.getPackageName();
        this.contentResolver = activity.getContentResolver();
        this.inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private String retrieveCurrentKeyboardName() {
        return Settings.Secure.getString(this.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD);
    }

    private boolean isAppKeyboardTheCurrentKeyboard() {
        return retrieveCurrentKeyboardName().startsWith(this.packageName);
    }

    private boolean isTestActivityFocused() {
        return this.activity.hasWindowFocus();
    }

    private void showKeyboardPickerDialog() {
        this.inputMethodManager.showInputMethodPicker();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void showKeyboardPickerDialogIfNeededAndWaitForSwitch() {
        if (!isAppKeyboardTheCurrentKeyboard()) {
            showKeyboardPickerDialog();

            do {
                sleep(POLLING_PERIOD_IN_MILLIS);
            } while (!isTestActivityFocused());
        }
    }
}
