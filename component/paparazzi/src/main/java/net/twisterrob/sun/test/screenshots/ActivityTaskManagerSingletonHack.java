package net.twisterrob.sun.test.screenshots;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.rules.ExternalResource;

import static org.mockito.Mockito.mock;

import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.util.Singleton;

import androidx.annotation.Nullable;

/**
 * Fixes the following exception during Paparazzi screenshot tests.
 * <pre>
 * java.lang.NullPointerException
 * 	at android.app.Activity.setTaskDescription(Activity.java:6742)
 * 	at android.app.Activity.onApplyThemeResource(Activity.java:5010)
 * 	at android.view.ContextThemeWrapper.initializeTheme(ContextThemeWrapper.java:216)
 * 	at android.view.ContextThemeWrapper.getTheme(ContextThemeWrapper.java:175)
 * 	at android.content.Context.obtainStyledAttributes(Context.java:738)
 * 	at android.view.Window.getWindowStyle(Window.java:703)
 * 	at com.android.internal.policy.PhoneWindow.generateLayout(PhoneWindow.java:2339)
 * 	at com.android.internal.policy.PhoneWindow.installDecor(PhoneWindow.java:2694)
 * 	at com.android.internal.policy.PhoneWindow.setContentView(PhoneWindow.java:428)
 * 	at android.app.Activity.setContentView(Activity.java:3326)
 * </pre>
 */
public class ActivityTaskManagerSingletonHack extends ExternalResource {

	private static final Field IActivityTaskManagerSingleton;

	static {
		try {
			IActivityTaskManagerSingleton = ActivityTaskManager.class.getDeclaredField("IActivityTaskManagerSingleton");
			IActivityTaskManagerSingleton.setAccessible(true);
			clearFinal(IActivityTaskManagerSingleton);
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	private @Nullable Object backup;

	@Override protected void before() throws Throwable {
		backup = IActivityTaskManagerSingleton.get(null);
		IActivityTaskManagerSingleton.set(null, new Singleton<IActivityTaskManager>() {
			@Override protected IActivityTaskManager create() {
				return mock(IActivityTaskManager.class);
			}
		});
	}

	@Override protected void after() {
		try {
			IActivityTaskManagerSingleton.set(null, backup);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Cannot restore original state", e);
		}
	}

	private static void clearFinal(@SuppressWarnings("TypeMayBeWeakened") Field field)
			throws NoSuchFieldException, IllegalAccessException {
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	}
}
