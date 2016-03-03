package io.github.sealor.prototype.android.espresso.keyboard.testing;

import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.ServiceTestRule;

import org.junit.Rule;
import org.junit.Test;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class MainActivityInstrumentationTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    @Test
    public void validateEditTextWithKeyboardInput() throws TimeoutException {
        KeyboardSwitchHelper helper = new KeyboardSwitchHelper(this.activityTestRule.getActivity());
        helper.showKeyboardPickerDialogIfNeededAndWaitForSwitch();

        Intent serviceIntent = new Intent(InstrumentationRegistry.getTargetContext(), MyKeyboard.class);
        IBinder binder = mServiceRule.bindService(serviceIntent);
        MyKeyboard keyboard = retrieveMyKeyboardInstance(binder);

        onView(withId(R.id.input)).perform(click());
        keyboard.onText("Stefan");
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.output)).check(matches(withText("Hello Stefan!")));
    }

    private MyKeyboard retrieveMyKeyboardInstance(IBinder binder) {
        try {
            Class wrapperClass = Class.forName("android.inputmethodservice.IInputMethodWrapper");
            Field mTargetField = wrapperClass.getDeclaredField("mTarget");
            mTargetField.setAccessible(true);

            WeakReference<MyKeyboard> weakReference = (WeakReference<MyKeyboard>) mTargetField.get(binder);
            return weakReference.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
