## IDE

Project is a standard Android Gradle project, so Android Studio or IntelliJ IDEA should work as usual.

## Debugging

### LogCat

```shell
# Statically hard-coded tags.
adb shell setprop log.tag.Sun V
adb shell setprop log.tag.WidgetConfiguration V
adb shell setprop log.tag.Config V
# Dynamic logging tag in LoggingAppWidgetProvider.
adb shell setprop log.tag.SunAngleWidgetProvider V
```
