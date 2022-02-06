For the full process see [.github/release.md](https://github.com/TWiStErRob/.github/blob/main/RELEASE.md).

## Steps to build and publish

1. Merge everything to `master` that needs to be in the release.
    * Review `version.properties` is right for the target release.
1. Run `gradlew assembleRelease` to produce an APK and mapping file.
1. Create a new Release in the [Alpha track](https://play.google.com/console/u/0/developers/7995455198986011414/app/4975572518830095346/tracks/4697933933227240965).
1. Upload the APK and the mapping file either to App Bundle Explorer or inline.
1. Action any potential warnings or errors.
1. Replace pre-populated release name (`<versionCode> (<versionName>)`) with just `<versionName>`. 
1. Copy Release Notes from previous alpha or production release.
1. Review and publish.
1. Smoke test on a real device installed from Google Play Store.
1. Promote to Production with 100% or staged rollout.
