## Releasing a new version

[This GitHub Action](.github/workflows/release.yaml) builds, and releases a new version of the wrapper to both Maven Central and Github whenever a new tag is pushed to the repository.

Full release procedure is as follows:

* Checkout the latest version of the `main` branch.
* Update wrapper POM version to release (remove -SNAPSHOT)
* Set wrapper dependency for examples to release (`examples/build.gradle`)
* Commit / push / merge PR
* Tag that version (eg `v0.0.5` - min `v` prefix!) with the [annotated tag](https://git-scm.com/book/en/v2/Git-Basics-Tagging) and `git push origin <tagname>` 
* tag will run the release workflow that needs to be *approved* by a maintainer 
* Update wrapper POM version to next `-SNAPSHOT` 
* Set wrapper dependency for examples to next snahpshot (`examples/build.gradle`)
* Update main `README.MD` and `outbound-context-propagation.md` files with new version number (sans -SNAPSHOT)
* Commit / push / merge PR

After the release is completed successfully, the following should happen:
* Github tag / release created
* Central Maven repo updated with the new release of `com.signalfx.public:otel-java-lambda-wrapper`  
* `README.MD` and `outbound-context-propagation.md` mentioning next release (sans -SNAPSHOT)
* `wrapper/pom.xml` and `examples/build.gradle` updated with next release (with -SNAPSHOT)
