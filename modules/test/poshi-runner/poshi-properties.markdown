# Poshi Properties

Below are the list of properties that are available in [poshi-runner.properties](poshi-runner/src/main/resources/poshi-runner.properties) which can be overridden in a poshi-runner-ext.properties file.

### accessibility.standards.json
Sets a JSON string that will determine accessibility rule evaluation while using the _assertAccessible_ Selenium method. See more information for rule formation at: [https://axe-core.org/docs/](https://axe-core.org/docs/)

### browser.firefox.bin.file
Sets the path of a Firefox binary so that a particular binary can be used in WebDriver. Useful if there are multiple Firefox installations on a machine. WebDriver will use this binary file when executing tests.

### browser.type
Sets the browser that WebDriver will run with. Valid values include: _android_, _androidchrome_, _chrome_, _edge_, _firefox_, _internetexplorer_, _iossafari_, _safari_. Note, all the mobile browsers have not been tested in a long time, and require other setups. Also note, some browsers are OS specific.

### browser.version
Sets the version of the browser that will run. Required for Internet Explorer and RemoteDriver instances.

### get.location.max.retries
Sets the number of attempts WebDriver will make to a location if the location of the WebElement cannot be returned.

### get.location.timeout
Sets the duration of the timeout (in seconds) that WebDriver will wait before determining a WebElement's location cannot be returned.

### ignore.errors
Sets a delimited list using `ignore.errors.delimiter` of ignorable console errors.

### ignore.errors.delimiter
Sets the character of a delimiter to split separate errors in the `ignore.errors` property.

### ignore.errors.file.name
Sets the path of an XML file that specifies errors that can be ignored.

### liferay.portal.branch
Sets the portal branch name that is being tested. Only for portal, and will soon be removed.

### liferay.portal.bundle
Sets the portal bundle name that is being tested. Only for portal, and will soon be removed.

### logger.resources.url
Sets the URL that Poshi log files (index.html files) use for CSS and JavaScript resources. This is maintained by QA Engineering and does not need to be updated for custom settings.

### mobile.android.home
Sets the directory path of the android home directory.

### mobile.browser
Sets a boolean to determine whether a browser is a mobile browser or not.

### mobile.device.name
Sets the name of the mobile device.

### output.dir.name
Sets a directory path where downloaded files are stored.

### portal.url
Sets the default URL that WebDriver will open to. As Poshi was intended for use primarily with Liferay Portal, `portal.url` actually means the default URL that WebDriver will open to.

### print.java.process.on.fail
Sets the name of a Java process to print its JStack output upon failure.

### save.screenshot
Sets a boolean to allow a screenshot to be saved when tests fail.

### save.web.page
Sets a boolean to allow the web page HTML, CSS, and JavaScript to be saved to a file when tests fail.

### selenium.chrome.driver.executable
Sets the name of the ChromeDriver executable file.

### selenium.desired.capabilities.platform
Sets the name of the platform for desired capabilities. This is only necessary for RemoteDriver and Internet Explorer.

### selenium.executable.dir.name
Sets the directory path of where all selenium executables will be found.

### selenium.ie.driver.executable
Sets the name of the IEDriver executable file.

### selenium.remote.driver.enabled
Sets a boolean that will enable the usage of RemoteDriver.

### selenium.remote.driver.hub
Sets the base URL to the Remote Driver hub.

### tcat.admin.repository
Sets the path of the TCat directory repository.

### tcat.enabled
This is a a boolean that determines whether a portal bundle is using TCat or not.

### test.assert.console.errors
Sets a boolean that will log Liferay console errors when using the assertLiferayErrors Selenium function.

### test.assert.javascript.errors
Sets a boolean to log JavaScript errors when using the assertJavaScriptErrors Selenium function.

### test.assert.warning.exceptions
Sets a boolean to fail after a test has run if the test had any Liferay console errors or JavaScript errors.

### test.base.dir.name
Sets the path of the directory containing Poshi files used for the test project.

### test.batch.group.ignore.regex
Sets a regular expression string of properties that are ignored when grouping test batches together in sequential test runs.

### test.batch.max.group.size
Sets the max number of the group of tests running in a batch.

### test.batch.max.subgroup.size
Sets the max number of the group of tests running in an axis.

### test.batch.property.names
Sets the names of properties used to filter out tests in PQL. Must be paired with test.batch.property.values.

### test.batch.property.values
Sets the values of properties used to filter out tests in PQL. Must be paired with test.batch.property.names.

### test.batch.run.type
Sets how groups of tests are organized. Valid values are _sequential_ or _single_. Sequential will group the tests based off of the other test.batch.* properties, allowing for multiple tests to be set in one axis. Single sets one test per axis. The grouping of tests is printed to another properties file which is read by the CI infrastructure.

### test.case.available.property.names
Sets a comma-delimited list of properties can be set for tests within Poshi.

### test.case.required.property.names
Sets a comma-delimited list of properties that must be set for tests within Poshi.

### test.console.log.file.name
Sets the path to a file of a log file to be evaluated. This is generally used more with Liferay Portal testing.

### test.console.shut.down.file.name
Sets the path to the log file containing the shutdown process of an application server.

### test.dependencies.dir.name
Sets the path to a directory that contains dependency files used for Poshi tests.

### test.include.dir.names
Sets a comma-delimited list of directory paths to include in the Poshi project files.

### test.name
Sets the test case(s) to run. This can be the specific test case command name, or the name of the test case file. Additionally, it can be a comma-delimited list of a combination of both, which will run sequentially. In order to actually take advantage of this, the tests themselves must be configured with proper teardowns to run sequentially.

### test.poshi.warnings.file.name
Sets the path to a file that contains valid Poshi warnings. Used primarily for Testray.

### test.retry.command.wait.time
Sets the time (in seconds) to wait before retrying element retrieval when elements have turned stale.

### test.run.environment
Sets a string to store the run environment. Generally, this denotes public or private by the values of CE or EE.

### test.run.locally
Sets a boolean to determine whether Poshi log frontend resources are used locally or remotely.

### test.skip.tear.down
Sets a boolean to determine whether the tear down is skipped or not.

### timeout.explicit.wait
Sets the time (in seconds) before a "wait" function times out.

### timeout.implicit.wait
Sets the time (in seconds) before all other functions time out.