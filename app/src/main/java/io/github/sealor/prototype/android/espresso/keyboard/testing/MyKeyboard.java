package io.github.sealor.prototype.android.espresso.keyboard.testing;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

public class MyKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    public final static int KEYCODE_ENTER = -10;

    private Keyboard keyboardLayout;
    private KeyboardView keyboardView;

    private boolean shifted = false;

    @Override
    public void onInitializeInterface() {
        super.onInitializeInterface();

        this.keyboardLayout = new Keyboard(this, R.xml.qwertz);
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
    }

    @Override
    public void onFinishInput() {
        super.onFinishInput();

        if (this.keyboardView != null) {
            this.keyboardView.closing();
        }
    }

    @Override
    public View onCreateInputView() {
        Log.i("tag", "createInputView");
        this.keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboardview, null);
        this.keyboardView.setKeyboard(this.keyboardLayout);
        this.keyboardView.setOnKeyboardActionListener(this);
        return this.keyboardView;
    }

    private void sendTypedKey(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    private void updateShiftState() {
        this.keyboardView.setShifted(this.shifted);
        this.keyboardLayout.setShifted(this.shifted);
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        Log.d("tag", "onKey: " + primaryCode);

        if (primaryCode > 0) {
            String text = String.valueOf((char) primaryCode);
            if (this.keyboardLayout.isShifted()) {
                text = text.toUpperCase();
            }
            onText(text);
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            sendTypedKey(KeyEvent.KEYCODE_DEL);
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            shifted = !shifted;
            updateShiftState();
        } else if (primaryCode == MyKeyboard.KEYCODE_ENTER) {
            sendTypedKey(KeyEvent.KEYCODE_ENTER);
        }
    }

    @Override
    public void onText(CharSequence text) {
        Log.d("tag", "onText: " + text);

        getCurrentInputConnection().commitText(text, 1);

        shifted = false;
        updateShiftState();
    }

    @Override
    public void onPress(int primaryCode) {
        Log.d("tag", "onPress: " + primaryCode);
    }

    @Override
    public void onRelease(int primaryCode) {
        Log.d("tag", "onRelease: " + primaryCode);
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {
    }
}
