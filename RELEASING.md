## Releasing a new version

[This GitHub Action](.github/workflows/release.yaml) builds, and releases a new version of the wrapper to both Maven Central and Github whenever a new "RC" tag is pushed to the repository.

Release tag format should be `rc<RELEASE VERSION>`.

Full release procedure is as follows:

* Tag the release candidate version (eg `rc0.0.5`) with the [annotated tag](https://git-scm.com/book/en/v2/Git-Basics-Tagging) and `git push origin <tagname>` 
* Release workflow will be run, it needs to be *approved* by a maintainer 
* Release will perform following actions:
  * updates POM / examples / docs version to requested release
  * commits
  * tags release 
  * builds binaries
  * creates GitHub release
  * performs M2 release
  * updates POM / examples to next development version
  * updates release properties
  * commits
  * pushed all changes

After the release is completed successfully, the following should happen:
* Github release tag (`r<VERSION>`) created
* Github release published
* Two commits created, reflecting preparation for the release and next development cycle
* Central Maven repo updated with the new release of `com.signalfx.public:otel-java-lambda-wrapper`  
* `README.MD` and `outbound-context-propagation.md` mentioning current release 
* `wrapper/pom.xml` and `examples/build.gradle` updated with next release (with -SNAPSHOT)
