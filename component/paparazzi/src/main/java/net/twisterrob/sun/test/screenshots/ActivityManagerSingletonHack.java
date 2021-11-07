package net.twisterrob.sun.test.screenshots;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import org.junit.rules.ExternalResource;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Singleton;

/**
 * Fixes the following exception during Paparazzi screenshot tests.
 * <pre>
 * java.lang.NullPointerException
 * 	at android.app.PendingIntent.getBroadcastAsUser(PendingIntent.java:572)
 * 	at android.app.PendingIntent.getBroadcast(PendingIntent.java:555)
 * 	at net.twisterrob.sun.android.logic.SunAngleWidgetUpdater.createRefreshIntent(SunAngleWidgetUpdater.java:240)
 * </pre>
 */
public class ActivityManagerSingletonHack extends ExternalResource {

	private static final Field iActivityManagerSingleton;

	static {
		try {
			iActivityManagerSingleton = ActivityManager.class.getDeclaredField("IActivityManagerSingleton");
			iActivityManagerSingleton.setAccessible(true);
			clearFinal(iActivityManagerSingleton);
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	private @Nullable Object backup;

	@Override protected void before() throws Throwable {
		backup = iActivityManagerSingleton.get(null);
		iActivityManagerSingleton.set(null, new Singleton<IActivityManager>() {
			@Override protected IActivityManager create() {
				return mockActivityManager();
			}
		});
	}

	@Override protected void after() {
		try {
			iActivityManagerSingleton.set(null, backup);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Cannot restore original state", e);
		}
	}

	/**
	 * Workaround because {@code IActivityManager manager = mock(IActivityManager.class)}
	 * throws {@link IncompatibleClassChangeError}.
	 */
	private static @NonNull IActivityManager mockActivityManager() {
		return (IActivityManager)Proxy.newProxyInstance(
				IActivityManager.class.getClassLoader(),
				new Class[] {IActivityManager.class},
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) {
						return null;
					}
				});
	}

	private static void clearFinal(Field field) throws NoSuchFieldException, IllegalAccessException {
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	}
}
