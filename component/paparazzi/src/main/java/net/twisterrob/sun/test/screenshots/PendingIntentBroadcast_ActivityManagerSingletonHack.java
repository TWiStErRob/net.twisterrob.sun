package net.twisterrob.sun.test.screenshots;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import org.junit.rules.ExternalResource;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.util.Singleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Fixes the following exception during Paparazzi screenshot tests.
 * <pre>
 * java.lang.NullPointerException
 *     at android.app.PendingIntent.getBroadcastAsUser(PendingIntent.java:649)
 *     at android.app.PendingIntent.getBroadcast(PendingIntent.java:632)
 *     at net.twisterrob.sun.android.logic.SunAngleWidgetView.createRefreshIntent(SunAngleWidgetView.java:187)
 *     at net.twisterrob.sun.android.logic.SunAngleWidgetView.createUpdateViews(SunAngleWidgetView.java:82)
 * </pre>
 */
public class PendingIntentBroadcast_ActivityManagerSingletonHack extends ExternalResource {

	private static final Field IActivityManagerSingleton;

	static {
		//noinspection RedundantSuppression false positive.
		//noinspection TryWithIdenticalCatches lint:NewApi Multi-catch with these reflection exceptions requires API level 19 (current min is 14) because they get compiled to the common but new super type ReflectiveOperationException. As a workaround either create individual catch statements, or catch Exception.
		try {
			IActivityManagerSingleton = ActivityManager.class.getDeclaredField("IActivityManagerSingleton");
			IActivityManagerSingleton.setAccessible(true);
			clearFinal(IActivityManagerSingleton);
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	private @Nullable Object backup;

	@Override protected void before() throws Throwable {
		backup = IActivityManagerSingleton.get(null);
		IActivityManagerSingleton.set(null, new Singleton<IActivityManager>() {
			@Override protected IActivityManager create() {
				return mockActivityManager();
			}
		});
	}

	@Override protected void after() {
		try {
			IActivityManagerSingleton.set(null, backup);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Cannot restore original state", e);
		}
	}

	/**
	 * Workaround because {@code IActivityManager manager = mock(IActivityManager.class)}
	 * throws {@link IncompatibleClassChangeError}.
	 */
	@SuppressWarnings("rawtypes") // Class[]
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

	private static void clearFinal(@NonNull Field field)
			throws NoSuchFieldException, IllegalAccessException {
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	}
}
