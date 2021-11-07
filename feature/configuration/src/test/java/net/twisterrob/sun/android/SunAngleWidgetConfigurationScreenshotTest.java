package net.twisterrob.sun.android;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManagerImpl;
import android.widget.SeekBar;

import com.google.testing.junit.testparameterinjector.TestParameterInjector;

import net.twisterrob.sun.algo.SunSearchResults;
import net.twisterrob.sun.algo.SunSearchResults.Moment;
import net.twisterrob.sun.algo.SunSearchResults.Range;
import net.twisterrob.sun.algo.SunSearchResults.SunSearchParams;
import net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation;
import net.twisterrob.sun.configuration.R;
import net.twisterrob.sun.test.screenshots.PaparazziCoat;
import net.twisterrob.sun.test.screenshots.ScreenshotTest;

@RunWith(TestParameterInjector.class)
@Category(ScreenshotTest.class)
public class SunAngleWidgetConfigurationScreenshotTest {

	@Rule
	public PaparazziCoat paparazzi = new PaparazziCoat();

	private @NonNull SunAngleWidgetConfiguration sut;

	@Before
	public void setUp() throws Throwable {
		sut = new SunAngleWidgetConfiguration();

		prepareActivity(sut, overrideWindowService(paparazzi.getContext()), null);
		sut.onCreate(null, null);
		sut.onPostCreate(null, null);
	}

	private @NonNull View detachView() {
		View view = sut.getWindow().getDecorView().findViewById(android.R.id.content);
		((ViewGroup)view.getParent()).removeView(view);
		return view;
	}

	@Test
	public void testNoLocation() {
		sut.updateUI(createResults(Double.NaN, Double.NaN));

		paparazzi.snapshot(detachView());
	}

	@Test
	public void testValidSettings() {
		sut.updateUI(createResults(0, 0));

		paparazzi.snapshot(detachView());
	}

	@Test
	public void testTooHigh() {
		((SeekBar)sut.findViewById(R.id.angle)).setProgress(90 + 60);

		sut.updateUI(createResults(0, 0));

		paparazzi.snapshot(detachView());
	}

	@Test
	public void testTooLow() {
		((SeekBar)sut.findViewById(R.id.angle)).setProgress(90 - 60);

		sut.updateUI(createResults(0, 0));

		paparazzi.snapshot(detachView());
	}

	private @NonNull SunSearchResults createResults(double latitude, double longitude) {
		return new SunSearchResults(
				new SunSearchParams(
						latitude,
						longitude,
						midnight(),
						ThresholdRelation.ABOVE,
						Double.NaN
				),
				new Moment(midnight(), Double.NaN),
				new Moment(midnight(), -45),
				new Moment(midnight(), +45),
				new Range(midnight(), midnight()),
				new Range(midnight(), midnight())
		);
	}

	private static @NonNull Calendar midnight() {
		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.HOUR_OF_DAY, 0);
		instance.set(Calendar.MINUTE, 0);
		instance.set(Calendar.SECOND, 0);
		instance.set(Calendar.MILLISECOND, 0);
		return instance;
	}

	private static void prepareActivity(
			@NonNull Activity activity,
			final @NonNull Context context,
			@Nullable Bundle savedInstanceState
	) throws IllegalAccessException, InvocationTargetException {
		Method attach = null;
		for (Method method : Activity.class.getDeclaredMethods()) {
			if (method.getName().equals("attach")) {
				method.setAccessible(true);
				attach = method;
				break;
			}
		}
		if (attach == null) {
			throw new IllegalStateException("No attach method");
		}
		ActivityInfo activityInfo = new ActivityInfo();
		activityInfo.applicationInfo = new ApplicationInfo();
		attach.invoke(
				activity,
				context,
				null /*ActivityThread aThread*/,
				null /*Instrumentation instr*/,
				null /*IBinder token*/,
				0 /*int ident*/,
				new Application() {
					{
						attachBaseContext(context);
					}
				} /*Application application*/,
				new Intent(context, SunAngleWidgetConfiguration.class) /*Intent intent*/,
				activityInfo /*ActivityInfo info*/,
				null /*CharSequence title*/,
				null /*Activity parent*/,
				null /*String id*/,
				null /*Activity.NonConfigurationInstances lastNonConfigurationInstances*/,
				null /*Configuration config*/,
				null /*String referrer*/,
				null /*IVoiceInteractor voiceInteractor*/,
				null /*Window window*/,
				null /*ActivityConfigCallback activityConfigCallback*/,
				null /*IBinder assistToken*/
		);
		activity.setTheme(context.getTheme());
		activity.onCreate(savedInstanceState, null);
	}

	private static @NonNull Context overrideWindowService(@NonNull Context context) {
		context = spy(context);
		doReturn(wrapAsWindowManagerImpl(context)).when(context).getSystemService(Context.WINDOW_SERVICE);
		doReturn(context).when(context).createDisplayContext(ArgumentMatchers.<Display>any());
		return context;
	}

	/**
	 * <pre>
	 * Caused by: java.lang.ClassCastException: class com.android.layoutlib.bridge.android.view.WindowManagerImpl
	 * cannot be cast to class android.view.WindowManagerImpl
	 * (com.android.layoutlib.bridge.android.view.WindowManagerImpl and android.view.WindowManagerImpl are in unnamed module of loader 'app')
	 * 	at android.view.Window.setWindowManager(Window.java:778)
	 * 	at android.app.Activity.attach(Activity.java:7750)
	 * </pre>
	 */
	private static @NonNull WindowManager wrapAsWindowManagerImpl(@NonNull Context context) {
		final WindowManager wrapped = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		return mock(WindowManagerImpl.class, new Answer<Object>() {
			@Override public Object answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getMethod().getDeclaringClass() == WindowManagerImpl.class) {
					return invocation.callRealMethod();
				} else {
					return invocation.getMethod().invoke(wrapped, invocation.getArguments());
				}
			}
		});
	}
}
