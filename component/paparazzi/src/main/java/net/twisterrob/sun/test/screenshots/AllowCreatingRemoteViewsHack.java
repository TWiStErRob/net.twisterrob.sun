package net.twisterrob.sun.test.screenshots;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

import org.junit.rules.ExternalResource;
import org.mockito.Answers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Fixes the following exception during Paparazzi screenshot tests.
 * <pre>
 * java.lang.IllegalStateException: Cannot create remote views out of an aplication.
 * 	at android.widget.RemoteViews.getApplicationInfo(RemoteViews.java:3757)
 * 	at android.widget.RemoteViews.<init>(RemoteViews.java:2212)
 * 	at net.twisterrob.sun.android.logic.SunAngleWidgetUpdater.createUpdateViews(SunAngleWidgetUpdater.java:149)
 * </pre>
 */
public class AllowCreatingRemoteViewsHack extends ExternalResource {

	private static final @NonNull Field sCurrentActivityThread;
	private static final @NonNull Field mInitialApplication;

	static {
		try {
			sCurrentActivityThread = ActivityThread.class.getDeclaredField("sCurrentActivityThread");
			sCurrentActivityThread.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException(e);
		}
		try {
			mInitialApplication = ActivityThread.class.getDeclaredField("mInitialApplication");
			mInitialApplication.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException(e);
		}
	}

	private final @NonNull Callable<Context> contextProvider;

	private @Nullable Object backup;

	public AllowCreatingRemoteViewsHack(@NonNull Callable<Context> contextProvider) {
		this.contextProvider = contextProvider;
	}

	@Override protected void before() throws Throwable {
		backup = sCurrentActivityThread.get(null);
		// Default visible constructor, convoluted way to create instance of it.
		ActivityThread thread = mock(ActivityThread.class, withSettings()
				.defaultAnswer(Answers.CALLS_REAL_METHODS)
				.useConstructor());
		mInitialApplication.set(thread, new Application() {
			{
				attachBaseContext(contextProvider.call());
			}
		});

		sCurrentActivityThread.set(null, thread);
	}

	@Override protected void after() {
		try {
			sCurrentActivityThread.set(null, backup);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Cannot restore original state", e);
		}
	}
}
