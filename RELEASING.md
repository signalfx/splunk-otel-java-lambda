## Releasing a new version

Internal Splunk build releases a new version of the wrapper to both Maven Central and Github whenever a new "RC" tag is pushed to the repository.

Release tag format should be `rc<RELEASE VERSION>`.

Full release procedure is as follows:

* Tag the release candidate version (eg `rc0.0.5`) with the [annotated tag](https://git-scm.com/book/en/v2/Git-Basics-Tagging) `git tag -a rc0.0.5 -m "release candidate 0.0.5"` and `git push origin <tagname>` 
* Release workflow will be run, it needs to be *approved* by a maintainer 
* Release will perform following actions:
  * updates POM / examples / docs version to requested release
  * commits the changes
  * tags release 
  * builds binaries
  * creates GitHub release
  * performs M2 release
  * updates POM / examples to next development version
  * updates release properties
  * commits the changes
  * creates PR with all the release changes (two commits)
* PR will need to be reviewed and merged by either approvers or maintainers 

After the release is completed successfully, the following should happen:
* Github release published
* Two commits created, reflecting preparation for the release and next development cycle
* Central Maven repo updated with the new release of `com.signalfx.public:otel-java-lambda-wrapper`  
* `README.MD` and `outbound-context-propagation.md` mentioning current release 
* `wrapper/pom.xml` and `examples/build.gradle` updated with next release (with -SNAPSHOT)